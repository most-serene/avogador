import { Trial } from "@trials/types.ts";

interface Exercise {
  id: string;
  trial: Trial;
  trialId?: string;
  name: string;
  statement: string;
  timeLimit: number;
  isVisible: boolean;
}

interface PartialExercise {
  courseId: string;
  trialId: string;
  name: string;
  statement: string;
  timeLimit: number;
  isVisible: boolean;
}

interface StroxCell {
  content: string;
  type: "HIDDEN" | "VISIBLE" | "EDITABLE";
}

interface Testcase {
  id: string;
  exerciseId: string;
  isVisible: boolean;
  index: number;
  input: string;
  output: string;
}

interface PartialTestcase {
  id?: string;
  input: string;
  output: string;
  isVisible: boolean;
}

interface Strox {
  sourceFileName: string;
  cells: StroxCell[];
}

interface Submission {
  id: string;
  exerciseId: string;
  userId: string;
  timestamp: Date;
  stroxCells: StroxCell[];
}

interface SubmissionResult {
  id: string;
  submissionId: string;
  testcaseId: string;
  status: SubmissionStatus;
}

interface SubmissionResultDetail {
  id: string;
  exerciseId: string;
  userId: string;
  timestamp: Date;
  stroxCells: StroxCell[];
  exercise: Exercise;
  results: SubmissionResult[];
}

type SubmissionStatus =
  | "PENDING"
  | "CORRECT"
  | "WRONG_ANSWER"
  | "TIME_LIMIT_EXCEEDED"
  | "RUNTIME_ERROR"
  | "COMPILE_ERROR";

type SubmissionResultMap = Record<string, SubmissionResult[]>;

export type {
  Exercise,
  PartialExercise,
  Strox,
  StroxCell,
  Testcase,
  PartialTestcase,
  Submission,
  SubmissionResult,
  SubmissionResultMap,
  SubmissionStatus,
  SubmissionResultDetail,
};
