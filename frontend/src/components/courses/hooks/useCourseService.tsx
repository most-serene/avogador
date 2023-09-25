import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import {
  Course,
  CourseMemberDetail,
  UserCourseDetail,
  UserCourse,
} from "@courses/types";
import { AxiosError } from "axios";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom";

const useCourseService = () => {
  const avogadorApi = useAvogadorApi();
  const [user] = useAtom(userAtom);

  const getCourseById: (courseId: string) => Promise<UserCourseDetail> =
    useCallback(
      async (courseId: string) => {
        const { data: course }: { data: UserCourseDetail } =
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

  const promoteUser: (courseId: string, userId: string) => Promise<UserCourse> =
    useCallback(
      async (courseId: string, userId: string) => {
        const { data }: { data: UserCourse } = await avogadorApi.put(
          `courses/${courseId}/collaborators/${userId}`,
        );

        return data;
      },
      [avogadorApi],
    );

  const demoteUser: (courseId: string, userId: string) => Promise<UserCourse> =
    useCallback(
      async (courseId: string, userId: string) => {
        const { data }: { data: UserCourse } = await avogadorApi.put(
          `courses/${courseId}/students/${userId}`,
        );

        return data;
      },
      [avogadorApi],
    );

  const getUserCourses: (userId: string) => Promise<UserCourse[]> = useCallback(
    async (userId: string) => {
      const { data }: { data: UserCourse[] } = await avogadorApi.get(
        `/courses/users/${userId}`,
      );
      localStorage.setItem("coursesNumber", String(data.length));
      return data;
    },
    [avogadorApi],
  );

  const createCourse: (name: string, year: string) => Promise<Course> =
    useCallback(
      async (name: string, year: string) => {
        const { data }: { data: Course } = await avogadorApi.post("/courses", {
          name,
          year,
        });
        return data;
      },
      [avogadorApi],
    );

  const leaveCourse: (course: Course) => Promise<void> = useCallback(
    async (course: Course) => {
      if (user == null) {
        throw new Error("user not defined");
      }
      return avogadorApi.delete(`courses/${course.id}/users/${user.id}`);
    },
    [avogadorApi, user],
  );

  return {
    getCourseById,
    joinCourse,
    getCourseMembers,
    promoteUser,
    demoteUser,
    getUserCourses,
    createCourse,
    leaveCourse,
  };
};

export default useCourseService;
