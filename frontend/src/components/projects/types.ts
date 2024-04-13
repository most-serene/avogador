interface Project {
  id: string;
  courseId: string;
  name: string;
  description: string;
  canSubmit: boolean;
  deadline: Date;
}

type ProjectStatus = "PENDING" | "ERROR" | "SUCCESS" | "CONFIRMED";

interface ProjectSubmission {
  id: string;
  project: Project;
  userId: string;
  timestamp: Date;
  status: ProjectStatus;
}

export type { Project, ProjectStatus, ProjectSubmission };
