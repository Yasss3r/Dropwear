import { auth } from "@/auth";
import Link from "next/link";
import { ShieldX, ArrowLeft, LogOut, ShieldAlert } from "lucide-react";

export const dynamic = "force-dynamic";

export default async function UnauthorizedPage({
  searchParams,
}: {
  searchParams?: { required?: string };
}) {
  const session = await auth();
  const currentRoles = session?.roles || [];
  const requiredRole = searchParams?.required || "ADMIN";

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-slate-900/90 rounded-2xl p-8 border border-rose-900/40 shadow-2xl text-center space-y-6">
        <div className="w-16 h-16 rounded-2xl bg-rose-500/10 border border-rose-500/30 flex items-center justify-center mx-auto text-rose-400">
          <ShieldX className="w-8 h-8" />
        </div>

        <div className="space-y-2">
          <span className="px-2.5 py-1 rounded-full text-xs font-bold uppercase tracking-wider bg-rose-500/20 text-rose-400 border border-rose-500/30">
            Erreur 403 &bull; Accès Refusé
          </span>
          <h1 className="text-2xl font-bold text-white pt-1">
            Privilèges insuffisants
          </h1>
          <p className="text-xs text-slate-400">
            Votre compte Keycloak ne dispose pas des habilitations nécessaires pour accéder à cette ressource protégée par le middleware RBAC.
          </p>
        </div>

        {/* Détails RBAC */}
        <div className="p-4 rounded-xl bg-slate-950/70 border border-slate-800 text-left space-y-3 text-xs">
          <div className="flex items-center justify-between">
            <span className="text-slate-400">Rôle requis :</span>
            <span className="font-mono font-bold text-rose-400 bg-rose-950/50 px-2 py-0.5 rounded border border-rose-900/60">
              {requiredRole}
            </span>
          </div>

          <div className="flex items-start justify-between gap-2 pt-2 border-t border-slate-800/80">
            <span className="text-slate-400">Vos rôles actuels :</span>
            <div className="flex flex-wrap justify-end gap-1">
              {currentRoles.length > 0 ? (
                currentRoles.map((r) => (
                  <span
                    key={r}
                    className="font-mono text-[10px] bg-slate-800 text-slate-300 px-1.5 py-0.5 rounded border border-slate-700"
                  >
                    {r}
                  </span>
                ))
              ) : (
                <span className="text-slate-500 italic">Aucun rôle</span>
              )}
            </div>
          </div>
        </div>

        {/* Boutons d'action */}
        <div className="flex flex-col sm:flex-row gap-3 pt-2">
          <Link
            href="/dashboard"
            className="flex-1 inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl text-xs font-semibold text-slate-200 bg-slate-800 hover:bg-slate-700 border border-slate-700 transition"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>Tableau de bord</span>
          </Link>

          <a
            href="/api/auth/logout"
            className="flex-1 inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl text-xs font-semibold text-white bg-rose-600 hover:bg-rose-500 shadow-lg shadow-rose-600/30 transition"
          >
            <LogOut className="w-4 h-4" />
            <span>Changer de compte</span>
          </a>
        </div>
      </div>
    </div>
  );
}
