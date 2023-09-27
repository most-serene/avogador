interface Course {
  id: string;
  name: string;
  year: string;
  isArchived: boolean;
}

interface CourseDetail extends Course {
  role: "STUDENT" | "COLLABORATOR" | "ADMIN" | "EXTERNAL";
}

interface UserCourse {
  course: Course;
  id: string;
  joinDate: Date;
  role: "STUDENT" | "COLLABORATOR" | "ADMIN" | "EXTERNAL";
  user: string;
}

interface UserCourseDetail extends CourseDetail {
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
  role: "STUDENT" | "COLLABORATOR" | "ADMIN" | "EXTERNAL";
  joinDate: Date;
}

export type {
  Course,
  CourseDetail,
  UserCourse,
  UserCourseDetail,
  CourseMemberDetail,
};
