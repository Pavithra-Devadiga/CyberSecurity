import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../api/client';
import StatusBadge from '../components/common/StatusBadge';
import SeverityBadge from '../components/common/SeverityBadge';
import ScoreGauge from '../components/common/ScoreGauge';
import {
  FileDown,
  ArrowLeft,
  Globe,
  ExternalLink,
  ShieldAlert,
  Server,
  Lock,
  Search,
  CheckCircle2,
  AlertCircle,
  Copy,
  ChevronDown,
  ChevronUp,
} from 'lucide-react';

export default function ScanDetailPage() {
  const { id } = useParams();
  const [scan, setScan] = useState(null);
  const [loading, setLoading] = useState(true);
  const [downloading, setDownloading] = useState(false);
  const [severityFilter, setSeverityFilter] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [expandedVuln, setExpandedVuln] = useState(null);

  const fetchScanDetails = async () => {
    try {
      const res = await api.get(`/scans/${id}`);
      if (res.data && res.data.data) {
        setScan(res.data.data);
      }
    } catch (err) {
      console.error('Failed to load scan details', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchScanDetails();
  }, [id]);

  // Poll if scan is running
  useEffect(() => {
    if (!scan || (scan.status !== 'RUNNING' && scan.status !== 'QUEUED')) return;

    const interval = setInterval(fetchScanDetails, 3000);
    return () => clearInterval(interval);
  }, [scan?.status]);

  const handleDownloadPdf = async () => {
    setDownloading(true);
    try {
      const response = await api.get(`/reports/download/${id}`, {
        responseType: 'blob',
      });
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `security-audit-scan-${id}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error('Failed to download PDF report', err);
    } finally {
      setDownloading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-400" />
      </div>
    );
  }

  if (!scan) {
    return (
      <div className="text-center py-16">
        <h3 className="text-lg font-bold text-white">Scan Not Found</h3>
        <p className="text-slate-400 text-sm mt-1">Unable to locate scan with ID #{id}.</p>
        <Link to="/scans" className="mt-4 inline-block text-emerald-400 text-sm hover:underline">
          Return to Scan History
        </Link>
      </div>
    );
  }

  const vulnerabilities = scan.vulnerabilities || [];
  const filteredVulns = vulnerabilities.filter((v) => {
    const matchesSev = severityFilter === 'ALL' || v.severity === severityFilter;
    const matchesSearch =
      searchQuery === '' ||
      v.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      v.description?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      v.cwe?.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesSev && matchesSearch;
  });

  return (
    <div className="space-y-8 max-w-6xl mx-auto">
      {/* Top Back & Header Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <Link
            to="/scans"
            className="p-2 rounded-xl bg-slate-900 border border-slate-800 text-slate-400 hover:text-white transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
          </Link>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-xs font-mono text-slate-500">AUDIT #{scan.id}</span>
              <StatusBadge status={scan.status} />
            </div>
            <h2 className="text-xl font-bold text-white tracking-tight flex items-center gap-2 mt-0.5">
              <span>{scan.targetUrl}</span>
              <a href={scan.targetUrl} target="_blank" rel="noreferrer" className="text-slate-500 hover:text-slate-300">
                <ExternalLink className="w-4 h-4" />
              </a>
            </h2>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={handleDownloadPdf}
            disabled={downloading || scan.status !== 'COMPLETED'}
            className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-700 text-white text-sm font-semibold transition-all disabled:opacity-50"
          >
            <FileDown className="w-4 h-4 text-emerald-400" />
            {downloading ? 'Generating PDF...' : 'Download PDF Report'}
          </button>
        </div>
      </div>

      {/* Primary Overview Cards */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Score & Posture Box */}
        <div className="p-6 rounded-2xl bg-slate-900/90 border border-slate-800 flex flex-col justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-400">
            Overall Security Posture
          </span>
          <div className="py-4">
            <ScoreGauge score={scan.securityScore} grade={scan.letterGrade} />
          </div>
          <div className="text-xs text-slate-400 border-t border-slate-800/80 pt-3 flex items-center justify-between">
            <span>Scan Profile: <strong className="text-slate-200">{scan.scanType}</strong></span>
            <span>Duration: <strong className="text-slate-200">
              {scan.startTime && scan.endTime
                ? `${Math.max(1, Math.round((new Date(scan.endTime) - new Date(scan.startTime)) / 1000))}s`
                : 'In progress'}
            </strong></span>
          </div>
        </div>

        {/* Engine Diagnostics & Exposure Checks */}
        <div className="p-6 rounded-2xl bg-slate-900/90 border border-slate-800 space-y-4 lg:col-span-2">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-400 block">
            Automated Audit Diagnostics
          </span>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <div className="p-3.5 rounded-xl bg-slate-950/60 border border-slate-800/80">
              <span className="text-[11px] text-slate-400 font-medium">Headers Inspected</span>
              <span className="text-xl font-bold text-white block mt-1">{scan.headersAnalyzed || 0}</span>
            </div>
            <div className="p-3.5 rounded-xl bg-slate-950/60 border border-slate-800/80">
              <span className="text-[11px] text-slate-400 font-medium">Endpoints Tested</span>
              <span className="text-xl font-bold text-white block mt-1">{scan.endpointsTested || 0}</span>
            </div>
            <div className="p-3.5 rounded-xl bg-slate-950/60 border border-slate-800/80">
              <span className="text-[11px] text-slate-400 font-medium">SSL / TLS Protocol</span>
              <span className="text-sm font-bold text-emerald-400 block mt-1 truncate">
                {scan.tlsVersion || (scan.sslValid ? 'Active TLS' : 'N/A')}
              </span>
            </div>
            <div className="p-3.5 rounded-xl bg-slate-950/60 border border-slate-800/80">
              <span className="text-[11px] text-slate-400 font-medium">SSL Valid</span>
              <span className="text-sm font-bold text-white block mt-1">
                {scan.sslValid === true ? (
                  <span className="text-emerald-400 flex items-center gap-1">
                    <CheckCircle2 className="w-3.5 h-3.5" /> Valid
                  </span>
                ) : scan.sslValid === false ? (
                  <span className="text-rose-400 flex items-center gap-1">
                    <AlertCircle className="w-3.5 h-3.5" /> Invalid / Plain
                  </span>
                ) : (
                  'Pending'
                )}
              </span>
            </div>
          </div>

          {/* Identified Technologies */}
          <div className="pt-2 border-t border-slate-800">
            <span className="text-xs text-slate-400 font-semibold block mb-2">Fingerprinted Technologies:</span>
            {scan.technologiesFound ? (
              <div className="flex flex-wrap gap-2">
                {scan.technologiesFound.split(', ').map((tech, idx) => (
                  <span key={idx} className="px-2.5 py-1 rounded-lg bg-slate-800 text-slate-300 text-xs font-mono border border-slate-700/60">
                    {tech}
                  </span>
                ))}
              </div>
            ) : (
              <span className="text-xs text-slate-500 italic">No technology signatures detected or disclosed.</span>
            )}
          </div>
        </div>
      </div>

      {/* Vulnerabilities Table & Filter */}
      <div className="rounded-2xl bg-slate-900/80 border border-slate-800 overflow-hidden shadow-xl">
        <div className="p-6 border-b border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h3 className="font-bold text-white text-base">Detected Vulnerabilities & Audit Findings</h3>
            <p className="text-xs text-slate-400 mt-0.5">
              Showing {filteredVulns.length} of {vulnerabilities.length} findings
            </p>
          </div>

          {/* Filters */}
          <div className="flex flex-wrap items-center gap-3">
            <div className="relative">
              <Search className="w-3.5 h-3.5 absolute left-3 top-3 text-slate-500" />
              <input
                type="text"
                placeholder="Search findings or CWE..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-8 pr-3 py-1.5 rounded-lg bg-slate-950 border border-slate-700 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-emerald-500"
              />
            </div>

            <div className="flex rounded-lg bg-slate-950 border border-slate-800 p-0.5 text-xs">
              {['ALL', 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'INFO'].map((sev) => (
                <button
                  key={sev}
                  onClick={() => setSeverityFilter(sev)}
                  className={`px-2.5 py-1 rounded-md font-semibold transition-colors ${
                    severityFilter === sev
                      ? 'bg-emerald-500 text-slate-950 shadow-sm'
                      : 'text-slate-400 hover:text-white'
                  }`}
                >
                  {sev}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Findings List */}
        <div className="divide-y divide-slate-800/80">
          {filteredVulns.length === 0 ? (
            <div className="text-center py-12 text-slate-400 text-sm">
              No findings match your filter criteria.
            </div>
          ) : (
            filteredVulns.map((v) => {
              const isExpanded = expandedVuln === v.id;
              return (
                <div key={v.id} className="p-5 hover:bg-slate-800/20 transition-colors">
                  <div
                    className="flex items-start justify-between gap-4 cursor-pointer"
                    onClick={() => setExpandedVuln(isExpanded ? null : v.id)}
                  >
                    <div className="space-y-1.5 flex-1">
                      <div className="flex items-center gap-2.5 flex-wrap">
                        <SeverityBadge severity={v.severity} />
                        <span className="font-bold text-white text-sm">{v.title}</span>
                        {v.cwe && (
                          <span className="px-2 py-0.5 rounded bg-slate-800 text-slate-400 font-mono text-[11px] border border-slate-700">
                            {v.cwe}
                          </span>
                        )}
                        <span className="px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 text-[11px] font-medium border border-emerald-500/20">
                          {v.category}
                        </span>
                      </div>
                      <p className="text-xs text-slate-400 line-clamp-2 leading-relaxed">
                        {v.description}
                      </p>
                    </div>

                    <button className="p-1 rounded-lg text-slate-500 hover:text-slate-300">
                      {isExpanded ? <ChevronUp className="w-5 h-5" /> : <ChevronDown className="w-5 h-5" />}
                    </button>
                  </div>

                  {/* Expanded Detail Panel */}
                  {isExpanded && (
                    <div className="mt-4 pt-4 border-t border-slate-800/80 space-y-3.5 text-xs">
                      {/* Evidence */}
                      {v.evidence && (
                        <div>
                          <span className="font-bold text-slate-300 block mb-1">Observed Evidence & HTTP Response:</span>
                          <pre className="p-3 rounded-lg bg-slate-950 border border-slate-800 text-slate-300 font-mono overflow-x-auto whitespace-pre-wrap text-[11px] leading-relaxed">
                            {v.evidence}
                          </pre>
                        </div>
                      )}

                      {/* Affected URL */}
                      {v.affectedUrl && (
                        <div>
                          <span className="font-bold text-slate-300 block mb-1">Affected Endpoint:</span>
                          <span className="font-mono text-emerald-400 bg-slate-950 px-2 py-1 rounded border border-slate-800 block break-all">
                            {v.affectedUrl}
                          </span>
                        </div>
                      )}

                      {/* Remediation */}
                      {v.remediation && (
                        <div className="p-3.5 rounded-xl bg-emerald-950/30 border border-emerald-500/30 text-emerald-300">
                          <span className="font-bold flex items-center gap-1.5 mb-1 text-emerald-400">
                            <CheckCircle2 className="w-4 h-4" /> Recommended Remediation Action:
                          </span>
                          <p className="leading-relaxed">{v.remediation}</p>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              );
            })
          )}
        </div>
      </div>
    </div>
  );
}
