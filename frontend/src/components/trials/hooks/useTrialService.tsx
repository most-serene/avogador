import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import { Practice, Trial } from "@trials/types.ts";

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

  const createPractice: (practice: Omit<Practice, "id">) => Promise<Practice> =
    useCallback(
      async (practice: Omit<Practice, "id">) => {
        const { data: createdPractice }: { data: Practice } =
          await avogadorApi.post("/trials/practices", practice);
        return createdPractice;
      },
      [avogadorApi],
    );

  return {
    getTrialsByCourseId,
    createPractice,
  };
};

export default useTrialService;
