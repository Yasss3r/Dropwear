import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "DropFlow Hub | Plateforme Microservices & Gestion des Commandes",
  description: "Portail unifié DropFlow Hub avec authentification Keycloak OpenID Connect",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="fr" className="h-full">
      <body className="h-full flex flex-col font-sans antialiased text-slate-800 bg-slate-50">
        {children}
      </body>
    </html>
  );
}
