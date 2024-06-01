import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import JSZip from "jszip";
import {
  Project,
  ProjectSubmission,
  UserProject,
  UserProjectDetail,
} from "@components/projects/types.ts";
import Papa from "papaparse";
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
        arr.push(file);
      }
      return arr;
    };

    const zip = new JSZip();
    const fileStream = generateFilesStream(files);
    const extensions = fileStream.map((f) => f.name.split(".").pop());

    return new Promise<Blob>((resolve, reject) => {
      // TODO: this check will be generalized
      if (extensions.filter((ext) => ext === "ipynb").length !== 1) {
        reject(new Error("None or more than one ipynb files"));
      } else {
        fileStream.map((file: File) => {
          zip.file(file.webkitRelativePath, file);
        });

        resolve(zip.generateAsync({ type: "blob" }));
      }
    });
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

  const unconfirmSubmission: (
    projectId: string,
    submissionId: string,
  ) => Promise<ProjectSubmission> = useCallback(
    async (projectId: string, submissionId: string) => {
      const { data: unconfirmedSubmission }: { data: ProjectSubmission } =
        await avogadorApi.put(
          `/projects/${projectId}/submissions/${submissionId}/confirm?revert=true`,
        );
      return unconfirmedSubmission;
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

  const getProjectMembers: (project: Project) => Promise<UserProjectDetail[]> =
    useCallback(
      async (project: Project) => {
        const { data: users }: { data: UserProjectDetail[] } =
          await avogadorApi.get(`/projects/${project.id}/users`);
        return users;
      },
      [avogadorApi],
    );

  const getMembersLastProjectSubmission: (
    project: Project,
  ) => Promise<ProjectSubmission[]> = useCallback(
    async (project: Project) => {
      const { data: submissions }: { data: ProjectSubmission[] } =
        await avogadorApi.get(`/projects/${project.id}/submissions`);
      return submissions;
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

  const processMarksCsvFile: (
    marksFile: File,
  ) => Promise<[{ email: string; mark: number }]> = useCallback(
    (marksFile: File) => {
      return new Promise((resolve, reject) => {
        Papa.parse(marksFile, {
          header: true,
          skipEmptyLines: true,
          complete: (results) => {
            resolve(results.data as [{ email: string; mark: number }]);
          },
          error: (error) => {
            reject(error);
          },
        });
      });
    },
    [],
  );

  const validateMarks: (
    marks: [{ email: string; mark: number }],
  ) => Promise<Record<string, number>> = useCallback(
    (marks: [{ email: string; mark: number }]) => {
      return new Promise((resolve, reject) => {
        if (marks.some((mark) => !(mark.email && mark.mark))) {
          reject(
            new Error(
              'Malformed file: the CSV file must contain two columns: "email" and "mark" (numeric)',
            ),
          );
        }

        for (const { email, mark } of marks) {
          if (email.split("@").length !== 2) {
            reject(new Error(`The email ${email} is not an email`));
          }
          if (mark < 0 || mark > 31 || !Number.isInteger(Number(mark))) {
            reject(
              new Error(
                `The mark of ${email} (${mark}) is out of (0,32) or not an integer`,
              ),
            );
          }
        }

        const marksRecord: Record<string, number> = {};
        for (const mark of marks) {
          marksRecord[mark.email] = mark.mark;
        }
        resolve(marksRecord);
      });
    },
    [],
  );

  const uploadMarks = useCallback(
    async (project: Project, marks: Record<string, number>) => {
      const { data: userProjectList }: { data: UserProject[] } =
        await avogadorApi.put(`/projects/${project.id}/users/marks`, marks);
      return userProjectList;
    },
    [avogadorApi],
  );

  const uploadMarksFile = useCallback(
    async (project: Project, marksFile: File) => {
      return processMarksCsvFile(marksFile)
        .then(validateMarks)
        .then((marks) => uploadMarks(project, marks));
    },
    [processMarksCsvFile, uploadMarks, validateMarks],
  );

  return {
    uploadProject,
    confirmSubmission,
    unconfirmSubmission,
    getProject,
    getProjectsByCourse,
    getSelfUserProject,
    getUserLatestProjectSubmission,
    getProjectMembers,
    getMembersLastProjectSubmission,
    getSubmissionTree,
    getSubmissionExecutionLog,
    createProject,
    joinProject,
    downloadSubmissionArchive,
    downloadOutputFile,
    updateProject,
    uploadMarksFile,
  };
};

export default useProjectService;
