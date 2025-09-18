// src/pages/Profile.tsx
import React, { useEffect, useState } from 'react';
import { authApi } from '../api/authApi';

export default function Profile() {
  const [user, setUser] = useState<any>(null);
  useEffect(() => {
    authApi.me().then((u) => setUser(u)).catch(() => setUser(null));
  }, []);
  if (!user) return <div>Please login to see your profile.</div>;
  return (
    <div className="bg-white p-4 rounded shadow">
      <h1 className="text-xl font-semibold">Profile</h1>
      <div className="mt-2">Name: {user.name}</div>
      <div className="mt-1">Email: {user.email}</div>
    </div>
  );
}
