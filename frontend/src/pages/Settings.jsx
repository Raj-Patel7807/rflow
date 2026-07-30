import { useEffect, useState } from "react";
import { api } from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function Settings() {
    const { user } = useAuth();
    const isDeveloper = user?.role === "DEVELOPER";
    const [configs, setConfigs] = useState([]);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    async function loadConfigs() {
        const data = await api("/api/gateway-configs");
        setConfigs(data);
    }

    useEffect(() => {
        loadConfigs().catch((err) => setError(err.message));
    }, []);

    async function updateConfig(key, value) {
        setError("");
        setMessage("");

        try {
            await api(`/api/gateway-configs/${key}`, {
                method: "PUT",
                body: JSON.stringify({ configValue: value }),
            });
            setMessage("Config updated");
            await loadConfigs();
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div className="page">
            <header className="page-header">
                <h2>Settings</h2>
                <p className="muted">Global gateway configuration settings</p>
            </header>

            {error && <div className="error-box">{error}</div>}
            {message && <div className="success-box">{message}</div>}

            <section className="panel">
                <h3>Gateway Configuration</h3>
                <div className="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>Key</th>
                                <th>Value</th>
                                <th>Description</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {configs.map((config) => (
                                <ConfigRow
                                    key={config.id}
                                    config={config}
                                    onSave={updateConfig}
                                    isDeveloper={isDeveloper}
                                />
                            ))}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    );
}

function ConfigRow({ config, onSave, isDeveloper }) {
    const [value, setValue] = useState(config.configValue);

    return (
        <tr>
            <td>
                <code>{config.configKey}</code>
            </td>
            <td>
                <input
                    value={value}
                    onChange={(e) => setValue(e.target.value)}
                    disabled={isDeveloper}
                />
            </td>
            <td>{config.description}</td>
            <td>
                {isDeveloper ? (
                    <span className="muted" style={{ fontSize: "12.5px", fontStyle: "italic" }}>Read-only</span>
                ) : (
                    <button
                        type="button"
                        className="btn-link"
                        onClick={() => onSave(config.configKey, value)}
                    >
                        Save
                    </button>
                )}
            </td>
        </tr>
    );
}
