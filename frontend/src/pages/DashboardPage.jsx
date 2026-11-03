import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import StatusBadge from '../components/common/StatusBadge';
import ScoreGauge from '../components/common/ScoreGauge';
import {
  ShieldAlert,
  ShieldCheck,
  Activity,
  Layers,
  ArrowRight,
  ExternalLink,
  PlusCircle,
  TrendingUp,
} from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  CartesianGrid,
} from 'recharts';

export default function DashboardPage() {
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMetrics = async () => {
      try {
        const res = await api.get('/dashboard/metrics');
        if (res.data && res.data.data) {
          setMetrics(res.data.data);
        }
      } catch (err) {
        console.error('Failed to load dashboard metrics', err);
      } finally {
        setLoading(false);
      }
    };

    fetchMetrics();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-400" />
      </div>
    );
  }

  // Format severity data for Recharts
  const severityData = [
    { name: 'Critical', count: metrics?.severityCounts?.CRITICAL || 0, color: '#ef4444' },
    { name: 'High', count: metrics?.severityCounts?.HIGH || 0, color: '#f97316' },
    { name: 'Medium', count: metrics?.severityCounts?.MEDIUM || 0, color: '#eab308' },
    { name: 'Low', count: metrics?.severityCounts?.LOW || 0, color: '#3b82f6' },
    { name: 'Info', count: metrics?.severityCounts?.INFO || 0, color: '#64748b' },
  ];

  // Category data for Pie chart
  const categoryData = metrics?.categoryCounts
    ? Object.entries(metrics.categoryCounts).map(([cat, count]) => ({
        name: cat.replace('_', ' '),
        value: count,
      }))
    : [];

  const PIE_COLORS = ['#10b981', '#3b82f6', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#06b6d4'];

  return (
    <div className="space-y-8">
      {/* Top Banner & Quick Actions */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 p-6 rounded-2xl bg-gradient-to-r from-slate-900 via-slate-900 to-emerald-950/40 border border-slate-800">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight">Vulnerability Management & Posture</h2>
          <p className="text-slate-400 text-sm mt-1">
            Automated Java 21 DAST engine scanning HTTP headers, SSL/TLS, endpoint leaks, SQLi & XSS heuristics.
          </p>
        </div>
        <Link
          to="/scans/new"
          className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-sm transition-all shadow-lg shadow-emerald-500/20 shrink-0"
        >
          <PlusCircle className="w-4 h-4" />
          Launch New Scan
        </Link>
      </div>

      {/* Primary KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 relative overflow-hidden">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-400">Total Scans Run</span>
            <div className="p-2 rounded-lg bg-blue-500/10 text-blue-400">
              <Layers className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-4 flex items-baseline gap-2">
            <span className="text-3xl font-extrabold text-white">{metrics?.totalScans || 0}</span>
            <span className="text-xs text-slate-400">all time</span>
          </div>
          <div className="mt-2 text-xs text-emerald-400 font-medium">
            {metrics?.completedScans || 0} completed successfully
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 relative overflow-hidden">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-400">Average Security Score</span>
            <div className="p-2 rounded-lg bg-emerald-500/10 text-emerald-400">
              <ShieldCheck className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-4 flex items-baseline gap-2">
            <span className="text-3xl font-extrabold text-white">{metrics?.averageSecurityScore || 100}</span>
            <span className="text-xs text-slate-400">/ 100</span>
          </div>
          <div className="mt-2 text-xs text-slate-400">
            Weighted posture across all monitored endpoints
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 relative overflow-hidden">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-400">Total Vulnerabilities</span>
            <div className="p-2 rounded-lg bg-red-500/10 text-red-400">
              <ShieldAlert className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-4 flex items-baseline gap-2">
            <span className="text-3xl font-extrabold text-white">{metrics?.totalVulnerabilities || 0}</span>
            <span className="text-xs text-slate-400">findings</span>
          </div>
          <div className="mt-2 text-xs text-rose-400 font-medium">
            {metrics?.severityCounts?.CRITICAL || 0} critical & {metrics?.severityCounts?.HIGH || 0} high
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 relative overflow-hidden">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-400">Active Scan Queue</span>
            <div className="p-2 rounded-lg bg-cyan-500/10 text-cyan-400">
              <Activity className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-4 flex items-baseline gap-2">
            <span className="text-3xl font-extrabold text-white">{metrics?.activeScans || 0}</span>
            <span className="text-xs text-slate-400">in progress</span>
          </div>
          <div className="mt-2 text-xs text-slate-400">
            {metrics?.activeScans === 0 ? 'Engine idle and ready' : 'Analyzing targets asynchronously'}
          </div>
        </div>
      </div>

      {/* Visual Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Severity Distribution Bar Chart */}
        <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h3 className="font-bold text-white text-base">Vulnerability Findings by Severity</h3>
              <p className="text-xs text-slate-400 mt-0.5">Distribution across audited assets</p>
            </div>
            <TrendingUp className="w-4 h-4 text-slate-400" />
          </div>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={severityData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                <XAxis dataKey="name" stroke="#64748b" fontSize={12} tickLine={false} />
                <YAxis stroke="#64748b" fontSize={12} allowDecimals={false} tickLine={false} />
                <Tooltip
                  contentStyle={{ backgroundColor: '#0f172a', borderColor: '#334155', borderRadius: '0.75rem' }}
                  itemStyle={{ color: '#f8fafc', fontWeight: 600 }}
                />
                <Bar dataKey="count" radius={[6, 6, 0, 0]}>
                  {severityData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Category Breakdown Pie Chart */}
        <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h3 className="font-bold text-white text-base">Findings by Vulnerability Category</h3>
              <p className="text-xs text-slate-400 mt-0.5">Categorization based on CWE & OWASP Top 10</p>
            </div>
            <Layers className="w-4 h-4 text-slate-400" />
          </div>
          <div className="h-64 flex items-center justify-center">
            {categoryData.length === 0 ? (
              <div className="text-sm text-slate-500">No category findings recorded yet</div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={categoryData}
                    cx="50%"
                    cy="50%"
                    innerRadius={55}
                    outerRadius={85}
                    paddingAngle={4}
                    dataKey="value"
                  >
                    {categoryData.map((entry, index) => (
                      <Cell key={`cat-${index}`} fill={PIE_COLORS[index % PIE_COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip
                    contentStyle={{ backgroundColor: '#0f172a', borderColor: '#334155', borderRadius: '0.75rem' }}
                    itemStyle={{ color: '#f8fafc', fontWeight: 600 }}
                  />
                </PieChart>
              </ResponsiveContainer>
            )}
          </div>
          {/* Legend */}
          <div className="flex flex-wrap items-center justify-center gap-3 mt-2 text-xs">
            {categoryData.map((item, idx) => (
              <div key={idx} className="flex items-center gap-1.5 text-slate-300">
                <span className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: PIE_COLORS[idx % PIE_COLORS.length] }} />
                <span>{item.name} ({item.value})</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Recent Scans Table */}
      <div className="rounded-2xl bg-slate-900/80 border border-slate-800 overflow-hidden">
        <div className="p-6 border-b border-slate-800 flex items-center justify-between">
          <div>
            <h3 className="font-bold text-white text-base">Recent Vulnerability Audits</h3>
            <p className="text-xs text-slate-400 mt-0.5">Latest target URL evaluations and security grades</p>
          </div>
          <Link
            to="/scans"
            className="text-xs font-semibold text-emerald-400 hover:text-emerald-300 flex items-center gap-1 transition-colors"
          >
            View All Scans
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-950/60 text-slate-400 text-xs uppercase tracking-wider font-semibold border-b border-slate-800">
              <tr>
                <th className="py-3.5 px-6">Target URL</th>
                <th className="py-3.5 px-6">Scan Type</th>
                <th className="py-3.5 px-6">Status</th>
                <th className="py-3.5 px-6">Score & Grade</th>
                <th className="py-3.5 px-6">Findings</th>
                <th className="py-3.5 px-6 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-200">
              {metrics?.recentScans && metrics.recentScans.length > 0 ? (
                metrics.recentScans.map((scan) => (
                  <tr key={scan.id} className="hover:bg-slate-800/30 transition-colors">
                    <td className="py-4 px-6 font-medium text-white max-w-xs truncate">
                      <div className="flex items-center gap-2">
                        <span className="truncate">{scan.targetUrl}</span>
                        <a
                          href={scan.targetUrl}
                          target="_blank"
                          rel="noreferrer"
                          className="text-slate-500 hover:text-slate-300"
                        >
                          <ExternalLink className="w-3.5 h-3.5 shrink-0" />
                        </a>
                      </div>
                    </td>
                    <td className="py-4 px-6 text-xs text-slate-400 font-mono">
                      {scan.scanType}
                    </td>
                    <td className="py-4 px-6">
                      <StatusBadge status={scan.status} />
                    </td>
                    <td className="py-4 px-6">
                      <ScoreGauge score={scan.securityScore} grade={scan.letterGrade} size="sm" />
                    </td>
                    <td className="py-4 px-6">
                      <span className="px-2.5 py-1 rounded-md bg-slate-800 text-slate-300 text-xs font-semibold">
                        {scan.vulnerabilityCount} findings
                      </span>
                    </td>
                    <td className="py-4 px-6 text-right">
                      <Link
                        to={`/scans/${scan.id}`}
                        className="text-xs font-semibold text-emerald-400 hover:text-emerald-300 hover:underline inline-flex items-center gap-1"
                      >
                        Inspect
                        <ArrowRight className="w-3 h-3" />
                      </Link>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="text-center py-8 text-slate-500 text-sm">
                    No scans performed yet. Click "Launch New Scan" to begin.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
