import ExerciseScreen from "@exercises/exerciseScreen/ExerciseScreen.tsx";
import {
  Button,
  CircularProgress,
  Paper,
  Skeleton,
  Tab,
  Tabs,
  Typography,
} from "@mui/material";
import { SyntheticEvent, useEffect, useState } from "react";
import Box from "@mui/material/Box";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { useNavigate, useParams } from "react-router-dom";
import { Exercise, SubmissionResult } from "@exercises/types.ts";
import { enqueueSnackbar } from "notistack";
import { ChevronLeft } from "@mui/icons-material";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import { ResourceNotFoundError } from "@error/types.ts";
import { Exam, isExam, isPractice, Practice, Trial } from "@trials/types.ts";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import {
  differenceInDays,
  differenceInMinutes,
  intervalToDuration,
} from "date-fns";

const ExerciseNavigatorWrapper = () => {
  const navigate = useNavigate();
  const globalErrorSetter = useGlobalErrorSetter();
  const { trialId, exerciseId } = useParams();
  const { getExercisesByTrialId, getUserLastSubmissionFromExercise } =
    useExerciseService();
  const { getTrialById, isTrialEnded } = useTrialService();
  const [exercises, setExercises] = useState<Exercise[]>();
  const [trial, setTrial] = useState<Practice | Exam>();
  const [openTab, setOpenTab] = useState<number>();
  const [timeLeft, setTimeLeft] = useState("");
  const [results, setResults] = useState<Record<string, SubmissionResult[]>>(
    {},
  );

  useEffect(() => {
    if (trialId == null) return;

    getTrialById(trialId)
      .then((trial) => {
        setTrial(trial);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });

    getExercisesByTrialId(trialId)
      .then((exercises) => {
        setExercises(exercises);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [trialId, getExercisesByTrialId, getTrialById]);

  useEffect(() => {
    if (exerciseId == null || exercises == null) return;

    exercises.forEach((exercise) => {
      getUserLastSubmissionFromExercise(exercise.id)
        .then((submission) => {
          setResults((prevState) => {
            return {
              ...prevState,
              [exercise.id]: Object.values(submission)[0],
            };
          });
        })
        .catch((err: Error) => {
          enqueueSnackbar(err.message, { variant: "error" });
        });
    });

    const exerciseIndex = exercises.findIndex(
      (exercise) => exercise.id === exerciseId,
    );
    if (exerciseIndex === -1) {
      globalErrorSetter(
        new ResourceNotFoundError(
          { id: exerciseId },
          "Exercise",
          `Exercise ${exerciseId} not found`,
        ),
      );
    }

    setOpenTab(exerciseIndex);
  }, [
    exercises,
    exerciseId,
    getUserLastSubmissionFromExercise,
    globalErrorSetter,
  ]);

  const handleTabChange = (event: SyntheticEvent, newValue: number) => {
    event.preventDefault();
    if (exercises == null) {
      enqueueSnackbar("Keep calm!", { variant: "error" });
      return;
    }
    navigate(`/trials/${trialId}/exercises/${exercises[newValue].id}`);
  };

  const setTimeLeftHandler = (trial: Trial) => {
    if (isPractice(trial)) {
      const delta = intervalToDuration({
        start: new Date(),
        end: trial.deadline,
      });

      if (differenceInMinutes(trial.deadline, new Date()) < 60) {
        setTimeLeft(`${delta.minutes}m ${delta.seconds}s`);
      } else if (differenceInDays(trial.deadline, new Date()) < 2) {
        setTimeLeft(
          `${(delta.hours ?? 0) + 24 * (delta.days ?? 0)}h ${delta.minutes}m`,
        );
      } else {
        setTimeLeft(`${differenceInDays(trial.deadline, new Date())}d`);
      }
    }
    if (isExam(trial)) {
      setTimeLeft("not implemented");
    }
  };

  if (exercises == null || openTab == null || trial == null) {
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

  setInterval(() => {
    setTimeLeftHandler(trial);
  }, 1000);

  return (
    <Box
      height="100%"
      sx={{
        flexGrow: 1,
        display: "flex",
      }}
    >
      <Box>
        <Button
          fullWidth
          onClick={() => {
            navigate(`/trials/${trialId}`);
          }}
        >
          <ChevronLeft />
        </Button>
        <Paper sx={{ p: 1 }}>
          {isTrialEnded(trial) ? (
            <Typography variant="body2" align="center" fontWeight="bold">
              Time&apos;s up!
            </Typography>
          ) : (
            <>
              <Typography variant="body2" align="center">
                Time Left:
              </Typography>
              <Typography variant="body2" align="center" fontWeight="bold">
                {timeLeft === "" ? <Skeleton /> : timeLeft}
              </Typography>
            </>
          )}
        </Paper>
        <Tabs orientation="vertical" value={openTab} onChange={handleTabChange}>
          {exercises.map((exercise, i) => (
            <Tab
              icon={
                <Box sx={{ position: "relative" }}>
                  <CircularProgress
                    variant="determinate"
                    sx={{
                      color: (theme) => {
                        // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
                        return results[exercise.id] == null
                          ? theme.palette.grey[500]
                          : "red";
                      },
                    }}
                    size="1rem"
                    thickness={8}
                    value={100}
                  />
                  <CircularProgress
                    size="1rem"
                    variant="determinate"
                    sx={{ position: "absolute", left: 0 }}
                    value={
                      // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
                      results[exercise.id] == null // TODO: investigate typing on this block
                        ? 0
                        : (results[exercise.id].filter(
                            ({ status }) => status === "CORRECT",
                          ).length /
                            results[exercise.id].length) *
                          100
                    }
                    color="success"
                    thickness={8}
                  />
                </Box>
              }
              iconPosition="end"
              key={i}
              label={`${i}`}
              title={exercise.name}
            />
          ))}
        </Tabs>
      </Box>
      <ExerciseScreen exerciseId={exercises[openTab].id} />
    </Box>
  );
};

export default ExerciseNavigatorWrapper;
