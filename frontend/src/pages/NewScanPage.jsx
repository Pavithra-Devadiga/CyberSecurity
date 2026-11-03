import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/client';
import {
  ShieldAlert,
  Play,
  CheckSquare,
  Square,
  AlertTriangle,
  Info,
  ShieldCheck,
  Globe,
  Sliders,
} from 'lucide-react';

export default function NewScanPage() {
  const navigate = useNavigate();
  const [targetUrl, setTargetUrl] = useState('');
  const [scanType, setScanType] = useState('FULL');
  const [selectedModules, setSelectedModules] = useState([
    'HEADERS',
    'SSL',
    'EXPOSURE',
    'SENSITIVE',
    'TECH',
    'SQLI',
    'XSS',
  ]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const availableModules = [
    { id: 'HEADERS', name: 'HTTP Security Headers', desc: 'Analyzes HSTS, CSP, X-Frame-Options, Referrer-Policy & server version disclosure' },
    { id: 'SSL', name: 'SSL/TLS & Certificates', desc: 'Checks TLS 1.2/1.3 protocols, weak ciphers, certificate expiration, SANs & RSA key size' },
    { id: 'EXPOSURE', name: 'Endpoint Exposure', desc: 'Safely probes for exposed /.env, /.git/HEAD, Spring Boot /actuator, /swagger-ui & robots.txt' },
    { id: 'SENSITIVE', name: 'Secrets & Leaks', desc: 'Scans for exposed AWS keys, private RSA keys, DB connection strings & stack traces' },
    { id: 'TECH', name: 'Technology Fingerprinting', desc: 'Detects web servers, CMS platforms, backend runtimes, and frontend frameworks' },
    { id: 'SQLI', name: 'SQL Injection Assessment', desc: 'Benign syntax probes and database error signature detection without destructive payloads' },
    { id: 'XSS', name: 'XSS Assessment', desc: 'Canary reflection tests, contextual HTML entity encoding validation & DOM sinks' },
    { id: 'ZAP', name: 'OWASP ZAP REST API', desc: 'Deep automated scanning via external OWASP ZAP daemon if configured' },
  ];

  const handleScanTypeChange = (type) => {
    setScanType(type);
    if (type === 'QUICK') {
      setSelectedModules(['HEADERS', 'SSL', 'TECH']);
    } else if (type === 'FULL') {
      setSelectedModules(['HEADERS', 'SSL', 'EXPOSURE', 'SENSITIVE', 'TECH', 'SQLI', 'XSS', 'ZAP']);
    } else if (type === 'HEADERS_ONLY') {
      setSelectedModules(['HEADERS']);
    } else if (type === 'SSL_ONLY') {
      setSelectedModules(['SSL']);
    }
  };

  const toggleModule = (id) => {
    setScanType('CUSTOM');
    if (selectedModules.includes(id)) {
      setSelectedModules(selectedModules.filter((m) => m !== id));
    } else {
      setSelectedModules([...selectedModules, id]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!targetUrl.trim()) {
      setError('Please provide a valid target URL.');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const payload = {
        targetUrl: targetUrl.trim(),
        scanType: scanType,
        modules: selectedModules,
      };

      const res = await api.post('/scans', payload);
      if (res.data && res.data.data) {
        navigate(`/scans/${res.data.data.id}`);
      }
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Failed to initiate vulnerability scan';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      <div>
        <h2 className="text-2xl font-bold text-white tracking-tight">Launch Vulnerability Audit</h2>
        <p className="text-slate-400 text-sm mt-1">
          Perform non-destructive web security testing, configuration audits, and automated exposure analysis.
        </p>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-400 flex items-start gap-3">
          <AlertTriangle className="w-5 h-5 shrink-0 mt-0.5" />
          <div className="text-sm">
            <span className="font-bold">Scan Initiation Error: </span>
            {error}
          </div>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Target URL Input */}
        <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-4">
          <label className="block text-sm font-bold text-slate-200">
            Target Domain or Web Application URL
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-500">
              <Globe className="w-5 h-5" />
            </div>
            <input
              type="text"
              value={targetUrl}
              onChange={(e) => setTargetUrl(e.target.value)}
              placeholder="https://example.com or example.com"
              className="w-full pl-12 pr-4 py-3.5 bg-slate-950 border border-slate-700/80 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 font-mono text-sm"
              required
            />
          </div>

          <div className="flex items-center gap-2 text-xs text-slate-400">
            <Info className="w-4 h-4 text-slate-500 shrink-0" />
            <span>
              Target must be a publicly accessible HTTP/HTTPS domain or authorized web service.
            </span>
          </div>
        </div>

        {/* Scan Type Presets */}
        <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-4">
          <div className="flex items-center justify-between">
            <label className="block text-sm font-bold text-slate-200">Select Audit Profile</label>
            <span className="text-xs text-slate-400 font-medium">Preset Configurations</span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
            {[
              { id: 'FULL', label: 'Full Deep Scan', desc: 'All 8 modules & heuristic analysis', popular: true },
              { id: 'QUICK', label: 'Quick Triage', desc: 'Headers, SSL and technology check' },
              { id: 'HEADERS_ONLY', label: 'Headers Only', desc: 'Fast CSP, HSTS, XFO assessment' },
              { id: 'SSL_ONLY', label: 'SSL/TLS Only', desc: 'Certificate validity & ciphers' },
            ].map((preset) => (
              <button
                key={preset.id}
                type="button"
                onClick={() => handleScanTypeChange(preset.id)}
                className={`p-4 rounded-xl text-left border transition-all ${
                  scanType === preset.id
                    ? 'bg-emerald-500/10 border-emerald-500/50 text-white shadow-sm shadow-emerald-950'
                    : 'bg-slate-950/60 border-slate-800 text-slate-400 hover:border-slate-700'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className={`font-bold text-sm ${scanType === preset.id ? 'text-emerald-400' : 'text-slate-200'}`}>
                    {preset.label}
                  </span>
                  {preset.popular && (
                    <span className="text-[10px] uppercase font-extrabold px-1.5 py-0.5 rounded bg-emerald-500/20 text-emerald-400">
                      Recommended
                    </span>
                  )}
                </div>
                <p className="text-xs text-slate-400 mt-1">{preset.desc}</p>
              </button>
            ))}
          </div>
        </div>

        {/* Modular Scanner Selection */}
        <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Sliders className="w-4 h-4 text-emerald-400" />
              <label className="text-sm font-bold text-slate-200">Active Scanner Modules</label>
            </div>
            <span className="text-xs text-slate-400">
              {selectedModules.length} of {availableModules.length} enabled
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 pt-2">
            {availableModules.map((mod) => {
              const isSelected = selectedModules.includes(mod.id);
              return (
                <div
                  key={mod.id}
                  onClick={() => toggleModule(mod.id)}
                  className={`p-3.5 rounded-xl border cursor-pointer transition-all flex items-start gap-3 select-none ${
                    isSelected
                      ? 'bg-slate-950/90 border-slate-700 text-slate-200'
                      : 'bg-slate-950/40 border-slate-800/80 text-slate-500 hover:border-slate-800'
                  }`}
                >
                  <div className="mt-0.5 text-emerald-400">
                    {isSelected ? <CheckSquare className="w-4 h-4" /> : <Square className="w-4 h-4 text-slate-600" />}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2">
                      <span className={`text-xs font-bold ${isSelected ? 'text-white' : 'text-slate-400'}`}>
                        {mod.name}
                      </span>
                      <span className="text-[10px] font-mono px-1.5 py-0.2 rounded bg-slate-800 text-slate-400">
                        {mod.id}
                      </span>
                    </div>
                    <p className="text-[11px] text-slate-400 mt-0.5 leading-relaxed">{mod.desc}</p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* SSRF & Authorization Disclaimer */}
        <div className="p-4 rounded-xl bg-slate-900/40 border border-slate-800 text-xs text-slate-400 space-y-2">
          <div className="flex items-center gap-2 text-slate-300 font-semibold">
            <ShieldCheck className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>Target Authorization & Safe Scanning Policy</span>
          </div>
          <p>
            By launching this audit, you confirm that you have explicit authorization to scan the target host. 
            All requests strictly adhere to benign heuristic analysis. Automatic SSRF protection blocks access to private 
            networks (RFC 1918), loopback interfaces (127.0.0.1, localhost), and cloud metadata services (169.254.169.254).
          </p>
        </div>

        {/* Submit Button */}
        <button
          type="submit"
          disabled={loading || selectedModules.length === 0}
          className="w-full py-4 rounded-xl bg-emerald-500 hover:bg-emerald-400 disabled:opacity-50 disabled:cursor-not-allowed text-slate-950 font-bold text-base transition-all flex items-center justify-center gap-2 shadow-lg shadow-emerald-500/20"
        >
          {loading ? (
            <>
              <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-slate-950" />
              <span>Initiating Audit & Validating Target...</span>
            </>
          ) : (
            <>
              <Play className="w-5 h-5 fill-current" />
              <span>Start Vulnerability Audit</span>
            </>
          )}
        </button>
      </form>
    </div>
  );
}
