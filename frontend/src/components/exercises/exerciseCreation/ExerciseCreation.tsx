import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import React, { useEffect } from "react";
import exerciseAtom, {
  getInitializedExercise,
} from "@exercises/exerciseCreation/ExerciseAtom.ts";
import templateAtom, {
  getInitializedTemplate,
} from "@exercises/exerciseCreation/TemplateAtom.ts";
import testcasesAtom from "@exercises/exerciseCreation/TestcasesAtom.ts";
import { Exercise } from "@exercises/types.ts";
import { enqueueSnackbar } from "notistack";
import { useAtom } from "jotai";
import ExerciseSettingsStepper from "@exercises/exerciseCreation/ExerciseSettingsStepper.tsx";
import { useLocation } from "react-router-dom";

interface ExerciseCreationState {
  state: null | {
    courseId: string;
    trialId: string;
  };
}

const ExerciseCreation = () => {
  const { createExercise, insertTestcase, createTemplate } =
    useExerciseService();
  const { state }: ExerciseCreationState =
    useLocation() as ExerciseCreationState;
  const [exercise, setExercise] = useAtom(exerciseAtom);
  const [template, setTemplate] = useAtom(templateAtom);
  const [testcases, setTestcases] = useAtom(testcasesAtom);

  useEffect(() => {
    setExercise(getInitializedExercise());
    setTemplate([...getInitializedTemplate()]);
    setTestcases([]);

    if (state != null) {
      setExercise((prev) => {
        return { ...prev, courseId: state.courseId, trialId: state.trialId };
      });
    }

    return () => {
      setExercise(getInitializedExercise());
      setTemplate([...getInitializedTemplate()]);
      setTestcases([]);
    };
  }, [state, setExercise, setTemplate, setTestcases]);

  const handleCreate = async (
    handleProgress: React.Dispatch<React.SetStateAction<number>>,
    handleStep: React.Dispatch<React.SetStateAction<string>>,
  ) => {
    let createdExercise: Exercise;

    const handleCatch = (err: unknown) => {
      if (err instanceof Error) {
        enqueueSnackbar(err.message, { variant: "error" });
      }
      handleStep("");
      handleProgress(0);
    };

    try {
      handleStep("Creating the Database entry");
      createdExercise = await createExercise({
        trialId: exercise.trialId,
        name: exercise.name.trim(),
        statement: exercise.statement,
        timeLimit: exercise.timeLimit,
        isVisible: exercise.isVisible,
      });
      handleProgress((prev) => prev + 25);
    } catch (err) {
      handleCatch(err);
      return;
    }

    try {
      handleStep("Creating the Template");
      await createTemplate(createdExercise, template);
      handleProgress((prev) => prev + 25);
    } catch (err) {
      handleCatch(err);
    }

    try {
      handleStep("Creating the Testcases");
      await Promise.all(
        testcases.map((testcase, i) => {
          return insertTestcase(createdExercise.id, { ...testcase, index: i });
        }),
      );
      handleProgress((prev) => prev + 25);
    } catch (err) {
      handleCatch(err);
      return;
    }
  };

  return (
    <ExerciseSettingsStepper
      onComplete={handleCreate}
      selectedCourse={state != null}
    />
  );
};

export default ExerciseCreation;
