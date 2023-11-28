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
    insertTestcase,
    createTemplate,
    updateExercise,
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

    const handleCatch = (err: unknown) => {
      if (err instanceof Error) {
        enqueueSnackbar(err.message, { variant: "error" });
      }
      handleStep("");
      handleProgress(0);
    };

    const updatedExercise: Exercise = {
      ...exercise,
      id: originalExercise.id,
      trial: originalExercise.trial,
    };

    handleStep("Updating exercise data");
    handleProgress((prev) => prev + 25);
    try {
      await updateExercise(updatedExercise);
    } catch (err) {
      handleCatch(err);
    }
    handleStep("Updating template");
    handleProgress((prev) => prev + 25);

    try {
      await createTemplate(updatedExercise, template);
    } catch (err) {
      handleCatch(err);
    }

    handleStep("Updating testcases");

    try {
      await Promise.all(
        testcases.map((testcase, i) => {
          return insertTestcase(updatedExercise.id, { ...testcase, index: i });
        }),
      );
      handleProgress((prev) => prev + 25);
      handleStep("Deleting old testcases");
    } catch (err) {
      handleCatch(err);
    }

    try {
      await Promise.all(
        originalTestcases
          .filter((otc) => !testcases.some((tc) => tc.id === otc.id))
          .map((testcase) => {
            return deleteTestcase(updatedExercise.id, testcase);
          }),
      );
    } catch (err) {
      handleCatch(err);
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
