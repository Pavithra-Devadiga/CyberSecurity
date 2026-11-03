import React from 'react';
import { CheckCircle2, Clock, Loader2, XCircle, Ban } from 'lucide-react';

export default function StatusBadge({ status }) {
  if (!status) return null;

  const config = {
    QUEUED: {
      bg: 'bg-slate-800 text-slate-300 border-slate-700',
      icon: Clock,
      label: 'Queued',
    },
    RUNNING: {
      bg: 'bg-cyan-500/15 text-cyan-400 border-cyan-500/30',
      icon: Loader2,
      label: 'Scanning...',
      spin: true,
    },
    COMPLETED: {
      bg: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30',
      icon: CheckCircle2,
      label: 'Completed',
    },
    FAILED: {
      bg: 'bg-rose-500/15 text-rose-400 border-rose-500/30',
      icon: XCircle,
      label: 'Failed',
    },
    CANCELLED: {
      bg: 'bg-amber-500/15 text-amber-400 border-amber-500/30',
      icon: Ban,
      label: 'Cancelled',
    },
  };

  const current = config[status] || config.QUEUED;
  const Icon = current.icon;

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium border ${current.bg}`}>
      <Icon className={`w-3.5 h-3.5 ${current.spin ? 'animate-spin' : ''}`} />
      {current.label}
    </span>
  );
}
