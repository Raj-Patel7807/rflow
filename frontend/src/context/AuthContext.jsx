import { createContext, useContext, useEffect, useState } from "react";
import { api } from "../api/client";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api("/api/auth/me").then((data) => {
                if (data.role !== "SUPER_ADMIN" && data.role !== "TENANT_ADMIN" && data.role !== "DEVELOPER") { setUser(null); return; }
                setUser(data);
            }).catch(() => setUser(null))
            .finally(() => setLoading(false));
    }, []);

    async function login(email, password) {
        const data = await api("/api/auth/login", {
            method: "POST",
            body: JSON.stringify({ email, password }),
        });

        if (data.role !== "SUPER_ADMIN" && data.role !== "TENANT_ADMIN" && data.role !== "DEVELOPER") {
            await api("/api/auth/logout", { method: "POST" }).catch(() => {});
            throw new Error("Only authorized roles can access this portal");
        }
        setUser(data);
        return data;
    }

    async function logout() {
        await api("/api/auth/logout", { method: "POST" });
        setUser(null);
    }

    const isSuperAdmin = user?.role === "SUPER_ADMIN";

    return (
        <AuthContext.Provider value={{ user, loading, login, logout, isSuperAdmin }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
