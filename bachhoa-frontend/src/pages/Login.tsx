// src/pages/Login.tsx
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { login } from '../features/auth/authSlice';
import { Navigate, Link } from 'react-router-dom';

const schema = z.object({
  email: z.string().email('Email is invalid'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});
type FormData = z.infer<typeof schema>;

export default function Login() {
  const dispatch = useAppDispatch();
  const auth = useAppSelector((s) => s.auth);
  const { register, handleSubmit, formState } = useForm<FormData>({
    resolver: zodResolver(schema),
  });
  const { errors } = formState;

  const onSubmit = (data: FormData) => {
    dispatch(login({ email: data.email, password: data.password }));
  };

  // if already logged in, redirect to profile/home
  if (auth.user) return <Navigate to="/profile" replace />;

  return (
    <div className="max-w-md mx-auto p-6 bg-white shadow rounded">
      <h2 className="text-xl font-semibold mb-4">Login</h2>

      {auth.error && <div className="mb-3 text-red-600 text-sm">{auth.error}</div>}

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1">Email</label>
          <input
            {...register('email')}
            type="email"
            className="w-full border rounded px-3 py-2"
            placeholder="you@example.com"
          />
          {errors.email && <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Password</label>
          <input
            {...register('password')}
            type="password"
            className="w-full border rounded px-3 py-2"
            placeholder="••••••••"
          />
          {errors.password && <p className="text-red-500 text-sm mt-1">{errors.password.message}</p>}
        </div>

        <div className="flex items-center justify-between">
          <button
            type="submit"
            className="bg-blue-600 text-white px-4 py-2 rounded disabled:opacity-60"
            disabled={auth.loading}
          >
            {auth.loading ? 'Signing in...' : 'Sign in'}
          </button>
          <Link to="/register" className="text-sm text-blue-600">
            Create account
          </Link>
        </div>
      </form>
    </div>
  );
}
