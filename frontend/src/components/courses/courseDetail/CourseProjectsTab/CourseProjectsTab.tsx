import {
  Card,
  CardActionArea,
  CardContent,
  Stack,
  Typography,
} from "@mui/material";
import Box from "@mui/material/Box";
import { useEffect, useState } from "react";
import { UserCourseDetail } from "@courses/types.ts";
import { Project } from "@components/projects/types.ts";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { enqueueSnackbar } from "notistack";
import ProjectItem from "@components/projects/ProjectItem.tsx";
import ProjectItemSkeleton from "@components/projects/ProjectItemSkeleton.tsx";
import { Add } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";

interface CourseProjectsTabProps {
  userCourse: UserCourseDetail | undefined;
}

interface CreateProjectButtonProps {
  courseId: string;
}

const CreateProjectButton = ({ courseId }: CreateProjectButtonProps) => {
  const navigate = useNavigate();

  return (
    <Card
      sx={{
        minWidth: { xs: "14rem" },
        flexBasis: "auto",
        height: "100%",
        border: 2,
        borderColor: "primary.main",
        borderStyle: "dashed",
        minHeight: "5rem",
      }}
      elevation={0}
    >
      <CardActionArea
        sx={{ height: "100%" }}
        component="a"
        onClick={() => {
          navigate("/projects/new", {
            state: {
              courseId: courseId,
            },
          });
        }}
      >
        <Box
          display="flex"
          justifyContent="center"
          alignItems="center"
          height="100%"
          padding={2}
          sx={{
            whiteSpace: "nowrap",
          }}
        >
          <Add fontSize={"large"} sx={{ mr: 2 }} />
          <Typography variant="h5"> New project </Typography>
        </Box>
      </CardActionArea>
    </Card>
  );
};

const CourseProjectsTab = ({ userCourse }: CourseProjectsTabProps) => {
  const [user] = useAtom(userAtom);
  const { getProjectsByCourse } = useProjectService();
  const [projects, setProjects] = useState<Project[]>();

  useEffect(() => {
    if (userCourse == null) return;
    getProjectsByCourse(userCourse.id)
      .then((projects) => {
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
            {userCourse != null &&
              (userCourse.role === "COLLABORATOR" ||
                userCourse.role === "ADMIN" ||
                (user != null && user.isSuperuser)) && (
                <CreateProjectButton courseId={userCourse.id} />
              )}
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
