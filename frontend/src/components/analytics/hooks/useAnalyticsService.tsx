import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi.tsx";
import { UserTrialProgress } from "@components/analytics/types.ts";

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

  return { getUserTrialProgress };
};

export default useAnalyticsService;
