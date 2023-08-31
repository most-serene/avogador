import { useCallback } from "react";
import { useAvogadorApi } from "../../../hooks/useAvogadorApi";
import { Course, CourseMemberDetail, GetCoursesDetailResponse } from "../types";
import { AxiosError } from "axios";

const useCourseService = () => {
  const avogadorApi = useAvogadorApi();

  const getCourseById: (courseId: string) => Promise<GetCoursesDetailResponse> =
    useCallback(
      async (courseId: string) => {
        const { data: course }: { data: GetCoursesDetailResponse } =
          await avogadorApi.get(`/courses/${courseId}`);
        return course;
      },
      [avogadorApi],
    );

  const joinCourse: (course: Course, joinCode: string) => Promise<boolean> =
    useCallback(
      async (course: Course, joinCode: string) => {
        try {
          await avogadorApi.put(`/courses/${course.id}/join/${joinCode}`);
          return true;
        } catch (e) {
          console.log(e);

          if (e instanceof AxiosError && e.response?.status === 403) {
            return false;
          }
          throw e;
        }
      },
      [avogadorApi],
    );

  const getCourseMembers: (courseId: string) => Promise<CourseMemberDetail[]> =
    useCallback(
      async (courseId: string) => {
        const { data }: { data: CourseMemberDetail[] } = await avogadorApi.get(
          `courses/${courseId}/users`,
        );

        return data;
      },
      [avogadorApi],
    );

  return {
    getCourseById,
    joinCourse,
    getCourseMembers,
  };
};

export default useCourseService;
