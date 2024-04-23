import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import JSZip from "jszip";
import {
  Project,
  ProjectSubmission,
  ProjectType,
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

  const uploadProject = useCallback(
    (
      projectId: string,
      files: FileList,
      onUploadProgress: (progressEvent: AxiosProgressEvent) => void,
      onFinish: () => void,
    ) => {
      createArchive(files)
        .then(mapZipBlobToFormData)
        .then((form) => {
          avogadorApi
            .post(`/projects/notebook/${projectId}/submissions`, form, {
              headers: {
                "Content-Type": "multipart/form-data",
              },
              onUploadProgress,
            })
            .then(() => {
              enqueueSnackbar("Project submitted, waiting for execution", {
                variant: "success",
              });
            })
            .catch((err: Error) => {
              console.error(err);
              enqueueSnackbar(err.message, {
                variant: "error",
              });
            })
            .finally(() => {
              onFinish();
            });
        })
        .catch((err: Error) => {
          console.error(err);
          enqueueSnackbar(err.message, {
            variant: "error",
          });
        });
    },
    [avogadorApi, createArchive, mapZipBlobToFormData],
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

  const createProject: (
    project: Omit<Project, "id">,
    type: ProjectType,
  ) => Promise<Project> = useCallback(
    async (project: Omit<Project, "id">, type: ProjectType) => {
      const { data: createdProject }: { data: Project } =
        await avogadorApi.post(`projects/${type.toLowerCase()}`, project);
      return createdProject;
    },
    [avogadorApi],
  );

  return {
    uploadProject,
    getProject,
    getProjectsByCourse,
    getUserLatestProjectSubmission,
    getSubmissionTree,
    createProject,
  };
};

export default useProjectService;
