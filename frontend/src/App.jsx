import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import { TenantProvider } from "./context/TenantContext";
import ProtectedRoute from "./components/ProtectedRoute";
import TenantRequired from "./components/TenantRequired";
import Layout from "./components/Layout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Tenants from "./pages/Tenants";
import Services from "./pages/Services";
import RateLimits from "./pages/RateLimits";
import ServiceHealth from "./pages/ServiceHealth";
import GatewayHealth from "./pages/GatewayHealth";
import ApiTester from "./pages/ApiTester";
import Logs from "./pages/Logs";
import Users from "./pages/Users";
import Settings from "./pages/Settings";
import Loader from "./components/Loader";
import "./App.css";

function AppContent() {
    const { loading } = useAuth();

    if (loading) {
        return <Loader message="Verifying authentication status" />;
    }

    return (
        <TenantProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<Login />} />

                    <Route
                        element={
                            <ProtectedRoute>
                                <Layout />
                            </ProtectedRoute>
                        }
                    >
                        <Route index element={<Dashboard />} />
                        <Route path="tenants" element={<Tenants />} />
                        <Route
                            path="services"
                            element={
                                <TenantRequired>
                                    <Services />
                                </TenantRequired>
                            }
                        />
                        <Route
                            path="rate-limits"
                            element={
                                <TenantRequired>
                                    <RateLimits />
                                </TenantRequired>
                            }
                        />
                        <Route
                            path="service-health"
                            element={
                                <TenantRequired>
                                    <ServiceHealth />
                                </TenantRequired>
                            }
                        />
                        <Route
                            path="gateway-health"
                            element={<GatewayHealth />}
                        />
                        <Route
                            path="tester"
                            element={
                                <TenantRequired>
                                    <ApiTester />
                                </TenantRequired>
                            }
                        />
                        <Route
                            path="logs"
                            element={
                                <TenantRequired>
                                    <Logs />
                                </TenantRequired>
                            }
                        />
                        <Route
                            path="users"
                            element={
                                <TenantRequired>
                                    <Users />
                                </TenantRequired>
                            }
                        />
                        <Route path="settings" element={<Settings />} />
                    </Route>

                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </BrowserRouter>
        </TenantProvider>
    );
}

function App() {
    return (
        <AuthProvider>
            <AppContent />
        </AuthProvider>
    );
}

export default App;
