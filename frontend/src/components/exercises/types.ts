interface Exercise {
  id: string;
  trialId: string;
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

interface PartialTestcase {
  input: string;
  output: string;
  isVisible: boolean;
}

type Strox = Record<number, StroxCell>;

export type { Exercise, PartialExercise, Strox, StroxCell, PartialTestcase };
