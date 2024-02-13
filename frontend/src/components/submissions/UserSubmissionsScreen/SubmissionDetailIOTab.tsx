import CopiableCard from "@structure/CopiableCard/CopiableCard.tsx";
import { useEffect, useState } from "react";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { Testcase } from "@exercises/types.ts";
import { enqueueSnackbar } from "notistack";
import { CircularProgress, Grid, Typography, useTheme } from "@mui/material";
import { DiffEditor } from "@monaco-editor/react";

interface SubmissionDetailIOTabProps {
  exerciseId: string;
  submissionId: string;
}

const SubmissionDetailIOTab = ({
  exerciseId,
  submissionId,
}: SubmissionDetailIOTabProps) => {
  const { getSubmissionOutputs, getTestcasesFromExercise } =
    useExerciseService();
  const theme = useTheme();
  const [outputs, setOutputs] = useState<Record<string, string | undefined>>();
  const [testcases, setTestcases] = useState<Testcase[]>();

  useEffect(() => {
    getTestcasesFromExercise(exerciseId)
      .then((testcases) => {
        setTestcases(testcases);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
    getSubmissionOutputs(exerciseId, submissionId)
      .then((outputs) => {
        setOutputs(outputs);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });

    return () => {
      setOutputs(undefined);
      setTestcases(undefined);
    };
  }, [
    exerciseId,
    submissionId,
    getSubmissionOutputs,
    getTestcasesFromExercise,
  ]);

  if (outputs == null || testcases == null) {
    return <CircularProgress />;
  }

  return (
    <Grid container spacing={1}>
      {outputs.compile !== "" && outputs.compile != null && (
        <Grid item xs={12}>
          <Typography variant="h5">Compilation Output</Typography>
          <CopiableCard>{outputs.compile}</CopiableCard>
        </Grid>
      )}
      {testcases.map((testcase, i) => (
        <>
          <Grid item xs={12}>
            <Typography variant="h5">Testcase {i}</Typography>
          </Grid>
          <Grid item xs={5}>
            <Typography variant="body1">Input</Typography>
            <CopiableCard>{testcase.input}</CopiableCard>
          </Grid>
          <Grid item xs={7}>
            <Typography variant="body1">Output</Typography>
            <DiffEditor
              theme={theme.palette.mode === "dark" ? "vs-dark" : "vs"}
              original={testcase.output}
              modified={outputs[testcase.id]}
              options={{
                readOnly: true,
                renderSideBySide: false,
                scrollBeyondLastLine: false,
                lineNumbers: "off",
                renderOverviewRuler: false,
                scrollbar: {
                  vertical: "hidden",
                  horizontal: "hidden",
                  handleMouseWheel: false,
                },
                lightbulb: {
                  enabled: false,
                },
              }}
            />
          </Grid>
        </>
      ))}
    </Grid>
  );
};

export default SubmissionDetailIOTab;
