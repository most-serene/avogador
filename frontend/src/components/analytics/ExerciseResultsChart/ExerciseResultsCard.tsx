import ExerciseResultsChart from "@components/analytics/ExerciseResultsChart/ExerciseResultsChart.tsx";
import {
  Card,
  CardContent,
  CircularProgress,
  FormControl,
  Grid,
  InputLabel,
  Select,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
import { Trial } from "@trials/types.ts";
import { Exercise } from "@exercises/types.ts";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { enqueueSnackbar } from "notistack";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import useAnalyticsService from "@components/analytics/hooks/useAnalyticsService.tsx";
import { ExerciseResults } from "@components/analytics/types.ts";
import MenuItem from "@mui/material/MenuItem";

interface ExerciseResultsCardProps {
  courseId: string;
  height?: number;
}

const ExerciseResultsCard = ({
  courseId,
  height = 200,
}: ExerciseResultsCardProps) => {
  const { getTrialsByCourseId } = useTrialService();
  const { getExercisesByTrialId } = useExerciseService();
  const { getExerciseResults } = useAnalyticsService();
  const [trials, setTrials] = useState<Trial[]>();
  const [selectedTrial, setSelectedTrial] = useState<string>("");
  const [exercises, setExercises] = useState<Exercise[]>([]);
  const [selectedExercise, setSelectedExercise] = useState<string>("");
  const [results, setResults] = useState<ExerciseResults>();

  useEffect(() => {
    getTrialsByCourseId(courseId)
      .then((trials) => {
        setTrials(trials);
        if (trials.length > 0) {
          setSelectedTrial(trials[trials.length - 1].id);
        }
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [courseId, getTrialsByCourseId]);

  useEffect(() => {
    if (selectedTrial === "") {
      return;
    }

    getExercisesByTrialId(selectedTrial)
      .then((exercises) => {
        setExercises(exercises);
        if (exercises.length > 0) {
          setSelectedExercise(exercises[0].id);
        }
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [courseId, selectedTrial, getExercisesByTrialId]);

  useEffect(() => {
    if (selectedTrial === "" || selectedExercise === "") {
      return;
    }

    getExerciseResults(selectedTrial, selectedExercise)
      .then((results) => {
        setResults(results);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [courseId, selectedTrial, selectedExercise, getExerciseResults]);

  if (trials == null) {
    return (
      <Card raised>
        <CardContent
          sx={{
            height: height,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <CircularProgress />
        </CardContent>
      </Card>
    );
  }

  return (
    <Card raised>
      <CardContent>
        <Grid container spacing={2}>
          <Grid item xs={6} sx={{ mb: 1 }}>
            <FormControl fullWidth>
              <InputLabel id="trialId">Trial</InputLabel>
              <Select
                labelId="trialId"
                label="Trial"
                value={selectedTrial}
                onChange={(event) => {
                  setSelectedTrial(event.target.value);
                  setSelectedExercise("");
                  setExercises([]);
                  setResults(undefined);
                }}
              >
                {trials.map((trial) => (
                  <MenuItem key={trial.id} value={trial.id}>
                    {trial.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={6}>
            <FormControl fullWidth>
              <InputLabel id="exerciseId">Exercise</InputLabel>
              <Select
                labelId="exerciseId"
                label="Exercise"
                value={selectedExercise}
                onChange={(event) => {
                  setSelectedExercise(event.target.value);
                }}
                disabled={trials.length === 0 || exercises.length === 0}
              >
                {exercises.map((exercise) => (
                  <MenuItem key={exercise.id} value={exercise.id}>
                    {exercise.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
        </Grid>
        {selectedExercise === "" ? (
          <Typography sx={{ height: height }} textAlign="center">
            Select an exercise to view its data
          </Typography>
        ) : (
          <ExerciseResultsChart results={results} height={height} />
        )}
      </CardContent>
    </Card>
  );
};

export default ExerciseResultsCard;
