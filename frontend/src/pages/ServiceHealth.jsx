import { useEffect, useState } from "react";
import { api } from "../api/client";
import { useTenant } from "../context/TenantContext";
import Pagination from "../components/Pagination";
import { useAuth } from "../context/AuthContext";

export default function ServiceHealth() {
    const { selectedTenant } = useTenant();
    const { user } = useAuth();
    const isDeveloper = user?.role === "DEVELOPER";
    const [services, setServices] = useState([]);
    const [healthLogs, setHealthLogs] = useState({
        logs: [],
        page: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0,
    });
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");
    const [checkingAll, setCheckingAll] = useState(false);
    const [checkingServiceId, setCheckingServiceId] = useState(null);

    async function loadData() {
        setError("");
        try {
            const [serviceList, logList] = await Promise.all([
                api("/api/services"),
                api("/api/health-logs?page=0&size=10"),
            ]);
            setServices(serviceList);
            setHealthLogs(logList);
        } catch (err) {
            setError(err.message);
        }
    }

    useEffect(() => {
        if (selectedTenant?.id) {
            loadData();
        }
    }, [selectedTenant?.id]);

    async function loadHealthPage(page) {
        try {
            const data = await api(`/api/health-logs?page=${page}&size=10`);
            setHealthLogs(data);
        } catch (err) {
            setError(err.message);
        }
    }

    async function triggerAllChecks() {
        setCheckingAll(true);
        setError("");
        setMessage("Triggering health check on all services...");
        try {
            await api("/api/health-logs/check-all", { method: "POST" });
            setMessage("Completed health checks for all services");
            await loadHealthPage(0);
        } catch (err) {
            setError(err.message);
        } finally {
            setCheckingAll(false);
        }
    }

    async function checkSingleHealth(service) {
        setCheckingServiceId(service.id);
        setError("");
        setMessage(`Checking health for ${service.serviceName}...`);
        try {
            const result = await api(`/api/health-logs/check?serviceId=${service.id}`, { method: "POST" }, );
            if (result.healthStatus === "UP") {
                setMessage(`Service ${service.serviceName} is UP. Response time: ${result.responseTimeMs}ms.`, );
            } else {
                setError(`Service ${service.serviceName} is ${result.healthStatus}. Response time: ${result.responseTimeMs}ms.`, );
            }
            await loadHealthPage(0);
        } catch (err) {
            setError(`Health check failed for ${service.serviceName}: ${err.message}`, );
        } finally {
            setCheckingServiceId(null);
        }
    }

    return (
        <div className="page">
            <header className="page-header page-header-responsive">
                <div>
                    <h2>Service Health Dashboard</h2>
                    <p className="muted">
                        Monitor status, execute tests, and analyze logs for all
                        backend services
                    </p>
                </div>
                {!isDeveloper && (
                <button
                    type="button"
                    className="btn-primary"
                    onClick={triggerAllChecks}
                    disabled={checkingAll || services.length === 0}
                >
                    {checkingAll ? "Checking all..." : "Test All Services"}
                </button>
                )}
            </header>

            {error && <div className="error-box">{error}</div>}
            {message && <div className="success-box">{message}</div>}

            <section className="panel">
                <h3>Service Status List</h3>
                <div className="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>Service Name</th>
                                <th>Target URL</th>
                                <th>Health Path</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {services.length === 0 ? (
                                <tr>
                                    <td colSpan="4" className="muted">
                                        No services registered for this tenant.
                                    </td>
                                </tr>
                            ) : (
                                services.map((service) => (
                                    <tr key={service.id}>
                                        <td>
                                            <strong>
                                                {service.serviceName}
                                            </strong>
                                        </td>
                                        <td>
                                            <code>{service.targetUrl}</code>
                                        </td>
                                        <td>
                                            <code>
                                                {service.healthCheckPath}
                                            </code>
                                        </td>
                                        <td>
                                            {isDeveloper ? (
                                                <span className="muted" style={{ fontSize: "12.5px", fontStyle: "italic" }}>Disabled in Demo Mode</span>
                                            ) : (
                                                <button
                                                    type="button"
                                                    className="btn-link"
                                                    onClick={() =>
                                                        checkSingleHealth(service)
                                                    }
                                                    disabled={
                                                        checkingServiceId ===
                                                        service.id
                                                    }
                                                >
                                                    {checkingServiceId === service.id ? "Checking..." : "Check Status"}
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </section>

            <section className="panel">
                <h3>Service Health History — {selectedTenant?.tenantName}</h3>
                <div className="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>Service</th>
                                <th>Status</th>
                                <th>Response Time</th>
                                <th>Checked At</th>
                            </tr>
                        </thead>
                        <tbody>
                            {healthLogs.logs.length === 0 ? (
                                <tr>
                                    <td colSpan="4" className="muted">
                                        No health logs recorded yet. Try running
                                        a test above.
                                    </td>
                                </tr>
                            ) : (
                                healthLogs.logs.map((log) => (
                                    <tr key={log.id}>
                                        <td>
                                            {log.serviceName || `Service (ID: ${log.serviceId})`}
                                        </td>
                                        <td>
                                            <span className={`pill ${log.healthStatus === "UP" ? "pill-green" : "pill-gray"}`}>
                                                {log.healthStatus}
                                            </span>
                                        </td>
                                        <td>{log.responseTimeMs ?? "-"} ms</td>
                                        <td>
                                            {log.checkedAt ? new Date(log.checkedAt,).toLocaleString() : "-"}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>

                <Pagination
                    page={healthLogs.page}
                    totalPages={healthLogs.totalPages}
                    totalElements={healthLogs.totalElements}
                    size={healthLogs.size}
                    onPageChange={loadHealthPage}
                />
            </section>
        </div>
    );
}
