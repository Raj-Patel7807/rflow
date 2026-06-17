import { useEffect, useState } from "react";
import { api } from "../api/client";
import { useTenant } from "../context/TenantContext";
import Pagination from "../components/Pagination";
import { truncate } from "../utils/format";

export default function Logs() {
    const { selectedTenant } = useTenant();
    const [data, setData] = useState({
        logs: [],
        page: 0,
        size: 20,
        totalElements: 0,
        totalPages: 0,
    });
    const [method, setMethod] = useState("");
    const [status, setStatus] = useState("");
    const [page, setPage] = useState(0);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);

    const pageSize = 20;

    async function loadLogs(nextPage = page) {
        setLoading(true);
        setError("");

        const params = new URLSearchParams({
            page: String(nextPage),
            size: String(pageSize),
        });

        if (method) params.set("method", method);
        if (status) params.set("status", status);

        try {
            const result = await api(`/api/logs?${params.toString()}`);
            setData(result);
            setPage(nextPage);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadLogs(0);
    }, [method, status, selectedTenant?.id]);

    function handlePageChange(nextPage) {
        loadLogs(nextPage);
    }

    return (
        <div className="page page-wide">
            <header className="page-header">
                <h2>Request Logs</h2>
                <p className="muted">
                    Traffic history for {selectedTenant?.tenantName}
                </p>
            </header>

            <section className="panel filters">
                <label>
                    Method
                    <select
                        value={method}
                        onChange={(e) => setMethod(e.target.value)}
                    >
                        <option value="">All</option>
                        <option value="GET">GET</option>
                        <option value="POST">POST</option>
                        <option value="PUT">PUT</option>
                        <option value="DELETE">DELETE</option>
                    </select>
                </label>

                <label>
                    Status Code
                    <input
                        type="number"
                        value={status}
                        onChange={(e) => setStatus(e.target.value)}
                        placeholder="e.g. 200"
                    />
                </label>

                <button
                    type="button"
                    className="btn-secondary"
                    onClick={() => loadLogs(page)}
                >
                    Refresh
                </button>
            </section>

            {error && <div className="error-box">{error}</div>}

            <section className="panel">
                {loading ? (
                    <p className="muted">Loading logs...</p>
                ) : data.logs.length === 0 ? (
                    <p className="muted">No logs found.</p>
                ) : (
                    <>
                        <div className="table-wrap">
                            <table className="compact-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Timestamp</th>
                                        <th>Method</th>
                                        <th>Route</th>
                                        <th>Query</th>
                                        <th>Service</th>
                                        <th>Client IP</th>
                                        <th>Status</th>
                                        <th>Time</th>
                                        <th>User Agent</th>
                                        <th>Error</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {data.logs.map((log) => (
                                        <tr key={log.id}>
                                            <td>{log.id}</td>
                                            <td className="nowrap">
                                                {log.createdAt
                                                    ? new Date(
                                                          log.createdAt,
                                                      ).toLocaleString()
                                                    : "-"}
                                            </td>
                                            <td>{log.requestMethod}</td>
                                            <td title={log.requestPath}>
                                                {truncate(log.requestPath, 30)}
                                            </td>
                                            <td title={log.queryString}>
                                                {truncate(log.queryString, 20)}
                                            </td>
                                            <td>
                                                {log.serviceName ||
                                                    log.serviceId ||
                                                    "-"}
                                            </td>
                                            <td>{log.clientIp || "-"}</td>
                                            <td>
                                                <span
                                                    className={`status-badge status-${Math.floor(log.responseStatus / 100)}xx`}
                                                >
                                                    {log.responseStatus}
                                                </span>
                                            </td>
                                            <td>{log.responseTimeMs} ms</td>
                                            <td title={log.userAgent}>
                                                {truncate(log.userAgent, 28)}
                                            </td>
                                            <td title={log.errorMessage}>
                                                {truncate(log.errorMessage, 24)}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        <Pagination
                            page={data.page}
                            totalPages={data.totalPages}
                            totalElements={data.totalElements}
                            size={data.size}
                            onPageChange={handlePageChange}
                        />
                    </>
                )}
            </section>
        </div>
    );
}
