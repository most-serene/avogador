import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { Project, UserProject } from "@components/projects/types.ts";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { enqueueSnackbar } from "notistack";
import { AxiosError } from "axios";
import { ArchivedCourseError, ForbiddenError } from "@error/types.ts";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import { CourseDetail } from "@courses/types.ts";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import ProjectDetailCollaboratorScreen from "@components/projects/ProjectDetailScreen/ProjectDetailCollaboratorScreen.tsx";
import ProjectDetailStudentScreen from "@components/projects/ProjectDetailScreen/ProjectDetailStudentScreen.tsx";
import JoinProjectScreen from "@components/projects/JoinProjectScreen/JoinProjectScreen.tsx";
import Box from "@mui/material/Box";
import { CircularProgress } from "@mui/material";

const ProjectDetailScreen = () => {
  const { projectId } = useParams();
  const globalErrorSetter = useGlobalErrorSetter();
  const [user] = useAtom(userAtom);

  const { getProject, getSelfUserProject, joinProject } = useProjectService();
  const { getCourseById } = useCourseService();

  const [project, setProject] = useState<Project>();
  const [selfUserProject, setSelfUserProject] = useState<UserProject | null>();
  const [userCourse, setUserCourse] = useState<CourseDetail>();

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
    if (project == null) return;
    getSelfUserProject(project)
      .then((selfUserProjectResponse) => {
        setSelfUserProject(selfUserProjectResponse);
      })
      .catch((err: Error) => {
        if (err instanceof AxiosError && err.response?.status === 404) {
          setSelfUserProject(null);
        } else {
          enqueueSnackbar(err.message, { variant: "error" });
        }
      });
  }, [getSelfUserProject, project]);

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
          return;
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
  }, [getCourseById, globalErrorSetter, project, user]);

  if (project == null || userCourse == null || selfUserProject === undefined) {
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

  if (userCourse.role === "STUDENT") {
    if (selfUserProject === null) {
      return (
        <JoinProjectScreen
          project={project}
          joinHandler={() => {
            joinProject(project)
              .then((up) => {
                setSelfUserProject(up);
              })
              .catch((err: Error) => {
                enqueueSnackbar(err.message, { variant: "error" });
              });
          }}
        />
      );
    }
    return <ProjectDetailStudentScreen project={project} course={userCourse} />;
  }

  return (
    <ProjectDetailCollaboratorScreen
      project={project}
      course={userCourse}
      onUpdate={(project) => {
        setProject(project);
      }}
    />
  );
};

export default ProjectDetailScreen;
