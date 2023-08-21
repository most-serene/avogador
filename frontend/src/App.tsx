import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Container, Grid, Typography } from "@mui/material";
import HomeScreen from "./components/home/HomeScreen";
import Navbar from "./components/misc/Navbar";
import ServicesStatus from "./components/misc/ServicesStatus";
import Footer from "./components/misc/Footer.tsx";
import { LoginPage } from "./components/authentication/LoginPage/LoginPage.tsx";
import AuthWrapper from "./AuthWrapper.tsx";
import CourseDetailScreen from "./components/courses/CourseDetailScreen.tsx";

const NotFound = () => {
  return (
    <>
      <Grid container style={{ marginTop: "2rem" }}>
        <Grid
          item
          xs
          display="flex"
          justifyContent="center"
          alignItems="center"
        >
          <Typography variant="body1">
            404 - Not found: nothing to see here
          </Typography>
        </Grid>
      </Grid>
    </>
  );
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
            <Route path="courses">
              <Route path={":courseId"} element={<CourseDetailScreen />} />
            </Route>
            <Route path="/login" element={<LoginPage />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </AuthWrapper>
      </BrowserRouter>
    </div>
  );
}

export default App;
