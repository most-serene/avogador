import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import { useAtom } from "jotai/index";
import userAtom from "@authentication/userAtom.ts";
import useWebSocket from "@hooks/useWebSocket.tsx";
import {
  Box,
  Card,
  CardContent,
  Container,
  Grid,
  Typography,
  useTheme,
} from "@mui/material";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { useEffect, useMemo, useState } from "react";
import { Project, ProjectSubmission } from "@components/projects/types.ts";
import { Message } from "@stomp/stompjs";
import { enqueueSnackbar } from "notistack";
import ProjectUploadForm from "@components/projects/ProjectDetailScreen/ProjectUploadForm.tsx";
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";
import LastProjectSubmission from "@components/projects/ProjectDetailScreen/LastProjectSubmission.tsx";
import { CourseDetail } from "@courses/types.ts";

interface ProjectOverviewTabProps {
  project: Project;
  course: CourseDetail;
}

const ProjectOverviewTab = ({ project, course }: ProjectOverviewTabProps) => {
  const globalErrorSetter = useGlobalErrorSetter();
  const [user] = useAtom(userAtom);
  const { subscribe } = useWebSocket();
  const theme = useTheme();

  const { getUserLatestProjectSubmission } = useProjectService();

  const [lastSubmission, setLastSubmission] =
    useState<ProjectSubmission | null>();
  const isSubmissionConfirmed = useMemo(
    () => lastSubmission != null && lastSubmission.status === "CONFIRMED",
    [lastSubmission],
  );

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
    if (!user) return;

    getUserLatestProjectSubmission(user, project)
      .then((submission) => {
        setLastSubmission(submission);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [getUserLatestProjectSubmission, globalErrorSetter, project, user]);

  return (
    <Container
      id="project-overview-container"
      maxWidth={false}
      sx={{
        height: "100%",
        display: { md: "flex", xs: "block" },
        flexFlow: "column",
      }}
    >
      <Grid
        container
        spacing={2}
        style={{ height: "100%", overflowY: "auto" }}
        className="hidden-scrollbar"
      >
        <Grid item md={8} xs={12} sx={{ height: "100%" }}>
          <ProjectUploadForm
            disabled={
              lastSubmission != null && lastSubmission.status === "CONFIRMED"
            }
            project={project}
            onUpload={(submission) => {
              setLastSubmission(submission);
            }}
            course={course}
          >
            <Box
              sx={{
                minHeight: "7rem",
                maxHeight: isSubmissionConfirmed ? "100%" : "45%",
                height: isSubmissionConfirmed ? "100%" : undefined,
                overflowY: "auto",
              }}
            >
              <Typography variant={"h4"} sx={{ mb: 1 }}>
                Description
              </Typography>
              <Markdown
                remarkPlugins={[remarkGfm]}
                className={`md-text ${theme.palette.mode}`}
              >
                {project.description}
              </Markdown>
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
                <LastProjectSubmission
                  submission={lastSubmission}
                  onConfirm={setLastSubmission}
                />
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default ProjectOverviewTab;
