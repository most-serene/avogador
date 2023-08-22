interface Course {
  id: string;
  name: string;
  year: string;
  isArchived: boolean;
}

interface CourseDetail {
  id: string;
  name: string;
  year: string;
  isArchived: boolean;
  role: "STUDENT" | "COLLABORATOR" | "ADMIN";
}

interface GetCoursesResponse {
  course: Course;
  id: number;
  joinDate: Date;
  role: "STUDENT" | "COLLABORATOR" | "ADMIN";
  user: number;
}

interface GetCoursesDetailResponse {
  id: string;
  isArchived: boolean;
  name: string;
  year: string;
  role: "STUDENT" | "COLLABORATOR" | "ADMIN";
  joinCode?: string;
}

export type {
  Course,
  CourseDetail,
  GetCoursesResponse,
  GetCoursesDetailResponse,
};
