interface Course {
  id: string;
  name: string;
  year: string;
  isArchived: boolean;
}

interface GetCoursesResponse {
  course: Course;
  id: number;
  joinDate: Date;
  role: "STUDENT" | "COLLABORATOR" | "ADMIN";
  user: number;
}

export type { Course, GetCoursesResponse };
