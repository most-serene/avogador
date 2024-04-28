import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import JSZip from "jszip";
import {
  Project,
  ProjectSubmission,
  UserProject,
} from "@components/projects/types.ts";
// eslint-disable-next-line import/named
import { AxiosProgressEvent } from "axios";
import { enqueueSnackbar } from "notistack";
import { User } from "@authentication/types.ts";

const useProjectService = () => {
  const avogadorApi = useAvogadorApi();

  const getProject: (projectId: string) => Promise<Project> = useCallback(
    async (projectId: string) => {
      const { data: project }: { data: Project } = await avogadorApi.get(
        `/projects/${projectId}`,
      );
      return project;
    },
    [avogadorApi],
  );

  const createArchive = useCallback((files: FileList) => {
    const generateFilesStream = (files: FileList) => {
      const arr: File[] = [];
      for (const file of files) {
        console.log(file);
        arr.push(file);
      }
      return arr;
    };

    const zip = new JSZip();
    generateFilesStream(files).map((file: File) => {
      zip.file(file.webkitRelativePath, file);
    });

    return zip.generateAsync({ type: "blob" });
  }, []);

  const mapZipBlobToFormData = useCallback((archive: Blob) => {
    const form = new FormData();
    form.append("project", archive, "project.zip");
    return form;
  }, []);

  const uploadProject: (
    projectId: string,
    files: FileList,
    onUploadProgress: (progressEvent: AxiosProgressEvent) => void,
    onFinish: () => void,
  ) => Promise<ProjectSubmission> = useCallback(
    (
      projectId: string,
      files: FileList,
      onUploadProgress: (progressEvent: AxiosProgressEvent) => void,
      onFinish: () => void,
    ) => {
      return createArchive(files)
        .then(mapZipBlobToFormData)
        .then((form) =>
          avogadorApi.post(
            `/projects/notebook/${projectId}/submissions`,
            form,
            {
              headers: {
                "Content-Type": "multipart/form-data",
              },
              onUploadProgress,
            },
          ),
        )
        .then(({ data: submission }: { data: ProjectSubmission }) => {
          enqueueSnackbar("Project submitted, waiting for execution", {
            variant: "success",
          });
          return submission;
        })
        .finally(() => {
          onFinish();
        });
    },
    [avogadorApi, createArchive, mapZipBlobToFormData],
  );

  const confirmSubmission: (
    submission: ProjectSubmission,
  ) => Promise<ProjectSubmission> = useCallback(
    async (submission: ProjectSubmission) => {
      const { data: confirmedSubmission }: { data: ProjectSubmission } =
        await avogadorApi.put(
          `/projects/${submission.project.id}/submissions/${submission.id}/confirm`,
        );
      return confirmedSubmission;
    },
    [avogadorApi],
  );

  const getProjectsByCourse: (courseId: string) => Promise<Project[]> =
    useCallback(
      async (courseId: string) => {
        const { data: projects }: { data: Project[] } = await avogadorApi.get(
          `/projects/courses/${courseId}`,
        );
        return projects;
      },
      [avogadorApi],
    );

  const getSelfUserProject: (project: Project) => Promise<UserProject | null> =
    useCallback(
      async (project: Project) => {
        const { data: userProject }: { data: UserProject | null } =
          await avogadorApi.get(`/projects/${project.id}/users/self`);
        return userProject;
      },
      [avogadorApi],
    );

  const getUserLatestProjectSubmission: (
    user: User,
    project: Project,
  ) => Promise<ProjectSubmission | null> = useCallback(
    async (user: User, project: Project) => {
      const { data: projectSubmission }: { data: ProjectSubmission[] } =
        await avogadorApi.get(
          `/projects/${project.id}/submissions/users/${user.id}?latest=true`,
        );
      return projectSubmission.length > 0 ? projectSubmission[0] : null;
    },
    [avogadorApi],
  );

  const getSubmissionTree: (submission: ProjectSubmission) => Promise<File> =
    useCallback(
      async (submission: ProjectSubmission) => {
        const { data: tree }: { data: File } = await avogadorApi.get(
          `/projects/${submission.project.id}/submissions/${submission.id}/download/extra?filename=tree.txt`,
          {
            responseType: "blob",
          },
        );
        return tree;
      },
      [avogadorApi],
    );

  const getSubmissionExecutionLog: (
    submission: ProjectSubmission,
  ) => Promise<File> = useCallback(
    async (submission: ProjectSubmission) => {
      const { data: log }: { data: File } = await avogadorApi.get(
        `/projects/${submission.project.id}/submissions/${submission.id}/download/extra?filename=exec.out`,
        {
          responseType: "blob",
        },
      );
      return log;
    },
    [avogadorApi],
  );

  const createProject: (project: Omit<Project, "id">) => Promise<Project> =
    useCallback(
      async (project: Omit<Project, "id">) => {
        const { data: createdProject }: { data: Project } =
          await avogadorApi.post(
            `projects/${project.projectType.toLowerCase()}`,
            project,
          );
        return createdProject;
      },
      [avogadorApi],
    );

  const joinProject = useCallback(
    async (project: Project) => {
      const { data: userProject }: { data: UserProject | null } =
        await avogadorApi.put(`/projects/${project.id}/join`);
      return userProject;
    },
    [avogadorApi],
  );

  const downloadSubmissionArchive = useCallback(
    (
      submission: ProjectSubmission,
      onDownloadProgress: (progressEvent: AxiosProgressEvent) => void,
    ) => {
      return avogadorApi.get(
        `/projects/${submission.project.id}/submissions/${submission.id}/download`,
        {
          responseType: "blob",
          onDownloadProgress,
        },
      );
    },
    [avogadorApi],
  );

  const downloadOutputFile = useCallback(
    (
      submission: ProjectSubmission,
      onDownloadProgress: (progressEvent: AxiosProgressEvent) => void,
    ) => {
      return avogadorApi.get(
        `/projects/${submission.project.id}/submissions/${submission.id}/download/extra?filename=report.html`,
        {
          responseType: "blob",
          onDownloadProgress,
        },
      );
    },
    [avogadorApi],
  );

  const updateProject: (project: Project) => Promise<Project> = useCallback(
    async (project: Project) => {
      const { data: updatedProject }: { data: Project } = await avogadorApi.put(
        `/projects/${project.projectType.toLowerCase()}/${project.id}`,
        project,
      );
      return updatedProject;
    },
    [avogadorApi],
  );

  return {
    uploadProject,
    confirmSubmission,
    getProject,
    getProjectsByCourse,
    getSelfUserProject,
    getUserLatestProjectSubmission,
    getSubmissionTree,
    getSubmissionExecutionLog,
    createProject,
    joinProject,
    downloadSubmissionArchive,
    downloadOutputFile,
    updateProject,
  };
};

export default useProjectService;
