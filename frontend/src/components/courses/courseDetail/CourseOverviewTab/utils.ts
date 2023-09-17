import { GetCoursesDetailResponse } from "@courses/types";

export const getJoinLink = (course: GetCoursesDetailResponse) => {
  return `https://${window.location.hostname}/courses/${course.id}/join?code=${course.joinCode}`;
};
