import { atom } from "jotai";
import { PartialExercise } from "@exercises/types.ts";

export default atom<PartialExercise>({
  courseId: "",
  trialId: "",
  name: "",
  statement: "",
  timeLimit: 1,
  isVisible: false,
});
