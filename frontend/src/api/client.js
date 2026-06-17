const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "";

export async function api(path, options = {}) {
    const headers = { ...options.headers };

    if (options.body && !headers["Content-Type"]) {
        headers["Content-Type"] = "application/json";
    }

    const url = path.startsWith("http") ? path : `${API_BASE_URL}${path}`;

    const response = await fetch(url, {
        ...options,
        credentials: "include",
        headers,
    });

    const text = await response.text();
    let data = null;

    if (text) {
        try {
            data = JSON.parse(text);
        } catch {
            data = text;
        }
    }

    if (!response.ok) {
        throw new Error(data?.error || "Request failed");
    }

    return data;
}

export async function gatewayRequest(tenantSlug, path, options = {}) {
    const cleanPath = path.startsWith("/") ? path : `/${path}`;
    
    // If API_BASE_URL is specified (direct connection mode), bypass the Vite dev proxy path (/gateway-test)
    // and hit the backend's tenant endpoint directly: e.g. http://localhost:8080/tenantSlug/path
    const url = API_BASE_URL
        ? `${API_BASE_URL}/${tenantSlug}${cleanPath}`
        : `/gateway-test/${tenantSlug}${cleanPath}`;

    const start = performance.now();

    const headers = { ...options.headers };

    if (options.body && !headers["Content-Type"]) {
        headers["Content-Type"] = "application/json";
    }

    const response = await fetch(url, {
        ...options,
        headers,
    });

    const elapsed = Math.round(performance.now() - start);
    const text = await response.text();

    let body = text;
    try {
        body = JSON.stringify(JSON.parse(text), null, 2);
    } catch {
        body = text;
    }

    return {
        status: response.status,
        body,
        responseTimeMs: elapsed,
    };
}
