import {
  Button,
  Card,
  CardContent,
  Checkbox,
  CircularProgress,
  Divider,
  FormControl,
  FormControlLabel,
  InputLabel,
  Select,
  styled,
  TextField,
} from "@mui/material";
import { lazy, useEffect, useState } from "react";
const MenuItem = lazy(() => import("@mui/material/MenuItem"));
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { UserCourse } from "@courses/types.ts";
import { Trial } from "@trials/types.ts";
import { enqueueSnackbar } from "notistack";
import { ForbiddenError } from "@error/types.ts";
const Grid = lazy(() => import("@mui/material/Grid"));
import exerciseAtom from "@exercises/exerciseCreation/ExerciseAtom.ts";
const Box = lazy(() => import("@mui/material/Box"));
import MarkdownEditor from "@structure/editors/MarkdownEditor.tsx";
import { Upload } from "@mui/icons-material";
import { Exercise, StroxCell, Testcase } from "@exercises/types.ts";
import testcasesAtom from "@exercises/exerciseCreation/TestcasesAtom.ts";
import templateAtom from "@exercises/exerciseCreation/TemplateAtom.ts";
const OldMarkdownEditor = lazy(
  () =>
    import("@exercises/exerciseCreation/mardownEditors/OldMarkdownEditor.tsx"),
);

const VisuallyHiddenInput = styled("input")({
  clip: "rect(0 0 0 0)",
  clipPath: "inset(50%)",
  height: 1,
  overflow: "hidden",
  position: "absolute",
  bottom: 0,
  left: 0,
  whiteSpace: "nowrap",
  width: 1,
});

interface ExerciseDump {
  exercise: Exercise;
  testcases: Testcase[];
  template: { cells: StroxCell[] };
}

interface ExerciseCreationInfo {
  disableTrialSelection?: boolean;
}

const ExerciseCreationInfo = ({
  disableTrialSelection = false,
}: ExerciseCreationInfo) => {
  const globalErrorSetter = useGlobalErrorSetter();
  const { getUserCourses } = useCourseService();
  const { getTrialsByCourseId, isTrialEnded } = useTrialService();
  const [user] = useAtom(userAtom);
  const [areCoursesFetched, setAreCoursesFetched] = useState(false);
  const [userCourses, setUserCourses] = useState<UserCourse[]>([]);
  const [trials, setTrials] = useState<Trial[]>([]);
  const [exercise, setExercise] = useAtom(exerciseAtom);
  const [, setTemplate] = useAtom(templateAtom);
  const [, setTestcases] = useAtom(testcasesAtom);
  const [isOldEditor, setIsOldEditor] = useState(false);

  useEffect(() => {
    if (user == null) return;

    getUserCourses(user.id)
      .then((userCourses: UserCourse[]) => {
        setUserCourses(
          userCourses
            .filter((userCourse) =>
              ["COLLABORATOR", "ADMIN"].includes(userCourse.role),
            )
            .filter((userCourse) => !userCourse.course.isArchived),
        );
        setAreCoursesFetched(true);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [getUserCourses, globalErrorSetter, user, setExercise]);

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
    user,
    exercise.courseId,
    userCourses,
    isTrialEnded,
  ]);

  const handleImport = ({
    exercise: importedExercise,
    testcases: importedTestcases,
    template: importedTemplate,
  }: ExerciseDump) => {
    setExercise({
      ...exercise,
      name: importedExercise.name,
      statement: importedExercise.statement,
      isVisible: importedExercise.isVisible,
      timeLimit: importedExercise.timeLimit,
    });
    setTestcases(
      importedTestcases.map(({ input, output, isVisible }) => {
        return { input, output, isVisible };
      }),
    );
    setTemplate(importedTemplate.cells);
  };

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
                disabled={disableTrialSelection}
                value={userCourses.length === 0 ? "" : exercise.courseId}
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
                    disabled={disableTrialSelection}
                    value={trials.length === 0 ? "" : exercise.trialId}
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
                setExercise({
                  ...exercise,
                  name: event.target.value.trimStart(),
                });
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
            <Box position="relative">
              <Divider sx={{ my: 2 }}>Problem Statement</Divider>
              <Card
                sx={{
                  position: "absolute",
                  right: 0,
                  top: "50%",
                  transform: "translate(0, -50%)",
                }}
              >
                <Button
                  onClick={() => {
                    setIsOldEditor((prevState) => !prevState);
                  }}
                >
                  Switch to {isOldEditor ? "new" : "old"} Editor
                </Button>
              </Card>
            </Box>
          </Grid>

          {isOldEditor ? (
            <OldMarkdownEditor />
          ) : (
            <Grid item xs={12}>
              <MarkdownEditor
                value={exercise.statement}
                onChange={(markdown) => {
                  setExercise({ ...exercise, statement: markdown });
                }}
              />
            </Grid>
          )}
          <Grid item xs={12} sx={{ display: "flex", justifyContent: "center" }}>
            <Button
              component="label"
              role={undefined}
              variant="outlined"
              startIcon={<Upload />}
            >
              Import from JSON
              <VisuallyHiddenInput
                type="file"
                onChange={(event) => {
                  if (
                    event.target.files != null &&
                    event.target.files.length === 1
                  ) {
                    const reader = new FileReader();
                    reader.onload = () => {
                      handleImport(
                        JSON.parse(reader.result as string) as ExerciseDump,
                      );
                    };
                    reader.readAsText(event.target.files[0]);
                  }
                }}
                multiple
              />
            </Button>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};

export default ExerciseCreationInfo;
