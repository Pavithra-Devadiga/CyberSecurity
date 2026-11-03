import React from 'react';
import { AlertCircle, AlertTriangle, Info, ShieldAlert } from 'lucide-react';

export default function SeverityBadge({ severity, showIcon = true, size = 'sm' }) {
  if (!severity) return null;

  const config = {
    CRITICAL: {
      bg: 'bg-red-500/15 text-red-400 border-red-500/30',
      icon: ShieldAlert,
    },
    HIGH: {
      bg: 'bg-orange-500/15 text-orange-400 border-orange-500/30',
      icon: AlertTriangle,
    },
    MEDIUM: {
      bg: 'bg-yellow-500/15 text-yellow-400 border-yellow-500/30',
      icon: AlertCircle,
    },
    LOW: {
      bg: 'bg-blue-500/15 text-blue-400 border-blue-500/30',
      icon: Info,
    },
    INFO: {
      bg: 'bg-slate-500/15 text-slate-400 border-slate-500/30',
      icon: Info,
    },
  };

  const style = config[severity] || config.INFO;
  const Icon = style.icon;

  const sizeClasses = size === 'lg' 
    ? 'px-3 py-1 text-sm font-semibold' 
    : 'px-2.5 py-0.5 text-xs font-medium';

  return (
    <span className={`inline-flex items-center gap-1.5 rounded-full border ${style.bg} ${sizeClasses}`}>
      {showIcon && <Icon className={size === 'lg' ? 'w-4 h-4' : 'w-3 h-3'} />}
      {severity}
    </span>
  );
}
