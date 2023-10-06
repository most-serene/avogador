import {
  Card,
  CardContent,
  FormControl,
  InputLabel,
  Select,
  Typography,
} from "@mui/material";
import MenuItem from "@mui/material/MenuItem";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { useLocation } from "react-router-dom";
import { useAtom } from "jotai/index";
import userAtom from "@authentication/userAtom.ts";
import { useEffect, useState } from "react";
import { UserCourse } from "@courses/types.ts";
import { Trial } from "@trials/types.ts";
import { enqueueSnackbar } from "notistack";
import { ForbiddenError } from "@error/types.ts";
import Grid from "@mui/material/Grid";

interface ExerciseCreationState {
  state: null | {
    courseId: string;
    trialId: string;
  };
}

const ExerciseCreationInfo = () => {
  const globalErrorSetter = useGlobalErrorSetter();
  const { getUserCourses } = useCourseService();
  const { getTrialsByCourseId } = useTrialService();
  const { state }: ExerciseCreationState =
    useLocation() as ExerciseCreationState;
  const [user] = useAtom(userAtom);
  const [areCoursesFetched, setAreCoursesFetched] = useState(false);
  const [userCourses, setUserCourses] = useState<UserCourse[]>([]);
  const [trials, setTrials] = useState<Trial[]>([]);
  const [courseId, setCourseId] = useState(state?.courseId ?? "");
  const [trialId, setTrialId] = useState(state?.trialId ?? "");

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
    if (user == null || courseId === "") return;

    getTrialsByCourseId(courseId)
      .then((trials: Trial[]) => {
        setTrials(
          trials.filter(
            (trial) =>
              trial.courseId === courseId &&
              userCourses.some(({ course }) => course.id === courseId),
          ),
        );
        console.log(trials);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [
    getTrialsByCourseId,
    globalErrorSetter,
    state,
    user,
    courseId,
    userCourses,
  ]);

  if (!areCoursesFetched) return <Typography> Loading... </Typography>;

  if (userCourses.length === 0) {
    globalErrorSetter(
      new ForbiddenError("/exercises/new", "You cannot create exercises"),
    );
  }

  return (
    <Card>
      <CardContent>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <FormControl fullWidth>
              <InputLabel id="courseId">Course</InputLabel>
              <Select
                value={courseId}
                onChange={(event) => {
                  setCourseId(event.target.value);
                }}
              >
                {userCourses.map(({ course }) => (
                  <MenuItem key={course.id} value={course.id}>
                    {course.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={6}>
            <FormControl fullWidth>
              {courseId !== "" ? (
                <>
                  <InputLabel id="courseId">Test</InputLabel>
                  <Select
                    value={trialId}
                    onChange={(event) => {
                      setTrialId(event.target.value);
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
                  <InputLabel id="courseId">Select a Course first</InputLabel>

                  <Select value="" disabled></Select>
                </>
              )}
            </FormControl>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};

export default ExerciseCreationInfo;
