import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Container, Grid, Typography } from "@mui/material";
import HomeScreen from "./components/home/HomeScreen";
import Navbar from "./components/misc/Navbar";
import Footer from "./components/misc/Footer.tsx";
import { LoginPage } from "./components/authentication/LoginPage/LoginPage.tsx";
import AuthWrapper from "./AuthWrapper.tsx";
import CourseDetailScreen from "./components/courses/CourseDetailScreen.tsx";
import StatusPage from "./components/misc/StatusPage/StatusPage.tsx";
import ProfileScreen from "./components/profile/ProfileScreen.tsx";
import JoinCourseScreen from "./components/courses/JoinCourse/JoinCourseScreen.tsx";
import { SnackbarProvider } from "notistack";

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
      <SnackbarProvider preventDuplicate={true}>
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
                    <StatusPage />
                  </Container>
                }
              />
              <Route path="courses">
                <Route path={":courseId"} element={<CourseDetailScreen />} />
                <Route path={":courseId/join"} element={<JoinCourseScreen />} />
              </Route>
              <Route path="/login" element={<LoginPage />} />
              <Route path="/profile" element={<ProfileScreen />} />
              <Route path="*" element={<NotFound />} />
            </Routes>
          </AuthWrapper>
        </BrowserRouter>
      </SnackbarProvider>
    </div>
  );
}

export default App;
