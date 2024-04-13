import { Project, ProjectSubmission } from "@components/projects/types.ts";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { useNavigate } from "react-router-dom";
import { useAtom } from "jotai/index";
import userAtom from "@authentication/userAtom.ts";
import { useEffect, useState } from "react";
import { enqueueSnackbar } from "notistack";
import {
  Card,
  CardActionArea,
  CardContent,
  Divider,
  Typography,
} from "@mui/material";
import Box from "@mui/material/Box";
import { format } from "date-fns";

interface ProjectItemProps {
  project: Project;
}
const ProjectItem = ({ project }: ProjectItemProps) => {
  const { getUserLatestProjectSubmission } = useProjectService();
  const navigate = useNavigate();
  const [user] = useAtom(userAtom);
  const [lastSubmission, setLastSubmission] =
    useState<ProjectSubmission | null>();

  useEffect(() => {
    if (user == null) return;
    getUserLatestProjectSubmission(user, project)
      .then((submission) => {
        setLastSubmission(submission);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [project, user, getUserLatestProjectSubmission]);

  return (
    <Card
      sx={{
        width: "30%",
        minWidth: { xl: "30%", lg: "45%", xs: "90%" },
        flexBasis: "auto",
        textOverflow: "ellipsis",
        position: "relative",
      }}
      raised
    >
      <CardActionArea
        sx={{ height: "100%" }}
        component="a"
        onClick={() => {
          navigate(`/projects/${project.id}`);
        }}
      >
        <CardContent sx={{ height: "100%" }}>
          <Box
            display={"flex"}
            justifyContent="space-between"
            alignItems={"center"}
            sx={{ mb: 1 }}
          >
            <Typography variant="h5" fontWeight="bold" width={"70%"} noWrap>
              {project.name}
            </Typography>
            <Typography variant="body1" textAlign={"right"}>
              Due to:
              <br /> {format(project.deadline, "dd/MM/yyyy HH:mm")}
            </Typography>
          </Box>
          <Typography
            variant="body1"
            fontStyle="italic"
            sx={{
              display: "-webkit-box",
              textOverflow: "ellipsis",
              wordWrap: "break-word",
              overflow: "hidden",
              WebkitLineClamp: 4,
              lineClamp: 4,
              WebkitBoxOrient: "vertical",
            }}
          >
            {project.description}
          </Typography>
        </CardContent>
        {lastSubmission != null && (
          <Box
            position={"absolute"}
            width={"100%"}
            style={{
              backgroundColor: "rgba(128, 128, 128, 0.5)",
              bottom: 0,
            }}
          >
            <Divider />
            <Typography padding={1}>
              Last submission:{" "}
              {format(lastSubmission.timestamp, "dd/MM/yyyy HH:mm")}
            </Typography>
          </Box>
        )}
      </CardActionArea>
    </Card>
  );
};

export default ProjectItem;
