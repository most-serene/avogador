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

interface CourseMemberDetail {
  id: string;
  user: {
    id: string;
    email: string;
    givenName: string;
    familyName: string;
    isProfessor: boolean;
    isSuperuser: boolean;
  };
  courseId: string;
  role: "STUDENT" | "COLLABORATOR" | "ADMIN";
  joinDate: string;
}

export type {
  Course,
  CourseDetail,
  GetCoursesResponse,
  GetCoursesDetailResponse,
  CourseMemberDetail,
};
