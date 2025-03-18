import React, { useState } from "react";
import Header from "./components/Header";
import Footer from "./components/Footer";
import LogInModal from "./components/LogInModal";
import { useNavigate } from "react-router-dom";
import TunaImage from "./components/TunaImage";
import "./index.css";

const TunaIOT = () => {
  const [showLogIn, setShowLogIn] = useState(false);
  const navigate = useNavigate();

  return (
    <div className="container">
      <Header />
      <main>
  
        <div className="button-container">
          <button className="btn large-btn" onClick={() => setShowLogIn(true)}>Log In</button>
          <button className="btn large-btn" onClick={() => navigate("/register-company")}>Join Us</button>
         
        </div>

        <TunaImage />

        {showLogIn && <LogInModal onClose={() => setShowLogIn(false)} />}

        <audio id="tuna-audio">
          <source src="/assets/tuna_audio.mp3" type="audio/mp3" />
        </audio>
      </main>
      <Footer />
    </div>
  );
};

export default TunaIOT;
