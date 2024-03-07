import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import {
  Course,
  CourseMemberDetail,
  UserCourseDetail,
  UserCourse,
} from "@courses/types";
// eslint-disable-next-line import/named
import { AxiosError, AxiosProgressEvent } from "axios";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom";
import { enqueueSnackbar } from "notistack";
import { saveResponseToFile } from "../../../utils/fileHandling.ts";

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

  const updateCourse: (course: Course) => Promise<Course> = useCallback(
    async (course: Course) => {
      const { data: updatedCourse }: { data: Course } = await avogadorApi.put(
        `/courses/${course.id}`,
        course,
      );
      return updatedCourse;
    },
    [avogadorApi],
  );

  const downloadCourseArchive: (
    course: Course,
    onDownloadProgress: (progressEvent: AxiosProgressEvent) => void,
  ) => Promise<void> = useCallback(
    async (
      course: Course,
      onDownloadProgress: (progressEvent: AxiosProgressEvent) => void,
    ) => {
      enqueueSnackbar("Download started", { variant: "info" });
      try {
        const res = await avogadorApi.get(`/courses/${course.id}/archive`, {
          responseType: "blob",
          onDownloadProgress: onDownloadProgress,
        });
        saveResponseToFile(res, `${course.name}.tar.gz`);
        return Promise.resolve();
      } catch (err) {
        enqueueSnackbar((err as Error).message, { variant: "error" });
        return Promise.reject();
      }
    },
    [avogadorApi],
  );

  const archiveCourse: (course: Course) => Promise<Course> = useCallback(
    async (course: Course) => {
      const { data: updatedCourse }: { data: Course } = await avogadorApi.put(
        `/courses/${course.id}/archive`,
        course,
      );
      return updatedCourse;
    },
    [avogadorApi],
  );

  const deleteCourse: (course: Course) => Promise<void> = useCallback(
    async (course: Course) => {
      await avogadorApi.delete(`/courses/${course.id}`);
    },
    [avogadorApi],
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
    updateCourse,
    archiveCourse,
    deleteCourse,
    downloadCourseArchive,
  };
};

export default useCourseService;
