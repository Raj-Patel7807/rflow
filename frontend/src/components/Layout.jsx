import { useState, useEffect } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useTenant } from "../context/TenantContext";
import { api } from "../api/client";

const links = [
    { to: "/", label: "Dashboard", end: true },
    { to: "/tenants", label: "Tenants" },
    { to: "/services", label: "Services" },
    { to: "/rate-limits", label: "Rate Limits" },
    { to: "/service-health", label: "Service Health" },
    { to: "/gateway-health", label: "Gateway Health" },
    { to: "/tester", label: "API Tester" },
    { to: "/logs", label: "Logs" },
    { to: "/users", label: "Users" },
    { to: "/settings", label: "Settings" },
];

function GatewayHealthIndicator() {
    const [status, setStatus] = useState("LOADING");

    useEffect(() => {
        async function checkHealth() {
            try {
                const data = await api("/api/gateway/health");
                setStatus(data.status || "UP");
            } catch {
                setStatus("DOWN");
            }
        }

        checkHealth();
        const interval = setInterval(checkHealth, 30000);
        return () => clearInterval(interval);
    }, []);

    let colorClass = "status-loading";
    let text = "Checking Gateway...";

    if (status === "UP") {
        colorClass = "status-up";
        text = "Gateway: UP";
    } else if (status === "DEGRADED") {
        colorClass = "status-degraded";
        text = "Gateway: DEGRADED";
    } else if (status === "DOWN") {
        colorClass = "status-down";
        text = "Gateway: DOWN";
    }

    return (
        <div
            className={`gateway-health-indicator ${colorClass}`}
            title="Gateway Status"
        >
            <span className="status-dot"></span>
            <span className="status-text">{text}</span>
        </div>
    );
}

export default function Layout() {
    const { user, logout } = useAuth();
    const { tenants, selectedTenant, selectTenant } = useTenant();
    const navigate = useNavigate();

    async function handleLogout() {
        await logout();
        navigate("/login");
    }

    async function handleTenantChange(event) {
        const value = event.target.value;
        await selectTenant(value || null);
    }

    return (
        <div className="layout">
            <aside className="sidebar">
                <div className="sidebar-top">
                    <div className="brand">
                        <img
                            src="/rflow-favicon.png"
                            alt="RFlow Logo"
                            className="brand-logo"
                        />
                        <div className="brand-text">
                            <h1>RFlow</h1>
                            <p>API Gateway Controller</p>
                        </div>
                    </div>

                    <nav>
                        {links.map((link) => (
                            <NavLink
                                key={link.to}
                                to={link.to}
                                end={link.end}
                                className={({ isActive }) =>
                                    `nav-link ${isActive ? "active" : ""}`
                                }
                            >
                                {link.label}
                            </NavLink>
                        ))}
                    </nav>
                </div>
            </aside>

            <main className="main">
                <header className="top-header">
                    <div className="header-left">
                        <div className="tenant-selector-wrapper">
                            <select
                                id="tenant-select"
                                value={selectedTenant?.id || ""}
                                onChange={handleTenantChange}
                                className="tenant-select"
                            >
                                <option value="">-- No Active Tenant --</option>
                                {tenants.map((tenant) => (
                                    <option key={tenant.id} value={tenant.id}>
                                        {tenant.tenantName} (/
                                        {tenant.tenantSlug})
                                    </option>
                                ))}
                            </select>
                            {selectedTenant && (
                                <span
                                    className={`pill ${selectedTenant.status === "ACTIVE" ? "pill-green" : "pill-gray"}`}
                                >
                                    {selectedTenant.status}
                                </span>
                            )}
                        </div>
                        <GatewayHealthIndicator />
                    </div>

                    <div className="header-right">
                        <div className="user-profile">
                            <div className="user-avatar">
                                {user?.fullName?.charAt(0).toUpperCase() || "A"}
                            </div>
                            <div className="user-details">
                                <strong>{user?.fullName}</strong>
                                <span>SUPER ADMIN</span>
                            </div>
                        </div>
                        <button
                            type="button"
                            className="btn-logout"
                            onClick={handleLogout}
                            title="Logout"
                        >
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                width="18"
                                height="18"
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                                strokeWidth="2"
                            >
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
                                />
                            </svg>
                        </button>
                    </div>
                </header>

                <div className="content-area">
                    <Outlet />
                </div>
            </main>
        </div>
    );
}
