import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { LogOut, User as UserIcon, Shield } from 'lucide-react';

export default function Navbar({ title = 'Dashboard' }) {
  const { user, logout } = useAuth();

  return (
    <header className="h-16 bg-slate-900/80 backdrop-blur-md border-b border-slate-800 px-8 flex items-center justify-between sticky top-0 z-30">
      <div>
        <h1 className="text-lg font-bold text-white tracking-tight">{title}</h1>
      </div>

      <div className="flex items-center gap-4">
        {/* User profile capsule */}
        <div className="flex items-center gap-3 pl-4 border-l border-slate-800">
          <div className="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-slate-300">
            <UserIcon className="w-4 h-4" />
          </div>
          <div className="flex flex-col text-left">
            <span className="text-sm font-semibold text-slate-200">
              {user?.username || 'Security Analyst'}
            </span>
            <span className="text-[11px] text-emerald-400 font-medium">
              {user?.role?.replace('ROLE_', '') || 'ANALYST'}
            </span>
          </div>

          <button
            onClick={logout}
            title="Sign Out"
            className="p-2 ml-2 rounded-lg text-slate-400 hover:text-rose-400 hover:bg-rose-500/10 transition-colors"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </header>
  );
}
