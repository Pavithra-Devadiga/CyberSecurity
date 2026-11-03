import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import {
  Bell,
  CheckCircle2,
  AlertTriangle,
  Info,
  CheckCheck,
  ChevronLeft,
  ChevronRight,
  ExternalLink,
} from 'lucide-react';

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);

  const fetchNotifications = async () => {
    try {
      const res = await api.get(`/notifications?page=${page}&size=10`);
      if (res.data && res.data.data) {
        setNotifications(res.data.data.content || []);
        setTotalPages(res.data.data.totalPages || 1);
      }
    } catch (err) {
      console.error('Failed to load notifications', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, [page]);

  const handleMarkAsRead = async (id) => {
    try {
      await api.patch(`/notifications/${id}/read`);
      setNotifications(
        notifications.map((n) => (n.id === id ? { ...n, isRead: true } : n))
      );
    } catch (err) {
      console.error('Failed to mark notification as read', err);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await api.post('/notifications/mark-all-read');
      setNotifications(notifications.map((n) => ({ ...n, isRead: true })));
    } catch (err) {
      console.error('Failed to mark all notifications as read', err);
    }
  };

  const getTypeIcon = (type) => {
    switch (type) {
      case 'SUCCESS':
        return <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0" />;
      case 'WARNING':
        return <AlertTriangle className="w-5 h-5 text-yellow-400 shrink-0" />;
      case 'ERROR':
        return <AlertTriangle className="w-5 h-5 text-red-400 shrink-0" />;
      default:
        return <Info className="w-5 h-5 text-blue-400 shrink-0" />;
    }
  };

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight">Security Alerts & Notifications</h2>
          <p className="text-slate-400 text-sm mt-1">
            Real-time audit updates, scan status alerts, and vulnerability escalation warnings.
          </p>
        </div>

        <button
          onClick={handleMarkAllAsRead}
          className="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-800 text-slate-300 hover:text-white text-xs font-semibold transition-colors"
        >
          <CheckCheck className="w-4 h-4 text-emerald-400" />
          Mark All as Read
        </button>
      </div>

      <div className="rounded-2xl bg-slate-900/80 border border-slate-800 overflow-hidden shadow-xl">
        <div className="divide-y divide-slate-800/80">
          {loading ? (
            <div className="text-center py-12">
              <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-400" />
            </div>
          ) : notifications.length === 0 ? (
            <div className="text-center py-12 text-slate-500 text-sm">
              No notifications at this time.
            </div>
          ) : (
            notifications.map((n) => (
              <div
                key={n.id}
                className={`p-5 flex items-start justify-between gap-4 transition-colors ${
                  n.isRead ? 'bg-transparent' : 'bg-emerald-500/[0.03]'
                }`}
              >
                <div className="flex items-start gap-3.5 flex-1">
                  <div className="mt-0.5">{getTypeIcon(n.type)}</div>
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <span className="font-bold text-sm text-white">{n.title}</span>
                      {!n.isRead && (
                        <span className="w-2 h-2 rounded-full bg-emerald-400" />
                      )}
                    </div>
                    <p className="text-xs text-slate-300 leading-relaxed">{n.message}</p>
                    <span className="text-[11px] text-slate-500 font-mono block pt-1">
                      {n.createdAt ? new Date(n.createdAt).toLocaleString() : ''}
                    </span>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  {n.scanId && (
                    <Link
                      to={`/scans/${n.scanId}`}
                      className="text-xs text-emerald-400 hover:underline inline-flex items-center gap-1"
                    >
                      Audit Report
                      <ExternalLink className="w-3 h-3" />
                    </Link>
                  )}
                  {!n.isRead && (
                    <button
                      onClick={() => handleMarkAsRead(n.id)}
                      className="text-xs text-slate-400 hover:text-white"
                      title="Mark as read"
                    >
                      <CheckCircle2 className="w-4 h-4" />
                    </button>
                  )}
                </div>
              </div>
            ))
          )}
        </div>

        {/* Pagination Bar */}
        <div className="p-4 border-t border-slate-800 flex items-center justify-between text-xs text-slate-400">
          <span>Page {page + 1} of {totalPages}</span>
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
