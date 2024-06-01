import "./App.css";
import { lazy, Suspense, useEffect, useRef, useState } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Container, Grid, IconButton, Typography } from "@mui/material";
import { closeSnackbar, SnackbarProvider } from "notistack";
import useGlitchTip from "@hooks/useGlitchTip.tsx";
import { useAtom } from "jotai";
import userAtom from "@components/authentication/userAtom.ts";
import CloseIcon from "@mui/icons-material/Close";
import useVersionChecker from "@hooks/useVersionChecker.tsx";
const SplashScreen = lazy(
  () => import("@structure/SplashScreen/SplashScreen.tsx"),
);
import Navbar from "@components/structure/Navbar.tsx";
import Footer from "@components/structure/Footer.tsx";
import ProjectSubmissionDetailScreen from "@components/projects/SubmissionDetailScreen/ProjectSubmissionDetailScreen.tsx";
const ProjectCreationScreen = lazy(
  () =>
    import(
      "@components/projects/projectCreationScreen/ProjectCreationScreen.tsx"
    ),
);
const ProjectDetailScreen = lazy(
  () =>
    import("@components/projects/ProjectDetailScreen/ProjectDetailScreen.tsx"),
);
const HomeScreen = lazy(() => import("@components/home/HomeScreen"));
const Box = lazy(() => import("@mui/material/Box"));
const AuthWrapper = lazy(() => import("./AuthWrapper.tsx"));
const StatusPage = lazy(
  () => import("@components/structure/StatusPage/StatusPage.tsx"),
);
const ProfileScreen = lazy(
  () => import("@components/profile/ProfileScreen.tsx"),
);
const JoinCourseScreen = lazy(
  () => import("@components/courses/JoinCourse/JoinCourseScreen.tsx"),
);
const ErrorHandlerWrapper = lazy(() => import("./ErrorHandlerWrapper.tsx"));
const CourseCreationScreen = lazy(
  () => import("@courses/courseCreation/CourseCreationScreen.tsx"),
);
const CoursesScreen = lazy(
  () => import("@courses/coursesPage/CoursesScreen.tsx"),
);
const MobileWrapper = lazy(
  () => import("@structure/MobileWrapper/MobileWrapper"),
);
const TrialCreationScreen = lazy(
  () => import("@trials/trialCreation/TrialCreationScreen.tsx"),
);
const TrialDetailScreen = lazy(
  () => import("@trials/trialDetail/TrialDetailScreen.tsx"),
);
const WebSocketWrapper = lazy(() => import("./WebSocketWrapper.tsx"));
const ExerciseNavigatorWrapper = lazy(
  () => import("@exercises/exerciseScreen/ExerciseNavigatorWrapper.tsx"),
);
const UserSubmissionsScreen = lazy(
  () =>
    import(
      "@components/submissions/UserSubmissionsScreen/UserSubmissionsScreen.tsx"
    ),
);
const UsersScreen = lazy(
  () => import("@components/users/usersScreen/UsersScreen.tsx"),
);
const ExerciseCreation = lazy(
  () => import("@exercises/exerciseCreation/ExerciseCreation.tsx"),
);
const ExerciseUpdate = lazy(
  () => import("@exercises/exerciseCreation/ExerciseUpdate.tsx"),
);
const SimilarityReport = lazy(
  () => import("@components/antiplagiarism/report/SimilarityReport.tsx"),
);
const CourseDetailScreen = lazy(
  () => import("@components/courses/courseDetail/CourseDetailScreen.tsx"),
);

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
    // checkWebAppVersion();
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
          <Suspense fallback={<SplashScreen />}>
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
                          path={"/trials/:trialId"}
                          element={<TrialDetailScreen />}
                        />
                        <Route
                          path={"/trials/:trialId/exercises/:exerciseId"}
                          element={<ExerciseNavigatorWrapper />}
                        />
                        <Route
                          path={"/trials/:trialId/users/:userId"}
                          element={<UserSubmissionsScreen />}
                        />
                        <Route
                          path="/exercises/new"
                          element={
                            <Container
                              maxWidth={"xl"}
                              style={{ height: "100%" }}
                            >
                              <ExerciseCreation />
                            </Container>
                          }
                        />
                        <Route
                          path="/exercises/:exerciseId/edit"
                          element={
                            <Container
                              maxWidth={"xl"}
                              style={{ height: "100%" }}
                            >
                              <ExerciseUpdate />
                            </Container>
                          }
                        />
                        <Route
                          path="/exercises/:exerciseId/similarity-report"
                          element={<SimilarityReport />}
                        />
                        <Route path="/profile" element={<ProfileScreen />} />
                        <Route path="/projects">
                          <Route path={":projectId"}>
                            <Route
                              path={""}
                              element={<ProjectDetailScreen />}
                            />
                            <Route
                              path={"users/:userId"}
                              element={<ProjectSubmissionDetailScreen />}
                            />
                          </Route>
                          <Route
                            path={"new"}
                            element={
                              <Container maxWidth={"xl"}>
                                <ProjectCreationScreen />
                              </Container>
                            }
                          />
                        </Route>
                        <Route path="*" element={<NotFound />} />
                      </Routes>
                    </Box>
                  </MobileWrapper>
                </WebSocketWrapper>
              </AuthWrapper>
            </ErrorHandlerWrapper>
          </Suspense>
          <Footer ref={footerRef} />
        </BrowserRouter>
      </SnackbarProvider>
    </div>
  );
}

export default App;
