import React, { useMemo, useState } from "react";
import Box from "@mui/material/Box";
import {
  Backdrop,
  Button,
  CircularProgress,
  Step,
  StepLabel,
  Stepper,
  Typography,
} from "@mui/material";
import { enqueueSnackbar } from "notistack";
import { useNavigate } from "react-router-dom";
import { useAtom } from "jotai";
import exerciseAtom from "@exercises/exerciseCreation/ExerciseAtom.ts";
import templateAtom from "@exercises/exerciseCreation/TemplateAtom.ts";
import testcasesAtom from "@exercises/exerciseCreation/TestcasesAtom.ts";
import ExerciseCreationInfo from "@exercises/exerciseCreation/ExerciseCreationInfo.tsx";
import ExerciseCreationTemplate from "@exercises/exerciseCreation/template/ExerciseCreationTemplate.tsx";
import ExerciseCreationTestcases from "@exercises/exerciseCreation/testcases/ExerciseCreationTestcases.tsx";
import ButtonWithConfirmation from "@structure/ButtonWithConfirmation/ButtonWithConfirmation.tsx";

interface ExerciseSettingsStepperProps {
  onComplete: (
    handleProgress: React.Dispatch<React.SetStateAction<number>>,
    handleStep: React.Dispatch<React.SetStateAction<string>>,
  ) => Promise<void>;
  exerciseId?: string;
  selectedCourse?: boolean;
}

const ExerciseSettingsStepper = ({
  onComplete: handleComplete,
  exerciseId,
  selectedCourse = false,
}: ExerciseSettingsStepperProps) => {
  const navigate = useNavigate();
  const [activeStep, setActiveStep] = useState(0);
  const [exercise] = useAtom(exerciseAtom);
  const [template] = useAtom(templateAtom);
  const [testcases] = useAtom(testcasesAtom);
  const [creationStatus, setCreationStatus] = useState("");
  const [creationPercentage, setCreationPercentage] = useState(0);

  const steps = useMemo(
    () => [
      {
        label: "General Info",
        component: (
          <ExerciseCreationInfo
            disableTrialSelection={exerciseId != null || selectedCourse}
          />
        ),
      },
      { label: "Template", component: <ExerciseCreationTemplate /> },
      { label: "Testcases", component: <ExerciseCreationTestcases /> },
    ],
    [exerciseId, selectedCourse],
  );

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
      testcases.every(({ output }) => output.trim() !== "")
    );
  }, [testcases]);

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
          {activeStep > 0 ? (
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
          ) : (
            <Box sx={{ mx: 1 }}>
              <ButtonWithConfirmation
                variant="outlined"
                disabled={activeStep > 0}
                confirmText={"Exit"}
                confirmColor="error"
                title="Exit exercise creation?"
                description="You will lose all recent changes"
                color="error"
                onConfirm={() => {
                  history.back();
                }}
              >
                Cancel
              </ButtonWithConfirmation>
            </Box>
          )}
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
          {exerciseId == null ? (
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
                handleComplete(setCreationPercentage, setCreationStatus)
                  .then(() => {
                    setTimeout(() => {
                      setCreationStatus("");
                      navigate(`/trials/${exercise.trialId}`);
                    }, 2000); // TODO: remove this delay after fixing rabbit waiting
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
                handleComplete(setCreationPercentage, setCreationStatus)
                  .then(() => {
                    setCreationStatus("");
                    navigate(
                      `/trials/${exercise.trialId}/exercises/${exerciseId}`,
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

export default ExerciseSettingsStepper;
