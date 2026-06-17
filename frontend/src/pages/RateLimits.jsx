import { useEffect, useState } from "react";
import { api } from "../api/client";
import { useTenant } from "../context/TenantContext";

const emptyForm = {
    serviceId: "",
    requestsLimit: 100,
    windowSeconds: 60,
    strategy: "IP",
};

export default function RateLimits() {
    const { selectedTenant } = useTenant();
    const [services, setServices] = useState([]);
    const [policies, setPolicies] = useState([]);
    const [form, setForm] = useState(emptyForm);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    async function loadData() {
        const [serviceList, policyList] = await Promise.all([
            api("/api/services"),
            api("/api/rate-limits"),
        ]);
        setServices(serviceList);
        setPolicies(policyList);
    }

    useEffect(() => {
        loadData().catch((err) => setError(err.message));
    }, [selectedTenant?.id]);

    function getServiceName(serviceId) {
        return (services.find((s) => s.id === serviceId)?.serviceName || serviceId);
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setMessage("");

        try {
            await api("/api/rate-limits", {
                method: "POST",
                body: JSON.stringify({
                    ...form,
                    serviceId: Number(form.serviceId),
                    requestsLimit: Number(form.requestsLimit),
                    windowSeconds: Number(form.windowSeconds),
                }),
            });
            setMessage("Rate limit policy added");
            setForm(emptyForm);
            await loadData();
        } catch (err) {
            setError(err.message);
        }
    }

    async function togglePolicy(policy) {
        try {
            await api(`/api/rate-limits/${policy.id}`, {
                method: "PUT",
                body: JSON.stringify({
                    requestsLimit: policy.requestsLimit,
                    windowSeconds: policy.windowSeconds,
                    isActive: !policy.isActive,
                }),
            });
            await loadData();
        } catch (err) {
            setError(err.message);
        }
    }

    async function deletePolicy(id) {
        if (!window.confirm("Delete this rate limit policy?")) return;
        try {
            await api(`/api/rate-limits/${id}`, { method: "DELETE" });
            setMessage("Policy deleted");
            await loadData();
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div className="page">
            <header className="page-header">
                <h2>Rate Limits</h2>
                <p className="muted">
                    Request limits for {selectedTenant?.tenantName} services
                </p>
            </header>

            {error && <div className="error-box">{error}</div>}
            {message && <div className="success-box">{message}</div>}

            <section className="panel">
                <h3>Add Policy</h3>
                <form className="form-grid" onSubmit={handleSubmit}>
                    <label>
                        Service
                        <select
                            value={form.serviceId}
                            onChange={(e) =>
                                setForm({ ...form, serviceId: e.target.value })
                            }
                            required
                        >
                            <option value="">Select service</option>
                            {services.map((service) => (
                                <option key={service.id} value={service.id}>
                                    {service.serviceName}
                                </option>
                            ))}
                        </select>
                    </label>

                    <label>
                        Requests Limit
                        <input
                            type="number"
                            value={form.requestsLimit}
                            onChange={(e) =>
                                setForm({
                                    ...form,
                                    requestsLimit: e.target.value,
                                })
                            }
                            required
                        />
                    </label>

                    <label>
                        Window (seconds)
                        <input
                            type="number"
                            value={form.windowSeconds}
                            onChange={(e) =>
                                setForm({
                                    ...form,
                                    windowSeconds: e.target.value,
                                })
                            }
                            required
                        />
                    </label>

                    <label>
                        Strategy
                        <select
                            value={form.strategy}
                            onChange={(e) =>
                                setForm({ ...form, strategy: e.target.value })
                            }
                        >
                            <option value="IP">IP</option>
                            <option value="API_KEY">API Key</option>
                            <option value="USER">User</option>
                        </select>
                    </label>

                    <div className="form-actions">
                        <button type="submit" className="btn-primary">
                            Add Policy
                        </button>
                    </div>
                </form>
            </section>

            <section className="panel">
                <h3>Active Policies</h3>
                <div className="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>Service</th>
                                <th>Limit</th>
                                <th>Window</th>
                                <th>Strategy</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {policies.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="muted">
                                        No rate limit policies yet.
                                    </td>
                                </tr>
                            ) : (
                                policies.map((policy) => (
                                    <tr key={policy.id}>
                                        <td>
                                            {getServiceName(policy.serviceId)}
                                        </td>
                                        <td>{policy.requestsLimit}</td>
                                        <td>{policy.windowSeconds}s</td>
                                        <td>{policy.strategy}</td>
                                        <td>
                                            <span
                                                className={`pill ${policy.isActive ? "pill-green" : "pill-gray"}`}
                                            >
                                                {policy.isActive ? "Active" : "Inactive"}
                                            </span>
                                        </td>
                                        <td className="actions">
                                            <button
                                                type="button"
                                                className="btn-link"
                                                onClick={() =>
                                                    togglePolicy(policy)
                                                }
                                            >
                                                {policy.isActive ? "Disable" : "Enable"}
                                            </button>
                                            <button
                                                type="button"
                                                className="btn-link danger"
                                                onClick={() =>
                                                    deletePolicy(policy.id)
                                                }
                                            >
                                                Delete
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    );
}
