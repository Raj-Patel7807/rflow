import { createContext, useContext, useEffect, useState } from "react";
import { api } from "../api/client";
import { useAuth } from "./AuthContext";

const TenantContext = createContext(null);

export function TenantProvider({ children }) {
    const { isSuperAdmin } = useAuth();
    const [tenants, setTenants] = useState([]);
    const [selectedTenant, setSelectedTenant] = useState(null);
    const [loading, setLoading] = useState(true);

    async function loadTenants() {
        const data = await api("/api/tenant/all");
        setTenants(data);
        return data;
    }

    async function loadSelected() {
        const data = await api("/api/tenant/selected");
        setSelectedTenant(data || null);
        return data;
    }

    useEffect(() => {
        if (!isSuperAdmin) {
            setLoading(false);
            return;
        }

        Promise.all([loadTenants(), loadSelected()])
            .catch(() => {})
            .finally(() => setLoading(false));
    }, [isSuperAdmin]);

    async function selectTenant(tenantId) {
        if (!tenantId) {
            await api("/api/tenant/clear", { method: "POST" });
            setSelectedTenant(null);
            return;
        }

        const tenant = await api("/api/tenant/select", {
            method: "POST",
            body: JSON.stringify({ tenantId: Number(tenantId) }),
        });
        setSelectedTenant(tenant);
        return tenant;
    }

    async function refreshTenants() {
        await loadTenants();
        await loadSelected();
    }

    return (
        <TenantContext.Provider
            value={{
                tenants,
                selectedTenant,
                loading,
                selectTenant,
                refreshTenants,
                loadTenants,
            }}
        >
            {children}
        </TenantContext.Provider>
    );
}

export function useTenant() {
    return useContext(TenantContext);
}
