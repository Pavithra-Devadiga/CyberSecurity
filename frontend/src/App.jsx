import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/common/Layout';
import DashboardPage from './pages/DashboardPage';
import NewScanPage from './pages/NewScanPage';
import ScansPage from './pages/ScansPage';
import ScanDetailPage from './pages/ScanDetailPage';
import VulnerabilitiesPage from './pages/VulnerabilitiesPage';
import ReportsPage from './pages/ReportsPage';
import NotificationsPage from './pages/NotificationsPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-400" />
      </div>
    );
  }

  return isAuthenticated ? children : <Navigate to="/login" replace />;
};

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route index element={<DashboardPage />} />
            <Route path="scans" element={<ScansPage />} />
            <Route path="scans/new" element={<NewScanPage />} />
            <Route path="scans/:id" element={<ScanDetailPage />} />
            <Route path="vulnerabilities" element={<VulnerabilitiesPage />} />
            <Route path="reports" element={<ReportsPage />} />
            <Route path="notifications" element={<NotificationsPage />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
