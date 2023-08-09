import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Container } from "@mui/material";
import HomeScreen from "./components/home/HomeScreen";
import Navbar from "./components/misc/Navbar";
import ServicesStatus from "./components/misc/ServicesStatus";
import { useEffect } from "react";
import { avogadorApi } from "./utils/axiosConf.ts";
import Footer from "./components/misc/Footer.tsx";

function App() {
  useEffect(() => {
    const storedCSRF = localStorage.getItem("Jwt-CSRF-Hash");
    if (storedCSRF !== null) {
      avogadorApi.defaults.headers.common["Jwt-CSRF-Hash"] = storedCSRF;
    }
  }, []);

  return (
    <div className="App">
      <Navbar />
      <BrowserRouter>
        <Routes>
          <Route
            path="/"
            element={
              <>
                <Container
                  className={"full-page-without-header-and-footer"}
                  maxWidth={false}
                >
                  <HomeScreen />
                </Container>
                <Footer />
              </>
            }
          />
          <Route
            path="/status"
            element={
              <Container>
                <ServicesStatus />
              </Container>
            }
          />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
