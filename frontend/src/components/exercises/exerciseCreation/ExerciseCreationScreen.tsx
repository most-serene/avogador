import {
  Backdrop,
  Button,
  CircularProgress,
  Step,
  StepLabel,
  Stepper,
  Typography,
} from "@mui/material";
import ExerciseCreationInfo from "@exercises/exerciseCreation/ExerciseCreationInfo.tsx";
import { useEffect, useMemo, useState } from "react";
import Box from "@mui/material/Box";
import { useAtom } from "jotai";
import exerciseAtom, {
  getInitializedExercise,
} from "@exercises/exerciseCreation/ExerciseAtom.ts";
import ExerciseCreationTemplate from "@exercises/exerciseCreation/template/ExerciseCreationTemplate.tsx";
import templateAtom, {
  getInitializedTemplate,
} from "@exercises/exerciseCreation/TemplateAtom.ts";
import ExerciseCreationTestcases from "@exercises/exerciseCreation/testcases/ExerciseCreationTestcases.tsx";
import testcasesAtom from "@exercises/exerciseCreation/TestcasesAtom.ts";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { enqueueSnackbar } from "notistack";
import { Exercise, Testcase } from "@exercises/types.ts";
import { useNavigate, useParams } from "react-router-dom";

interface ExerciseCreationScreenProps {
  originalExercise?: Exercise;
}

const steps = [
  { label: "General Info", component: <ExerciseCreationInfo /> },
  { label: "Template", component: <ExerciseCreationTemplate /> },
  { label: "Testcases", component: <ExerciseCreationTestcases /> },
];

const ExerciseCreationScreen = ({
  originalExercise,
}: ExerciseCreationScreenProps) => {
  const {
    createExercise,
    createTestcase,
    createTemplate,
    updateExercise,
    updateTestcase,
  } = useExerciseService();
  const navigate = useNavigate();
  const { exerciseId } = useParams();
  const [activeStep, setActiveStep] = useState(0);
  const [exercise, setExercise] = useAtom(exerciseAtom);
  const [template, setTemplate] = useAtom(templateAtom);
  const [testcases, setTestcases] = useAtom(testcasesAtom);
  const [creationStatus, setCreationStatus] = useState("");
  const [creationPercentage, setCreationPercentage] = useState(0);
  const [isInitialized, setIsInitialized] = useState(exerciseId != null);

  const isInformationStepComplete = useMemo<boolean>(() => {
    return (
      exercise.courseId !== "" &&
      exercise.trialId !== "" &&
      exercise.name.trim() !== "" &&
      exercise.statement.trim() !== "" &&
      exercise.timeLimit > 0
    );
  }, [exercise]);

  const isTemplateStepComplete = useMemo<boolean>(() => {
    return template.some((cell) => cell.type === "EDITABLE");
  }, [template]);

  const isTestcasesStepComplete = useMemo<boolean>(() => {
    return (
      testcases.length > 0 &&
      testcases.every(
        ({ input, output }) => input.trim() !== "" && output.trim() !== "",
      )
    );
  }, [testcases]);

  useEffect(() => {
    if (exerciseId == null) {
      setExercise(getInitializedExercise());
      setTemplate([...getInitializedTemplate()]);
      setTestcases([]);
      setIsInitialized(true);

      return () => {
        setExercise(getInitializedExercise());
        setTemplate([...getInitializedTemplate()]);
        setTestcases([]);
      };
    }
  }, [exerciseId, setExercise, setTemplate, setTestcases]);

  const handleSubmit = async () => {
    let createdExercise: Exercise;
    const createdTestcases: Testcase[] = [];

    try {
      setCreationStatus("Creating the Database entry");
      createdExercise = await createExercise({
        trialId: exercise.trialId,
        name: exercise.name,
        statement: exercise.statement,
        timeLimit: exercise.timeLimit,
        isVisible: exercise.isVisible,
      });
      setCreationPercentage((prev) => prev + 25);
    } catch (err) {
      if (err instanceof Error) {
        enqueueSnackbar(err.message, { variant: "error" });
      }
      setCreationStatus("");
      setCreationPercentage(0);
      return;
    }

    try {
      await createTemplate(createdExercise, template);
      setCreationPercentage((prev) => prev + 25);
    } catch (err) {
      if (err instanceof Error) {
        enqueueSnackbar(err.message, { variant: "error" });
      }
      setCreationStatus("");
      setCreationPercentage(0);
    }

    let i = 1;
    for (const testcase of testcases) {
      let createdTestcase: Testcase;
      try {
        setCreationStatus(
          `Creating the Testcase entry ${i++}/${testcases.length}`,
        );
        createdTestcase = await createTestcase(createdExercise.id, testcase);
        setCreationPercentage(
          (prev) => prev + ((i - 1) / testcases.length) * 50,
        );
      } catch (err) {
        if (err instanceof Error) {
          enqueueSnackbar(err.message, { variant: "error" });
        }
        setCreationStatus("");
        setCreationPercentage(0);
        return;
      }
      createdTestcases.push(createdTestcase);
    }
  };

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
  };

  if (!isInitialized) {
    return <></>;
  }

  return (
    <>
      <Box height="100%">
        <Stepper activeStep={activeStep} alternativeLabel>
          {steps.map(({ label }) => (
            <Step key={label}>
              <StepLabel>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>
        <Box marginY={2} style={{ height: "calc(100% - 150px)" }}>
          {steps[activeStep].component}
        </Box>
        <Box display="flex" justifyContent="center">
          <Button
            variant="outlined"
            sx={{ mx: 1 }}
            disabled={activeStep <= 0}
            onClick={() => {
              setActiveStep(Math.max(activeStep - 1, 0));
            }}
          >
            Go back
          </Button>
          <Button
            variant="outlined"
            sx={{
              mx: 1,
              display: activeStep >= steps.length - 1 ? "none" : "block",
            }}
            disabled={
              activeStep >= steps.length - 1 ||
              (activeStep === 0 && !isInformationStepComplete) ||
              (activeStep === 1 && !isTemplateStepComplete)
            }
            onClick={() => {
              setActiveStep(Math.min(activeStep + 1, steps.length - 1));
            }}
          >
            Next
          </Button>
          {originalExercise == null ? (
            <Button
              variant="outlined"
              sx={{
                mx: 1,
                display: activeStep < steps.length - 1 ? "none" : "block",
              }}
              disabled={
                activeStep != steps.length - 1 || !isTestcasesStepComplete
              }
              onClick={() => {
                handleSubmit()
                  .then(() => {
                    setCreationStatus("");
                    navigate(`/practices/${exercise.trialId}`);
                  })
                  .catch((err: Error) => {
                    enqueueSnackbar(err.message, { variant: "error" });
                  });
              }}
            >
              Create
            </Button>
          ) : (
            <Button
              variant="outlined"
              sx={{
                mx: 1,
                display: activeStep < steps.length - 1 ? "none" : "block",
              }}
              disabled={
                activeStep != steps.length - 1 || !isTestcasesStepComplete
              }
              onClick={() => {
                handleUpdate()
                  .then(() => {
                    setCreationStatus("");
                    navigate(
                      `/practices/${exercise.trialId}/exercises/${originalExercise.id}`,
                    );
                  })
                  .catch((err: Error) => {
                    enqueueSnackbar(err.message, { variant: "error" });
                  });
              }}
            >
              Update
            </Button>
          )}
        </Box>
      </Box>
      <Backdrop
        sx={{
          zIndex: (theme) => theme.zIndex.drawer + 1,
        }}
        open={creationStatus !== ""}
      >
        <Box display="flex" flexDirection="column" alignItems="center">
          <CircularProgress
            variant="determinate"
            value={creationPercentage}
            color="primary"
            sx={{ mb: 2 }}
          />
          <Typography>{creationStatus}</Typography>
        </Box>
      </Backdrop>
    </>
  );
};

export default ExerciseCreationScreen;
