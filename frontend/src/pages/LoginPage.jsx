import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Shield, Lock, User, AlertCircle, ArrowRight } from 'lucide-react';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login(username, password);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid username or password');
    } finally {
      setLoading(false);
    }
  };

  const handleFillDemo = (userType) => {
    if (userType === 'admin') {
      setUsername('admin');
      setPassword('admin123');
    } else {
      setUsername('demo_analyst');
      setPassword('password123');
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col justify-center items-center px-4 relative overflow-hidden">
      {/* Background glow effects */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none" />

      <div className="w-full max-w-md space-y-6 relative z-10">
        {/* Brand */}
        <div className="text-center space-y-2">
          <div className="inline-flex p-3 bg-emerald-500/10 border border-emerald-500/30 rounded-2xl text-emerald-400 mb-2">
            <Shield className="w-8 h-8" />
          </div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight">
            Cyber<span className="text-emerald-400">Shield</span> Auditor
          </h1>
          <p className="text-sm text-slate-400">
            Sign in to access your security scanning dashboard
          </p>
        </div>

        {/* Demo Fast Login Buttons */}
        <div className="p-3.5 rounded-xl bg-slate-900/90 border border-slate-800 text-xs text-slate-300 space-y-2">
          <div className="flex items-center justify-between">
            <span className="font-semibold text-slate-400 uppercase tracking-wider text-[10px]">
              Instant Demo Access
            </span>
            <span className="text-[10px] text-emerald-400 font-mono">Pre-seeded</span>
          </div>
          <div className="grid grid-cols-2 gap-2">
            <button
              type="button"
              onClick={() => handleFillDemo('admin')}
              className="py-1.5 px-2.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 font-medium transition-colors text-left"
            >
              👑 <strong className="text-white">Admin</strong> (admin123)
            </button>
            <button
              type="button"
              onClick={() => handleFillDemo('analyst')}
              className="py-1.5 px-2.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 font-medium transition-colors text-left"
            >
              🛡️ <strong className="text-white">Analyst</strong> (password123)
            </button>
          </div>
        </div>

        {/* Login Card */}
        <div className="p-8 rounded-2xl bg-slate-900 border border-slate-800 shadow-2xl">
          {error && (
            <div className="mb-5 p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-400 text-xs flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-1.5">
                Username
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-500">
                  <User className="w-4 h-4" />
                </div>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="admin"
                  required
                  className="w-full pl-10 pr-4 py-2.5 bg-slate-950 border border-slate-700/80 rounded-xl text-sm text-white placeholder-slate-600 focus:outline-none focus:border-emerald-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-1.5">
                Password
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-500">
                  <Lock className="w-4 h-4" />
                </div>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  required
                  className="w-full pl-10 pr-4 py-2.5 bg-slate-950 border border-slate-700/80 rounded-xl text-sm text-white placeholder-slate-600 focus:outline-none focus:border-emerald-500"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full mt-2 py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 disabled:opacity-50 text-slate-950 font-bold text-sm transition-all flex items-center justify-center gap-2 shadow-lg shadow-emerald-500/20"
            >
              {loading ? (
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-slate-950" />
              ) : (
                <>
                  <span>Sign In</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </form>

          <div className="mt-6 text-center text-xs text-slate-400">
            Don't have an account?{' '}
            <Link to="/register" className="text-emerald-400 font-semibold hover:underline">
              Create Account
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
