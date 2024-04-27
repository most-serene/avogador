import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { Project } from "@components/projects/types.ts";
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

const ProjectDetailScreen = () => {
  const { projectId } = useParams();
  const globalErrorSetter = useGlobalErrorSetter();
  const [user] = useAtom(userAtom);

  const { getProject } = useProjectService();
  const { getCourseById } = useCourseService();

  const [project, setProject] = useState<Project>();
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

  if (project == null || userCourse == null) return <></>;

  // TODO: check if user hasn't joined

  if (userCourse.role === "STUDENT") {
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
