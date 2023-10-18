import {
  Card,
  CardContent,
  Checkbox,
  CircularProgress,
  Divider,
  FormControl,
  FormControlLabel,
  InputLabel,
  Select,
  TextField,
} from "@mui/material";
import MenuItem from "@mui/material/MenuItem";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { useLocation } from "react-router-dom";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { useEffect, useState } from "react";
import { UserCourse } from "@courses/types.ts";
import { Trial } from "@trials/types.ts";
import { enqueueSnackbar } from "notistack";
import { ForbiddenError } from "@error/types.ts";
import Grid from "@mui/material/Grid";
import Markdown from "react-markdown";
import exerciseAtom from "@exercises/exerciseCreation/ExerciseAtom.ts";
import Box from "@mui/material/Box";

interface ExerciseCreationState {
  state: null | {
    courseId: string;
    trialId: string;
  };
}

const ExerciseCreationInfo = () => {
  const globalErrorSetter = useGlobalErrorSetter();
  const { getUserCourses } = useCourseService();
  const { getTrialsByCourseId, isTrialEnded } = useTrialService();
  const { state }: ExerciseCreationState =
    useLocation() as ExerciseCreationState;
  const [user] = useAtom(userAtom);
  const [areCoursesFetched, setAreCoursesFetched] = useState(false);
  const [userCourses, setUserCourses] = useState<UserCourse[]>([]);
  const [trials, setTrials] = useState<Trial[]>([]);
  const [exercise, setExercise] = useAtom(exerciseAtom);

  useEffect(() => {
    if (user == null) return;

    getUserCourses(user.id)
      .then((userCourses: UserCourse[]) => {
        setUserCourses(
          userCourses.filter((userCourse) =>
            ["COLLABORATOR", "ADMIN"].includes(userCourse.role),
          ),
        );
        setAreCoursesFetched(true);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [getUserCourses, globalErrorSetter, state, user]);

  useEffect(() => {
    if (user == null || exercise.courseId === "") return;

    getTrialsByCourseId(exercise.courseId)
      .then((trials: Trial[]) => {
        setTrials(
          trials.filter(
            (trial) =>
              trial.courseId === exercise.courseId &&
              !isTrialEnded(trial) &&
              userCourses.some(({ course }) => course.id === exercise.courseId),
          ),
        );
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [
    getTrialsByCourseId,
    globalErrorSetter,
    state,
    user,
    exercise.courseId,
    userCourses,
    isTrialEnded,
  ]);

  if (!areCoursesFetched) {
    return (
      <Card
        style={{ overflow: "scroll", height: "100%" }}
        className={"hidden-scrollbar"}
      >
        <Box
          display="flex"
          justifyContent="center"
          alignItems="center"
          height="100%"
        >
          <CircularProgress />
        </Box>
      </Card>
    );
  }

  if (userCourses.length === 0) {
    globalErrorSetter(
      new ForbiddenError("/exercises/new", "You cannot create exercises"),
    );
  }

  return (
    <Card
      style={{ overflow: "scroll", height: "100%" }}
      className={"hidden-scrollbar"}
    >
      <CardContent>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <FormControl fullWidth>
              <InputLabel id="courseId">Course</InputLabel>
              <Select
                value={exercise.courseId}
                label="Course"
                onChange={(event) => {
                  setExercise({
                    ...exercise,
                    courseId: event.target.value,
                    trialId: "",
                  });
                }}
              >
                {userCourses.map(({ course }) => (
                  <MenuItem key={course.id} value={course.id}>
                    {course.name} ({course.year})
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={6}>
            <FormControl fullWidth>
              {exercise.courseId !== "" ? (
                <>
                  <InputLabel id="trialId">Test</InputLabel>
                  <Select
                    value={exercise.trialId}
                    label="Test"
                    onChange={(event) => {
                      setExercise({ ...exercise, trialId: event.target.value });
                    }}
                  >
                    {trials.map((trial) => (
                      <MenuItem key={trial.id} value={trial.id}>
                        {trial.name}
                      </MenuItem>
                    ))}
                  </Select>
                </>
              ) : (
                <>
                  <InputLabel id="trialId">Select a Course first</InputLabel>

                  <Select value="" disabled></Select>
                </>
              )}
            </FormControl>
          </Grid>
          <Grid item xs={8}>
            <TextField
              fullWidth
              label="Exercise Name"
              value={exercise.name}
              onChange={(event) => {
                setExercise({ ...exercise, name: event.target.value });
              }}
            />
          </Grid>
          <Grid item xs={2}>
            <TextField
              fullWidth
              type="number"
              label="Time limit (seconds)"
              value={exercise.timeLimit}
              onChange={(event) => {
                setExercise({
                  ...exercise,
                  timeLimit: Number.parseInt(event.target.value),
                });
              }}
            />
          </Grid>
          <Grid item xs={2} style={{ display: "flex", alignItems: "center" }}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={exercise.isVisible}
                  onClick={() => {
                    setExercise({
                      ...exercise,
                      isVisible: !exercise.isVisible,
                    });
                  }}
                />
              }
              label="Public"
            />
          </Grid>

          <Grid item xs={12}>
            <Divider sx={{ my: 2 }}>Problem Statement</Divider>
          </Grid>

          <Grid item xs={6}>
            <TextField
              fullWidth
              multiline
              minRows={3}
              label="Problem statement"
              value={exercise.statement}
              onChange={(event) => {
                setExercise({ ...exercise, statement: event.target.value });
              }}
            />
          </Grid>
          <Grid item xs={6}>
            <Markdown>
              {exercise.statement === ""
                ? "_Start writing to see the markdown preview of the problem statement_"
                : exercise.statement}
            </Markdown>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};

export default ExerciseCreationInfo;
