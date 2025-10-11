// src/pages/Register.tsx
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { register as registerAction } from '../features/auth/authSlice';
import { Navigate, Link } from 'react-router-dom';

const schema = z
  .object({
    name: z.string().min(1, 'Name is required'),
    email: z.string().email('Email is invalid'),
    password: z.string().min(6, 'Password must be at least 6 characters'),
    confirm: z.string().min(6, 'Confirm password must be at least 6 characters'),
  })
  .refine((v) => v.password === v.confirm, {
    message: 'Passwords do not match',
    path: ['confirm'],
  });

type FormData = z.infer<typeof schema>;

export default function Register() {
  const dispatch = useAppDispatch();
  const auth = useAppSelector((s) => s.auth);

  const { register, handleSubmit, formState } = useForm<FormData>({
    resolver: zodResolver(schema),
  });
  const { errors } = formState;

  const onSubmit = (data: FormData) => {
    dispatch(registerAction({ name: data.name, email: data.email, password: data.password }));
  };

  if (auth.user) return <Navigate to="/profile" replace />;

  return (
    <div className="max-w-md mx-auto p-6 bg-white shadow rounded">
      <h2 className="text-xl font-semibold mb-4">Create account</h2>

      {auth.error && <div className="mb-3 text-red-600 text-sm">{auth.error}</div>}

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1">Full name</label>
          <input {...register('name')} className="w-full border rounded px-3 py-2" placeholder="Your name" />
          {errors.name && <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Email</label>
          <input {...register('email')} type="email" className="w-full border rounded px-3 py-2" placeholder="you@example.com" />
          {errors.email && <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Password</label>
          <input {...register('password')} type="password" className="w-full border rounded px-3 py-2" placeholder="••••••••" />
          {errors.password && <p className="text-red-500 text-sm mt-1">{errors.password.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Confirm password</label>
          <input {...register('confirm')} type="password" className="w-full border rounded px-3 py-2" placeholder="••••••••" />
          {errors.confirm && <p className="text-red-500 text-sm mt-1">{errors.confirm.message}</p>}
        </div>

        <div className="flex items-center justify-between">
          <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded" disabled={auth.loading}>
            {auth.loading ? 'Creating...' : 'Create account'}
          </button>
          <Link to="/login" className="text-sm text-blue-600">Already have an account?</Link>
        </div>
      </form>
    </div>
  );
}
