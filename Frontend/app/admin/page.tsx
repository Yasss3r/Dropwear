import { auth } from "@/auth";
import { redirect } from "next/navigation";
import Link from "next/link";
import {
  ShieldCheck,
  ArrowLeft,
  Server,
  Database,
  Users,
  Lock,
  ExternalLink,
  Cpu,
} from "lucide-react";

export const dynamic = "force-dynamic";

export default async function AdminPage() {
  const session = await auth();

  if (!session?.user) {
    redirect("/login");
  }

  const roles = session.roles || [];
  if (!roles.includes("ADMIN")) {
    redirect("/unauthorized?required=ADMIN");
  }

  const SERVICES = [
    { name: "API Gateway", port: 8080, type: "Routing & Auth Filter", status: "UP" },
    { name: "Keycloak IdP", port: 8081, type: "OpenID Connect Server", status: "UP" },
    { name: "Eureka Server", port: 8761, type: "Service Discovery", status: "UP" },
    { name: "Config Server", port: 8888, type: "Centralized Config", status: "UP" },
    { name: "Inventory Service", port: 8084, type: "Stock & PostgreSQL", status: "UP" },
    { name: "Kafka Broker", port: 9092, type: "Event Streaming KRaft", status: "UP" },
  ];

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 flex flex-col">
      <header className="border-b border-slate-800 bg-slate-900/80 backdrop-blur sticky top-0 z-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <Link
              href="/dashboard"
              className="p-2 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 transition"
              title="Retour au tableau de bord"
            >
              <ArrowLeft className="w-4 h-4" />
            </Link>
            <div className="flex items-center gap-2">
              <span className="font-bold text-lg text-white">DropFlow</span>
              <span className="px-2 py-0.5 text-xs font-bold rounded bg-rose-500/20 text-rose-400 border border-rose-500/30">
                Console Administration
              </span>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <span className="text-xs text-slate-400 font-medium">Session ADMIN active :</span>
            <span className="text-xs font-mono text-white bg-slate-800 px-2.5 py-1 rounded-md border border-slate-700">
              {session.user.email}
            </span>
          </div>
        </div>
      </header>

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        <div className="p-6 rounded-2xl bg-gradient-to-r from-rose-950/40 via-slate-800 to-slate-900 border border-rose-900/40 shadow-xl">
          <div className="flex items-center gap-3 mb-2">
            <ShieldCheck className="w-6 h-6 text-rose-400" />
            <h1 className="text-xl font-bold text-white">Espace Réservé &bull; Habilitation [ADMIN]</h1>
          </div>
          <p className="text-xs text-slate-300">
            Cette page est protégée à la fois par le <code>middleware.ts</code> Next.js et par la validation côté serveur du jeton JWT Keycloak. Les utilisateurs sans le rôle <code>ADMIN</code> sont automatiquement redirigés vers <code>/unauthorized</code>.
          </p>
        </div>

        {/* Supervision des microservices */}
        <div className="bg-slate-800/80 rounded-2xl p-6 border border-slate-700 shadow-lg space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <Server className="w-5 h-5 text-teal-400" />
              <span>État des composants du Cluster</span>
            </h2>
            <span className="text-xs text-emerald-400 flex items-center gap-1">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
              Infrastructure Opérationnelle
            </span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {SERVICES.map((srv) => (
              <div
                key={srv.name}
                className="p-4 rounded-xl bg-slate-900/70 border border-slate-700/60 flex items-start justify-between"
              >
                <div>
                  <h3 className="text-sm font-semibold text-white">{srv.name}</h3>
                  <p className="text-xs text-slate-400 mt-0.5">{srv.type}</p>
                  <p className="text-[11px] font-mono text-slate-500 mt-2">Port : {srv.port}</p>
                </div>
                <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                  {srv.status}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Détails de sécurité OIDC */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="p-6 rounded-2xl bg-slate-800/80 border border-slate-700 space-y-3">
            <h3 className="text-sm font-bold text-white flex items-center gap-2">
              <Lock className="w-4 h-4 text-brand-400" />
              <span>Flux d&apos;autorisation OpenID Connect</span>
            </h3>
            <ul className="text-xs text-slate-300 space-y-2 list-disc list-inside">
              <li>Type de flux : <strong>Authorization Code Flow avec PKCE S256</strong></li>
              <li>Type de client : <strong>Confidentiel (client-secret géré côté serveur)</strong></li>
              <li>Extraction des rôles : <code>realm_access.roles</code> &bull; Mapés sur les authorities Spring</li>
              <li>Gestion des sessions : <strong>OIDC RP-Initiated Logout</strong> (déconnexion globale)</li>
            </ul>
          </div>

          <div className="p-6 rounded-2xl bg-slate-800/80 border border-slate-700 space-y-3">
            <h3 className="text-sm font-bold text-white flex items-center gap-2">
              <Users className="w-4 h-4 text-sky-400" />
              <span>Matrice des 7 Rôles Métier</span>
            </h3>
            <p className="text-xs text-slate-400">
              Chaque utilisateur Keycloak se voit attribuer un rôle strict définissant son périmètre d&apos;action sur la plateforme :
            </p>
            <div className="flex flex-wrap gap-1.5 pt-1">
              {[
                "ADMIN",
                "ECOMMERCANT",
                "FOURNISSEUR",
                "AGENT_CONFIRMATION",
                "AGENT_LOGISTIQUE",
                "AGENT_FINANCIER",
                "AGENT_SUPPORT",
              ].map((r) => (
                <span
                  key={r}
                  className="px-2 py-0.5 rounded text-[10px] font-mono font-semibold bg-slate-900 text-slate-300 border border-slate-700"
                >
                  {r}
                </span>
              ))}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
