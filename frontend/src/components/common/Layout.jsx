import React, { useState, useEffect } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import Sidebar from './Sidebar';
import Navbar from './Navbar';
import api from '../../api/client';

export default function Layout() {
  const [unreadCount, setUnreadCount] = useState(0);
  const location = useLocation();

  useEffect(() => {
    const fetchUnread = async () => {
      try {
        const res = await api.get('/notifications/unread-count');
        if (res.data && res.data.data) {
          setUnreadCount(res.data.data.unreadCount || 0);
        }
      } catch {
        // Ignore unauthenticated or offline errors
      }
    };

    fetchUnread();
    const interval = setInterval(fetchUnread, 15000); // refresh every 15s
    return () => clearInterval(interval);
  }, [location.pathname]);

  const getPageTitle = (path) => {
    if (path.startsWith('/scans/new')) return 'Launch Vulnerability Scan';
    if (path.startsWith('/scans/')) return 'Scan Audit Report Details';
    if (path === '/scans') return 'Scan History & Results';
    if (path === '/vulnerabilities') return 'Vulnerability Findings Database';
    if (path === '/reports') return 'Security Audit Reports';
    if (path === '/notifications') return 'Security Alerts & Notifications';
    return 'Security Posture Dashboard';
  };

  return (
    <div className="flex min-h-screen bg-slate-950 text-slate-100">
      <Sidebar unreadCount={unreadCount} />
      <div className="flex-1 flex flex-col min-w-0">
        <Navbar title={getPageTitle(location.pathname)} />
        <main className="flex-1 p-8 overflow-y-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
