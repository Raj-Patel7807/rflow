import { useState } from "react";
import { gatewayRequest } from "../api/client";
import { useTenant } from "../context/TenantContext";

export default function ApiTester() {
    const { selectedTenant } = useTenant();
    const [method, setMethod] = useState("GET");
    const [url, setUrl] = useState("/api/users");
    const [headersText, setHeadersText] = useState("");
    const [body, setBody] = useState("");
    const [response, setResponse] = useState(null);
    const [error, setError] = useState("");
    const [sending, setSending] = useState(false);

    async function handleSend(event) {
        event.preventDefault();
        setError("");
        setResponse(null);
        setSending(true);

        try {
            const headers = {};

            headersText
                .split("\n")
                .map((line) => line.trim())
                .filter(Boolean)
                .forEach((line) => {
                    const index = line.indexOf(":");
                    if (index > 0) {
                        headers[line.slice(0, index).trim()] = line
                            .slice(index + 1)
                            .trim();
                    }
                });

            const options = { method, headers };

            if (method !== "GET" && method !== "DELETE" && body.trim()) { options.body = body; }

            const result = await gatewayRequest(
                selectedTenant.tenantSlug,
                url,
                options,
            );
            setResponse(result);
        } catch (err) {
            setError(err.message);
        } finally {
            setSending(false);
        }
    }

    const fullPath = `/${selectedTenant?.tenantSlug || "tenant"}${url.startsWith("/") ? url : `/${url}`}`;

    return (
        <div className="page">
            <header className="page-header">
                <h2>API Tester</h2>
                <p className="muted">
                    Send requests through <code>{fullPath}</code> for{" "}
                    {selectedTenant?.tenantName}
                </p>
            </header>

            {error && <div className="error-box">{error}</div>}

            <form className="panel tester-form" onSubmit={handleSend}>
                <div className="tester-row">
                    <select
                        value={method}
                        onChange={(e) => setMethod(e.target.value)}
                    >
                        <option>GET</option>
                        <option>POST</option>
                        <option>PUT</option>
                        <option>DELETE</option>
                    </select>

                    <input
                        value={url}
                        onChange={(e) => setUrl(e.target.value)}
                        placeholder="/api/users"
                        required
                    />

                    <button
                        type="submit"
                        className="btn-primary"
                        disabled={sending}
                    >
                        {sending ? "Sending..." : "Send"}
                    </button>
                </div>

                <label>
                    Headers (optional, one per line: Key: Value)
                    <textarea
                        rows={3}
                        value={headersText}
                        onChange={(e) => setHeadersText(e.target.value)}
                        placeholder="Content-Type: application/json"
                    />
                </label>

                {method !== "GET" && method !== "DELETE" && (
                    <label>
                        Request Body (JSON)
                        <textarea
                            rows={8}
                            value={body}
                            onChange={(e) => setBody(e.target.value)}
                            placeholder='{"name": "John"}'
                        />
                    </label>
                )}
            </form>

            {response && (
                <section className="panel">
                    <h3>Response</h3>
                    <div className="response-meta">
                        <span>Status: {response.status}</span>
                        <span>Time: {response.responseTimeMs} ms</span>
                    </div>
                    <pre className="code-block">
                        {response.body || "(empty body)"}
                    </pre>
                </section>
            )}
        </div>
    );
}
