import "./App.css";
import { BrowserRouter, Route, Routes, useNavigate } from "react-router-dom";
import { Container } from "@mui/material";
import HomeScreen from "./components/home/HomeScreen";
import Navbar from "./components/misc/Navbar";
import ServicesStatus from "./components/misc/ServicesStatus";
import { useEffect } from "react";
import Footer from "./components/misc/Footer.tsx";
import { LoginPage } from "./components/authentication/LoginPage/LoginPage.tsx";
import { useAuthService } from "./components/authentication/hooks/useAuthService.tsx";
import { User } from "./components/authentication/types.ts";

const NotFound = () => {
  const navigate = useNavigate();
  navigate("/");
  return <></>;
};

function App() {
  const { getCurrent } = useAuthService();

  useEffect(() => {
    getCurrent().then((u: User | null) => {
      if (u === null && window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
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
          <Route path="/login" element={<LoginPage />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
