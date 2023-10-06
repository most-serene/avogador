import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Container, Grid, Typography } from "@mui/material";
import HomeScreen from "@components/home/HomeScreen";
import Navbar from "@components/structure/Navbar.tsx";
import Footer from "@components/structure/Footer.tsx";
import AuthWrapper from "./AuthWrapper.tsx";
import StatusPage from "@components/structure/StatusPage/StatusPage.tsx";
import ProfileScreen from "@components/profile/ProfileScreen.tsx";
import JoinCourseScreen from "@components/courses/JoinCourse/JoinCourseScreen.tsx";
import { SnackbarProvider } from "notistack";
import Box from "@mui/material/Box";
import { useEffect, useRef, useState } from "react";
import CourseDetailScreen from "@components/courses/courseDetail/CourseDetailScreen.tsx";
import useGlitchTip from "@hooks/useGlitchTip.tsx";
import { useAtom } from "jotai";
import userAtom from "@components/authentication/userAtom.ts";
import ErrorHandlerWrapper from "./ErrorHandlerWrapper.tsx";
import CourseCreationScreen from "@courses/courseCreation/CourseCreationScreen.tsx";
import CoursesScreen from "@courses/coursesPage/CoursesScreen.tsx";
import MobileWrapper from "@structure/MobileWrapper/MobileWrapper";
import TrialCreationScreen from "@trials/traialCreation/TrialCreationScreen.tsx";
import TrialDetailScreen from "@trials/trialDetail/TrialDetailScreen.tsx";

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
  const navbarRef = useRef<HTMLElement>(null);
  const footerRef = useRef<HTMLElement>(null);

  const [occupiedHeight, setOccupiedHeight] = useState(0);

  const { connectToGlitchTip } = useGlitchTip();
  const [user] = useAtom(userAtom);

  useEffect(() => {
    if (user) {
      connectToGlitchTip(user);
    }
  }, [user, connectToGlitchTip]);

  useEffect(() => {
    setOccupiedHeight(
      8 * 2 +
        (navbarRef.current?.clientHeight ?? 0) +
        (footerRef.current?.clientHeight ?? 0),
    );
  }, [navbarRef, footerRef]);

  return (
    <div className="App">
      <SnackbarProvider preventDuplicate={true}>
        <BrowserRouter>
          <Navbar ref={navbarRef} />
          <ErrorHandlerWrapper>
            <AuthWrapper>
              <MobileWrapper>
                <Box
                  id="fullScreenWrapper"
                  height={`calc(100vh - ${occupiedHeight}px)`}
                >
                  <Routes>
                    <Route
                      path="/"
                      element={
                        <Container maxWidth={false} sx={{ height: "100%" }}>
                          <HomeScreen />
                        </Container>
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
                      <Route
                        path={""}
                        element={
                          <Container maxWidth={"xl"}>
                            <CoursesScreen />
                          </Container>
                        }
                      />
                      <Route
                        path={"new"}
                        element={
                          <Container maxWidth={"xl"}>
                            <CourseCreationScreen />
                          </Container>
                        }
                      />
                      <Route
                        path={":courseId"}
                        element={<CourseDetailScreen />}
                      />
                      <Route
                        path={":courseId/join"}
                        element={<JoinCourseScreen />}
                      />
                    </Route>
                    <Route
                      path={"/trials/new"}
                      element={<TrialCreationScreen />}
                    />
                    <Route
                      path={"/practices/:trialId"}
                      element={<TrialDetailScreen trialType={"PRACTICE"} />}
                    />
                    <Route path="/profile" element={<ProfileScreen />} />
                    <Route path="*" element={<NotFound />} />
                  </Routes>
                </Box>
              </MobileWrapper>
            </AuthWrapper>
          </ErrorHandlerWrapper>
          <Footer ref={footerRef} />
        </BrowserRouter>
      </SnackbarProvider>
    </div>
  );
}

export default App;
