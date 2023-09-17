import {
  Button,
  Card,
  CardContent,
  Skeleton,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import useCourseService from "@courses/hooks/useCourseService";
import { GetCoursesDetailResponse } from "@courses/types";
import { enqueueSnackbar } from "notistack";
import Box from "@mui/material/Box";
import { useGlobalErrorSetter } from "@components/error/GlobalErrorState";
import { ResourceNotFoundError } from "@components/error/types";
import { AxiosError } from "axios";

const JoinCourse = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const { getCourseById, joinCourse } = useCourseService();
  const { courseId } = useParams();
  const navigate = useNavigate();
  const [course, setCourse] = useState<GetCoursesDetailResponse>();
  const [error, setError] = useState<boolean>(false);
  const globalErrorSetter = useGlobalErrorSetter();

  const joinCode = useMemo<string>(() => {
    const code = searchParams.get("code");
    return code ?? "";
  }, [searchParams]);

  const joinHandler = () => {
    if (course === undefined) return;
    joinCourse(course, joinCode)
      .then((res) => {
        if (res) {
          enqueueSnackbar(`You joined ${course.name} successfully!`, {
            variant: "success",
          });
          navigate(`/courses/${course.id}`);
        } else {
          setError(true);
          enqueueSnackbar(`Your join code is wrong`, { variant: "error" });
        }
      })
      .catch((e) => {
        console.error(e);
        enqueueSnackbar("An error has occurred", { variant: "error" });
      });
  };

  useEffect(() => {
    if (courseId === undefined) return;

    getCourseById(courseId)
      .then((c) => {
        setCourse(c);
      })
      .catch((err) => {
        console.error(err);
        if (err instanceof AxiosError && err.response?.status === 404) {
          globalErrorSetter(
            new ResourceNotFoundError(
              { id: courseId },
              "Course",
              `Course ${courseId} not found`,
            ),
          );
        }
      });
  }, [getCourseById, courseId, globalErrorSetter]);

  return (
    <Card sx={{ width: "32rem" }} raised>
      <CardContent>
        <Stack spacing={2}>
          <Typography variant="h5" color="text.secondary" gutterBottom>
            {course ? `Join the course: ${course.name}` : <Skeleton />}
          </Typography>

          <TextField
            fullWidth
            value={joinCode}
            error={error}
            label="Join code"
            onChange={(event) => {
              setError(false);
              setSearchParams({
                code: event.target.value,
              });
            }}
          />
        </Stack>
        <Box display="flex" justifyContent="center" marginTop=".5rem">
          <Button
            disabled={course === undefined || joinCode === ""}
            onClick={joinHandler}
            variant={"outlined"}
          >
            Join
          </Button>
        </Box>
      </CardContent>
    </Card>
  );
};

export default JoinCourse;
