import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./CompanyDataPage.css";

const CompanyDataPage = () => {
    const { companyId } = useParams();
    const navigate = useNavigate();
    
    const [companyInfo, setCompanyInfo] = useState(null);
    const [products, setProducts] = useState([]);
    const [contacts, setContacts] = useState([]);
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [showContacts, setShowContacts] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
    const [productToDelete, setProductToDelete] = useState(null);
    const [showContactModal, setShowContactModal] = useState(false);
    const [showProductModal, setShowProductModal] = useState(false);
    const [duplicateContact, setDuplicateContact] = useState(false);
    const [duplicateProduct, setDuplicateProduct] = useState(false);
    const [productSuccess, setProductSuccess] = useState(false);
    
    const [newContact, setNewContact] = useState({
      contact_id: "", 
      contact_name: "",
      contact_email: "",
      contact_phone_number: "",
      password: "",
    });
    
    const [newProduct, setNewProduct] = useState({
        product_id: "",
        product_name: "",
        product_description: "",
    });
    
    const [formErrors, setFormErrors] = useState({});
    const [productFormErrors, setProductFormErrors] = useState({});

    useEffect(() => {
        fetchCompanyData();
        fetchContacts();
        fetchProducts();
    }, [companyId]);

    const fetchCompanyData = async () => {
        try {
            const response = await fetch(`http://localhost:8080/IOTWebsiteBack/company-data?company_id=${companyId}`);
            const data = await response.json();
            if (!data.error) {
                setCompanyInfo({
                    name: data[0][1],
                    plan: data[0][3]
                });
            }
        } catch (error) {
            console.error("Error fetching company data:", error);
            setError("Failed to load company data.");
        }
    };

    const fetchContacts = async () => {
        try {
            const response = await fetch(`http://localhost:8080/IOTWebsiteBack/contacts?company_id=${companyId}`);
            const data = await response.json();
            if (!data.error) setContacts(data);
        } catch (error) {
            console.error("Error fetching contacts:", error);
        }
    };

    const fetchProducts = async () => {
        setIsLoading(true);
        try {
            const response = await fetch(`http://localhost:8080/IOTWebsiteBack/products?company_id=${companyId}`);
            const data = await response.json();
            if (!data.error) setProducts(data);
        } catch (error) {
            setError("Failed to load products.");
        } finally {
            setIsLoading(false);
        }
    };

    const handleRegisterProduct = () => {
        setShowProductModal(true);
        setProductFormErrors({});
        setDuplicateProduct(false);
        setNewProduct({
            product_id: "",
            product_name: "",
            product_description: "",
        });
    };

    const handleRegisterContact = () => {
      setShowContactModal(true);
      // Reset form state when opening modal
      setFormErrors({});
      setDuplicateContact(false);
    };

    const validateContactForm = () => {
      const errors = {};
      
      if (!newContact.contact_id.trim()) errors.contact_id = "Contact ID is required";
      if (!newContact.contact_name.trim()) errors.contact_name = "Name is required";
      
      // Add email format validation
      if (!newContact.contact_email.trim()) {
          errors.contact_email = "Email is required";
      } else if (!/\S+@\S+\.\S+/.test(newContact.contact_email)) {
          errors.contact_email = "Please enter a valid email address";
      }
      
      if (!newContact.contact_phone_number.trim()) errors.contact_phone_number = "Phone number is required";
      if (!newContact.password.trim()) errors.password = "Password is required";
      
      setFormErrors(errors);
      return Object.keys(errors).length === 0;
    };
    
    const validateProductForm = () => {
        const errors = {};
        
        if (!newProduct.product_id.trim()) errors.product_id = "Product ID is required";
        if (!newProduct.product_name.trim()) errors.product_name = "Product name is required";
        if (!newProduct.product_description.trim()) errors.product_description = "Product description is required";
        
        setProductFormErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleAddContact = async () => {
        setDuplicateContact(false);
        
        if (!validateContactForm()) {
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/IOTWebsiteBack/contact", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ ...newContact, company_id: companyId }),
            });

            const data = await response.json();

            if (response.ok) {
                setShowContactModal(false);
                setNewContact({ contact_id: "", contact_name: "", contact_email: "", contact_phone_number: "", password: "" });
                fetchContacts(); // Refresh the contacts list
            } else if (data.error && data.error.includes("Duplicate entry")) {
                setDuplicateContact(true);
            } else {
                setError(data.error || "Failed to add contact.");
            }
        } catch (error) {
            setError("Error connecting to the server.");
        }
    };
    
    const handleAddProduct = async () => {
        setDuplicateProduct(false);
        
        // Validate all fields are filled
        if (!validateProductForm()) {
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/IOTWebsiteBack/product", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    company_id: companyId,
                    ...newProduct,
                }),
            });

            const data = await response.json();

            if (response.ok) {
                setProductSuccess(true);
                fetchProducts(); // Refresh the products list
            } else if (data.error && data.error.includes("Duplicate entry")) {
                setDuplicateProduct(true);
            } else {
                setError(data.error || "Product registration failed.");
            }
        } catch (error) {
            setError("Error connecting to the server.");
        }
    };
    
    const resetProductForm = () => {
        setNewProduct({
            product_id: "",
            product_name: "",
            product_description: "",
        });
        setProductSuccess(false);
    };

    const confirmDeleteProduct = (productId, productName) => {
        setProductToDelete({ id: productId, name: productName });
        setShowDeleteConfirm(true);
    };

    const handleDeleteProduct = async () => {
        if (!productToDelete) return;
        
        setIsDeleting(true);
        try {
            const response = await fetch("http://localhost:8080/IOTWebsiteBack/product", {
                method: "DELETE",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ product_id: productToDelete.id }),
            });

            if (response.ok) {
                setProducts(products.filter(product => product[0] !== productToDelete.id));
                setShowDeleteConfirm(false);
                setProductToDelete(null);
            }
        } catch (error) {
            setError("Failed to delete product.");
        } finally {
            setIsDeleting(false);
        }
    };

    const toggleContactsView = () => {
        setShowContacts(!showContacts);
    };

    const handleContactInputChange = (e) => {
        const { name, value } = e.target;
        setNewContact({ ...newContact, [name]: value });
        
        // Clear the specific error when user types in a field
        if (formErrors[name]) {
            setFormErrors({ ...formErrors, [name]: "" });
        }
    };
    
    const handleProductInputChange = (e) => {
        const { name, value } = e.target;
        setNewProduct({
            ...newProduct,
            [name]: name === "product_description" && value.length > 60 ? newProduct[name] : 
                    name === "product_name" && value.length > 30 ? newProduct[name] : value,
        });
        
        // Clear the specific error when user types in a field
        if (productFormErrors[name]) {
            setProductFormErrors({ ...productFormErrors, [name]: "" });
        }
    };

    return (
        <div className="company-page-container">
            {error && (
                <>
                    <div className="error-overlay" onClick={() => setError(null)}></div>
                    <div className="error-modal">
                        <div className="error-content">
                            <h2>Error</h2>
                            <p>{error}</p>
                            <button onClick={() => setError(null)}>Close</button>
                        </div>
                    </div>
                </>
            )}

            {/* Company Header Section */}
            {companyInfo && (
                <div className="company-header">
                    <div className="company-info-card">
                        <h1>{companyInfo.name}</h1>
                        <div className="company-details">
                            <p><span>Subscription:</span> {companyInfo.plan}</p>
                            <p><span>Company ID:</span> {companyId}</p>
                        </div>
                        <div className="company-actions">
                            <button className="primary-btn" onClick={toggleContactsView}>
                                {showContacts ? "Hide Contacts" : "Show Contacts"}
                            </button>
                            <button className="secondary-btn" onClick={handleRegisterContact}>
                                Register New Contact
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Contacts Section */}
            {showContacts && companyInfo && (
                <div className="contacts-section">
                    <h2>Company Contacts</h2>
                    {contacts.length > 0 ? (
                        <table className="data-table">
                            <thead>
                                <tr>
                                    <th>Contact ID</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                </tr>
                            </thead>
                            <tbody>
                                {contacts.map((contact, index) => (
                                    <tr key={index}>
                                        <td>{contact[0]}</td>
                                        <td>{contact[1]}</td>
                                        <td>{contact[2]}</td>
                                        <td>{contact[3]}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <p className="no-data">No contacts registered.</p>
                    )}
                </div>
            )}

            {/* Products Section */}
            <div className="products-section">
                <div className="section-header">
                    <h2>Company Products</h2>
                    <button className="primary-btn" onClick={handleRegisterProduct}>
                        Register New Product
                    </button>
                </div>

                {isLoading ? (
                    <div className="loading">Loading products...</div>
                ) : products.length > 0 ? (
                    <table className="data-table">
                        <thead>
                            <tr>
                                <th>Product ID</th>
                                <th>Product Name</th>
                                <th>Description</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {products.map(product => (
                                <tr key={product[0]}>
                                    <td>{product[0]}</td>
                                    <td>{product[1]}</td>
                                    <td>{product[2]}</td>
                                    <td>
                                        <button 
                                            className="delete-btn"
                                            onClick={() => confirmDeleteProduct(product[0], product[1])}
                                        >
                                            Remove
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <p className="no-data">No products registered for this company.</p>
                )}
            </div>

            {/* Delete Confirmation Modal */}
            {showDeleteConfirm && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Confirm Deletion</h2>
                        <p>Are you sure you want to remove <strong>{productToDelete?.name}</strong>?</p>
                        <div className="modal-actions">
                            <button className="cancel-btn" onClick={() => setShowDeleteConfirm(false)}>Cancel</button>
                            <button className="confirm-btn" onClick={handleDeleteProduct}>Confirm Delete</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Add contact Modal */}
            {showContactModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Add New Contact</h2>
                        <div className="form-group">
                            <input 
                                type="text" 
                                name="contact_id"
                                placeholder="Contact ID" 
                                value={newContact.contact_id} 
                                onChange={handleContactInputChange}
                                className={formErrors.contact_id ? "error-input" : ""}
                            />
                            {formErrors.contact_id && <p className="error-text">{formErrors.contact_id}</p>}
                        </div>
                        <div className="form-group">
                            <input 
                                type="text" 
                                name="contact_name"
                                placeholder="Name" 
                                value={newContact.contact_name} 
                                onChange={handleContactInputChange}
                                className={formErrors.contact_name ? "error-input" : ""}
                            />
                            {formErrors.contact_name && <p className="error-text">{formErrors.contact_name}</p>}
                        </div>
                        <div className="form-group">
                            <input 
                                type="email" 
                                name="contact_email"
                                placeholder="Email" 
                                value={newContact.contact_email} 
                                onChange={handleContactInputChange}
                                className={formErrors.contact_email ? "error-input" : ""}
                            />
                            {formErrors.contact_email && <p className="error-text">{formErrors.contact_email}</p>}
                        </div>
                        <div className="form-group">
                            <input 
                                type="text" 
                                name="contact_phone_number"
                                placeholder="Phone" 
                                value={newContact.contact_phone_number} 
                                onChange={handleContactInputChange}
                                className={formErrors.contact_phone_number ? "error-input" : ""}
                            />
                            {formErrors.contact_phone_number && <p className="error-text">{formErrors.contact_phone_number}</p>}
                        </div>
                        <div className="form-group">
                            <input 
                                type="password" 
                                name="password"
                                placeholder="Password" 
                                value={newContact.password} 
                                onChange={handleContactInputChange}
                                className={formErrors.password ? "error-input" : ""}
                            />
                            {formErrors.password && <p className="error-text">{formErrors.password}</p>}
                        </div>
                        
                        {duplicateContact && (
                            <div className="error-message">
                                <p>This Contact ID is already in use. Please choose a different one.</p>
                            </div>
                        )}
                        
                        <div className="modal-actions">
                            <button className="confirm-btn" onClick={handleAddContact}>Add</button>
                            <button className="cancel-btn" onClick={() => setShowContactModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}
            
            {/* Add Product Modal */}
            {showProductModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        {!productSuccess ? (
                            <>
                                <h2>Register New Product</h2>
                                <div className="form-group">
                                    <input 
                                        type="text" 
                                        name="product_id"
                                        placeholder="Product ID" 
                                        value={newProduct.product_id} 
                                        onChange={handleProductInputChange}
                                        className={productFormErrors.product_id ? "error-input" : ""}
                                    />
                                    {productFormErrors.product_id && <p className="error-text">{productFormErrors.product_id}</p>}
                                </div>
                                <div className="form-group">
                                    <input 
                                        type="text" 
                                        name="product_name"
                                        placeholder="Product Name (max 30 characters)" 
                                        value={newProduct.product_name} 
                                        onChange={handleProductInputChange}
                                        className={productFormErrors.product_name ? "error-input" : ""}
                                    />
                                    <p className="char-count">{newProduct.product_name.length}/30</p>
                                    {productFormErrors.product_name && <p className="error-text">{productFormErrors.product_name}</p>}
                                </div>
                                <div className="form-group">
                                    <textarea 
                                        name="product_description"
                                        placeholder="Product Description (max 60 characters)" 
                                        value={newProduct.product_description} 
                                        onChange={handleProductInputChange}
                                        rows="4"
                                        className={productFormErrors.product_description ? "error-input" : ""}
                                    />
                                    <p className="char-count">{newProduct.product_description.length}/60</p>
                                    {productFormErrors.product_description && <p className="error-text">{productFormErrors.product_description}</p>}
                                </div>
                                
                                {duplicateProduct && (
                                    <div className="error-message">
                                        <p>This Product ID is already in use. Please choose a different one.</p>
                                    </div>
                                )}
                                
                                <div className="modal-actions">
                                    <button className="confirm-btn" onClick={handleAddProduct}>Register Product</button>
                                    <button className="cancel-btn" onClick={() => setShowProductModal(false)}>Back</button>
                                </div>
                            </>
                        ) : (
                            <div className="success-content">
                                <h2>Product Registered Successfully!</h2>
                                <p>What would you like to do next?</p>
                                <div className="modal-actions">
                                    <button className="confirm-btn" onClick={resetProductForm}>Register Another Product</button>
                                    <button className="cancel-btn" onClick={() => {
                                        setShowProductModal(false);
                                        setProductSuccess(false);
                                    }}>Back to Company Page</button>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            )}

            <button className="return-btn" onClick={() => navigate('/')}>Logout</button>
        </div>
    );
};

export default CompanyDataPage;