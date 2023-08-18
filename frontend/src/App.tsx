import "./App.css";
import { BrowserRouter, Route, Routes, useNavigate } from "react-router-dom";
import { Container } from "@mui/material";
import HomeScreen from "./components/home/HomeScreen";
import Navbar from "./components/misc/Navbar";
import ServicesStatus from "./components/misc/ServicesStatus";
import Footer from "./components/misc/Footer.tsx";
import { LoginPage } from "./components/authentication/LoginPage/LoginPage.tsx";
import AuthWrapper from "./AuthWrapper.tsx";

const NotFound = () => {
  const navigate = useNavigate();
  navigate("/");
  return <></>;
};

function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Navbar />
        <AuthWrapper>
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
        </AuthWrapper>
      </BrowserRouter>
    </div>
  );
}

export default App;
