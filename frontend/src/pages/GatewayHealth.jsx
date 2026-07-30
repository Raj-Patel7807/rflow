import { useEffect, useState } from "react";
import { api } from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function GatewayHealth() {
    const { user } = useAuth();
    const isDeveloper = user?.role === "DEVELOPER";
    const [health, setHealth] = useState(null);
    const [systemStats, setSystemStats] = useState(null);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);

    async function loadHealthData() {
        setLoading(true);
        setError("");
        try {
            const [healthData, systemData] = await Promise.all([
                api("/api/gateway/health"),
                api("/api/dashboard/system"),
            ]);
            setHealth(healthData);
            setSystemStats(systemData);
        } catch (err) {
            setError(`Failed to retrieve health metrics: ${err.message}`);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadHealthData();
    }, []);

    function formatUptime(ms) {
        if (!ms) return "-";
        const seconds = Math.floor((ms / 1000) % 60);
        const minutes = Math.floor((ms / (1000 * 60)) % 60);
        const hours = Math.floor((ms / (1000 * 60 * 60)) % 24);
        const days = Math.floor(ms / (1000 * 60 * 60 * 24));

        return `${days > 0 ? `${days}d ` : ""}${hours}h ${minutes}m ${seconds}s`;
    }

    function formatBytes(bytes) {
        if (!bytes) return "-";
        const mb = bytes / (1024 * 1024);
        return `${mb.toFixed(1)} MB`;
    }

    const memoryPercent = health?.memory ? ((health.memory.usedMemory / health.memory.maxMemory) * 100).toFixed(1,) : 0;

    return (
        <div className="page">
            <header className="page-header page-header-responsive">
                <div>
                    <h2>Gateway Health Status</h2>
                    <p className="muted">
                        Real-time gateway system information, resource usage,
                        and active dependencies
                    </p>
                </div>
                {!isDeveloper && (
                <button
                    type="button"
                    className="btn-secondary"
                    onClick={loadHealthData}
                    disabled={loading}
                >
                    {loading ? "Refreshing..." : "Refresh Status"}
                </button>
                )}
            </header>

            {error && <div className="error-box">{error}</div>}

            {loading && !health ? (
                <div className="page-loading">Fetching system metrics...</div>
            ) : (
                <>
                    {health && (
                        <div
                            className="stat-grid"
                            style={{ marginBottom: "24px" }}
                        >
                            <div className="stat-card">
                                <p className="stat-label">Gateway Status</p>
                                <div
                                    style={{
                                        display: "flex",
                                        alignItems: "center",
                                        gap: "10px",
                                        marginTop: "10px",
                                    }}
                                >
                                    <span
                                        className={`pill ${
                                            health.status === "UP"
                                                ? "pill-green"
                                                : health.status === "DEGRADED"
                                                  ? "pill-gray"
                                                  : "pill-gray"
                                        }`}
                                        style={{
                                            fontSize: "15px",
                                            fontWeight: "bold",
                                        }}
                                    >
                                        {health.status}
                                    </span>
                                </div>
                            </div>

                            <div className="stat-card">
                                <p className="stat-label">Gateway Uptime</p>
                                <p
                                    className="stat-value"
                                    style={{
                                        fontSize: "20px",
                                        marginTop: "16px",
                                    }}
                                >
                                    {formatUptime(health.uptimeMs)}
                                </p>
                            </div>

                            <div className="stat-card">
                                <p className="stat-label">
                                    Response Time (Self)
                                </p>
                                <p
                                    className="stat-value"
                                    style={{
                                        fontSize: "20px",
                                        marginTop: "16px",
                                    }}
                                >
                                    {health.responseTimeMs} ms
                                </p>
                            </div>
                        </div>
                    )}

                    {health?.memory && (
                        <section
                            className="panel"
                            style={{ marginBottom: "24px" }}
                        >
                            <h3>Gateway Memory Usage (JVM)</h3>
                            <div className="jvm-memory-details">
                                <span>
                                    Used:{" "}
                                    <strong>
                                        {formatBytes(health.memory.usedMemory)}
                                    </strong>
                                </span>
                                <span>
                                    Max Capacity:{" "}
                                    <strong>
                                        {formatBytes(health.memory.maxMemory)}
                                    </strong>
                                </span>
                                <span>
                                    Usage: <strong>{memoryPercent}%</strong>
                                </span>
                            </div>
                            <div
                                style={{
                                    width: "100%",
                                    height: "14px",
                                    background: "var(--border)",
                                    borderRadius: "10px",
                                    overflow: "hidden",
                                }}
                            >
                                <div
                                    style={{
                                        width: `${memoryPercent}%`,
                                        height: "100%",
                                        background:
                                            "linear-gradient(90deg, var(--accent) 0%, #818cf8 100%)",
                                        borderRadius: "10px",
                                        transition: "width 0.4s ease",
                                    }}
                                />
                            </div>
                        </section>
                    )}

                    <div className="gateway-health-grid">
                        {health?.dependencies && (
                            <section className="panel" style={{ margin: 0 }}>
                                <h3>Gateway Internal Components</h3>
                                <div
                                    style={{
                                        display: "flex",
                                        flexDirection: "column",
                                        gap: "12px",
                                    }}
                                >
                                    {Object.entries(health.dependencies).map(
                                        ([name, status]) => (
                                            <div
                                                key={name}
                                                style={{
                                                    display: "flex",
                                                    justifyContent:
                                                        "space-between",
                                                    alignItems: "center",
                                                    padding: "10px 14px",
                                                    background:
                                                        "rgba(255,255,255,0.02)",
                                                    border: "1px solid var(--border)",
                                                    borderRadius: "8px",
                                                }}
                                            >
                                                <code
                                                    style={{ fontSize: "13px" }}
                                                >
                                                    {name}
                                                </code>
                                                <span
                                                    className={`pill ${status === "UP" ? "pill-green" : "pill-gray"}`}
                                                >
                                                    {status}
                                                </span>
                                            </div>
                                        ),
                                    )}
                                </div>
                            </section>
                        )}

                        {systemStats && (
                            <section className="panel" style={{ margin: 0 }}>
                                <h3>Global Database Stats</h3>
                                <table style={{ width: "100%" }}>
                                    <tbody>
                                        <tr>
                                            <td
                                                style={{
                                                    border: "none",
                                                    padding: "8px 0",
                                                    color: "var(--muted)",
                                                }}
                                            >
                                                Total Registered Tenants
                                            </td>
                                            <td
                                                style={{
                                                    border: "none",
                                                    padding: "8px 0",
                                                    textAlign: "right",
                                                    fontWeight: "bold",
                                                }}
                                            >
                                                {systemStats.totalTenants}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td
                                                style={{
                                                    border: "none",
                                                    padding: "8px 0",
                                                    color: "var(--muted)",
                                                }}
                                            >
                                                Active Tenants
                                            </td>
                                            <td
                                                style={{
                                                    border: "none",
                                                    padding: "8px 0",
                                                    textAlign: "right",
                                                    fontWeight: "bold",
                                                }}
                                            >
                                                {systemStats.activeTenants}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td
                                                style={{
                                                    border: "none",
                                                    padding: "8px 0",
                                                    color: "var(--muted)",
                                                }}
                                            >
                                                Total Services Linked
                                            </td>
                                            <td
                                                style={{
                                                    border: "none",
                                                    padding: "8px 0",
                                                    textAlign: "right",
                                                    fontWeight: "bold",
                                                }}
                                            >
                                                {systemStats.totalServices}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td
                                                style={{
                                                    border: "none",
                                                    padding: "8px 0",
                                                    color: "var(--muted)",
                                                }}
                                            >
                                                Total Registered Users
                                            </td>
                                            <td
                                                style={{
                                                    border: "none",
                                                    padding: "8px 0",
                                                    textAlign: "right",
                                                    fontWeight: "bold",
                                                }}
                                            >
                                                {systemStats.totalUsers}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td
                                                style={{
                                                    border: "none",
                                                    padding: "8px 0",
                                                    color: "var(--muted)",
                                                }}
                                            >
                                                Total Processed Requests
                                            </td>
                                            <td
                                                style={{
                                                    border: "none",
                                                    padding: "8px 0",
                                                    textAlign: "right",
                                                    fontWeight: "bold",
                                                }}
                                            >
                                                {systemStats.totalRequests}
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </section>
                        )}
                    </div>
                </>
            )}
        </div>
    );
}
