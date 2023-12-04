import { SubmissionStatus } from "@exercises/types.ts";

interface UserTrialProgress {
  trialId: string;
  name: string;
  passed: number;
  missing: number;
  wrong: number;
}

type ExerciseResults = Record<SubmissionStatus, number>;

export type { UserTrialProgress, ExerciseResults };
