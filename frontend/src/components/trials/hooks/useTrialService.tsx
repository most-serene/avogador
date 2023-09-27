import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import { Trial } from "@trials/types.ts";

const useTrialService = () => {
  const avogadorApi = useAvogadorApi();

  const getTrialsByCourseId: (courseId: string) => Promise<Trial[]> =
    useCallback(
      async (courseId: string) => {
        const { data: trials }: { data: Trial[] } = await avogadorApi.get(
          `/trials/courses/${courseId}`,
        );
        return trials;
      },
      [avogadorApi],
    );

  return {
    getTrialsByCourseId,
  };
};

export default useTrialService;
