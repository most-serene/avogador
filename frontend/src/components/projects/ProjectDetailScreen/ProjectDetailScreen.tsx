import {
  Box,
  Button,
  Card,
  CardContent,
  Container,
  Grid,
  Typography,
} from "@mui/material";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { Project, ProjectSubmission } from "@components/projects/types.ts";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { enqueueSnackbar } from "notistack";
import { AxiosError } from "axios";
import { ArchivedCourseError, ForbiddenError } from "@error/types.ts";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import { CourseDetail } from "@courses/types.ts";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import LastProjectSubmission from "@components/projects/ProjectDetailScreen/LastProjectSubmission.tsx";
import ProjectUploadForm from "@components/projects/ProjectDetailScreen/ProjectUploadForm.tsx";
import useWebSocket from "@hooks/useWebSocket.tsx";
import { Message } from "@stomp/stompjs";

const ProjectDetailScreen = () => {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const globalErrorSetter = useGlobalErrorSetter();
  const [user] = useAtom(userAtom);

  const { getProject, getUserLatestProjectSubmission } = useProjectService();
  const { getCourseById } = useCourseService();

  const [project, setProject] = useState<Project>();
  const [userCourse, setUserCourse] = useState<CourseDetail>();
  const [lastSubmission, setLastSubmission] =
    useState<ProjectSubmission | null>();

  const { subscribe } = useWebSocket();

  useEffect(() => {
    if (lastSubmission == null) return;
    subscribe(`/${lastSubmission.id}/status`, (message: Message) => {
      console.log(message);
      const projectSubmission = JSON.parse(message.body) as ProjectSubmission;
      setLastSubmission({ ...projectSubmission });
    })
      .then(() => {
        // empty-function
      })
      .catch((err) => {
        console.error(err);
      });
  }, [lastSubmission, subscribe]);

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
      <Grid
        container
        spacing={2}
        style={{ height: "100%", overflowY: "hidden" }}
      >
        <Grid item md={8} xs={12} sx={{ height: "100%" }}>
          <ProjectUploadForm
            project={project}
            setSubmission={(submission) => {
              setLastSubmission(submission);
            }}
          >
            <Box
              sx={{
                minHeight: "7rem",
                maxHeight: "45%",
                overflowY: "auto",
              }}
            >
              <Typography variant={"h4"} sx={{ mb: 1 }}>
                Description
              </Typography>
              <Typography variant={"body1"}>{project.description}</Typography>
            </Box>
          </ProjectUploadForm>
        </Grid>
        <Grid item md={4} xs={12} sx={{ height: "100%" }}>
          <Card sx={{ height: "100%" }}>
            <CardContent
              sx={{ display: "flex", flexFlow: "column", height: "100%" }}
            >
              {lastSubmission === null && (
                <Typography variant={"h5"}>No submission uploaded</Typography>
              )}
              {lastSubmission != null && (
                <LastProjectSubmission submission={lastSubmission} />
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default ProjectDetailScreen;
