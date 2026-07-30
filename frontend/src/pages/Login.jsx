import { useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Login() {
    const { login, user, loading } = useAuth();
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    if (!loading && user) { return <Navigate to="/" replace />; }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setSubmitting(true);

        try {
            await login(email, password);
            navigate("/");
        } catch (err) {
            setError(err.message);
        } finally {
            setSubmitting(false);
        }
    }

    const fillDemoCredentials = () => {
        setEmail("demo_user@rflow.com");
        setPassword("DemoUser@7807");
    };

    return (
        <div className="login-page">
            <div >
                <form className="login-card" onSubmit={handleSubmit}>
                    <div className="login-logo-container">
                        <img
                            src="/rflow-favicon.png"
                            alt="RFlow Logo"
                            className="login-logo"
                        />
                        <h2 className="login-brand-name">RFlow</h2>
                    </div>
                    <p className="muted">
                        Only SUPER_ADMIN can access this portal
                    </p>

                    {error && <div className="error-box">{error}</div>}

                    <label>
                        Email
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="user@example.com"
                            required
                        />
                    </label>

                    <label>
                        Password
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Your password"
                            required
                        />
                    </label>

                    <button
                        type="submit"
                        className="btn-primary"
                        disabled={submitting}
                        style={{ width: "100%" }}
                    >
                        {submitting ? "Logging in..." : "Login"}
                    </button>
                </form>

                <div className="demo-account-section">
                    <div className="demo-account-header">
                        <strong className="demo-account-title">Demo Account</strong>
                        <button 
                            type="button" 
                            onClick={fillDemoCredentials}
                            className="demo-account-quickfill"
                        >
                            Quick Fill
                        </button>
                    </div>
                    <div 
                        onClick={fillDemoCredentials}
                        className="demo-account-card"
                        title="Click to auto-fill credentials"
                    >
                        <div className="demo-account-field">Email: <code>demo_user@rflow.com</code></div>
                        <div className="demo-account-field">Password: <code>DemoUser@7807</code></div>
                        <div className="demo-account-footer">Read-only access. Changes are disabled.</div>
                    </div>
                </div>
            </div>
        </div>
    );
}
