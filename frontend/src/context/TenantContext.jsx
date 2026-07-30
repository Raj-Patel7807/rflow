import { createContext, useContext, useEffect, useState } from "react";
import { api } from "../api/client";
import { useAuth } from "./AuthContext";

const TenantContext = createContext(null);

export function TenantProvider({ children }) {
    const { user, isSuperAdmin } = useAuth();
    const [tenants, setTenants] = useState([]);
    const [selectedTenant, setSelectedTenant] = useState(null);
    const [loading, setLoading] = useState(true);

    async function loadTenants() {
        if (!isSuperAdmin) return [];
        const data = await api("/api/tenant/all");
        setTenants(data); return data;
    }

    async function loadSelected() {
        if (!isSuperAdmin) return selectedTenant;
        const data = await api("/api/tenant/selected");
        setSelectedTenant(data || null); return data;
    }

    useEffect(() => {
        if (!user) {
            setSelectedTenant(null);
            setTenants([]);
            setLoading(false);
            return;
        }

        if (!isSuperAdmin) {
            setSelectedTenant({
                id: user.tenantId,
                tenantName: user.tenantName,
                tenantSlug: user.tenantSlug,
                status: "ACTIVE"
            });
            setTenants([]);
            setLoading(false);
            return;
        }

        Promise.all([loadTenants(), loadSelected()])
            .catch(() => {})
            .finally(() => setLoading(false));
    }, [user, isSuperAdmin]);

    async function selectTenant(tenantId) {
        if (!isSuperAdmin) return;
        if (!tenantId) {
            await api("/api/tenant/clear", { method: "POST" });
            setSelectedTenant(null); return;
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
            value={{tenants, selectedTenant, loading, selectTenant, refreshTenants, loadTenants,}}
        >
            {children}
        </TenantContext.Provider>
    );
}

export function useTenant() {
    return useContext(TenantContext);
}
