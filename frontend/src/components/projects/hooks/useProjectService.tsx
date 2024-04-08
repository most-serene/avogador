import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import JSZip from "jszip";
import { Project } from "@components/projects/types.ts";
// eslint-disable-next-line import/named
import { AxiosProgressEvent } from "axios";
import { enqueueSnackbar } from "notistack";

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
                variant: "info",
              });
            })
            .catch((err: Error) => {
              console.error(err);
              enqueueSnackbar(err.message, {
                variant: "error",
              });
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

  return {
    uploadProject,
    getProject,
  };
};

export default useProjectService;
