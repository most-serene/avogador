interface Project {
  id: string;
  courseId: string;
  name: string;
  description: string;
  canSubmit: boolean;
  deadline: Date;
}

type ProjectStatus = "PENDING" | "ERROR" | "SUCCESS" | "CONFIRMED";

type ProjectType = "NOTEBOOK" | "OTHER";

interface ProjectSubmission {
  id: string;
  project: Project;
  userId: string;
  timestamp: Date;
  status: ProjectStatus;
}

type NotebookKernel = "IPYKERNEL";

interface ProjectData {
  isValid: boolean;
}

interface NotebookData extends ProjectData {
  kernel: NotebookKernel | undefined;
}

export type {
  Project,
  ProjectStatus,
  ProjectType,
  ProjectSubmission,
  NotebookKernel,
  ProjectData,
  NotebookData,
};
