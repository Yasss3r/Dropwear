import NextAuth from "next-auth";
import Keycloak from "next-auth/providers/keycloak";
import type { JWT } from "next-auth/jwt";

async function refreshAccessToken(token: JWT): Promise<JWT> {
  try {
    const issuer = process.env.AUTH_KEYCLOAK_ISSUER;
    if (!issuer || !token.refreshToken) {
      throw new Error("Missing issuer or refresh token");
    }

    const tokenEndpoint = `${issuer}/protocol/openid-connect/token`;

    const response = await fetch(tokenEndpoint, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        client_id: process.env.AUTH_KEYCLOAK_ID || "",
        client_secret: process.env.AUTH_KEYCLOAK_SECRET || "",
        grant_type: "refresh_token",
        refresh_token: token.refreshToken,
      }),
    });

    const refreshedTokens = await response.json();

    if (!response.ok) {
      throw refreshedTokens;
    }

    // Parse roles from new access token if available
    let roles = token.roles || [];
    if (refreshedTokens.access_token) {
      try {
        const payloadBase64 = refreshedTokens.access_token.split(".")[1];
        const payloadJson = Buffer.from(payloadBase64, "base64").toString("utf-8");
        const payload = JSON.parse(payloadJson);
        const parsedRoles = payload.realm_access?.roles || payload.roles;
        if (Array.isArray(parsedRoles) && parsedRoles.length > 0) {
          roles = parsedRoles.filter(
            (r: string) => !["default-roles-dropflow", "offline_access", "uma_authorization"].includes(r)
          );
        }
      } catch {
        // keep current roles
      }
    }

    return {
      ...token,
      accessToken: refreshedTokens.access_token,
      refreshToken: refreshedTokens.refresh_token ?? token.refreshToken,
      expiresAt: Math.floor(Date.now() / 1000) + (refreshedTokens.expires_in ?? 300),
      roles,
      error: undefined,
    };
  } catch {
    return {
      ...token,
      error: "RefreshTokenError",
    };
  }
}

export const { handlers, signIn, signOut, auth } = NextAuth({
  trustHost: true,
  providers: [
    Keycloak({
      clientId: process.env.AUTH_KEYCLOAK_ID,
      clientSecret: process.env.AUTH_KEYCLOAK_SECRET,
      issuer: process.env.AUTH_KEYCLOAK_ISSUER,
      authorization: {
        params: {
          scope: "openid email profile roles",
        },
      },
    }),
  ],
  pages: {
    signIn: "/login",
    error: "/login",
  },
  session: {
    strategy: "jwt",
    maxAge: 30 * 24 * 60 * 60, // 30 days
  },
  callbacks: {
    async jwt({ token, account, profile }) {
      // 1. Premier sign-in : extraction initiale des tokens et des rôles
      if (account) {
        let extractedRoles: string[] = [];

        // Lecture depuis le jeton d'accès JWT (realm_access.roles)
        if (account.access_token) {
          try {
            const parts = account.access_token.split(".");
            if (parts.length > 1) {
              const decoded = JSON.parse(Buffer.from(parts[1], "base64").toString("utf-8"));
              const realmRoles = decoded.realm_access?.roles || decoded.roles;
              if (Array.isArray(realmRoles)) {
                extractedRoles = realmRoles;
              }
            }
          } catch {
            // Ignorer si le décodage échoue
          }
        }

        // Repli sur le profil Keycloak
        if (extractedRoles.length === 0 && profile) {
          const profileWithRoles = profile as { realm_access?: { roles?: string[] }; roles?: string[] };
          if (Array.isArray(profileWithRoles.realm_access?.roles)) {
            extractedRoles = profileWithRoles.realm_access.roles;
          } else if (Array.isArray(profileWithRoles.roles)) {
            extractedRoles = profileWithRoles.roles;
          }
        }

        // Nettoyage des rôles techniques par défaut Keycloak
        const domainRoles = extractedRoles.filter(
          (role) => !["default-roles-dropflow", "offline_access", "uma_authorization"].includes(role)
        );

        return {
          ...token,
          accessToken: account.access_token,
          refreshToken: account.refresh_token,
          expiresAt: account.expires_at ?? Math.floor(Date.now() / 1000) + 300,
          roles: domainRoles.length > 0 ? domainRoles : extractedRoles,
        };
      }

      // 2. Requêtes ultérieures : le jeton est-il encore valide ? (marge de sécurité de 10s)
      if (token.expiresAt && Date.now() < (token.expiresAt - 10) * 1000) {
        return token;
      }

      // 3. Jeton expiré : rafraîchissement automatique auprès de Keycloak
      return refreshAccessToken(token);
    },

    async session({ session, token }) {
      session.accessToken = token.accessToken;
      session.roles = token.roles || [];
      session.error = token.error;

      if (session.user) {
        session.user.roles = token.roles || [];
      }

      return session;
    },
  },
});
