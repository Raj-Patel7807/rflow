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
    const { user, logout, isSuperAdmin } = useAuth();
    const filteredLinks = links;
    const { tenants, selectedTenant, selectTenant } = useTenant();
    const navigate = useNavigate();
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const [isProfileDropdownOpen, setIsProfileDropdownOpen] = useState(false);

    async function handleLogout() {
        await logout();
        navigate("/login");
    }

    async function handleTenantChange(event) {
        const value = event.target.value;
        await selectTenant(value || null);
    }

    return (
        <div className={`layout ${isSidebarOpen ? "sidebar-open" : ""}`}>
            {isSidebarOpen && (
                <div
                    className="sidebar-backdrop"
                    onClick={() => setIsSidebarOpen(false)}
                />
            )}

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
                        {filteredLinks.map((link) => (
                            <NavLink
                                key={link.to}
                                to={link.to}
                                end={link.end}
                                className={({ isActive }) => `nav-link ${isActive ? "active" : ""}`}
                                onClick={() => setIsSidebarOpen(false)}
                            >
                                {link.label}
                            </NavLink>
                        ))}
                    </nav>
                </div>

                <div className="sidebar-footer">
                    <div className="user-info">
                        <strong>{user?.fullName}</strong>
                        <span>{user?.role?.replace("_", " ")}</span>
                    </div>
                    <button
                        type="button"
                        className="btn-sidebar-logout"
                        onClick={handleLogout}
                    >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="16"
                            height="16"
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
                        <span>Sign Out</span>
                    </button>
                </div>
            </aside>

            <main className="main">
                <header className="top-header">
                    <button
                        type="button"
                        className="sidebar-toggle"
                        onClick={() => setIsSidebarOpen(!isSidebarOpen)}
                        aria-label="Toggle navigation menu"
                    >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="24"
                            height="24"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                            strokeWidth="2"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                d="M4 6h16M4 12h16M4 18h16"
                            />
                        </svg>
                    </button>

                    <div className="header-left">
                        {isSuperAdmin ? (
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
                                            {tenant.tenantName} (/{tenant.tenantSlug})
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
                        ) : (
                            selectedTenant && (
                                <div style={{ display: "flex", alignItems: "center", gap: "8px", background: "rgba(255, 255, 255, 0.03)", padding: "6px 12px", borderRadius: "6px", border: "1px solid var(--border)" }}>
                                    <span style={{ fontSize: "12px", color: "var(--muted)", fontWeight: "500", textTransform: "uppercase" }}>Tenant</span>
                                    <strong style={{ fontSize: "14px", color: "var(--foreground)" }}>{selectedTenant.tenantName}</strong>
                                </div>
                            )
                        )}
                        <GatewayHealthIndicator />
                    </div>

                    <div className="header-right">
                        <div className="user-menu-wrapper">
                            <button
                                type="button"
                                className="user-profile-btn"
                                onClick={() => setIsProfileDropdownOpen(!isProfileDropdownOpen)}
                                aria-label="User Menu"
                            >
                                <div className="user-avatar">
                                    {user?.fullName?.charAt(0).toUpperCase() || "A"}
                                </div>
                                <div className="user-details">
                                    <strong>{user?.fullName}</strong>
                                    <span>{user?.role?.replace("_", " ")}</span>
                                </div>
                                <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    width="14"
                                    height="14"
                                    fill="none"
                                    viewBox="0 0 24 24"
                                    stroke="currentColor"
                                    strokeWidth="2.5"
                                    className={`dropdown-chevron ${isProfileDropdownOpen ? "open" : ""}`}
                                >
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                                </svg>
                            </button>

                            {isProfileDropdownOpen && (
                                <>
                                    <div
                                        className="dropdown-backdrop"
                                        onClick={() => setIsProfileDropdownOpen(false)}
                                    />
                                    <div className="user-dropdown">
                                        <div className="dropdown-header">
                                            <strong>{user?.fullName}</strong>
                                            <span>{user?.role?.replace("_", " ")}</span>
                                        </div>
                                        <div className="dropdown-divider" />
                                        <button
                                            type="button"
                                            className="dropdown-item logout-item"
                                            onClick={() => {
                                                setIsProfileDropdownOpen(false);
                                                handleLogout();
                                            }}
                                        >
                                            <svg
                                                xmlns="http://www.w3.org/2000/svg"
                                                width="16"
                                                height="16"
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
                                            <span>Sign Out</span>
                                        </button>
                                    </div>
                                </>
                            )}
                        </div>
                    </div>
                </header>

                <div className="content-area">
                    <Outlet />
                </div>
            </main>
        </div>
    );
}
