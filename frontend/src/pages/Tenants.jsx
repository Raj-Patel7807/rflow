import { useEffect, useState } from "react";
import { api } from "../api/client";
import { useTenant } from "../context/TenantContext";

const emptyForm = {
    tenantName: "",
    tenantSlug: "",
    ownerEmail: "",
    ownerName: "",
    ownerPassword: "",
    status: "ACTIVE",
};

export default function Tenants() {
    const { tenants, refreshTenants, selectTenant } = useTenant();
    const [form, setForm] = useState(emptyForm);
    const [editingId, setEditingId] = useState(null);
    const [editForm, setEditForm] = useState({
        tenantName: "",
        ownerEmail: "",
        status: "ACTIVE",
    });
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    useEffect(() => {
        refreshTenants().catch((err) => setError(err.message));
    }, []);

    function updateForm(field, value) {
        setForm((prev) => {
            const next = { ...prev, [field]: value };

            if (field === "tenantName" && !editingId) {
                next.tenantSlug = value.toLowerCase().trim()
                    .replace(/\s+/g, "-").replace(/[^a-z0-9-]/g, "");
            }
            return next;
        });
    }

    async function handleCreate(event) {
        event.preventDefault();
        setError("");
        setMessage("");

        try {
            await api("/api/tenant", {
                method: "POST",
                body: JSON.stringify(form),
            });
            setMessage("Tenant created");
            setForm(emptyForm);
            await refreshTenants();
        } catch (err) {
            setError(err.message);
        }
    }

    function startEdit(tenant) {
        setEditingId(tenant.id);
        setEditForm({
            tenantName: tenant.tenantName,
            ownerEmail: tenant.ownerEmail,
            status: tenant.status,
        });
    }

    async function handleUpdate(event) {
        event.preventDefault();

        try {
            await api(`/api/tenant/${editingId}`, {
                method: "PUT",
                body: JSON.stringify(editForm),
            });
            setMessage("Tenant updated");
            setEditingId(null);
            await refreshTenants();
        } catch (err) {
            setError(err.message);
        }
    }

    async function deleteTenant(id) {
        if (!window.confirm("Delete this tenant and all its data?")) return;
        try {
            await api(`/api/tenant/${id}`, { method: "DELETE" });
            setMessage("Tenant deleted");
            await refreshTenants();
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleSelect(tenant) {
        await selectTenant(tenant.id);
        setMessage(`Switched to ${tenant.tenantName}`);
    }

    return (
        <div className="page">
            <header className="page-header">
                <h2>Tenants</h2>
                <p className="muted">
                    Create and manage all tenants in the system
                </p>
            </header>

            {error && <div className="error-box">{error}</div>}
            {message && <div className="success-box">{message}</div>}

            <section className="panel">
                <h3>Create Tenant</h3>
                <form className="form-grid" onSubmit={handleCreate}>
                    <label>
                        Tenant Name
                        <input
                            value={form.tenantName}
                            onChange={(e) =>
                                updateForm("tenantName", e.target.value)
                            }
                            placeholder="Acme Corp"
                            required
                        />
                    </label>

                    <label>
                        Tenant Slug
                        <input
                            value={form.tenantSlug}
                            onChange={(e) =>
                                setForm({ ...form, tenantSlug: e.target.value })
                            }
                            placeholder="acme"
                            required
                        />
                    </label>

                    <label>
                        Owner Email
                        <input
                            type="email"
                            value={form.ownerEmail}
                            onChange={(e) =>
                                setForm({ ...form, ownerEmail: e.target.value })
                            }
                            placeholder="owner@acme.com"
                            required
                        />
                    </label>

                    <label>
                        Owner Name
                        <input
                            value={form.ownerName || ""}
                            onChange={(e) =>
                                setForm({ ...form, ownerName: e.target.value })
                            }
                            placeholder="John Doe"
                            required
                        />
                    </label>

                    <label>
                        Owner Password
                        <input
                            type="password"
                            value={form.ownerPassword || ""}
                            onChange={(e) =>
                                setForm({
                                    ...form,
                                    ownerPassword: e.target.value,
                                })
                            }
                            placeholder="Choose password"
                            required
                        />
                    </label>

                    <label>
                        Status
                        <select
                            value={form.status}
                            onChange={(e) =>
                                setForm({ ...form, status: e.target.value })
                            }
                        >
                            <option value="ACTIVE">Active</option>
                            <option value="DISABLED">Disabled</option>
                            <option value="SUSPENDED">Suspended</option>
                        </select>
                    </label>

                    <div className="form-actions">
                        <button type="submit" className="btn-primary">
                            Create Tenant
                        </button>
                    </div>
                </form>
            </section>

            <section className="panel">
                <h3>All Tenants</h3>
                <div className="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Slug</th>
                                <th>Owner</th>
                                <th>Status</th>
                                <th>Gateway Path</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {tenants.map((tenant) => (
                                <tr key={tenant.id}>
                                    {editingId === tenant.id ? (
                                        <>
                                            <td>
                                                <input
                                                    value={editForm.tenantName}
                                                    onChange={(e) =>
                                                        setEditForm({
                                                            ...editForm,
                                                            tenantName:
                                                                e.target.value,
                                                        })
                                                    }
                                                />
                                            </td>
                                            <td>
                                                <code>
                                                    /{tenant.tenantSlug}
                                                </code>
                                            </td>
                                            <td>
                                                <input
                                                    value={editForm.ownerEmail}
                                                    onChange={(e) =>
                                                        setEditForm({
                                                            ...editForm,
                                                            ownerEmail:
                                                                e.target.value,
                                                        })
                                                    }
                                                />
                                            </td>
                                            <td>
                                                <select
                                                    value={editForm.status}
                                                    onChange={(e) =>
                                                        setEditForm({
                                                            ...editForm,
                                                            status: e.target
                                                                .value,
                                                        })
                                                    }
                                                >
                                                    <option value="ACTIVE">
                                                        Active
                                                    </option>
                                                    <option value="DISABLED">
                                                        Disabled
                                                    </option>
                                                    <option value="SUSPENDED">
                                                        Suspended
                                                    </option>
                                                </select>
                                            </td>
                                            <td>
                                                <code>
                                                    /{tenant.tenantSlug}/*
                                                </code>
                                            </td>
                                            <td className="actions">
                                                <button
                                                    type="button"
                                                    className="btn-link"
                                                    onClick={handleUpdate}
                                                >
                                                    Save
                                                </button>
                                                <button
                                                    type="button"
                                                    className="btn-link"
                                                    onClick={() => setEditingId(null)}
                                                >
                                                    Cancel
                                                </button>
                                            </td>
                                        </>
                                    ) : (
                                        <>
                                            <td>{tenant.tenantName}</td>
                                            <td>
                                                <code>
                                                    /{tenant.tenantSlug}
                                                </code>
                                            </td>
                                            <td>{tenant.ownerEmail}</td>
                                            <td>
                                                <span
                                                    className={`pill ${tenant.status === "ACTIVE" ? "pill-green" : "pill-gray"}`}
                                                >
                                                    {tenant.status}
                                                </span>
                                            </td>
                                            <td>
                                                <code>
                                                    /{tenant.tenantSlug}/*
                                                </code>
                                            </td>
                                            <td className="actions">
                                                <button
                                                    type="button"
                                                    className="btn-link"
                                                    onClick={() => handleSelect(tenant)}
                                                >
                                                    Select
                                                </button>
                                                <button
                                                    type="button"
                                                    className="btn-link"
                                                    onClick={() => startEdit(tenant)}
                                                >
                                                    Edit
                                                </button>
                                                <button
                                                    type="button"
                                                    className="btn-link danger"
                                                    onClick={() => deleteTenant(tenant.id)}
                                                >
                                                    Delete
                                                </button>
                                            </td>
                                        </>
                                    )}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    );
}
