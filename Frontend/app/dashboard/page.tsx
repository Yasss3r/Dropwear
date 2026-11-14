import { auth } from "@/auth";
import { redirect } from "next/navigation";
import Link from "next/link";
import {
  ShieldAlert,
  User,
  Mail,
  ShieldCheck,
  KeyRound,
  LogOut,
  ExternalLink,
  Layers,
  Clock,
  Server,
  ArrowRight,
} from "lucide-react";

export const dynamic = "force-dynamic";

export default async function DashboardPage() {
  const session = await auth();

  if (!session?.user) {
    redirect("/login");
  }

  const { user, roles = [], accessToken, error } = session;
  const isAdmin = roles.includes("ADMIN");

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 flex flex-col">
      {/* Barre de navigation supérieure */}
      <header className="border-b border-slate-800 bg-slate-900/80 backdrop-blur sticky top-0 z-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-brand-600 to-orange-500 flex items-center justify-center font-bold text-white shadow-md shadow-brand-500/20">
              DF
            </div>
            <div>
              <span className="font-bold text-lg text-white">DropFlow</span>
              <span className="text-brand-500 font-bold ml-1">Hub</span>
            </div>
            <span className="ml-3 px-2 py-0.5 text-xs font-medium rounded-full bg-slate-800 text-slate-300 border border-slate-700">
              Tableau de bord
            </span>
          </div>

          <div className="flex items-center gap-4">
            <div className="hidden sm:flex flex-col text-right">
              <span className="text-sm font-semibold text-slate-200">
                {user.name || "Utilisateur"}
              </span>
              <span className="text-xs text-slate-400">{user.email}</span>
            </div>

            <a
              href="/api/auth/logout"
              className="inline-flex items-center gap-2 px-3 py-2 text-xs font-medium rounded-lg text-rose-300 bg-rose-950/40 border border-rose-800/50 hover:bg-rose-900/60 hover:text-white transition"
              title="Déconnexion de Next.js et fin de session Keycloak"
            >
              <LogOut className="w-3.5 h-3.5" />
              <span>Déconnexion</span>
            </a>
          </div>
        </div>
      </header>

      {/* Contenu principal */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        {/* Alerte éventuelle sur le jeton de rafraîchissement */}
        {error === "RefreshTokenError" && (
          <div className="p-4 rounded-xl bg-amber-950/50 border border-amber-800/80 flex items-start gap-3 text-amber-200">
            <ShieldAlert className="w-5 h-5 flex-shrink-0 text-amber-400 mt-0.5" />
            <div className="text-sm">
              <p className="font-semibold">Session expirée auprès de Keycloak</p>
              <p className="text-xs text-amber-300/80 mt-1">
                Le rafraîchissement automatique du jeton d&apos;accès a échoué. Veuillez vous reconnecter pour renouveler vos autorisations.
              </p>
            </div>
          </div>
        )}

        {/* Carte Profil & Rôles */}
        <section className="bg-slate-800/90 rounded-2xl p-6 border border-slate-700/70 shadow-xl">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 pb-6 border-b border-slate-700/60">
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-slate-700 to-slate-600 flex items-center justify-center text-slate-200 font-bold text-2xl border border-slate-600 shadow-inner">
                {user.name ? user.name.charAt(0).toUpperCase() : <User className="w-8 h-8" />}
              </div>
              <div>
                <h1 className="text-2xl font-bold text-white flex items-center gap-2">
                  <span>Bienvenue, {user.name || "Utilisateur"}</span>
                  <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                    Connecté (SSO)
                  </span>
                </h1>
                <div className="flex items-center gap-2 text-sm text-slate-400 mt-1">
                  <Mail className="w-4 h-4 text-slate-400" />
                  <span>{user.email}</span>
                </div>
              </div>
            </div>

            <div className="flex flex-wrap items-center gap-2">
              <span className="text-xs text-slate-400 font-medium mr-1 flex items-center gap-1">
                <ShieldCheck className="w-4 h-4 text-teal-400" /> Rôles assignés :
              </span>
              {roles.length > 0 ? (
                roles.map((role) => (
                  <span
                    key={role}
                    className={`px-3 py-1 text-xs font-bold rounded-lg border uppercase tracking-wider ${
                      role === "ADMIN"
                        ? "bg-rose-500/20 text-rose-300 border-rose-500/30"
                        : role === "ECOMMERCANT"
                        ? "bg-brand-500/20 text-brand-300 border-brand-500/30"
                        : role === "FOURNISSEUR"
                        ? "bg-teal-500/20 text-teal-300 border-teal-500/30"
                        : "bg-indigo-500/20 text-indigo-300 border-indigo-500/30"
                    }`}
                  >
                    {role}
                  </span>
                ))
              ) : (
                <span className="text-xs text-slate-500 italic">Aucun rôle spécifique détecté</span>
              )}
            </div>
          </div>

          {/* Métadonnées de session OIDC */}
          <div className="mt-6 grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div className="p-4 rounded-xl bg-slate-900/60 border border-slate-700/40">
              <div className="flex items-center gap-2 text-xs font-medium text-slate-400 mb-1">
                <Layers className="w-3.5 h-3.5 text-teal-400" />
                <span>Fournisseur d&apos;identité (IdP)</span>
              </div>
              <p className="text-sm font-semibold text-slate-200">Keycloak 26.0 (Quarkus)</p>
              <p className="text-xs text-slate-500 mt-0.5">Realm: dropflow (port 8081)</p>
            </div>

            <div className="p-4 rounded-xl bg-slate-900/60 border border-slate-700/40">
              <div className="flex items-center gap-2 text-xs font-medium text-slate-400 mb-1">
                <KeyRound className="w-3.5 h-3.5 text-brand-400" />
                <span>Client OAuth 2.0</span>
              </div>
              <p className="text-sm font-semibold text-slate-200">dropflow-web</p>
              <p className="text-xs text-slate-500 mt-0.5">Confidential &bull; PKCE S256</p>
            </div>

            <div className="p-4 rounded-xl bg-slate-900/60 border border-slate-700/40">
              <div className="flex items-center gap-2 text-xs font-medium text-slate-400 mb-1">
                <Clock className="w-3.5 h-3.5 text-sky-400" />
                <span>Jeton d&apos;accès JWT</span>
              </div>
              <p className="text-sm font-semibold text-emerald-400 flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
                Valide & Actif
              </p>
              <p className="text-xs text-slate-500 mt-0.5 font-mono truncate max-w-[220px]">
                {accessToken ? `${accessToken.substring(0, 24)}...` : "Absent"}
              </p>
            </div>
          </div>
        </section>

        {/* Modules du système & RBAC */}
        <section className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-bold text-white flex items-center gap-2">
              <Server className="w-5 h-5 text-brand-500" />
              <span>Services et Espaces Applicatifs</span>
            </h2>
            <span className="text-xs text-slate-400">Contrôle d&apos;accès basé sur les rôles (RBAC)</span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
            {/* Carte Administration */}
            <div className="relative p-5 rounded-2xl bg-slate-800/80 border border-slate-700/70 hover:border-slate-600 transition flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-3">
                  <span className="px-2 py-0.5 text-[11px] font-semibold rounded bg-rose-500/20 text-rose-300 border border-rose-500/30">
                    Requis: ADMIN
                  </span>
                  {isAdmin && (
                    <span className="text-[11px] text-emerald-400 font-medium">Autorisé ✓</span>
                  )}
                </div>
                <h3 className="text-base font-bold text-white">Espace Administration</h3>
                <p className="text-xs text-slate-400 mt-1">
                  Gestion centralisée des utilisateurs, surveillance des microservices et configuration globale.
                </p>
              </div>

              <div className="mt-4 pt-4 border-t border-slate-700/50">
                <Link
                  href="/admin"
                  className={`inline-flex items-center justify-between w-full px-3 py-2 rounded-xl text-xs font-medium transition ${
                    isAdmin
                      ? "bg-rose-600 hover:bg-rose-500 text-white shadow-md shadow-rose-600/20"
                      : "bg-slate-700/50 text-slate-400 hover:bg-slate-700 hover:text-slate-200"
                  }`}
                >
                  <span>{isAdmin ? "Accéder à l'administration" : "Tester la restriction RBAC"}</span>
                  <ArrowRight className="w-4 h-4" />
                </Link>
              </div>
            </div>

            {/* Carte E-Commerce */}
            <div className="p-5 rounded-2xl bg-slate-800/80 border border-slate-700/70 flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-3">
                  <span className="px-2 py-0.5 text-[11px] font-semibold rounded bg-brand-500/20 text-brand-300 border border-brand-500/30">
                    ECOMMERCANT / ADMIN
                  </span>
                </div>
                <h3 className="text-base font-bold text-white">Gestion des Ventes & Boutiques</h3>
                <p className="text-xs text-slate-400 mt-1">
                  Catalogue produits, synchronisation des commandes et suivi des livraisons clients.
                </p>
              </div>
              <div className="mt-4 pt-4 border-t border-slate-700/50 text-xs text-slate-500 flex items-center gap-1.5">
                <span>Microservices : Catalog & Order Service</span>
              </div>
            </div>

            {/* Carte Fournisseur & Stock */}
            <div className="p-5 rounded-2xl bg-slate-800/80 border border-slate-700/70 flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-3">
                  <span className="px-2 py-0.5 text-[11px] font-semibold rounded bg-teal-500/20 text-teal-300 border border-teal-500/30">
                    FOURNISSEUR / ADMIN
                  </span>
                </div>
                <h3 className="text-base font-bold text-white">Inventaire & Approvisionnements</h3>
                <p className="text-xs text-slate-400 mt-1">
                  Réservation des stocks, réapprovisionnement et alertes automatiques de rupture.
                </p>
              </div>
              <div className="mt-4 pt-4 border-t border-slate-700/50 text-xs text-slate-500 flex items-center gap-1.5">
                <span>Microservice : Inventory Service (Port 8084)</span>
              </div>
            </div>
          </div>
        </section>

        {/* Liens utiles vers l'infrastructure */}
        <section className="bg-slate-800/60 rounded-2xl p-5 border border-slate-700/40">
          <h3 className="text-sm font-semibold text-slate-300 mb-3 flex items-center gap-2">
            <ExternalLink className="w-4 h-4 text-slate-400" />
            <span>Consoles d&apos;infrastructure actives</span>
          </h3>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs">
            <a
              href="http://localhost:8081/admin/dropflow/console"
              target="_blank"
              rel="noreferrer"
              className="p-3 rounded-xl bg-slate-900/60 border border-slate-700/40 hover:border-brand-500/50 transition"
            >
              <div className="font-semibold text-slate-200">Keycloak Console</div>
              <div className="text-[11px] text-slate-400 mt-0.5">Port 8081 (Realm dropflow)</div>
            </a>
            <a
              href="http://localhost:8761"
              target="_blank"
              rel="noreferrer"
              className="p-3 rounded-xl bg-slate-900/60 border border-slate-700/40 hover:border-brand-500/50 transition"
            >
              <div className="font-semibold text-slate-200">Eureka Registry</div>
              <div className="text-[11px] text-slate-400 mt-0.5">Port 8761 (Service Discovery)</div>
            </a>
            <a
              href="http://localhost:8090"
              target="_blank"
              rel="noreferrer"
              className="p-3 rounded-xl bg-slate-900/60 border border-slate-700/40 hover:border-brand-500/50 transition"
            >
              <div className="font-semibold text-slate-200">Kafka UI</div>
              <div className="text-[11px] text-slate-400 mt-0.5">Port 8090 (Events & Topics)</div>
            </a>
            <a
              href="http://localhost:8080/actuator/health"
              target="_blank"
              rel="noreferrer"
              className="p-3 rounded-xl bg-slate-900/60 border border-slate-700/40 hover:border-brand-500/50 transition"
            >
              <div className="font-semibold text-slate-200">API Gateway</div>
              <div className="text-[11px] text-slate-400 mt-0.5">Port 8080 (Spring Cloud)</div>
            </a>
          </div>
        </section>
      </main>
    </div>
  );
}
