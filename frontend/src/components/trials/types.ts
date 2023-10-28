import { User } from "@authentication/types.ts";

interface Trial {
  id: string;
  courseId: string;
  name: string;
  isVisible: boolean;
  isPublic: boolean;
  language: "C" | "CPP" | "PYTHON" | "JAVA";
  startTimestamp: Date;
  trialType: "PRACTICE" | "EXAM";
}

interface Practice extends Trial {
  deadline: Date;
}

interface Exam extends Trial {
  duration: number;
  extraTime: number;
}

function isPractice(trial: Trial): trial is Practice {
  return trial.trialType === "PRACTICE";
}

function isExam(trial: Trial): trial is Exam {
  return trial.trialType === "EXAM";
}

interface UserExerciseSummary {
  submissionId?: string;
  userId: string;
  exerciseId: string;
  status: "CORRECT" | "WRONG" | "PENDING" | "MISSING";
}

interface UserTrialSummary {
  user: User;
  startTime: Date;
  summary: [
    {
      exerciseId: string;
      status: "CORRECT" | "WRONG" | "PENDING" | "MISSING";
    },
  ];
}

interface UserTrial {
  id: string;
  userId: string;
  trial: Trial;
  startTime?: Date;
  finishTime?: Date;
  deadline?: Date;
  hasExtraTime: boolean;
}

interface UserTrialDetail {
  id: string;
  user: User;
  trialId: string;
  startTime: Date;
  finishTime: Date;
  deadline: Date;
  hasExtraTime: boolean;
}

export type {
  Trial,
  Practice,
  Exam,
  UserTrial,
  UserExerciseSummary,
  UserTrialSummary,
  UserTrialDetail,
};
export { isPractice, isExam };
