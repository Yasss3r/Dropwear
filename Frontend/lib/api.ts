import { auth } from "@/auth";

const GATEWAY_BASE_URL =
  process.env.NEXT_PUBLIC_GATEWAY_URL || "http://localhost:8080";

export interface ApiFetchOptions extends RequestInit {
  token?: string;
}

/**
 * Client HTTP sécurisé vers la Spring Cloud API Gateway de DropFlow Hub.
 * Injecte automatiquement le jeton JWT Keycloak dans l'en-tête "Authorization: Bearer <accessToken>".
 */
export async function apiFetch<T = unknown>(
  endpoint: string,
  options: ApiFetchOptions = {}
): Promise<T> {
  const { token: providedToken, headers: customHeaders, ...fetchOptions } = options;

  let accessToken = providedToken;

  // Récupération automatique du jeton de session côté serveur (Server Components / Server Actions)
  if (!accessToken && typeof window === "undefined") {
    try {
      const session = await auth();
      accessToken = session?.accessToken;
    } catch {
      // Pas de session disponible dans ce contexte
    }
  }

  const url = endpoint.startsWith("http")
    ? endpoint
    : `${GATEWAY_BASE_URL}${endpoint.startsWith("/") ? "" : "/"}${endpoint}`;

  const headers = new Headers(customHeaders);

  if (accessToken) {
    headers.set("Authorization", `Bearer ${accessToken}`);
  }

  if (!headers.has("Content-Type") && !(fetchOptions.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }

  const response = await fetch(url, {
    ...fetchOptions,
    headers,
  });

  if (!response.ok) {
    const errorBody = await response.text();
    let errorMessage = errorBody;
    try {
      const parsed = JSON.parse(errorBody);
      errorMessage = parsed.message || parsed.error || errorBody;
    } catch {
      // laisser tel quel
    }

    throw new Error(
      `Erreur Gateway [HTTP ${response.status}]: ${errorMessage || response.statusText}`
    );
  }

  const contentType = response.headers.get("content-type");
  if (contentType && contentType.includes("application/json")) {
    return response.json() as Promise<T>;
  }

  return response.text() as unknown as T;
}
