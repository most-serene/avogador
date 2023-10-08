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

type Strox = Record<number, StroxCell>;

export type { Exercise, PartialExercise, Strox, StroxCell };
