import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import SeverityBadge from '../components/common/SeverityBadge';
import {
  Bug,
  Search,
  Filter,
  ExternalLink,
  ChevronLeft,
  ChevronRight,
  X,
  CheckCircle2,
  AlertTriangle,
} from 'lucide-react';

export default function VulnerabilitiesPage() {
  const [vulns, setVulns] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [severity, setSeverity] = useState('');
  const [category, setCategory] = useState('');
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [selectedVuln, setSelectedVuln] = useState(null);

  const fetchVulnerabilities = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      params.append('page', page);
      params.append('size', 12);
      if (severity) params.append('severity', severity);
      if (category) params.append('category', category);
      if (search) params.append('search', search);

      const res = await api.get(`/vulnerabilities?${params.toString()}`);
      if (res.data && res.data.data) {
        setVulns(res.data.data.content || []);
        setTotalPages(res.data.data.totalPages || 1);
        setTotalElements(res.data.data.totalElements || 0);
      }
    } catch (err) {
      console.error('Failed to load vulnerabilities', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchVulnerabilities();
  }, [page, severity, category]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchVulnerabilities();
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight">Global Vulnerability Database</h2>
          <p className="text-slate-400 text-sm mt-1">
            Aggregated findings, exploit mechanics analysis, and specific remediation advisories.
          </p>
        </div>
        <div className="text-xs font-semibold px-3 py-1.5 rounded-xl bg-slate-900 border border-slate-800 text-slate-300">
          Total Findings: <strong className="text-emerald-400">{totalElements}</strong>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="p-4 rounded-2xl bg-slate-900/80 border border-slate-800 flex flex-wrap items-center gap-3">
        <form onSubmit={handleSearchSubmit} className="flex-1 min-w-[240px] relative">
          <Search className="w-4 h-4 absolute left-3.5 top-3 text-slate-500" />
          <input
            type="text"
            placeholder="Search by title, description or CWE..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-10 pr-4 py-2 bg-slate-950 border border-slate-700/80 rounded-xl text-sm text-white placeholder-slate-500 focus:outline-none focus:border-emerald-500"
          />
        </form>

        <select
          value={severity}
          onChange={(e) => { setSeverity(e.target.value); setPage(0); }}
          className="px-3.5 py-2 bg-slate-950 border border-slate-700/80 rounded-xl text-xs font-medium text-slate-300 focus:outline-none focus:border-emerald-500"
        >
          <option value="">All Severities</option>
          <option value="CRITICAL">Critical</option>
          <option value="HIGH">High</option>
          <option value="MEDIUM">Medium</option>
          <option value="LOW">Low</option>
          <option value="INFO">Informational</option>
        </select>

        <select
          value={category}
          onChange={(e) => { setCategory(e.target.value); setPage(0); }}
          className="px-3.5 py-2 bg-slate-950 border border-slate-700/80 rounded-xl text-xs font-medium text-slate-300 focus:outline-none focus:border-emerald-500"
        >
          <option value="">All Categories</option>
          <option value="SECURITY_HEADERS">Security Headers</option>
          <option value="SSL_TLS">SSL / TLS</option>
          <option value="SQL_INJECTION">SQL Injection</option>
          <option value="XSS">XSS Assessment</option>
          <option value="ENDPOINT_EXPOSURE">Endpoint Exposure</option>
          <option value="SENSITIVE_DATA">Sensitive Data Leaks</option>
          <option value="TECHNOLOGY_DISCLOSURE">Technology Disclosure</option>
          <option value="OWASP_ZAP">OWASP ZAP</option>
        </select>
      </div>

      {/* Vulnerabilities Cards Grid */}
      {loading ? (
        <div className="text-center py-16">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-400" />
        </div>
      ) : vulns.length === 0 ? (
        <div className="text-center py-16 rounded-2xl bg-slate-900/40 border border-slate-800 text-slate-400 text-sm">
          No vulnerabilities match the selected criteria.
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {vulns.map((v) => (
            <div
              key={v.id}
              onClick={() => setSelectedVuln(v)}
              className="p-5 rounded-2xl bg-slate-900/80 border border-slate-800/80 hover:border-slate-700 cursor-pointer transition-all hover:bg-slate-900 flex flex-col justify-between space-y-4"
            >
              <div className="space-y-2.5">
                <div className="flex items-center justify-between gap-2">
                  <SeverityBadge severity={v.severity} />
                  {v.cwe && (
                    <span className="font-mono text-[10px] px-2 py-0.5 rounded bg-slate-800 text-slate-400 border border-slate-700">
                      {v.cwe}
                    </span>
                  )}
                </div>

                <h3 className="font-bold text-white text-sm line-clamp-2 leading-snug">
                  {v.title}
                </h3>

                <p className="text-xs text-slate-400 line-clamp-3 leading-relaxed">
                  {v.description}
                </p>
              </div>

              <div className="pt-3 border-t border-slate-800/80 flex items-center justify-between text-[11px] text-slate-500">
                <span className="truncate max-w-[150px]">{v.category}</span>
                {v.scanId && (
                  <span className="text-emerald-400 font-semibold hover:underline">
                    Scan #{v.scanId}
                  </span>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination Bar */}
      <div className="p-4 rounded-xl bg-slate-900/80 border border-slate-800 flex items-center justify-between text-xs text-slate-400">
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

      {/* Vulnerability Modal */}
      {selectedVuln && (
        <div className="fixed inset-0 z-50 bg-black/75 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-700 rounded-2xl max-w-2xl w-full max-h-[85vh] overflow-y-auto p-6 space-y-5 shadow-2xl">
            <div className="flex items-start justify-between gap-4">
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <SeverityBadge severity={selectedVuln.severity} size="lg" />
                  {selectedVuln.cwe && (
                    <span className="font-mono text-xs px-2 py-0.5 rounded bg-slate-800 text-slate-300 border border-slate-700">
                      {selectedVuln.cwe}
                    </span>
                  )}
                </div>
                <h3 className="text-lg font-bold text-white mt-1.5">{selectedVuln.title}</h3>
              </div>
              <button
                onClick={() => setSelectedVuln(null)}
                className="p-1.5 rounded-lg bg-slate-800 text-slate-400 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-4 text-xs">
              <div>
                <span className="font-bold text-slate-300 block mb-1">Description:</span>
                <p className="text-slate-300 leading-relaxed text-sm">{selectedVuln.description}</p>
              </div>

              {selectedVuln.affectedUrl && (
                <div>
                  <span className="font-bold text-slate-300 block mb-1">Affected Target:</span>
                  <span className="font-mono text-emerald-400 bg-slate-950 px-2.5 py-1.5 rounded-lg border border-slate-800 block break-all text-xs">
                    {selectedVuln.affectedUrl}
                  </span>
                </div>
              )}

              {selectedVuln.evidence && (
                <div>
                  <span className="font-bold text-slate-300 block mb-1">Evidence & Verification:</span>
                  <pre className="p-3 bg-slate-950 border border-slate-800 text-slate-300 font-mono text-[11px] rounded-lg whitespace-pre-wrap overflow-x-auto leading-relaxed">
                    {selectedVuln.evidence}
                  </pre>
                </div>
              )}

              {selectedVuln.remediation && (
                <div className="p-4 rounded-xl bg-emerald-950/40 border border-emerald-500/40 text-emerald-200 space-y-1">
                  <span className="font-bold text-emerald-400 flex items-center gap-1.5 text-sm">
                    <CheckCircle2 className="w-4 h-4" /> Remediation Guidance:
                  </span>
                  <p className="leading-relaxed text-xs">{selectedVuln.remediation}</p>
                </div>
              )}
            </div>

            <div className="pt-4 border-t border-slate-800 flex justify-between items-center">
              {selectedVuln.scanId ? (
                <Link
                  to={`/scans/${selectedVuln.scanId}`}
                  className="text-xs font-semibold text-emerald-400 hover:underline"
                >
                  View Originating Audit Report (Scan #{selectedVuln.scanId})
                </Link>
              ) : <div />}
              <button
                onClick={() => setSelectedVuln(null)}
                className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-white font-semibold text-xs"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
