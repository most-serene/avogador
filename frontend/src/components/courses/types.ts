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

interface UserCourse {
  course: Course;
  id: string;
  joinDate: Date;
  role: "STUDENT" | "COLLABORATOR" | "ADMIN";
  user: string;
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
  UserCourse,
  GetCoursesDetailResponse,
  CourseMemberDetail,
};
