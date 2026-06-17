import { useEffect, useState } from "react";
import { api } from "../api/client";
import { useTenant } from "../context/TenantContext";

function StatCard({ label, value }) {
    return (
        <div className="stat-card">
            <p className="stat-label">{label}</p>
            <p className="stat-value">{value}</p>
        </div>
    );
}

export default function Dashboard() {
    const { selectedTenant } = useTenant();
    const [systemStats, setSystemStats] = useState(null);
    const [stats, setStats] = useState(null);
    const [recent, setRecent] = useState([]);
    const [chart, setChart] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        setError("");
        setStats(null);
        setRecent([]);
        setChart([]);

        api("/api/dashboard/system").then(setSystemStats)
            .catch((err) => setError(err.message));
    }, []);

    useEffect(() => {
        if (!selectedTenant) return;
        setError("");
        Promise.all([
            api("/api/dashboard/stats"),
            api("/api/dashboard/recent-requests?limit=10"),
            api("/api/dashboard/request-chart"),
        ]).then(([statsData, recentData, chartData]) => {
                setStats(statsData);
                setRecent(recentData);
                setChart(chartData);
            }).catch((err) => setError(err.message));
    }, [selectedTenant]);

    if (error) { return <div className="error-box">{error}</div>; }

    const maxChartValue = Math.max(...chart.map((row) => Number(row[1]) || 0), 1, );

    return (
        <div className="page">
            <header className="page-header">
                <h2>Dashboard</h2>
                <p className="muted">
                    {selectedTenant ? `Overview for ${selectedTenant.tenantName}` : "System-wide overview — select a tenant for details"}
                </p>
            </header>

            {systemStats && (
                <section className="panel">
                    <h3>System Overview</h3>
                    <div className="stat-grid">
                        <StatCard label="Total Tenants" value={systemStats.totalTenants} />
                        <StatCard label="Active Tenants" value={systemStats.activeTenants} />
                        <StatCard label="Total Services" value={systemStats.totalServices} />
                        <StatCard label="Total Users" value={systemStats.totalUsers} />
                        <StatCard label="Total Requests" value={systemStats.totalRequests} />
                    </div>
                </section>
            )}

            {selectedTenant && stats && (
                <>
                    <section className="panel">
                        <h3>{selectedTenant.tenantName} Stats</h3>
                        <div className="stat-grid">
                            <StatCard label="Total Requests" value={stats.totalRequests} />
                            <StatCard label="Blocked Requests" value={stats.blockedRequests} />
                            <StatCard label="Active Services" value={stats.activeServices} />
                            <StatCard label="Avg Response Time" value={`${stats.avgResponseTimeMs} ms`} />
                        </div>
                    </section>

                    <section className="panel">
                        <h3>Recent Requests</h3>
                        {recent.length === 0 ? (
                            <p className="muted">
                                No requests logged yet for this tenant.
                            </p>
                        ) : (
                            <div className="table-wrap">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Method</th>
                                            <th>Route</th>
                                            <th>Status</th>
                                            <th>Time</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {recent.map((log) => (
                                            <tr key={log.id}>
                                                <td>{log.requestMethod}</td>
                                                <td>{log.requestPath}</td>
                                                <td>
                                                    <span
                                                        className={`status-badge status-${Math.floor(log.responseStatus / 100)}xx`}
                                                    >
                                                        {log.responseStatus}
                                                    </span>
                                                </td>
                                                <td>{log.responseTimeMs} ms</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </section>

                    {chart.length > 0 && (
                        <section className="panel">
                            <h3>Requests Over Time</h3>
                            <div className="chart">
                                {chart.map((row) => {
                                    const date = row[0];
                                    const count = Number(row[1]);
                                    const height = `${(count / maxChartValue) * 100}%`;

                                    return (
                                        <div
                                            key={date}
                                            className="chart-bar-wrap"
                                            title={`${date}: ${count} requests`}
                                        >
                                            <div
                                                className="chart-bar"
                                                style={{ height }}
                                            />
                                            <span>{count}</span>
                                        </div>
                                    );
                                })}
                            </div>
                        </section>
                    )}
                </>
            )}
        </div>
    );
}
