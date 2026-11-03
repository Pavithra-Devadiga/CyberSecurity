import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import StatusBadge from '../components/common/StatusBadge';
import ScoreGauge from '../components/common/ScoreGauge';
import {
  History,
  ExternalLink,
  ArrowRight,
  PlusCircle,
  RefreshCw,
  Ban,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';

export default function ScansPage() {
  const [scans, setScans] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);

  const fetchScans = async () => {
    try {
      const res = await api.get(`/scans?page=${page}&size=10`);
      if (res.data && res.data.data) {
        setScans(res.data.data.content || []);
        setTotalPages(res.data.data.totalPages || 1);
      }
    } catch (err) {
      console.error('Failed to load scans', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchScans();
  }, [page]);

  // Auto-poll if any scan is RUNNING or QUEUED
  useEffect(() => {
    const hasActive = scans.some((s) => s.status === 'RUNNING' || s.status === 'QUEUED');
    if (!hasActive) return;

    const interval = setInterval(fetchScans, 4000);
    return () => clearInterval(interval);
  }, [scans]);

  const handleCancel = async (id, e) => {
    e.preventDefault();
    try {
      await api.post(`/scans/${id}/cancel`);
      fetchScans();
    } catch (err) {
      console.error('Failed to cancel scan', err);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight">Vulnerability Scan History</h2>
          <p className="text-slate-400 text-sm mt-1">
            Real-time tracking of security audits, automated findings, and posture grades.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={fetchScans}
            className="p-2.5 rounded-xl border border-slate-800 bg-slate-900 text-slate-400 hover:text-white transition-colors"
            title="Refresh List"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
          <Link
            to="/scans/new"
            className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-sm transition-all shadow-md shadow-emerald-500/20"
          >
            <PlusCircle className="w-4 h-4" />
            New Audit
          </Link>
        </div>
      </div>

      {/* Scans Table */}
      <div className="rounded-2xl bg-slate-900/80 border border-slate-800 overflow-hidden shadow-xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-950/70 text-slate-400 text-xs uppercase tracking-wider font-semibold border-b border-slate-800">
              <tr>
                <th className="py-4 px-6">ID</th>
                <th className="py-4 px-6">Target Domain</th>
                <th className="py-4 px-6">Profile</th>
                <th className="py-4 px-6">Status</th>
                <th className="py-4 px-6">Score & Posture</th>
                <th className="py-4 px-6">Severity Breakdown</th>
                <th className="py-4 px-6 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-200">
              {loading ? (
                <tr>
                  <td colSpan={7} className="text-center py-12">
                    <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-400" />
                  </td>
                </tr>
              ) : scans.length === 0 ? (
                <tr>
                  <td colSpan={7} className="text-center py-12 text-slate-500 text-sm">
                    No scans found. Start your first security audit above.
                  </td>
                </tr>
              ) : (
                scans.map((scan) => (
                  <tr key={scan.id} className="hover:bg-slate-800/30 transition-colors">
                    <td className="py-4 px-6 font-mono text-xs text-slate-500">#{scan.id}</td>
                    <td className="py-4 px-6 font-medium text-white max-w-xs truncate">
                      <div className="flex items-center gap-2">
                        <span className="truncate">{scan.targetUrl}</span>
                        <a
                          href={scan.targetUrl}
                          target="_blank"
                          rel="noreferrer"
                          className="text-slate-500 hover:text-slate-300"
                        >
                          <ExternalLink className="w-3 h-3 shrink-0" />
                        </a>
                      </div>
                      <span className="text-[11px] text-slate-500 font-mono block mt-0.5">
                        {scan.createdAt ? new Date(scan.createdAt).toLocaleString() : ''}
                      </span>
                    </td>
                    <td className="py-4 px-6">
                      <span className="text-xs font-mono font-medium px-2 py-0.5 rounded bg-slate-800 text-slate-300">
                        {scan.scanType}
                      </span>
                    </td>
                    <td className="py-4 px-6">
                      <StatusBadge status={scan.status} />
                    </td>
                    <td className="py-4 px-6">
                      {scan.status === 'COMPLETED' ? (
                        <ScoreGauge score={scan.securityScore} grade={scan.letterGrade} size="sm" />
                      ) : (
                        <span className="text-xs text-slate-500 italic">Pending analysis...</span>
                      )}
                    </td>
                    <td className="py-4 px-6">
                      <div className="flex items-center gap-1.5 text-xs font-bold">
                        {scan.criticalCount > 0 && (
                          <span className="px-2 py-0.5 rounded bg-red-500/15 text-red-400 border border-red-500/30">
                            {scan.criticalCount} C
                          </span>
                        )}
                        {scan.highCount > 0 && (
                          <span className="px-2 py-0.5 rounded bg-orange-500/15 text-orange-400 border border-orange-500/30">
                            {scan.highCount} H
                          </span>
                        )}
                        {scan.mediumCount > 0 && (
                          <span className="px-2 py-0.5 rounded bg-yellow-500/15 text-yellow-400 border border-yellow-500/30">
                            {scan.mediumCount} M
                          </span>
                        )}
                        {scan.lowCount > 0 && (
                          <span className="px-2 py-0.5 rounded bg-blue-500/15 text-blue-400 border border-blue-500/30">
                            {scan.lowCount} L
                          </span>
                        )}
                        {scan.vulnerabilityCount === 0 && scan.status === 'COMPLETED' && (
                          <span className="text-xs text-emerald-400 font-medium">Clean</span>
                        )}
                      </div>
                    </td>
                    <td className="py-4 px-6 text-right">
                      <div className="flex items-center justify-end gap-2">
                        {(scan.status === 'RUNNING' || scan.status === 'QUEUED') && (
                          <button
                            onClick={(e) => handleCancel(scan.id, e)}
                            className="px-2.5 py-1 rounded-lg text-xs font-semibold text-rose-400 hover:bg-rose-500/10 border border-rose-500/30 flex items-center gap-1"
                          >
                            <Ban className="w-3 h-3" />
                            Cancel
                          </button>
                        )}
                        <Link
                          to={`/scans/${scan.id}`}
                          className="px-3 py-1.5 rounded-lg text-xs font-semibold bg-emerald-500/10 text-emerald-400 hover:bg-emerald-500/20 border border-emerald-500/30 inline-flex items-center gap-1.5 transition-colors"
                        >
                          Report
                          <ArrowRight className="w-3 h-3" />
                        </Link>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Bar */}
        <div className="p-4 border-t border-slate-800 flex items-center justify-between text-xs text-slate-400">
          <span>
            Page {page + 1} of {totalPages}
          </span>
          <div className="flex items-center gap-2">
            <button
              disabled={page === 0}
              onClick={() => setPage(page - 1)}
              className="p-1.5 rounded-lg border border-slate-800 bg-slate-950 disabled:opacity-40 hover:bg-slate-800 transition-colors"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <button
              disabled={page >= totalPages - 1}
              onClick={() => setPage(page + 1)}
              className="p-1.5 rounded-lg border border-slate-800 bg-slate-950 disabled:opacity-40 hover:bg-slate-800 transition-colors"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
