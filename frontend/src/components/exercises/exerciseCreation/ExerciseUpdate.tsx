import { useParams } from "react-router-dom";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { useAtom } from "jotai/index";
import exerciseAtom from "@exercises/exerciseCreation/ExerciseAtom.ts";
import templateAtom from "@exercises/exerciseCreation/TemplateAtom.ts";
import testcasesAtom from "@exercises/exerciseCreation/TestcasesAtom.ts";
import { useEffect, useState } from "react";
import { Exercise, Testcase } from "@exercises/types.ts";
import { enqueueSnackbar } from "notistack";
import ExerciseSettingsStepper from "@exercises/exerciseCreation/ExerciseSettingsStepper.tsx";

const ExerciseUpdate = () => {
  const { exerciseId } = useParams();
  const {
    getExerciseById,
    getTestcasesFromExercise,
    getTemplateFromExercise,
    createTestcase,
    createTemplate,
    updateExercise,
    updateTestcase,
  } = useExerciseService();
  const { getTrialById } = useTrialService();
  const [exercise, setExercise] = useAtom(exerciseAtom);
  const [template, setTemplate] = useAtom(templateAtom);
  const [testcases, setTestcases] = useAtom(testcasesAtom);
  const [originalExercise, setOriginalExercise] = useState<Exercise>();
  const [originalTestcases, setOriginalTestcases] = useState<Testcase[]>([]);

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
            setOriginalTestcases(testcases);
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

  const handleUpdate = async () => {
    if (originalExercise == null) return;

    const updatedExercise: Exercise = {
      ...exercise,
      id: originalExercise.id,
      trial: originalExercise.trial,
    };

    await updateExercise(updatedExercise);
    await createTemplate(updatedExercise, template);

    for (const testcase of testcases) {
      if (testcase.id == null) {
        await createTestcase(updatedExercise.id, testcase);
      } else {
        await updateTestcase(updatedExercise.id, testcase);
      }
    }

    for (const testcase of originalTestcases.filter((otc) =>
      testcases.some((tc) => tc.id == otc.id),
    )) {
      console.log(testcase);
    }
  };

  return (
    <ExerciseSettingsStepper
      onComplete={handleUpdate}
      exerciseId={exerciseId}
    />
  );
};

export default ExerciseUpdate;
