import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Container, Grid, IconButton, Typography } from "@mui/material";
import HomeScreen from "@components/home/HomeScreen";
import Navbar from "@components/structure/Navbar.tsx";
import Footer from "@components/structure/Footer.tsx";
import AuthWrapper from "./AuthWrapper.tsx";
import StatusPage from "@components/structure/StatusPage/StatusPage.tsx";
import ProfileScreen from "@components/profile/ProfileScreen.tsx";
import JoinCourseScreen from "@components/courses/JoinCourse/JoinCourseScreen.tsx";
import { closeSnackbar, SnackbarProvider } from "notistack";
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
import TrialCreationScreen from "@trials/trialCreation/TrialCreationScreen.tsx";
import TrialDetailScreen from "@trials/trialDetail/TrialDetailScreen.tsx";
import WebSocketWrapper from "./WebSocketWrapper.tsx";
import CloseIcon from "@mui/icons-material/Close";
import ExerciseNavigatorWrapper from "@exercises/exerciseScreen/ExerciseNavigatorWrapper.tsx";
import UserSubmissionsScreen from "@components/submissions/UserSubmissionsScreen/UserSubmissionsScreen.tsx";
import UsersScreen from "@components/users/usersScreen/UsersScreen.tsx";
import useVersionChecker from "@hooks/useVersionChecker.tsx";
import ExerciseCreation from "@exercises/exerciseCreation/ExerciseCreation.tsx";
import ExerciseUpdate from "@exercises/exerciseCreation/ExerciseUpdate.tsx";
import SimilarityReport from "@components/antiplagiarism/report/SimilarityReport.tsx";

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
  const { checkWebAppVersion } = useVersionChecker();
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

  useEffect(() => {
    checkWebAppVersion();
    setInterval(() => {
      checkWebAppVersion();
    }, 5000 * 60);
  }, [checkWebAppVersion]);

  return (
    <div className="App">
      <SnackbarProvider
        preventDuplicate={true}
        action={(snackbarId) => (
          <IconButton
            onClick={() => {
              closeSnackbar(snackbarId);
            }}
          >
            <CloseIcon />
          </IconButton>
        )}
      >
        <BrowserRouter basename={import.meta.env.VITE_REACT_BASE_URL as string}>
          <Navbar ref={navbarRef} />
          <ErrorHandlerWrapper>
            <AuthWrapper>
              <WebSocketWrapper>
                <MobileWrapper>
                  <Box
                    id="fullScreenWrapper"
                    height={`calc(100dvh - ${occupiedHeight}px)`}
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
                      <Route
                        path="/users"
                        element={
                          <Container maxWidth={false} sx={{ height: "100%" }}>
                            <UsersScreen />
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
                      <Route
                        path={"/practices/:trialId/exercises/:exerciseId"}
                        element={<ExerciseNavigatorWrapper />}
                      />
                      <Route
                        path={"/practices/:trialId/users/:userId"}
                        element={<UserSubmissionsScreen />}
                      />
                      <Route
                        path="/exercises/new"
                        element={
                          <Container maxWidth={"xl"} style={{ height: "100%" }}>
                            <ExerciseCreation />
                          </Container>
                        }
                      />
                      <Route
                        path="/exercises/:exerciseId/edit"
                        element={
                          <Container maxWidth={"xl"} style={{ height: "100%" }}>
                            <ExerciseUpdate />
                          </Container>
                        }
                      />
                      <Route
                        path="/exercises/:exerciseId/similarity-report"
                        element={<SimilarityReport />}
                      />
                      <Route path="/profile" element={<ProfileScreen />} />
                      <Route path="*" element={<NotFound />} />
                    </Routes>
                  </Box>
                </MobileWrapper>
              </WebSocketWrapper>
            </AuthWrapper>
          </ErrorHandlerWrapper>
          <Footer ref={footerRef} />
        </BrowserRouter>
      </SnackbarProvider>
    </div>
  );
}

export default App;
