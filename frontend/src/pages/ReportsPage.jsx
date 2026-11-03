import React, { useState, useEffect } from 'react';
import api from '../api/client';
import ScoreGauge from '../components/common/ScoreGauge';
import {
  FileText,
  FileDown,
  ExternalLink,
  ShieldCheck,
  CheckCircle2,
  Calendar,
} from 'lucide-react';

export default function ReportsPage() {
  const [scans, setScans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [downloadingId, setDownloadingId] = useState(null);

  useEffect(() => {
    const fetchReports = async () => {
      try {
        const res = await api.get('/scans?page=0&size=20');
        if (res.data && res.data.data) {
          // Filter to completed scans
          const completed = (res.data.data.content || []).filter((s) => s.status === 'COMPLETED');
          setScans(completed);
        }
      } catch (err) {
        console.error('Failed to load reports', err);
      } finally {
        setLoading(false);
      }
    };

    fetchReports();
  }, []);

  const handleDownload = async (scanId) => {
    setDownloadingId(scanId);
    try {
      const response = await api.get(`/reports/download/${scanId}`, {
        responseType: 'blob',
      });
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `security-audit-report-scan-${scanId}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error('Failed to download report', err);
    } finally {
      setDownloadingId(null);
    }
  };

  return (
    <div className="space-y-6 max-w-5xl mx-auto">
      <div>
        <h2 className="text-2xl font-bold text-white tracking-tight">Security Audit Reports</h2>
        <p className="text-slate-400 text-sm mt-1">
          Generate and download executive PDF compliance summaries and detailed remediation reports generated via OpenPDF.
        </p>
      </div>

      {loading ? (
        <div className="text-center py-16">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-400" />
        </div>
      ) : scans.length === 0 ? (
        <div className="text-center py-16 rounded-2xl bg-slate-900/40 border border-slate-800 text-slate-400 text-sm">
          No completed scans available for PDF report generation yet. Run a scan from the dashboard to produce a report.
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {scans.map((scan) => (
            <div
              key={scan.id}
              className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 hover:border-slate-700 transition-all flex flex-col justify-between space-y-4"
            >
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <span className="font-mono text-xs text-slate-500">REPORT #{scan.id}</span>
                  <span className="px-2.5 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 text-xs font-semibold">
                    {scan.scanType}
                  </span>
                </div>

                <h3 className="font-bold text-white text-base truncate">
                  {scan.targetUrl}
                </h3>

                <div className="py-2">
                  <ScoreGauge score={scan.securityScore} grade={scan.letterGrade} size="sm" />
                </div>

                <div className="text-xs text-slate-400 space-y-1">
                  <div className="flex items-center gap-1.5">
                    <Calendar className="w-3.5 h-3.5 text-slate-500" />
                    <span>Audited: {scan.createdAt ? new Date(scan.createdAt).toLocaleString() : 'Recent'}</span>
                  </div>
                  <div className="flex items-center gap-1.5">
                    <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
                    <span>{scan.vulnerabilityCount} security findings detected</span>
                  </div>
                </div>
              </div>

              <div className="pt-4 border-t border-slate-800 flex items-center justify-between">
                <span className="text-xs text-slate-500 font-mono">Format: PDF</span>
                <button
                  onClick={() => handleDownload(scan.id)}
                  disabled={downloadingId === scan.id}
                  className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs transition-all shadow-md shadow-emerald-500/20 disabled:opacity-50"
                >
                  <FileDown className="w-3.5 h-3.5" />
                  {downloadingId === scan.id ? 'Generating...' : 'Download PDF'}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
