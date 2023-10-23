import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import {
  Exam,
  isExam,
  isPractice,
  Practice,
  Trial,
  UserTrial,
  UserTrialDetail,
} from "@trials/types.ts";
import { User } from "@authentication/types.ts";
import { addMinutes } from "date-fns";

const useTrialService = () => {
  const avogadorApi = useAvogadorApi();

  const getTrialById: (trialId: string) => Promise<Practice | Exam> =
    useCallback(
      async (trialId: string) => {
        const { data: trial }: { data: Practice | Exam } =
          await avogadorApi.get(`/trials/${trialId}`);
        return trial;
      },
      [avogadorApi],
    );

  const getPracticeById: (trialId: string) => Promise<Practice> = useCallback(
    async (trialId: string) => {
      const { data: trial }: { data: Practice } = await avogadorApi.get(
        `/trials/practices/${trialId}`,
      );
      return trial;
    },
    [avogadorApi],
  );

  const getUserTrial: (user: User, trial: Trial) => Promise<UserTrial | null> =
    useCallback(
      async (user: User, trial: Trial) => {
        const { data: userTrial }: { data: UserTrial | null } =
          await avogadorApi.get(`/trials/${trial.id}/users/${user.id}`);
        return userTrial;
      },
      [avogadorApi],
    );

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

  const joinPractice: (practiceId: string) => Promise<UserTrial | null> =
    useCallback(
      async (practiceId: string) => {
        const { data: userTrial }: { data: UserTrial | null } =
          await avogadorApi.put(`/trials/practices/${practiceId}/join`);
        return userTrial;
      },
      [avogadorApi],
    );

  const getUsersFromTrial: (trial: Trial) => Promise<UserTrialDetail[]> =
    useCallback(
      async (trial: Trial) => {
        const { data: userTrials }: { data: UserTrialDetail[] } =
          await avogadorApi.get(`/trials/${trial.id}/users`);
        return userTrials;
      },
      [avogadorApi],
    );

  // Non-api calls
  const isTrialScheduled: (trial: Trial) => boolean = useCallback(
    (trial: Trial) => {
      return trial.startTimestamp > new Date();
    },
    [],
  );

  const isExamOngoing: (trial: Exam) => boolean = useCallback((exam: Exam) => {
    return (
      addMinutes(exam.startTimestamp, exam.duration + exam.extraTime) >
      new Date()
    );
  }, []);

  const isPracticeOngoing: (practice: Practice) => boolean = useCallback(
    (practice: Practice) => {
      return practice.deadline > new Date();
    },
    [],
  );

  const isTrialOngoing: (trial: Trial) => boolean = useCallback(
    (trial: Trial) => {
      return (
        !isTrialScheduled(trial) &&
        ((isPractice(trial) && isPracticeOngoing(trial)) ||
          (isExam(trial) && isExamOngoing(trial)))
      );
    },
    [isExamOngoing, isPracticeOngoing, isTrialScheduled],
  );

  const isTrialEnded: (trial: Trial) => boolean = useCallback(
    (trial: Trial) => {
      return !isTrialScheduled(trial) && !isTrialOngoing(trial);
    },
    [isTrialOngoing, isTrialScheduled],
  );

  return {
    getTrialById,
    getPracticeById,
    getUserTrial,
    getTrialsByCourseId,
    createPractice,
    getUserTrials,
    joinPractice,
    getUsersFromTrial,
    isTrialScheduled,
    isTrialOngoing,
    isTrialEnded,
  };
};

export default useTrialService;
