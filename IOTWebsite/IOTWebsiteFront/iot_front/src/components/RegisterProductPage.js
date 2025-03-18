import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import './RegisterCompanyPage.css'; 

const RegisterProductPage = () => {
    const { companyId } = useParams();  // Get company ID from URL
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        product_id: '',
        product_name: '',
        product_description: '',
    });

    const [errorMessage, setErrorMessage] = useState('');
    const [duplicateProduct, setDuplicateProduct] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);
    

    const handleChange = (e) => {
      const { name, value } = e.target;
      setFormData((prevData) => ({
          ...prevData,
          [name]: name === "product_description" && value.length > 60 ? prevData[name] : 
                  name === "product_name" && value.length > 30 ? prevData[name] : value,
      }));
    };

    const handleSubmit = async (e) => {
      e.preventDefault();
      setErrorMessage('');
      setDuplicateProduct(false);
  
      try {
          const response = await fetch("http://localhost:8080/IOTWebsiteBack/product", {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({
                  company_id: companyId,
                  ...formData,
              }),
          });
  
          const data = await response.json();
  
          if (response.ok) {
            setShowSuccess(true);

          } else if (data.error.includes("Duplicate entry")) { 
              setDuplicateProduct(true);

          } else {
              setErrorMessage(data.error || "Product registration failed.");
          }
      } catch (error) {
          setErrorMessage("Error connecting to the server.");
      }
    };
  
    // Reset the form for registering another product
    const resetForm = () => {
        setFormData({
            product_id: '',
            product_name: '',
            product_description: '',
        });
        setShowSuccess(false);
    };

    return (
        <div className="register-container">
            <div className="register-card">
                <h2>Register New Product</h2>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        name="product_id"
                        placeholder="Product ID"
                        value={formData.product_id}
                        onChange={handleChange}
                        required
                    />
                    <input
                        type="text"
                        name="product_name"
                        placeholder="Product Name"
                        value={formData.product_name}
                        onChange={handleChange}
                        required
                    />
                    <textarea
                        name="product_description"
                        placeholder="Product Description (max 60 characters)"
                        value={formData.product_description}
                        onChange={handleChange}
                        rows="4"
                        required
                    />
                    <p>{formData.product_description.length}/60</p>

                    <div className="modal-actions">
                        <button 
                            type="submit" 
                            className="register-btn" 
                            style={{height: "45px !important", minHeight: "45px !important"}}>
                            Register Product
                        </button>
                        <button 
                            type="button" 
                            className="cancel-btn" 
                            style={{height: "45px !important", minHeight: "45px !important"}} 
                            onClick={() => navigate(`/company-data/${companyId}`)}>
                            Back
                        </button>
                    </div>
                </form>
            </div>

            {showSuccess && (
                <div className="success-modal">
                    <div className="success-content">
                        <h2>Product Registered Successfully!</h2>
                        <p>What would you like to do next?</p>
                        <div className="success-actions">
                            <button className="action-btn primary-action" onClick={resetForm}>Register Another Product</button>
                            <button className="action-btn secondary-action" onClick={() => navigate(`/company-data/${companyId}`)}>Go to Company Page</button>
                        </div>
                    </div>
                </div>
            )}

            {errorMessage && (
                <>
                    <div className="error-overlay" onClick={() => setErrorMessage(null)}></div>
                    <div className="error-modal">
                        <div className="error-content">
                            <h2>Error</h2>
                            <p>{errorMessage}</p>
                            <button onClick={() => setErrorMessage(null)}>Close</button>
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
        </div>
    );
};

export default RegisterProductPage;