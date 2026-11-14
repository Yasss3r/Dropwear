import { auth } from "@/auth";
import { NextResponse } from "next/server";

export default auth((req) => {
  const { nextUrl } = req;
  const isLoggedIn = !!req.auth?.user;
  const userRoles = (req.auth?.roles as string[]) || [];

  const isLoginPage = nextUrl.pathname.startsWith("/login");
  const isAuthApi = nextUrl.pathname.startsWith("/api/auth");
  const isUnauthorizedPage = nextUrl.pathname.startsWith("/unauthorized");

  // Laisser passer les API d'authentification
  if (isAuthApi) {
    return NextResponse.next();
  }

  // 1. Rediriger l'utilisateur connecté s'il tente d'aller sur /login
  if (isLoggedIn && isLoginPage) {
    return NextResponse.redirect(new URL("/dashboard", nextUrl));
  }

  // 2. Rediriger vers /login si non authentifié et tentative d'accès à une page protégée
  if (!isLoggedIn && !isLoginPage && !isUnauthorizedPage) {
    const callbackUrl = encodeURIComponent(nextUrl.pathname + nextUrl.search);
    return NextResponse.redirect(new URL(`/login?callbackUrl=${callbackUrl}`, nextUrl));
  }

  // 3. Contrôle d'accès basé sur les rôles (RBAC)
  if (isLoggedIn) {
    // Route /admin réservée strictement au rôle ADMIN
    if (nextUrl.pathname.startsWith("/admin") && !userRoles.includes("ADMIN")) {
      return NextResponse.redirect(new URL("/unauthorized?required=ADMIN", nextUrl));
    }
  }

  return NextResponse.next();
});

export const config = {
  matcher: [
    /*
     * Match all request paths except:
     * - _next/static (fichiers statiques)
     * - _next/image (optimisation d'images)
     * - favicon.ico
     * - images et icônes
     */
    "/((?!_next/static|_next/image|favicon.ico|.*\\.(?:svg|png|jpg|jpeg|gif|webp)$).*)",
  ],
};
