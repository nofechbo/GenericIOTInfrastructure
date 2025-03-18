import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './RegisterCompanyPage.css';

const RegisterCompanyPage = () => {
    const navigate = useNavigate();
    
    const [formData, setFormData] = useState({
        company_id: '',
        company_name: '',
        company_address: '',
        subscription_plan: '',
        contact_id: '',
        contact_name: '',
        email: '',
        phone_number: '',
        password: '',
    });

    const [errorMessage, setErrorMessage] = useState('');
    const [duplicateCompany, setDuplicateCompany] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);
    const [registeredCompanyId, setRegisteredCompanyId] = useState(null);
    const [showProductModal, setShowProductModal] = useState(false);
    const [newProduct, setNewProduct] = useState({
        product_id: '',
        product_name: '',
        product_description: '',
    });
    const [productErrorMessage, setProductErrorMessage] = useState('');
    const [duplicateProduct, setDuplicateProduct] = useState(false);
    const [productSuccess, setProductSuccess] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleProductChange = (e) => {
        const { name, value } = e.target;
        setNewProduct((prevData) => ({
            ...prevData,
            [name]: name === "product_description" && value.length > 60 ? prevData[name] : 
                    name === "product_name" && value.length > 30 ? prevData[name] : value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        setDuplicateCompany(false);
    
        try {
            const response = await fetch("http://localhost:8080/IOTWebsiteBack/company", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData),
            });
    
            const data = await response.json();
    
            if (response.ok) {
                setShowSuccess(true);
                setRegisteredCompanyId(formData.company_id);

            } else if (data.error.includes("Duplicate entry")) { 
                setDuplicateCompany(true);

            } else {
                setErrorMessage(data.error || "Company registration failed.");
            }
        } catch (error) {
            setErrorMessage("Error connecting to the server.");
        }
    };

    const handleRegisterProduct = () => {
        setShowProductModal(true);
        setNewProduct({
            product_id: '',
            product_name: '',
            product_description: '',
        });
        setProductErrorMessage('');
        setDuplicateProduct(false);
        setProductSuccess(false);
    };

    const handleProductSubmit = async (e) => {
        e.preventDefault();
        setProductErrorMessage('');
        setDuplicateProduct(false);
    
        try {
            const response = await fetch("http://localhost:8080/IOTWebsiteBack/product", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    company_id: registeredCompanyId,
                    ...newProduct,
                }),
            });
    
            const data = await response.json();
    
            if (response.ok) {
                setProductSuccess(true);
            } else if (data.error && data.error.includes("Duplicate entry")) {
                setDuplicateProduct(true);
            } else {
                setProductErrorMessage(data.error || "Product registration failed.");
            }
        } catch (error) {
            setProductErrorMessage("Error connecting to the server.");
        }
    };

    const resetProductForm = () => {
        setNewProduct({
            product_id: '',
            product_name: '',
            product_description: '',
        });
        setProductSuccess(false);
    };
  
    return (
        <div className="register-container">
            <div className="register-card">
                <h2>Register Your Company</h2>
                <form onSubmit={handleSubmit}>
                    <input type="text" name="company_id" placeholder="Company ID" value={formData.company_id} onChange={handleChange} required />
                    <input type="text" name="company_name" placeholder="Company Name" value={formData.company_name} onChange={handleChange} required />
                    <input type="text" name="company_address" placeholder="Company Address" value={formData.company_address} onChange={handleChange} required />

                    <select name="subscription_plan" value={formData.subscription_plan} onChange={handleChange} required>
                        <option value="">Select Subscription Plan</option>
                        <option value="basic">Basic</option>
                        <option value="premium">Premium</option>
                    </select>

                    <input type="text" name="contact_id" placeholder="Contact ID" value={formData.contact_id} onChange={handleChange} required />
                    <input type="text" name="contact_name" placeholder="Contact Name" value={formData.contact_name} onChange={handleChange} required />
                    <input type="email" name="email" placeholder="Email" value={formData.email} onChange={handleChange} required />
                    <input type="tel" name="phone_number" placeholder="Phone Number" value={formData.phone_number} onChange={handleChange} required />
                    <input type="password" name="password" placeholder="Password" value={formData.password} onChange={handleChange} required />

                    <button type="submit" className="register-btn">Register</button>
                    <button type="button" className="cancel-btn" onClick={() => navigate('/')}>Return to Home</button>
                </form>
            </div>

            {showSuccess && (
                <div className="success-modal">
                    <div className="success-content">
                        <h2>Company Registered Successfully!</h2>
                        <p>What would you like to do next?</p>
                        <button className="register-btn" onClick={handleRegisterProduct}>Register a Product</button>
                        <button className="register-btn" onClick={() => navigate('/')}>Return to Home Page</button>
                    </div>
                </div>
            )}

            {showProductModal && (
                <div className="modal-overlay">
                    <div className="product-modal-content">
                        {!productSuccess ? (
                            <>
                                <h2>Register New Product</h2>
                                <form onSubmit={handleProductSubmit}>
                                    <input
                                        type="text"
                                        name="product_id"
                                        placeholder="Product ID"
                                        value={newProduct.product_id}
                                        onChange={handleProductChange}
                                        required
                                    />
                                    <input
                                        type="text"
                                        name="product_name"
                                        placeholder="Product Name (max 30 characters)"
                                        value={newProduct.product_name}
                                        onChange={handleProductChange}
                                        required
                                    />
                                    <p>{newProduct.product_name.length}/30</p>
                                    
                                    <textarea
                                        name="product_description"
                                        placeholder="Product Description (max 60 characters)"
                                        value={newProduct.product_description}
                                        onChange={handleProductChange}
                                        rows="4"
                                        required
                                    />
                                    <p>{newProduct.product_description.length}/60</p>

                                    <div className="modal-actions">
                                        <button type="submit" className="confirm-btn">Register Product</button>
                                        <button 
                                            type="button" 
                                            className="cancel-btn" 
                                            onClick={() => setShowProductModal(false)}
                                        >
                                            Back
                                        </button>
                                    </div>
                                </form>
                            </>
                        ) : (
                            <div className="success-content">
                                <h2>Product Registered Successfully!</h2>
                                <p>What would you like to do next?</p>
                                <div className="success-actions">
                                    <button 
                                        className="action-btn primary-action" 
                                        onClick={resetProductForm}
                                    >
                                        Register Another Product
                                    </button>
                                    <button 
                                        className="action-btn secondary-action" 
                                        onClick={() => navigate(`/company-data/${registeredCompanyId}`)}
                                    >
                                        Go to Company Page
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            )}

            {productErrorMessage && (
                <>
                    <div className="error-overlay" onClick={() => setProductErrorMessage('')}></div>
                    <div className="error-modal">
                        <div className="error-content">
                            <h2>Error</h2>
                            <p>{productErrorMessage}</p>
                            <button onClick={() => setProductErrorMessage('')}>Close</button>
                        </div>
                    </div>
                </>
            )}

            {duplicateProduct && (
                <>
                    <div className="duplicate-overlay" onClick={() => setDuplicateProduct(false)}></div>
                    <div className="duplicate-modal">
                        <div className="duplicate-content">
                            <h2>Product Already Registered</h2>
                            <p>This product is already in the system.</p>
                            <button onClick={() => setDuplicateProduct(false)}>Close</button>
                        </div>
                    </div>
                </>
            )}

            {errorMessage && (
                <>
                    <div className="error-overlay" onClick={() => setErrorMessage('')}></div>
                    <div className="error-modal">
                        <div className="error-content">
                            <h2>Error</h2>
                            <p>{errorMessage}</p>
                            <button onClick={() => setErrorMessage('')}>Close</button>
                        </div>
                    </div>
                </>
            )}

            {duplicateCompany && (
                <>
                    <div className="duplicate-overlay" onClick={() => setDuplicateCompany(false)}></div>
                    <div className="duplicate-modal">
                        <div className="duplicate-content">
                            <h2>Company Already Registered</h2>
                            <p>This company is already in the system.</p>
                            <button onClick={() => navigate('/')}>Return to Home Page</button>
                            <button className="close-btn" onClick={() => setDuplicateCompany(false)}>Close</button>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
};

export default RegisterCompanyPage;