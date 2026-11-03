import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  ShieldAlert,
  History,
  Bug,
  FileText,
  Bell,
  Shield,
  Zap,
} from 'lucide-react';

export default function Sidebar({ unreadCount = 0 }) {
  const navItems = [
    { to: '/', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/scans/new', label: 'New Scan', icon: ShieldAlert },
    { to: '/scans', label: 'Scan History', icon: History },
    { to: '/vulnerabilities', label: 'Vulnerabilities', icon: Bug },
    { to: '/reports', label: 'Reports', icon: FileText },
    { to: '/notifications', label: 'Notifications', icon: Bell, badge: unreadCount },
  ];

  return (
    <aside className="w-64 bg-slate-900 border-r border-slate-800 flex flex-col shrink-0 min-h-screen">
      {/* Brand Header */}
      <div className="h-16 flex items-center px-6 border-b border-slate-800 gap-3">
        <div className="p-2 bg-emerald-500/10 border border-emerald-500/30 rounded-lg text-emerald-400">
          <Shield className="w-5 h-5" />
        </div>
        <div>
          <span className="font-bold text-lg text-white tracking-tight flex items-center gap-1.5">
            Cyber<span className="text-emerald-400">Shield</span>
          </span>
          <span className="block text-[10px] text-slate-400 font-medium tracking-wider uppercase -mt-0.5">
            Security Auditor
          </span>
        </div>
      </div>

      {/* Navigation Links */}
      <nav className="p-4 space-y-1.5 flex-1">
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) =>
                `flex items-center justify-between px-3.5 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-emerald-500/15 text-emerald-400 border border-emerald-500/30 shadow-sm shadow-emerald-950/50'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                }`
              }
            >
              <div className="flex items-center gap-3">
                <Icon className="w-4 h-4 shrink-0" />
                <span>{item.label}</span>
              </div>
              {item.badge > 0 && (
                <span className="px-2 py-0.5 text-xs font-bold rounded-full bg-emerald-500 text-slate-950">
                  {item.badge}
                </span>
              )}
            </NavLink>
          );
        })}
      </nav>

      {/* Engine Status Card */}
      <div className="p-4 border-t border-slate-800/80 m-4 rounded-xl bg-slate-950/60 border">
        <div className="flex items-center gap-2 mb-1.5">
          <Zap className="w-4 h-4 text-emerald-400" />
          <span className="text-xs font-bold text-slate-200">Engine Online</span>
        </div>
        <p className="text-[11px] text-slate-400 leading-relaxed">
          SSRF Protection active. Java 21 Spring Boot scanning daemon connected.
        </p>
      </div>
    </aside>
  );
}
