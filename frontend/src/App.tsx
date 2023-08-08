import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Container } from "@mui/material";
import HomeScreen from "./components/home/HomeScreen";
import Navbar from "./components/misc/Navbar";
import ServicesStatus from "./components/misc/ServicesStatus";

function App() {
  return (
    <div className="App">
      <Navbar />
      <BrowserRouter>
        <Routes>
          <Route
            path="/"
            element={
              <Container
                className={"full-page-without-header"}
                maxWidth={false}
              >
                <HomeScreen />
              </Container>
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
