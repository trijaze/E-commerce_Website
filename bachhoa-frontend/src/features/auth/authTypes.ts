export interface User {
  userId: number;
  username: string;
  phoneNumber: string;
  email: string;
  createdAt: string;
  role: 'customer' | 'admin';
}

export interface AuthState {
  user: User | null;
  loading: boolean;
  error: string | null;
}