import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './LogInModal.css';

const LogInModal = ({ onClose }) => {
    const [companyId, setCompanyId] = useState('');
    const [contactName, setContactName] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate(); // ✅ Use React Router navigation

    const handleLogin = async () => {
        setError('');

        try {
            const response = await fetch("http://localhost:8080/IOTWebsiteBack/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ company_id: companyId, contact_name: contactName, password }),
            });

            const data = await response.json();

            if (response.ok) {
                navigate(`/company-data/${companyId}`);  // ✅ Redirects dynamically with company_id
            } else {
                setError(data.error || "Incorrect Company ID, Contact Name, or Password.");
            }
        } catch (error) {
            setError("Error connecting to the server. Please check your network.");
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal">
                <h2>Login</h2>
                {error && <p className="error-message">{error}</p>}
                <input
                    type="text"
                    placeholder="Company ID"
                    value={companyId}
                    onChange={(e) => setCompanyId(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Contact Name"
                    value={contactName}
                    onChange={(e) => setContactName(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button onClick={handleLogin}>Log In</button>
                <button className="close-btn" onClick={onClose}>Close</button>
            </div>
        </div>
    );
};

export default LogInModal;
