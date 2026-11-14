import { signOut } from "@/auth";
import { NextResponse } from "next/server";

export async function GET(request: Request) {
  const issuer = process.env.AUTH_KEYCLOAK_ISSUER || "http://localhost:8081/realms/dropflow";
  const clientId = process.env.AUTH_KEYCLOAK_ID || "dropflow-web";
  const origin = new URL(request.url).origin;
  const postLogoutRedirectUri = `${origin}/login`;

  // 1. Destruction du cookie de session NextAuth
  await signOut({ redirect: false });

  // 2. Redirection vers l'endpoint RP-Initiated Logout de Keycloak pour fermer la session SSO
  const keycloakLogoutUrl = `${issuer}/protocol/openid-connect/logout?post_logout_redirect_uri=${encodeURIComponent(
    postLogoutRedirectUri
  )}&client_id=${encodeURIComponent(clientId)}`;

  return NextResponse.redirect(keycloakLogoutUrl);
}
