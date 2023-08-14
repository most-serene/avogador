import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Container } from "@mui/material";
import HomeScreen from "./components/home/HomeScreen";
import Navbar from "./components/misc/Navbar";
import ServicesStatus from "./components/misc/ServicesStatus";
import { createContext, useEffect, useState } from "react";
import { avogadorApi } from "./utils/axiosConf.ts";
import Footer from "./components/misc/Footer.tsx";
import { User } from "./components/authentication/types.ts";

export const UserContext = createContext<User | null>(null);

function App() {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const storedCSRF = localStorage.getItem("Jwt-CSRF-Hash");
    if (storedCSRF !== null) {
      avogadorApi.defaults.headers.common["Jwt-CSRF-Hash"] = storedCSRF;
    }

    avogadorApi
      .get("/users/current")
      .then(({ data }: { data: User }) => {
        console.log(data);
        setUser(data);
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);

  return (
    <div className="App">
      <UserContext.Provider value={user}>
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
      </UserContext.Provider>
    </div>
  );
}

export default App;
