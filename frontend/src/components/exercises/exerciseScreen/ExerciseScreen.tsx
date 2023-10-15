import Grid from "@mui/material/Grid";
import { CircularProgress } from "@mui/material";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { enqueueSnackbar } from "notistack";
import { Exercise, Testcase } from "@exercises/types.ts";
import ExerciseStatement from "@exercises/exerciseScreen/ExerciseStatement.tsx";
import SubmissionEditor from "@exercises/exerciseScreen/SubmissionEditor.tsx";

const ExerciseScreen = () => {
  const { getExerciseById, getTestcasesFromExercise, getTemplateFromExercise } =
    useExerciseService();
  const { exerciseId, trialId } = useParams();
  const [exercise, setExercise] = useState<Exercise>();
  const [testcases, setTestcases] = useState<Testcase[]>([]);

  useEffect(() => {
    if (exerciseId == null) return;

    getExerciseById(exerciseId)
      .then((exercise) => {
        setExercise(exercise);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });

    getTestcasesFromExercise(exerciseId)
      .then((testcases) => {
        setTestcases(testcases);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [
    exerciseId,
    getExerciseById,
    getTestcasesFromExercise,
    getTemplateFromExercise,
  ]);

  if (trialId == null || exerciseId == null || exercise == null) {
    return <CircularProgress />;
  }

  return (
    <Grid container style={{ height: "100%" }}>
      <Grid item xs={5} style={{ height: "100%" }}>
        <ExerciseStatement exercise={exercise} testcases={testcases} />
      </Grid>
      <Grid item xs={7} style={{ height: "100%" }}>
        <SubmissionEditor exerciseId={exerciseId} trialId={trialId} />
      </Grid>
    </Grid>
  );
};

export default ExerciseScreen;
