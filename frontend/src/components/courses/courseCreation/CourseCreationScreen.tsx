import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { useEffect, useState } from "react";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import {
  Button,
  Card,
  CardContent,
  CircularProgress,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import { useLocation, useNavigate } from "react-router-dom";
import { AxiosError } from "axios";
import { enqueueSnackbar } from "notistack";
import AcademicYearPicker from "@structure/AcademicYearPicker/AcademicYearPicker.tsx";
import { getCourseYear } from "@structure/AcademicYearPicker/utils.ts";
import { ForbiddenError } from "@error/types.ts";

export default function CourseCreationScreen() {
  const navigate = useNavigate();
  const location = useLocation();
  const [user] = useAtom(userAtom);
  const globalErrorSetter = useGlobalErrorSetter();
  const { createCourse } = useCourseService();
  const [name, setName] = useState("");
  const [year, setYear] = useState(getCourseYear());
  const [isError, setIsError] = useState(false);
  const [isRequestProcessing, setIsRequestProcessing] = useState(false);
  const [isUserFetched, setIsUserFetched] = useState(false);

  const handleSubmit = () => {
    setIsRequestProcessing(true);
    createCourse(name, year)
      .then((course) => {
        enqueueSnackbar(`Course ${name} (${year}) created successfully`, {
          variant: "success",
        });
        navigate(`/courses/${course.id}`);
      })
      .catch((err) => {
        if (
          err instanceof AxiosError &&
          err.response?.status === 400 &&
          (err.response.data as { message: string }).message ===
            "Already existing course"
        ) {
          setIsError(true);
          enqueueSnackbar("Duplicate course name and year", {
            variant: "error",
          });
        }
      })
      .finally(() => {
        setIsRequestProcessing(false);
      });
  };

  useEffect(() => {
    if (user == null) {
      setIsUserFetched(false);
      return;
    }

    setIsUserFetched(true);
    if (!(user.isProfessor || user.isSuperuser)) {
      globalErrorSetter(
        new ForbiddenError(location.pathname, `You cannot access this page`),
      );
    }
  }, [user, globalErrorSetter, location.pathname]);

  if (!isUserFetched) {
    return (
      <Box display="flex" justifyContent="center" paddingTop={2}>
        {" "}
        <CircularProgress />{" "}
      </Box>
    );
  }

  return (
    <Box display="flex" justifyContent="center" paddingTop={2}>
      <Card sx={{ width: "48rem" }}>
        <CardContent>
          <Stack spacing={2}>
            <Typography variant="h5" color="text.secondary">
              New Course
            </Typography>

            <Box>
              <Grid container spacing={1}>
                <Grid item xs={12} md={8}>
                  <TextField
                    error={isError}
                    fullWidth
                    label="Name"
                    value={name}
                    onChange={(event) => {
                      setIsError(false);
                      setName(event.target.value);
                    }}
                  />
                </Grid>
                <Grid item xs={12} md={4}>
                  <AcademicYearPicker
                    value={year}
                    onChange={(val) => {
                      setYear(val);
                      setIsError(false);
                    }}
                    error={isError}
                  />
                </Grid>
              </Grid>
            </Box>
            <Box display="flex" justifyContent="center">
              <Button
                variant="outlined"
                disabled={name === "" || isRequestProcessing}
                onClick={handleSubmit}
              >
                {isRequestProcessing && (
                  <CircularProgress size={"1rem"} sx={{ mr: 1 }} />
                )}
                Create
              </Button>
            </Box>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
