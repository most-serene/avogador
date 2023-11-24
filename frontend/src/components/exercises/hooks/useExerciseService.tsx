import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import {
  Exercise,
  PartialExercise,
  PartialTestcase,
  Strox,
  StroxCell,
  Submission,
  SubmissionResultMap,
  Testcase,
} from "@exercises/types.ts";
import { Trial, UserExerciseSummary } from "@trials/types.ts";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { enqueueSnackbar } from "notistack";
import { saveResponseToFile } from "../../../utils/fileHandling.ts";

const useExerciseService = () => {
  const avogadorApi = useAvogadorApi();
  const [user] = useAtom(userAtom);

  const createExercise: (
    exercise: Omit<PartialExercise, "courseId">,
  ) => Promise<Exercise> = useCallback(
    async (exercise: Omit<PartialExercise, "courseId">) => {
      const { data: createdExercise }: { data: Exercise } =
        await avogadorApi.post("/exercises", exercise);
      return createdExercise;
    },
    [avogadorApi],
  );

  const createTemplate: (
    exercise: Exercise,
    template: StroxCell[],
  ) => Promise<void> = useCallback(
    async (exercise: Exercise, template: StroxCell[]) => {
      await avogadorApi.post(`/exercises/${exercise.id}/template`, {
        cells: template,
      });
    },
    [avogadorApi],
  );

  const createTestcase: (
    exerciseId: string,
    testcase: PartialTestcase,
  ) => Promise<Testcase> = useCallback(
    async (exerciseId: string, testcase: PartialTestcase) => {
      const { data: createdTestcase }: { data: Testcase } =
        await avogadorApi.post(`/exercises/${exerciseId}/testcases`, testcase);
      return createdTestcase;
    },
    [avogadorApi],
  );

  const createSubmission: (
    exerciseId: string,
    submission: StroxCell[],
  ) => Promise<Submission> = useCallback(
    async (exerciseId: string, submission: StroxCell[]) => {
      if (user == null) {
        throw new Error("User is null or undefined");
      }
      const { data: createdSubmission }: { data: Submission } =
        await avogadorApi.post(`/exercises/${exerciseId}/submissions`, {
          exerciseId: exerciseId,
          userId: user.id,
          stroxCells: submission.filter((cell) => cell.type === "EDITABLE"),
        });
      return createdSubmission;
    },
    [avogadorApi, user],
  );

  const downloadSubmission: (submission: Submission) => void = useCallback(
    (submission: Submission) => {
      enqueueSnackbar("download started", { variant: "info" });
      avogadorApi
        .get(
          `/exercises/${submission.exerciseId}/submissions/${submission.id}/download`,
          {
            responseType: "blob",
            onDownloadProgress: (progressEvent) => {
              console.log(progressEvent);
            },
          },
        )
        .then((res) => {
          saveResponseToFile(res, `${submission.id}.tar.gz`);
        })
        .catch((err: Error) => {
          enqueueSnackbar(err.message, { variant: "error" });
        });
    },
    [avogadorApi],
  );

  const getExercisesByTrialId: (trialId: string) => Promise<Exercise[]> =
    useCallback(
      async (trialId: string) => {
        const { data: exercises }: { data: Exercise[] } = await avogadorApi.get(
          `/exercises/trials/${trialId}`,
        );
        return exercises;
      },
      [avogadorApi],
    );

  const getExercisesByTrial: (trial: Trial) => Promise<Exercise[]> =
    useCallback(
      async (trial: Trial) => {
        return getExercisesByTrialId(trial.id);
      },
      [getExercisesByTrialId],
    );

  const getExerciseById: (exerciseId: string) => Promise<Exercise> =
    useCallback(
      async (exerciseId: string) => {
        const { data: exercise }: { data: Exercise } = await avogadorApi.get(
          `/exercises/${exerciseId}`,
        );
        return exercise;
      },
      [avogadorApi],
    );

  const getTestcasesFromExercise: (exerciseId: string) => Promise<Testcase[]> =
    useCallback(
      async (exerciseId: string) => {
        const { data: testcases }: { data: Testcase[] } = await avogadorApi.get(
          `/exercises/${exerciseId}/testcases`,
        );
        return testcases;
      },
      [avogadorApi],
    );

  const getTemplateFromExercise: (
    exerciseId: string,
    merged?: boolean,
  ) => Promise<Strox> = useCallback(
    async (exerciseId: string, merged?: boolean) => {
      const { data: template }: { data: Strox } = await avogadorApi.get(
        `/exercises/${exerciseId}/template?merged=${merged ?? false}`,
      );
      return template;
    },
    [avogadorApi],
  );

  const getUserLastSubmissionFromExercise: (
    exerciseId: string,
  ) => Promise<SubmissionResultMap> = useCallback(
    async (exerciseId: string) => {
      if (user == null) {
        throw new Error("We are still verifying the user's identity");
      }

      const { data: submission }: { data: SubmissionResultMap } =
        await avogadorApi.get(
          `/exercises/${exerciseId}/users/${user.id}/results?latest=true`,
        );
      return submission;
    },
    [avogadorApi, user],
  );

  const getUserSubmissionsResultsFromExercise: (
    exerciseId: string,
    userId: string,
  ) => Promise<SubmissionResultMap> = useCallback(
    async (exerciseId: string, userId: string) => {
      const { data: submissions }: { data: SubmissionResultMap } =
        await avogadorApi.get(
          `/exercises/${exerciseId}/users/${userId}/results`,
        );
      return submissions;
    },
    [avogadorApi],
  );

  const getUserSubmissionsFromExercise: (
    exerciseId: string,
    userId: string,
  ) => Promise<Submission[]> = useCallback(
    async (exerciseId: string, userId: string) => {
      const { data: submissions }: { data: Submission[] } =
        await avogadorApi.get(
          `/exercises/${exerciseId}/submissions/users/${userId}`,
        );
      return submissions;
    },
    [avogadorApi],
  );

  const getExerciseResultSummary: (
    exerciseId: string,
  ) => Promise<UserExerciseSummary[]> = useCallback(
    async (exerciseId: string) => {
      const { data: summary }: { data: UserExerciseSummary[] } =
        await avogadorApi.get(`/exercises/${exerciseId}/results`);
      return summary;
    },
    [avogadorApi],
  );

  const getSubmissionOutputs: (
    exerciseId: string,
    submissionId: string,
  ) => Promise<Record<string, string>> = useCallback(
    async (exerciseId: string, submissionId: string) => {
      const { data: submission }: { data: Record<string, string> } =
        await avogadorApi.get(
          `/exercises/${exerciseId}/submissions/${submissionId}/outputs`,
        );
      return submission;
    },
    [avogadorApi],
  );

  const updateExercise: (exercise: Exercise) => Promise<Exercise> = useCallback(
    async (exercise: Exercise) => {
      const { data: updatedExercise }: { data: Exercise } =
        await avogadorApi.put(`/exercises/${exercise.id}`, {
          ...exercise,
          trialId: exercise.trialId ?? exercise.trial.id,
        });
      return updatedExercise;
    },
    [avogadorApi],
  );

  const updateTestcase: (
    exerciseId: string,
    testcase: PartialTestcase,
  ) => Promise<Testcase> = useCallback(
    async (exerciseId: string, testcase: PartialTestcase) => {
      const { data: updatedTestcase }: { data: Testcase } =
        await avogadorApi.put(
          `/exercises/${exerciseId}/testcases/${testcase.id}`,
          testcase,
        );
      return updatedTestcase;
    },
    [avogadorApi],
  );

  const deleteTestcase: (
    exerciseId: string,
    testcase: Testcase,
  ) => Promise<void> = useCallback(
    async (exerciseId: string, testcase: Testcase) => {
      await avogadorApi.delete(
        `/exercises/${exerciseId}/testcases/${testcase.id}`,
      );
    },
    [avogadorApi],
  );

  return {
    createExercise,
    createTemplate,
    createTestcase,
    createSubmission,
    downloadSubmission,
    getExercisesByTrial,
    getExercisesByTrialId,
    getExerciseById,
    getTestcasesFromExercise,
    getTemplateFromExercise,
    getUserLastSubmissionFromExercise,
    getUserSubmissionsFromExercise,
    getUserSubmissionsResultsFromExercise,
    getExerciseResultSummary,
    getSubmissionOutputs,
    updateExercise,
    updateTestcase,
    deleteTestcase,
  };
};

export default useExerciseService;
