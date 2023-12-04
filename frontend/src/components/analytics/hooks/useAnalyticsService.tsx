import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi.tsx";
import {
  ExerciseResults,
  UserTrialProgress,
} from "@components/analytics/types.ts";
import { parseJSON } from "date-fns";

const useAnalyticsService = () => {
  const avogadorApi = useAvogadorApi();

  const getUserTrialProgress: (
    userId: string,
    courseId: string,
  ) => Promise<UserTrialProgress[]> = useCallback(
    async (userId: string, courseId: string) => {
      const { data: progress }: { data: UserTrialProgress[] } =
        await avogadorApi.get(
          `analytics/users/${userId}/courses/${courseId}/progress`,
        );

      return progress;
    },
    [avogadorApi],
  );

  const getExerciseResults: (
    trialId: string,
    exerciseId: string,
  ) => Promise<ExerciseResults> = useCallback(
    async (trialId: string, exerciseId: string) => {
      const { data: results }: { data: ExerciseResults } =
        await avogadorApi.get(
          `analytics/trials/${trialId}/exercises/${exerciseId}/results`,
        );
      return results;
    },
    [avogadorApi],
  );

  const getSubmissionTimeSeries: (courseId: string) => Promise<Date[]> =
    useCallback(
      async (courseId: string) => {
        const { data: submissionsTimestamps }: { data: string[] } =
          await avogadorApi.get(
            `analytics/courses/${courseId}/submissions-trend`,
          );

        return submissionsTimestamps.map((timestamp) => parseJSON(timestamp));
      },
      [avogadorApi],
    );

  return { getUserTrialProgress, getExerciseResults, getSubmissionTimeSeries };
};

export default useAnalyticsService;
