import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import {
  Exercise,
  PartialExercise,
  PartialTestcase,
  Testcase,
} from "@exercises/types.ts";

const useExerciseService = () => {
  const avogadorApi = useAvogadorApi();

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

  return {
    createExercise,
    createTestcase,
  };
};

export default useExerciseService;
