import {
  Button,
  Card,
  CardContent,
  Checkbox,
  Container,
  FormControl,
  FormControlLabel,
  InputLabel,
  Select,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import { useLocation, useNavigate } from "react-router-dom";
import MenuItem from "@mui/material/MenuItem";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import { ForbiddenError } from "@error/types.ts";
import { enqueueSnackbar } from "notistack";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { UserCourse } from "@courses/types.ts";
import { DateTimePicker } from "@mui/x-date-pickers";
import { addDays } from "date-fns";
import useTrialService from "@trials/hooks/useTrialService.tsx";

const TrialCreationScreen = () => {
  const globalErrorSetter = useGlobalErrorSetter();
  const { createPractice } = useTrialService();
  const { getUserCourses } = useCourseService();
  const navigate = useNavigate();
  const { state }: { state: null | { courseId: string } } = useLocation() as {
    state: null | { courseId: string };
  };
  const [user] = useAtom(userAtom);
  const [courses, setCourses] = useState<UserCourse[]>([]);
  const [courseId, setCourseId] = useState<string>(state?.courseId ?? "");
  const [startDate, setStartDate] = useState(addDays(new Date(), 1));
  const [deadline, setDeadline] = useState(addDays(new Date(), 7));
  const [language, setLanguage] = useState<"C" | "CPP" | "PYTHON" | "JAVA">();
  const [trialName, setTrialName] = useState<string>("");
  const [trialType] = useState<"PRACTICE" | "EXAM">("PRACTICE");
  const [isVisible, setIsVisible] = useState(false);
  const [isPublic, setIsPublic] = useState(true);

  useEffect(() => {
    if (user == null) return;

    getUserCourses(user.id)
      .then((userCourses: UserCourse[]) => {
        if (
          !userCourses.some(
            (userCourse) =>
              userCourse.role === "COLLABORATOR" || userCourse.role === "ADMIN",
          )
        ) {
          globalErrorSetter(
            new ForbiddenError("/trials/new", "You cannot create trials"),
          );
        }

        const preCourseId = state === null ? undefined : state.courseId;

        if (preCourseId == null) {
          setCourses(
            userCourses.filter(
              (userCourse) =>
                userCourse.role === "COLLABORATOR" ||
                userCourse.role === "ADMIN",
            ),
          );
          return;
        }

        const preCourse = userCourses.find(
          (userCourse) => userCourse.course.id === preCourseId,
        );

        if (
          preCourse != null &&
          (preCourse.role === "COLLABORATOR" || preCourse.role === "ADMIN")
        ) {
          setCourses([preCourse]);
        } else {
          globalErrorSetter(
            new ForbiddenError(
              "/trials/new",
              `You cannot create trials in ${preCourseId}`,
            ),
          );
        }
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [getUserCourses, globalErrorSetter, state, user]);

  const isFormValid = useMemo(() => {
    return (
      courseId != "" &&
      trialName.trim() !== "" &&
      language != null &&
      startDate < deadline
    );
  }, [courseId, deadline, language, startDate, trialName]);

  const handleTrialCreation = useCallback(() => {
    if (language === undefined) {
      throw new Error("Illegal state");
    }

    createPractice({
      courseId: courseId,
      name: trialName.trim(),
      isVisible: isVisible,
      isPublic: isPublic,
      language: language,
      startTimestamp: startDate,
      trialType: trialType,
      deadline: deadline,
    })
      .then((createdPractice) => {
        enqueueSnackbar(
          `Practice ${createdPractice.name} created successfully`,
          {
            variant: "success",
          },
        );

        navigate(`/practices/${createdPractice.id}`);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [
    courseId,
    createPractice,
    deadline,
    isPublic,
    isVisible,
    language,
    navigate,
    startDate,
    trialName,
    trialType,
  ]);

  return (
    <Container>
      <Box display="flex" justifyContent="center" paddingTop={2}>
        <Card sx={{ width: "48rem" }}>
          <CardContent>
            <Stack spacing={2}>
              <Typography variant="h5" color="text.secondary">
                New Trial
              </Typography>

              <Box>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <FormControl fullWidth>
                      <InputLabel id="courseId">Course</InputLabel>
                      <Select
                        labelId="courseId"
                        id="courseId"
                        label="Course"
                        disabled={courses.length === 1}
                        value={courseId}
                        onChange={(event) => {
                          setCourseId(event.target.value);
                        }}
                      >
                        {courses.map((c) => (
                          <MenuItem key={c.course.id} value={c.course.id}>
                            {c.course.name} - {c.course.year}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Grid>
                  <Grid item xs={6}>
                    <FormControl fullWidth>
                      <InputLabel id="trialType">Trial type</InputLabel>
                      <Select
                        labelId="trialType"
                        id="trialType"
                        label="Trial type"
                        disabled
                        value={trialType}
                      >
                        <MenuItem value={"PRACTICE"}>Practice</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>

                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Trial name"
                      value={trialName}
                      onChange={(event) => {
                        setTrialName(event.target.value);
                      }}
                    />
                  </Grid>
                  <Grid item xs={8}>
                    <FormControl fullWidth>
                      <InputLabel id="language">Language</InputLabel>
                      <Select
                        labelId="language"
                        value={language}
                        onChange={(event) => {
                          setLanguage(
                            event.target.value as
                              | "C"
                              | "CPP"
                              | "PYTHON"
                              | "JAVA"
                              | undefined,
                          );
                        }}
                        id="language"
                        label="Language"
                      >
                        <MenuItem value={"C"}>C</MenuItem>
                        <MenuItem value={"CPP"}>C++</MenuItem>
                        <MenuItem value={"PYTHON"}>Python</MenuItem>
                        <MenuItem value={"JAVA"}>Java</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>
                  <Grid item xs={2}>
                    <FormControlLabel
                      value={isVisible}
                      onChange={() => {
                        setIsVisible(!isVisible);
                      }}
                      control={<Checkbox />}
                      label="Visible"
                    />
                  </Grid>
                  <Grid item xs={2}>
                    <FormControlLabel
                      control={<Checkbox />}
                      label="Public"
                      value={isPublic}
                      onChange={() => {
                        setIsPublic(!isPublic);
                      }}
                      disabled
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <DateTimePicker
                      sx={{ width: "100%" }}
                      ampm={false}
                      disablePast
                      value={startDate}
                      onChange={(newVal) => {
                        if (newVal) setStartDate(newVal);
                      }}
                      label="Start timestamp"
                      maxDate={deadline}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <DateTimePicker
                      sx={{ width: "100%" }}
                      ampm={false}
                      value={deadline}
                      onChange={(newVal) => {
                        if (newVal) setDeadline(newVal);
                      }}
                      disablePast
                      label="Deadline"
                      minDate={startDate}
                    />
                  </Grid>
                </Grid>
              </Box>

              <Box display="flex" justifyContent="center">
                <Button
                  variant="outlined"
                  disabled={!isFormValid}
                  onClick={handleTrialCreation}
                >
                  Create
                </Button>
              </Box>
            </Stack>
          </CardContent>
        </Card>
      </Box>
    </Container>
  );
};

export default TrialCreationScreen;
