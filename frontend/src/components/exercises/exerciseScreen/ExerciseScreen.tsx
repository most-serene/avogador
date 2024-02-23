import Grid from "@mui/material/Grid";
import { CircularProgress } from "@mui/material";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { enqueueSnackbar } from "notistack";
import { Exercise, SubmissionResultMap, Testcase } from "@exercises/types.ts";
import ExerciseStatement from "@exercises/exerciseScreen/ExerciseStatement.tsx";
import SubmissionEditor from "@exercises/exerciseScreen/SubmissionEditor.tsx";
import SubmissionResultsPopup from "@exercises/exerciseScreen/SubmissionResultsPopup.tsx";
import { AxiosError } from "axios";
import { ArchivedCourseError } from "@error/types.ts";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";

interface ExerciseScreenProps {
  exerciseId: string;
}

const ExerciseScreen = ({ exerciseId }: ExerciseScreenProps) => {
  const {
    getExerciseById,
    getTestcasesFromExercise,
    getUserLastSubmissionFromExercise,
  } = useExerciseService();
  const globalErrorSetter = useGlobalErrorSetter();
  const { trialId } = useParams();
  const [exercise, setExercise] = useState<Exercise>();
  const [testcases, setTestcases] = useState<Testcase[]>([]);
  const [submissionResult, setSubmissionResult] =
    useState<SubmissionResultMap>();

  useEffect(() => {
    getExerciseById(exerciseId)
      .then((exercise) => {
        setExercise(exercise);
      })
      .catch((err: Error) => {
        if (err instanceof AxiosError && err.response?.status === 410) {
          globalErrorSetter(new ArchivedCourseError(err.message));
        } else {
          enqueueSnackbar(err.message, { variant: "error" });
        }
      });

    getTestcasesFromExercise(exerciseId)
      .then((testcases) => {
        setTestcases(testcases);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });

    getUserLastSubmissionFromExercise(exerciseId)
      .then((results) => {
        setSubmissionResult(results);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [
    exerciseId,
    getExerciseById,
    getTestcasesFromExercise,
    getUserLastSubmissionFromExercise,
    globalErrorSetter,
  ]);

  if (trialId == null) {
    return <CircularProgress />;
  }

  return (
    <Grid container style={{ height: "100%" }}>
      <Grid item xs={5} style={{ height: "100%" }} position="relative">
        <ExerciseStatement exercise={exercise} testcases={testcases} />
        <SubmissionResultsPopup
          submissionResult={submissionResult ?? {}}
          exerciseId={exerciseId}
        />
      </Grid>
      <Grid item xs={7} style={{ height: "100%" }}>
        <SubmissionEditor
          submissionDisabled={
            submissionResult == null ||
            (Object.keys(submissionResult).length > 0 &&
              Object.values(submissionResult)[0].some(
                (result) => result.status === "PENDING",
              ))
          }
          setSubmissionResult={setSubmissionResult}
          exerciseId={exerciseId}
          trialId={trialId}
        />
      </Grid>
    </Grid>
  );
};

export default ExerciseScreen;
