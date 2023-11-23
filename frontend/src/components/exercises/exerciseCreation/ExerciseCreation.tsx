import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import React, { useEffect } from "react";
import exerciseAtom, {
  getInitializedExercise,
} from "@exercises/exerciseCreation/ExerciseAtom.ts";
import templateAtom, {
  getInitializedTemplate,
} from "@exercises/exerciseCreation/TemplateAtom.ts";
import testcasesAtom from "@exercises/exerciseCreation/TestcasesAtom.ts";
import { Exercise, Testcase } from "@exercises/types.ts";
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
  const { createExercise, createTestcase, createTemplate } =
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
    const createdTestcases: Testcase[] = [];

    try {
      handleStep("Creating the Database entry");
      createdExercise = await createExercise({
        trialId: exercise.trialId,
        name: exercise.name,
        statement: exercise.statement,
        timeLimit: exercise.timeLimit,
        isVisible: exercise.isVisible,
      });
      handleProgress((prev) => prev + 25);
    } catch (err) {
      if (err instanceof Error) {
        enqueueSnackbar(err.message, { variant: "error" });
      }
      handleStep("");
      handleProgress(0);
      return;
    }

    try {
      await createTemplate(createdExercise, template);
      handleProgress((prev) => prev + 25);
    } catch (err) {
      if (err instanceof Error) {
        enqueueSnackbar(err.message, { variant: "error" });
      }
      handleStep("");
      handleProgress(0);
    }

    let i = 1;
    for (const testcase of testcases) {
      let createdTestcase: Testcase;
      try {
        handleStep(`Creating the Testcase entry ${i++}/${testcases.length}`);
        createdTestcase = await createTestcase(createdExercise.id, testcase);
        handleProgress((prev) => prev + ((i - 1) / testcases.length) * 50);
      } catch (err) {
        if (err instanceof Error) {
          enqueueSnackbar(err.message, { variant: "error" });
        }
        handleStep("");
        handleProgress(0);
        return;
      }
      createdTestcases.push(createdTestcase);
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
