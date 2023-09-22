import { UserCourseDetail } from "@courses/types";

export const getJoinLink = (course: UserCourseDetail) => {
  return `https://${window.location.hostname}/courses/${course.id}/join?code=${course.joinCode}`;
};
