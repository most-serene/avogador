import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import { Practice, Trial, UserTrial } from "@trials/types.ts";
import { User } from "@authentication/types.ts";

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

  const getUserTrials: (user: User) => Promise<UserTrial[]> = useCallback(
    async (user: User) => {
      const { data: userTrials }: { data: UserTrial[] } = await avogadorApi.get(
        `/trials/users/${user.id}`,
      );
      return userTrials;
    },
    [avogadorApi],
  );

  return {
    getTrialsByCourseId,
    createPractice,
    getUserTrials,
  };
};

export default useTrialService;
