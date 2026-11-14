import { auth, signIn } from "@/auth";
import { redirect } from "next/navigation";
import { ShieldCheck, LogIn, Lock, CheckCircle2, UserCircle2 } from "lucide-react";

export const dynamic = "force-dynamic";

const TEST_ACCOUNTS = [
  { role: "ADMIN", email: "admin@dropflow.com", pass: "admin123", desc: "Super-administrateur plateforme" },
  { role: "ECOMMERCANT", email: "ecom@dropflow.com", pass: "ecom123", desc: "Gestion des boutiques et ventes" },
  { role: "FOURNISSEUR", email: "fournisseur@dropflow.com", pass: "fournisseur123", desc: "Gestion des stocks & catalogue" },
  { role: "AGENT_CONFIRMATION", email: "confirmation@dropflow.com", pass: "confirm123", desc: "Validation des commandes" },
  { role: "AGENT_LOGISTIQUE", email: "logistique@dropflow.com", pass: "logistics123", desc: "Préparation et expéditions" },
  { role: "AGENT_FINANCIER", email: "financier@dropflow.com", pass: "finance123", desc: "Facturation et règlements" },
  { role: "AGENT_SUPPORT", email: "support@dropflow.com", pass: "support123", desc: "Assistance et litiges clients" },
];

export default async function LoginPage({
  searchParams,
}: {
  searchParams?: { callbackUrl?: string; error?: string };
}) {
  const session = await auth();
  if (session?.user) {
    redirect("/dashboard");
  }

  const callbackUrl = searchParams?.callbackUrl || "/dashboard";

  return (
    <main className="min-h-screen flex flex-col justify-center py-12 sm:px-6 lg:px-8 bg-gradient-to-br from-slate-900 via-slate-800 to-slate-950 text-slate-100">
      <div className="sm:mx-auto sm:w-full sm:max-w-md text-center">
        <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-tr from-brand-600 to-brand-500 shadow-lg shadow-brand-500/30 mb-4">
          <ShieldCheck className="w-9 h-9 text-white" />
        </div>
        <h1 className="text-3xl font-extrabold tracking-tight text-white sm:text-4xl">
          DropFlow <span className="text-brand-500">Hub</span>
        </h1>
        <p className="mt-2 text-sm text-slate-400">
          Portail microservices unifié &bull; Authentification OpenID Connect
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md px-4">
        <div className="bg-slate-800/80 backdrop-blur-md py-8 px-6 shadow-2xl rounded-2xl sm:px-10 border border-slate-700/60">
          <div className="space-y-6">
            <div>
              <p className="text-xs font-semibold tracking-wider uppercase text-teal-400 text-center mb-1">
                Authentification déléguée
              </p>
              <h2 className="text-xl font-bold text-center text-white">
                Connexion sécurisée
              </h2>
              <p className="mt-1 text-xs text-center text-slate-400">
                La gestion des identités, rôles et jetons JWT est assurée par le serveur Keycloak.
              </p>
            </div>

            <form
              action={async () => {
                "use server";
                await signIn("keycloak", { redirectTo: callbackUrl });
              }}
            >
              <button
                type="submit"
                className="w-full flex items-center justify-center gap-3 py-3 px-4 rounded-xl text-white font-medium bg-gradient-to-r from-brand-600 to-orange-500 hover:from-brand-700 hover:to-orange-600 shadow-lg shadow-brand-600/30 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-brand-500 transition-all duration-150 transform hover:-translate-y-0.5 cursor-pointer"
              >
                <LogIn className="w-5 h-5" />
                <span>Se connecter avec Keycloak</span>
              </button>
            </form>

            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-slate-700" />
              </div>
              <div className="relative flex justify-center text-xs uppercase">
                <span className="bg-slate-800 px-2 text-slate-400 font-medium">
                  Protocoles & Sécurité
                </span>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-2 text-xs text-slate-300">
              <div className="flex items-center gap-1.5 p-2 rounded-lg bg-slate-900/50 border border-slate-700/40">
                <CheckCircle2 className="w-3.5 h-3.5 text-teal-400 flex-shrink-0" />
                <span>OAuth 2.0 / OIDC</span>
              </div>
              <div className="flex items-center gap-1.5 p-2 rounded-lg bg-slate-900/50 border border-slate-700/40">
                <CheckCircle2 className="w-3.5 h-3.5 text-teal-400 flex-shrink-0" />
                <span>PKCE S256 Flow</span>
              </div>
              <div className="flex items-center gap-1.5 p-2 rounded-lg bg-slate-900/50 border border-slate-700/40">
                <CheckCircle2 className="w-3.5 h-3.5 text-teal-400 flex-shrink-0" />
                <span>Client Confidentiel</span>
              </div>
              <div className="flex items-center gap-1.5 p-2 rounded-lg bg-slate-900/50 border border-slate-700/40">
                <CheckCircle2 className="w-3.5 h-3.5 text-teal-400 flex-shrink-0" />
                <span>JWT Refresh Auto</span>
              </div>
            </div>
          </div>
        </div>

        {/* Comptes de test DEV */}
        <div className="mt-8 bg-slate-900/90 rounded-2xl p-5 border border-slate-800 text-xs text-slate-300">
          <div className="flex items-center gap-2 mb-3 font-semibold text-slate-200">
            <UserCircle2 className="w-4 h-4 text-brand-400" />
            <span>Comptes de test préconfigurés (Keycloak dev) :</span>
          </div>
          <div className="space-y-2 max-h-56 overflow-y-auto pr-1">
            {TEST_ACCOUNTS.map((acc) => (
              <div
                key={acc.role}
                className="flex items-center justify-between p-2 rounded-lg bg-slate-800/60 border border-slate-700/50 hover:border-brand-500/40 transition"
              >
                <div>
                  <span className="inline-block px-1.5 py-0.5 rounded text-[10px] font-bold bg-brand-500/20 text-brand-400 mr-1.5">
                    {acc.role}
                  </span>
                  <span className="font-mono text-slate-200">{acc.email}</span>
                </div>
                <div className="flex items-center gap-1 font-mono text-[11px] text-slate-400">
                  <Lock className="w-3 h-3 text-slate-500" />
                  <span>{acc.pass}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </main>
  );
}
