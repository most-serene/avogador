import { User } from "@authentication/types.ts";

interface Project {
  id: string;
  courseId: string;
  name: string;
  description: string;
  canSubmit: boolean;
  deadline: Date;
  projectType: ProjectType;
}

interface NotebookProject extends Project {
  kernel: NotebookKernel;
}
function isNotebookProject(project: Project): project is NotebookProject {
  return project.projectType === "NOTEBOOK";
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

interface UserProjectDetail {
  id: string;
  projectId: string;
  user: User;
  mark?: number;
}

interface ProjectSubmissionDetail extends UserProjectDetail {
  submissionId?: string;
  timestamp?: Date;
  status: ProjectStatus | undefined;
}

type NotebookKernel = "IPYKERNEL";

interface ProjectData {
  isValid: boolean;
}

interface NotebookData extends ProjectData {
  kernel: NotebookKernel | undefined;
}

interface UserProject {
  id: string;
  userId: string;
  project: Project;
  mark: number;
}

export type {
  Project,
  NotebookProject,
  ProjectStatus,
  ProjectType,
  ProjectSubmission,
  UserProjectDetail,
  ProjectSubmissionDetail,
  NotebookKernel,
  ProjectData,
  NotebookData,
  UserProject,
};
export { isNotebookProject };
