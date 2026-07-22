import { useEffect, useState } from "react";
import "./Loader.css";

export default function Loader({ message = "Connecting to services..." }) {
    const [statusText, setStatusText] = useState(message);
    const [seconds, setSeconds] = useState(0);

    useEffect(() => {
        const timer = setInterval(() => {
            setSeconds((prev) => prev + 1);
        }, 1000);
        return () => clearInterval(timer);
    }, []);

    useEffect(() => {
        // Customize status text for Render cold starts
        if (seconds >= 40) {
            setStatusText("Almost there! The server is taking a little longer than usual to wake up.");
        } else if (seconds >= 25) {
            setStatusText("Render is waking up the server. First requests may take up to a minute.");
        } else if (seconds >= 20) {
            setStatusText("Preparing your workspace...");
        } else if (seconds >= 10) {
            setStatusText("Starting backend services...");
        } else if (seconds >= 4) {
            setStatusText("Connecting to RFlow...");
        } else {
            setStatusText(message);
        }
    }, [seconds, message]);

    return (
        <div className="loader-overlay">
            <div className="loader-container">
                <div className="loader-visual-wrapper">
                    <div className="loader-glow-aura"></div>
                    <div className="loader-gateway">
                        <div className="loader-ring loader-ring-1"></div>
                        <div className="loader-ring loader-ring-2"></div>
                        <div className="loader-ring loader-ring-3"></div>
                        <div className="loader-core">
                            <img src="/rflow-favicon.png" alt="RFlow Logo" className="loader-logo" />
                        </div>
                        {/* Flowing traffic particles */}
                        <div className="loader-traffic-particle p1"></div>
                        <div className="loader-traffic-particle p2"></div>
                        <div className="loader-traffic-particle p3"></div>
                        <div className="loader-traffic-particle p4"></div>
                    </div>
                </div>
                
                <div className="loader-text-wrapper">
                    <h2 className="loader-brand">RFlow</h2>
                    <div className="loader-progress-bar-container">
                        <div className="loader-progress-bar-fill"></div>
                    </div>
                    <div className="loader-status-container">
                        <div className="loader-spinner-mini"></div>
                        <span className="loader-message">{statusText}</span>
                    </div>
                    {seconds > 5 && (
                        <span className="loader-time-elapsed">Elapsed: {seconds}s</span>
                    )}
                </div>
            </div>
        </div>
    );
}
