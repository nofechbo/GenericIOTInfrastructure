import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import TunaIOT from "./TunaIOT"; 
import RegisterCompanyPage from "./components/RegisterCompanyPage";
import RegisterProductPage from "./components/RegisterProductPage";
import CompanyDataPage from "./components/CompanyDataPage";
import StoredCredentialsPage from "./components/StoredCredentialsPage";

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          
          <Route path="/" element={<TunaIOT />} />
          <Route path="/register-company" element={<RegisterCompanyPage />} />
          <Route path="/register-product/:companyId" element={<RegisterProductPage />} />
          <Route path="/company-data/:companyId" element={<CompanyDataPage />} />
          <Route path="/stored-credentials" element={<StoredCredentialsPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
