import {
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Container,
  Divider,
  Grid,
  Typography,
} from "@mui/material";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import {
  Project,
  ProjectStatus,
  ProjectSubmission,
} from "@components/projects/types.ts";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { enqueueSnackbar } from "notistack";
import { AxiosError } from "axios";
import { ArchivedCourseError, ForbiddenError } from "@error/types.ts";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import { CourseDetail } from "@courses/types.ts";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import { format } from "date-fns";
import ButtonWithConfirmation from "@structure/ButtonWithConfirmation/ButtonWithConfirmation.tsx";
import { Description, Download, Terminal } from "@mui/icons-material";

const getSubmissionStatusBadge = (status: ProjectStatus) => {
  const getProps = (
    status: ProjectStatus,
  ): { label: string; color?: "info" | "success" | "error" | "warning" } => {
    switch (status) {
      case "CONFIRMED":
        return { label: "CONFIRMED", color: "info" };
      case "SUCCESS":
        return { label: "SUCCESS", color: "success" };
      case "ERROR":
        return { label: "ERROR", color: "error" };
      case "PENDING":
        return { label: "PENDING", color: "warning" };
      default:
        return {
          label: "Unexpected status",
        };
    }
  };
  const { label, color } = getProps(status);
  return <Chip sx={{ ml: 1 }} label={label} color={color ?? "secondary"} />;
};

const ProjectDetailScreen = () => {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const globalErrorSetter = useGlobalErrorSetter();
  const [user] = useAtom(userAtom);

  const { getProject, getUserLatestProjectSubmission, uploadProject } =
    useProjectService();
  const { getCourseById } = useCourseService();

  const [project, setProject] = useState<Project>();
  const [userCourse, setUserCourse] = useState<CourseDetail>();
  const [lastSubmission, setLastSubmission] =
    useState<ProjectSubmission | null>();
  const [showFileInput, setShowFileInput] = useState(false);
  const [files, setFiles] = useState<FileList | null>();

  useEffect(() => {
    if (user == null || projectId == null) return;
    getProject(projectId)
      .then((project) => {
        setProject(project);
      })
      .catch((err: AxiosError) => {
        if (err.response && err.response.status === 403) {
          globalErrorSetter(
            new ForbiddenError(
              location.pathname,
              `${user.email} does not belong to the associated course`,
            ),
          );
        } else {
          enqueueSnackbar(err.message, { variant: "error" });
        }
      });
  }, [getProject, globalErrorSetter, projectId, user]);

  useEffect(() => {
    if (project == null || !user) return;
    getCourseById(project.courseId)
      .then((userCourseResponse) => {
        if (userCourseResponse.role === "EXTERNAL" && !user.isSuperuser) {
          globalErrorSetter(
            new ForbiddenError(
              location.pathname,
              `${user.email} does not belong to the associated course`,
            ),
          );
        }
        setUserCourse(userCourseResponse);
      })
      .catch((err: Error) => {
        if (err instanceof AxiosError && err.response?.status === 410) {
          globalErrorSetter(new ArchivedCourseError(err.message));
        } else {
          enqueueSnackbar(err.message, { variant: "error" });
        }
      });

    getUserLatestProjectSubmission(user, project)
      .then((submission) => {
        setLastSubmission(submission);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [
    getCourseById,
    getUserLatestProjectSubmission,
    globalErrorSetter,
    project,
    user,
  ]);

  if (project == null || userCourse == null) return <></>;

  const handleUpload = () => {
    if (files == null) return;
    uploadProject(project.id, files, () => {
      //TODO
    });
  };

  return (
    <Container
      maxWidth={false}
      sx={{
        height: "100%",
        display: { md: "flex", xs: "block" },
        flexFlow: "column",
      }}
    >
      <Box
        sx={{
          display: { md: "flex", xs: "block" },
          alignItems: "center",
          justifyContent: "center",
          width: "100%",
          position: "relative",
          mb: 1,
        }}
      >
        <Button
          sx={{ position: { md: "absolute", xs: "static" }, left: 0 }}
          variant={"outlined"}
          onClick={() => {
            navigate(`/courses/${userCourse.id}?tab=2`);
          }}
        >
          <ArrowBackIosNewIcon />
          Back to{" "}
          {userCourse.name.length > 20
            ? userCourse.name.substring(0, 18) + "..."
            : userCourse.name}
        </Button>
        <Typography variant={"h3"} textAlign={"center"} sx={{ mb: 1 }}>
          {project.name}
        </Typography>
      </Box>
      <Grid container spacing={2} sx={{ height: "100%" }}>
        <Grid item md={8} xs={12} sx={{ height: "100%" }}>
          <Card
            sx={{ height: "100%", position: "relative" }}
            onDragOver={(event) => {
              event.preventDefault();
              const dt = event.dataTransfer;
              if (dt.types.includes("Files")) {
                setShowFileInput(true);
              }
            }}
            onDragLeave={() => {
              setShowFileInput(false);
            }}
          >
            {showFileInput && (
              <Box
                sx={{
                  position: "absolute",
                  left: 0,
                  top: 0,
                  width: "100%",
                  height: "100%",
                  backgroundColor: "rgba(0,0,0, 0.5)",
                }}
              >
                <Box
                  sx={{
                    m: "1.5rem",
                    border: "dashed grey 2px",
                    width: "calc(100%- 3rem)",
                    height: "calc(100% - 3rem)",
                    zIndex: -1,
                  }}
                  display={"flex"}
                  justifyContent={"center"}
                  alignItems={"center"}
                >
                  <Typography variant={"h3"}>
                    Drop your submission here
                  </Typography>
                </Box>
                <input
                  type={"file"}
                  style={{
                    position: "absolute",
                    top: 0,
                    left: 0,
                    width: "100%",
                    height: "100%",
                    opacity: 0,
                    zIndex: 1,
                  }}
                  {...{
                    webkitdirectory: "",
                    mozdirectory: "",
                    directory: "",
                  }}
                  onChange={(event) => {
                    console.log(event);
                    setFiles(event.target.files);
                    setShowFileInput(false);
                  }}
                ></input>
              </Box>
            )}
            <CardContent
              sx={{
                display: "flex",
                flexFlow: "column",
                height: "100%",
              }}
            >
              <Box
                sx={{
                  maxHeight: "45%",
                }}
              >
                <Typography variant={"h4"} sx={{ mb: 1 }}>
                  Description
                </Typography>
                <Typography variant={"body1"}>{project.description}</Typography>
              </Box>
              <Divider sx={{ my: 2 }} />
              <Typography variant={"h4"}>Submit</Typography>
              <Box
                margin={2}
                height={"100%"}
                display={"flex"}
                justifyContent={"center"}
                alignItems={"center"}
                sx={{
                  backgroundColor: "rgba(0,0,0,0.1)",
                }}
              >
                {!showFileInput &&
                  (files == null ? (
                    <Typography variant={"h5"}>
                      Drag the submbission folder and drop it here
                    </Typography>
                  ) : (
                    <Button onClick={handleUpload}>Upload</Button>
                  ))}
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item md={4} xs={12} sx={{ height: "100%" }}>
          <Card>
            <CardContent>
              {lastSubmission === null && (
                <Typography variant={"h5"}>No submissions found</Typography>
              )}
              {lastSubmission != null && (
                <>
                  <Typography variant={"h5"}>Your last submission</Typography>
                  <Box sx={{ my: 2 }}>
                    <Box display={"flex"} justifyContent={"space-between"}>
                      <Typography variant={"h6"}>
                        Status:{" "}
                        {getSubmissionStatusBadge(lastSubmission.status)}
                      </Typography>
                      {lastSubmission.status === "SUCCESS" && (
                        <ButtonWithConfirmation
                          variant={"outlined"}
                          title={"Confirm submission"}
                          description={
                            "If you confirm the submission, you won't be able to modify it anymore. This action is irreversible."
                          }
                          onConfirm={() => {
                            // TODO: Confirm submission code
                          }}
                        >
                          Confirm Submission
                        </ButtonWithConfirmation>
                      )}
                    </Box>
                    <Typography variant={"h6"} display={"inline"}>
                      Submitted at:{" "}
                    </Typography>
                    <Typography display={"inline"}>
                      {format(lastSubmission.timestamp, "dd/MM/yyyy HH:mm")}
                    </Typography>
                    <Grid container spacing={1} sx={{ mt: 0 }}>
                      <Grid
                        item
                        xl={4}
                        lg={6}
                        xs={12}
                        sx={{ display: "flex", justifyContent: "center" }}
                      >
                        <Button variant={"outlined"} startIcon={<Download />}>
                          Download ZIP
                        </Button>
                      </Grid>
                      <Grid
                        item
                        xl={4}
                        lg={6}
                        xs={12}
                        sx={{ display: "flex", justifyContent: "center" }}
                      >
                        <Button
                          variant={"outlined"}
                          startIcon={<Description />}
                        >
                          Output
                        </Button>
                      </Grid>
                      <Grid
                        item
                        xl={4}
                        xs={12}
                        sx={{ display: "flex", justifyContent: "center" }}
                      >
                        <Button variant={"outlined"} startIcon={<Terminal />}>
                          Execution Log
                        </Button>
                      </Grid>
                    </Grid>
                  </Box>
                  <Divider sx={{ mb: 2 }} />
                  <Typography variant={"h5"}>Submission Structure</Typography>
                  <Typography fontFamily={"monospace"}>Tree view</Typography>
                </>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default ProjectDetailScreen;
