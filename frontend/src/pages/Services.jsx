import { useEffect, useState } from "react";
import { api } from "../api/client";
import { useTenant } from "../context/TenantContext";

const emptyForm = {
    serviceName: "",
    serviceSlug: "",
    routePrefix: "",
    targetUrl: "",
    description: "",
    status: "ACTIVE",
    healthCheckPath: "/health",
    requestTimeoutMs: 5000,
};

export default function Services() {
    const { selectedTenant } = useTenant();
    const [services, setServices] = useState([]);
    const [form, setForm] = useState(emptyForm);
    const [editingId, setEditingId] = useState(null);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    async function loadServices() {
        const data = await api("/api/services");
        setServices(data);
    }

    useEffect(() => {
        loadServices().catch((err) => setError(err.message));
    }, [selectedTenant?.id]);

    function updateForm(field, value) {
        setForm((prev) => {
            const next = { ...prev, [field]: value };
            if (field === "serviceName" && !editingId) {
                next.serviceSlug = value.toLowerCase().trim()
                    .replace(/\s+/g, "-").replace(/[^a-z0-9-]/g, "");
            }
            return next;
        });
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setMessage("");

        try {
            if (editingId) {
                await api(`/api/services/${editingId}`, {
                    method: "PUT",
                    body: JSON.stringify(form),
                });
                setMessage("Service updated");
            } else {
                await api("/api/services", {
                    method: "POST",
                    body: JSON.stringify(form),
                });
                setMessage("Service added");
            }

            setForm(emptyForm);
            setEditingId(null);
            await loadServices();
        } catch (err) {
            setError(err.message);
        }
    }

    function startEdit(service) {
        setEditingId(service.id);
        setForm({
            serviceName: service.serviceName,
            serviceSlug: service.serviceSlug,
            routePrefix: service.routePrefix,
            targetUrl: service.targetUrl,
            description: service.description || "",
            status: service.status,
            healthCheckPath: service.healthCheckPath || "/health",
            requestTimeoutMs: service.requestTimeoutMs || 5000,
        });
        setMessage("");
        setError("");
    }

    function cancelEdit() {
        setEditingId(null);
        setForm(emptyForm);
    }

    async function toggleStatus(service) {
        const nextStatus = service.status === "ACTIVE" ? "DISABLED" : "ACTIVE";

        try {
            await api(`/api/services/${service.id}`, {
                method: "PUT",
                body: JSON.stringify({
                    serviceName: service.serviceName,
                    serviceSlug: service.serviceSlug,
                    routePrefix: service.routePrefix,
                    targetUrl: service.targetUrl,
                    description: service.description,
                    status: nextStatus,
                    healthCheckPath: service.healthCheckPath,
                    requestTimeoutMs: service.requestTimeoutMs,
                }),
            });
            await loadServices();
        } catch (err) {
            setError(err.message);
        }
    }

    async function deleteService(id) {
        if (!window.confirm("Delete this service?")) return;

        try {
            await api(`/api/services/${id}`, { method: "DELETE" });
            setMessage("Service deleted");
            await loadServices();
        } catch (err) {
            setError(err.message);
        }
    }

    async function checkServiceHealth(service) {
        setError("");
        setMessage(`Checking health for ${service.serviceName}...`);

        try {
            const result = await api(`/api/health-logs/check?serviceId=${service.id}`, {method: "POST",},);
            if (result.healthStatus === "UP") {
                setMessage(`Service ${service.serviceName} is UP. Response time: ${result.responseTimeMs}ms.`,);
            } else {
                setError(`Service ${service.serviceName} is ${result.healthStatus}. Response time: ${result.responseTimeMs}ms.`,);
            }
        } catch (err) {
            setError(`Failed to check service health: ${err.message}`);
        }
    }

    return (
        <div className="page">
            <header className="page-header">
                <h2>Service Management</h2>
                <p className="muted">Routes and backends for {selectedTenant?.tenantName}</p>
            </header>

            {error && <div className="error-box">{error}</div>}
            {message && <div className="success-box">{message}</div>}

            <section className="panel">
                <h3>{editingId ? "Edit Service" : "Add Service"}</h3>
                <form className="form-grid" onSubmit={handleSubmit}>
                    <label>
                        Service Name
                        <input
                            value={form.serviceName}
                            onChange={(e) =>
                                updateForm("serviceName", e.target.value)
                            }
                            placeholder="user-service"
                            required
                        />
                    </label>

                    <label>
                        Route Prefix
                        <input
                            value={form.routePrefix}
                            onChange={(e) =>
                                updateForm("routePrefix", e.target.value)
                            }
                            placeholder="/api/users"
                            required
                        />
                    </label>

                    <label>
                        Target URL
                        <input
                            value={form.targetUrl}
                            onChange={(e) =>
                                updateForm("targetUrl", e.target.value)
                            }
                            placeholder="http://localhost:8081"
                            required
                        />
                    </label>

                    <label>
                        Status
                        <select
                            value={form.status}
                            onChange={(e) =>
                                updateForm("status", e.target.value)
                            }
                        >
                            <option value="ACTIVE">Active</option>
                            <option value="DISABLED">Disabled</option>
                            <option value="MAINTENANCE">Maintenance</option>
                        </select>
                    </label>

                    <div className="form-actions">
                        <button type="submit" className="btn-primary">
                            {editingId ? "Update Service" : "Add Service"}
                        </button>
                        {editingId && (
                            <button
                                type="button"
                                className="btn-secondary"
                                onClick={cancelEdit}
                            >
                                Cancel
                            </button>
                        )}
                    </div>
                </form>
            </section>

            <section className="panel">
                <h3>Registered Services</h3>
                <div className="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>Service</th>
                                <th>Route</th>
                                <th>URL</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {services.map((service) => (
                                <tr key={service.id}>
                                    <td>{service.serviceName}</td>
                                    <td>{service.routePrefix}</td>
                                    <td>{service.targetUrl}</td>
                                    <td>
                                        <span className={`pill ${service.status === "ACTIVE" ? "pill-green" : "pill-gray"}`}>
                                            {service.status}
                                        </span>
                                    </td>
                                    <td className="actions">
                                        <button
                                            type="button"
                                            className="btn-link"
                                            onClick={() => checkServiceHealth(service)}
                                        >
                                            Check Health
                                        </button>
                                        <button
                                            type="button"
                                            className="btn-link"
                                            onClick={() => startEdit(service)}
                                        >
                                            Edit
                                        </button>
                                        <button
                                            type="button"
                                            className="btn-link"
                                            onClick={() => toggleStatus(service)}
                                        >
                                            {service.status === "ACTIVE"
                                                ? "Disable"
                                                : "Enable"}
                                        </button>
                                        <button
                                            type="button"
                                            className="btn-link danger"
                                            onClick={() => deleteService(service.id)}
                                        >
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    );
}
