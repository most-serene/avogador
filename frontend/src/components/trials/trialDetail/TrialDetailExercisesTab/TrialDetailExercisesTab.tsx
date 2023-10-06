import { Exam, Practice } from "@trials/types.ts";

interface TrialDetailExercisesTabProps {
  trial: Practice | Exam;
}

const TrialDetailExercisesTab = ({ trial }: TrialDetailExercisesTabProps) => {
  return <>Trial exercises tab {trial.name}</>;
};

export default TrialDetailExercisesTab;
