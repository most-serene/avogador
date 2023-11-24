import { useParams } from "react-router-dom";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { useAtom } from "jotai";
import exerciseAtom from "@exercises/exerciseCreation/ExerciseAtom.ts";
import templateAtom from "@exercises/exerciseCreation/TemplateAtom.ts";
import testcasesAtom from "@exercises/exerciseCreation/TestcasesAtom.ts";
import React, { useEffect, useState } from "react";
import { Exercise, Testcase } from "@exercises/types.ts";
import { enqueueSnackbar } from "notistack";
import ExerciseSettingsStepper from "@exercises/exerciseCreation/ExerciseSettingsStepper.tsx";
import { CircularProgress } from "@mui/material";
import Box from "@mui/material/Box";

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
    deleteTestcase,
  } = useExerciseService();
  const { getTrialById } = useTrialService();
  const [exercise, setExercise] = useAtom(exerciseAtom);
  const [template, setTemplate] = useAtom(templateAtom);
  const [testcases, setTestcases] = useAtom(testcasesAtom);
  const [originalExercise, setOriginalExercise] = useState<Exercise>();
  const [originalTestcases, setOriginalTestcases] = useState<Testcase[]>();

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

  const handleUpdate = async (
    handleProgress: React.Dispatch<React.SetStateAction<number>>,
    handleStep: React.Dispatch<React.SetStateAction<string>>,
  ) => {
    if (originalExercise == null || originalTestcases == null) return;

    const updatedExercise: Exercise = {
      ...exercise,
      id: originalExercise.id,
      trial: originalExercise.trial,
    };

    handleStep("Updating exercise data");
    handleProgress((prev) => prev + 25);
    await updateExercise(updatedExercise);
    handleStep("Updating template");
    handleProgress((prev) => prev + 25);

    await createTemplate(updatedExercise, template);

    handleStep("Updating testcases");
    let i = 0;
    for (const testcase of testcases) {
      handleProgress((prev) => prev + (50 * ++i) / testcases.length);
      if (testcase.id == null) {
        await createTestcase(updatedExercise.id, testcase);
      } else {
        await updateTestcase(updatedExercise.id, testcase);
      }
    }

    handleStep("Deleting old testcases");
    for (const testcase of originalTestcases.filter(
      (otc) => !testcases.some((tc) => tc.id === otc.id),
    )) {
      deleteTestcase(originalExercise.id, testcase).catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
    }
    handleProgress(100);
  };

  if (originalExercise == null || originalTestcases == null) {
    return (
      <Box
        height="100%"
        display="flex"
        justifyContent="center"
        alignItems="center"
      >
        <CircularProgress />
      </Box>
    );
  }

  return (
    <ExerciseSettingsStepper
      onComplete={handleUpdate}
      exerciseId={exerciseId}
    />
  );
};

export default ExerciseUpdate;
