import { CircularProgress } from "@mui/material";
import { useParams } from "react-router-dom";
import { useEffect } from "react";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import { AxiosError } from "axios";
import { useGlobalErrorSetter } from "@components/error/GlobalErrorState.tsx";
import {
  ArchivedCourseError,
  ForbiddenError,
  ResourceNotFoundError,
} from "@components/error/types.ts";
import { courseDetailAtom } from "@courses/courseDetail/courseDetailAtom";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import CourseDetailCollaboratorScreen from "@courses/courseDetail/CourseDetailCollaboratorScreen.tsx";
import CourseDetailStudentScreen from "@courses/courseDetail/CourseDetailStudentScreen.tsx";
import { enqueueSnackbar } from "notistack";
import Box from "@mui/material/Box";

export default function CourseDetailScreen() {
  const { getCourseById } = useCourseService();
  const { courseId } = useParams();
  const globalErrorSetter = useGlobalErrorSetter();
  const [course, setCourse] = useAtom(courseDetailAtom);
  const [user] = useAtom(userAtom);

  useEffect(() => {
    if (courseId === undefined) return;

    getCourseById(courseId)
      .then((c) => {
        setCourse(c);
      })
      .catch((err: Error) => {
        if (
          err instanceof AxiosError &&
          (err.response?.status === 404 || err.response?.status === 400)
        ) {
          globalErrorSetter(
            new ResourceNotFoundError(
              { id: courseId },
              "Course",
              `Course ${courseId} not found`,
            ),
          );
        } else if (err instanceof AxiosError && err.response?.status === 410) {
          globalErrorSetter(new ArchivedCourseError(err.message));
        } else {
          enqueueSnackbar(err.message, { variant: "error" });
        }
      });

    return () => {
      setCourse(undefined);
    };
  }, [getCourseById, courseId, globalErrorSetter, setCourse]);

  useEffect(() => {
    if (!course || !user) return;
    if (course.role === "EXTERNAL" && !user.isSuperuser) {
      globalErrorSetter(
        new ForbiddenError(
          location.pathname,
          `${user.email} does not belong to the associated course`,
        ),
      );
    }
  }, [course, globalErrorSetter, user]);

  if (user == null || course == null) {
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

  if (
    user.isSuperuser ||
    course.role === "COLLABORATOR" ||
    course.role === "ADMIN"
  ) {
    return <CourseDetailCollaboratorScreen />;
  }
  return <CourseDetailStudentScreen />;
}
