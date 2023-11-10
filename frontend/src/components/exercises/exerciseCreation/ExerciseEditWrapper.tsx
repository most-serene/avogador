import { useParams } from "react-router-dom";
import ExerciseCreationScreen from "@exercises/exerciseCreation/ExerciseCreationScreen.tsx";
import { useEffect, useState } from "react";
import exerciseAtom from "@exercises/exerciseCreation/ExerciseAtom.ts";
import templateAtom from "@exercises/exerciseCreation/TemplateAtom.ts";
import testcasesAtom from "@exercises/exerciseCreation/TestcasesAtom.ts";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { useAtom } from "jotai";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { enqueueSnackbar } from "notistack";
import { Exercise } from "@exercises/types.ts";

const ExerciseEditWrapper = () => {
  const { exerciseId } = useParams();
  const { getExerciseById, getTestcasesFromExercise, getTemplateFromExercise } =
    useExerciseService();
  const { getTrialById } = useTrialService();
  const [, setExercise] = useAtom(exerciseAtom);
  const [, setTemplate] = useAtom(templateAtom);
  const [, setTestcases] = useAtom(testcasesAtom);
  const [exercise, setOriginalExercise] = useState<Exercise>();

  useEffect(() => {
    if (exerciseId == null) return;

    getExerciseById(exerciseId)
      .then((exercise) => {
        setOriginalExercise(exercise);
        setExercise({
          ...exercise,
          courseId: exercise.trial.courseId,
          trialId: exercise.trial.id,
        });
        getTestcasesFromExercise(exerciseId)
          .then((testcases) => {
            setTestcases([...testcases]);
          })
          .catch((err: Error) => {
            enqueueSnackbar(err.message, { variant: "error" });
          });
        getTemplateFromExercise(exerciseId, false)
          .then((template) => {
            setTemplate(template.cells);
          })
          .catch((err: Error) => {
            enqueueSnackbar(err.message, { variant: "error" });
          });
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [
    exerciseId,
    getExerciseById,
    getTemplateFromExercise,
    getTestcasesFromExercise,
    getTrialById,
    setExercise,
    setTemplate,
    setTestcases,
  ]);

  return <ExerciseCreationScreen originalExercise={exercise} />;
};

export default ExerciseEditWrapper;
