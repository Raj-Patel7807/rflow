import { useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Login() {
    const { login, user, isSuperAdmin, loading } = useAuth();
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    if (!loading && user && isSuperAdmin) { return <Navigate to="/" replace />; }

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

    return (
        <div className="login-page">
            <form className="login-card" onSubmit={handleSubmit}>
                <div className="login-logo-container">
                    <img
                        src="/rflow-favicon.png"
                        alt="RFlow Logo"
                        className="login-logo"
                    />
                    <h2 className="login-brand-name">RFlow</h2>
                </div>
                <h1>Super Admin Login</h1>
                <p className="muted">
                    Only SUPER_ADMIN accounts can access this portal
                </p>

                {error && <div className="error-box">{error}</div>}

                <label>
                    Email
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="superadmin@example.com"
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
                >
                    {submitting ? "Logging in..." : "Login"}
                </button>
            </form>
        </div>
    );
}
