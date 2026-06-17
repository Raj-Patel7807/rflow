import { useTenant } from "../context/TenantContext";

export default function TenantRequired({ children }) {
    const { tenants, selectedTenant, selectTenant, loading } = useTenant();

    if (loading) {
        return <div className="page-loading">Loading tenant...</div>;
    }

    if (!selectedTenant) {
        return (
            <div className="page">
                <div className="select-tenant-placeholder">
                    <h3>Select a Tenant</h3>
                    <p>
                        Choose an active tenant to manage its services, rate
                        limits, users, and logs.
                    </p>
                    <select
                        value=""
                        onChange={(e) => selectTenant(e.target.value || null)}
                    >
                        <option value="">-- Select Active Tenant --</option>
                        {tenants.map((tenant) => (
                            <option key={tenant.id} value={tenant.id}>
                                {tenant.tenantName} (/{tenant.tenantSlug})
                            </option>
                        ))}
                    </select>
                </div>
            </div>
        );
    }

    return children;
}
