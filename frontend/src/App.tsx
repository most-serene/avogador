import "./App.css";
import { BrowserRouter, Route, Routes, useNavigate } from "react-router-dom";
import { Container } from "@mui/material";
import HomeScreen from "./components/home/HomeScreen";
import Navbar from "./components/misc/Navbar";
import ServicesStatus from "./components/misc/ServicesStatus";
import { createContext, useEffect, useState } from "react";
import Footer from "./components/misc/Footer.tsx";
import { User } from "./components/authentication/types.ts";
import { LoginPage } from "./components/authentication/LoginPage/LoginPage.tsx";
import { useAuthService } from "./components/authentication/hooks/useAuthService.tsx";

export const UserContext = createContext<{
  user: User | null | undefined;
  setUser: (user: User | null) => void;
}>({
  user: undefined,
  setUser: () => {
    // do nothing
  },
});

const NotFound = () => {
  const navigate = useNavigate();
  navigate("/");
  return <></>;
};

function App() {
  const { getCurrent } = useAuthService();
  const [user, setUser] = useState<User | null>();

  useEffect(() => {
    getCurrent();
  }, [getCurrent]);

  return (
    <div className="App">
      <UserContext.Provider
        value={{
          user,
          setUser,
        }}
      >
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
      </UserContext.Provider>
    </div>
  );
}

export default App;
