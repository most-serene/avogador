import {
  Card,
  CardActionArea,
  CardContent,
  Divider,
  Skeleton,
  Stack,
  Typography,
} from "@mui/material";
import Box from "@mui/material/Box";
import { useEffect, useState } from "react";
import { UserCourseDetail } from "@courses/types.ts";
import { Project, ProjectSubmission } from "@components/projects/types.ts";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { enqueueSnackbar } from "notistack";
import { format } from "date-fns";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { useNavigate } from "react-router-dom";

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
              Last submission: YYYY/MM/DD hh:mm
            </Typography>
          </Box>
        )}
      </CardActionArea>
    </Card>
  );
};

const ProjectItemSkeleton = () => {
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
      <CardContent sx={{ height: "100%" }}>
        <Box
          display={"flex"}
          justifyContent="space-between"
          alignItems={"center"}
          sx={{ mb: 1 }}
        >
          <Typography variant="h5" fontWeight="bold" width={"60%"} noWrap>
            <Skeleton />
          </Typography>
          <div>
            <Typography variant="body1">
              <Skeleton sx={{ ml: "4rem" }} />
            </Typography>
            <Typography variant="body1" width={"8rem"}>
              <Skeleton />
            </Typography>
          </div>
        </Box>
        <Typography variant="body1" fontStyle="italic">
          <Skeleton />
          <Skeleton />
          <Skeleton />
        </Typography>
      </CardContent>
    </Card>
  );
};

interface CourseProjectsTabProps {
  userCourse: UserCourseDetail | undefined;
}

const CourseProjectsTab = ({ userCourse }: CourseProjectsTabProps) => {
  const [projects, setProjects] = useState<Project[]>();
  const { getProjectsByCourse } = useProjectService();

  useEffect(() => {
    if (userCourse == null) return;
    getProjectsByCourse(userCourse.id)
      .then((projects) => {
        console.log(projects);
        setProjects([...projects]);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [userCourse, getProjectsByCourse]);

  return (
    <Box width={"100%"} height="100%" paddingBottom={0}>
      <Card sx={{ height: "50%", m: 1, minHeight: "20rem", width: "100%" }}>
        <CardContent
          sx={{
            height: "100%",
            display: "flex",
            flexFlow: "column",
            width: "100%",
          }}
        >
          <Typography variant="h4" sx={{ mb: 1 }}>
            Ongoing
          </Typography>
          <Stack
            direction={"row"}
            spacing={2}
            sx={{
              width: "100%",
              py: 1,
              overflowX: "scroll",
              height: "100%",
            }}
            className={"hidden-scrollbar"}
          >
            {projects
              ?.filter((project) => project.deadline > new Date())
              .map((project) => (
                <ProjectItem project={project} key={project.id} />
              )) ?? <ProjectItemSkeleton />}
          </Stack>
        </CardContent>
      </Card>

      <Card sx={{ height: "50%", m: 1, minHeight: "20rem", width: "100%" }}>
        <CardContent
          sx={{
            height: "100%",
            display: "flex",
            flexFlow: "column",
            width: "100%",
          }}
        >
          <Typography variant="h4" sx={{ mb: 1 }}>
            Finished
          </Typography>
          <Stack
            direction={"row"}
            spacing={2}
            sx={{
              width: "100%",
              py: 1,
              overflowX: "scroll",
              height: "100%",
            }}
            className={"hidden-scrollbar"}
          >
            {projects
              ?.filter((project) => project.deadline <= new Date())
              .map((project) => (
                <ProjectItem project={project} key={project.id} />
              )) ?? <ProjectItemSkeleton />}
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
};

export default CourseProjectsTab;
