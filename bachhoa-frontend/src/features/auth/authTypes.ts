// src/features/auth/authTypes.ts
export type User = {
  id: string;
  name?: string | null;
  email: string;
  role?: 'USER' | 'ADMIN';
  createdAt?: string;
};

export type AuthState = {
  user: User | null;
  loading: boolean;
  error: string | null;
};
