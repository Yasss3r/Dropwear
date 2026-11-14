import { DefaultSession } from "next-auth";
import "next-auth/jwt";

declare module "next-auth" {
  interface Session {
    user: {
      id?: string;
      roles: string[];
    } & DefaultSession["user"];
    accessToken?: string;
    roles: string[];
    error?: "RefreshTokenError" | string;
  }

  interface User {
    roles?: string[];
  }
}

declare module "next-auth/jwt" {
  interface JWT {
    accessToken?: string;
    refreshToken?: string;
    expiresAt?: number;
    roles: string[];
    error?: "RefreshTokenError" | string;
  }
}
