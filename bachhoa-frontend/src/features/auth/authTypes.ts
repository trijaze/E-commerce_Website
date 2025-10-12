export interface User {
  id: number;
  username: string;
  phone: string;
  email: string;
  createdAt: string;
  role: 'user' | 'admin';
}

export interface AuthState {
  user: User | null;
  loading: boolean;
  error: string | null;
}