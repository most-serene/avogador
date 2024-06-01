import { useParams } from "react-router-dom";
import {
  Box,
  Card,
  CardContent,
  CircularProgress,
  Container,
  Divider,
  Grid,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { Project, ProjectSubmission } from "@components/projects/types.ts";
import { enqueueSnackbar } from "notistack";
import useUserService from "@components/users/hooks/useUserService.tsx";
import { User } from "@authentication/types.ts";
import { format } from "date-fns";
import ProjectSubmissionButtons from "@components/projects/ProjectDetailScreen/ProjectSubmissionButtons/ProjectSubmissionButtons.tsx";
import ProjectSubmissionStatus from "@components/projects/ProjectDetailScreen/ProjectSubmissionStatus.tsx";
import BackButton from "@components/structure/BackButton";

const ProjectSubmissionDetailScreen = () => {
  const { projectId, userId } = useParams();
  const {
    getProject,
    getUserLatestProjectSubmission,
    getSubmissionTree,
    downloadOutputFile,
  } = useProjectService();
  const { getUserById } = useUserService();

  const [project, setProject] = useState<Project>();
  const [submission, setSubmission] = useState<ProjectSubmission | null>();
  const [submitter, setSubmitter] = useState<User>();
  const [tree, setTree] = useState<string>();
  const [outputHtml, setOutputHtml] = useState<string>();
  const [progress, setProgress] = useState<number>();

  useEffect(() => {
    if (projectId == null) return;
    getProject(projectId)
      .then((project) => {
        setProject(project);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [projectId, getProject]);

  useEffect(() => {
    if (userId == null || project == null) return;
    getUserLatestProjectSubmission(userId, project)
      .then((submission) => {
        setSubmission(submission);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [project, userId, getUserLatestProjectSubmission]);

  useEffect(() => {
    if (submission == null) return;
    getUserById(submission.userId)
      .then((user) => {
        setSubmitter(user);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });

    getSubmissionTree(submission)
      .then((result) => result.text())
      .then((content) => {
        setTree(content);
      })
      .catch(() => {
        setTree(undefined);
      });

    downloadOutputFile(submission, (progressEvent) => {
      if (progressEvent.total != null) {
        setProgress(
          Math.round((100 * progressEvent.loaded) / progressEvent.total),
        );
      }
    })
      .then((result) => (result.data as Blob).text())
      .then((content: string) => {
        setOutputHtml(content);
      })
      .catch(() => {
        setTree(undefined);
      });
  }, [submission, getUserById, getSubmissionTree, downloadOutputFile]);

  if (submission == null || submitter == null) {
    return (
      <Box
        style={{
          display: "flex",
          height: "100%",
        }}
        justifyContent={"center"}
        alignItems={"center"}
      >
        <CircularProgress size={80} />
      </Box>
    );
  }

  return (
    <Container
      maxWidth={false}
      sx={{
        position: "relative",
        height: "100%",
        display: "flex",
        flexFlow: "column",
      }}
    >
      <BackButton destination={`/projects/${submission.project.id}?tab=1`}>
        {submission.project.name}
      </BackButton>
      <Typography variant={"h4"} textAlign={"center"} sx={{ mb: 2 }}>
        {submitter.familyName} {submitter.givenName} - {submission.project.name}
      </Typography>
      <Grid container spacing={2} sx={{ height: "100%" }}>
        <Grid item xs={9} sx={{ height: "100%" }}>
          <Card sx={{ height: "100%" }}>
            <CardContent sx={{ height: "100%" }}>
              {outputHtml == null ? (
                <Box
                  height="100%"
                  width="100%"
                  display="flex"
                  justifyContent="center"
                  alignItems="center"
                >
                  <CircularProgress variant="determinate" value={progress} />
                </Box>
              ) : (
                <iframe
                  loading="lazy"
                  width="100%"
                  height="100%"
                  style={{ borderRadius: "8px" }}
                  title="submission-output"
                  srcDoc={outputHtml}
                  id="iframe"
                />
              )}
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={3} sx={{ height: "100%" }}>
          <Card sx={{ height: "100%" }}>
            <CardContent sx={{ height: "100%" }}>
              <Typography variant={"h6"} display={"inline"}>
                Submitted at:{" "}
              </Typography>
              <Typography display={"inline"}>
                {format(submission.timestamp, "dd/MM/yyyy HH:mm")}
              </Typography>
              <Box sx={{ mb: 1 }}>
                <ProjectSubmissionStatus submission={submission} />
              </Box>

              <ProjectSubmissionButtons submission={submission} />

              <Divider sx={{ my: 1 }} />
              <Box overflow={"auto"}>
                <Typography
                  fontFamily={"monospace"}
                  sx={{
                    whiteSpace: "pre",
                    display: "inline",
                  }}
                >
                  {tree}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default ProjectSubmissionDetailScreen;
