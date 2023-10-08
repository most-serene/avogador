import { Button, Step, StepLabel, Stepper, Typography } from "@mui/material";
import ExerciseCreationInfo from "@exercises/exerciseCreation/ExerciseCreationInfo.tsx";
import { useEffect, useState } from "react";
import Box from "@mui/material/Box";
import { useAtom } from "jotai";
import exerciseAtom, {
  getInitializedExercise,
} from "@exercises/exerciseCreation/ExerciseAtom.ts";
import ExerciseCreationTemplate from "@exercises/exerciseCreation/template/ExerciseCreationTemplate.tsx";
import templateAtom, {
  getInitializedTemplate,
} from "@exercises/exerciseCreation/TemplateAtom.ts";

const steps = [
  { label: "General Info", component: <ExerciseCreationInfo /> },
  { label: "Template", component: <ExerciseCreationTemplate /> },
  { label: "Testcases", component: <Typography>Testcases</Typography> },
];

const ExerciseCreationScreen = () => {
  const [activeStep, setActiveStep] = useState(0);
  const [, setExercise] = useAtom(exerciseAtom);
  const [, setTemplate] = useAtom(templateAtom);

  useEffect(() => {
    setExercise(getInitializedExercise());
    setTemplate([...getInitializedTemplate()]);

    return () => {
      setExercise(getInitializedExercise());
      setTemplate([...getInitializedTemplate()]);
    };
  }, [setExercise, setTemplate]);

  return (
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
          sx={{ mx: 1 }}
          disabled={activeStep >= steps.length - 1}
          onClick={() => {
            setActiveStep(Math.min(activeStep + 1, steps.length - 1));
          }}
        >
          Next
        </Button>
      </Box>
    </Box>
  );
};

export default ExerciseCreationScreen;
