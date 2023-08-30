import {
  Button,
  Card,
  CardActions,
  CardContent,
  Grid,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import useCourseService from "../hook/useCourseService";
import { GetCoursesDetailResponse } from "../types";

const JoinCourse = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const { getCourseById, joinCourse } = useCourseService();
  const { courseId } = useParams();
  const navigate = useNavigate();
  const [course, setCourse] = useState<GetCoursesDetailResponse>();

  const joinCode = useMemo<string>(() => {
    const code = searchParams.get("code");
    return code ?? "";
  }, [searchParams]);

  const joinHandler = () => {
    if (course === undefined) return;
    joinCourse(course, joinCode)
      .then((res) => {
        if (res) {
          //TODO: send notification
          navigate(`/courses/${course.id}`);
        } else {
          //TODO: send notification
        }
      })
      .catch((e) => {
        console.log(e);
      });
  };

  useEffect(() => {
    console.log(courseId);
    if (courseId === undefined) return;

    getCourseById(courseId)
      .then((c) => {
        setCourse(c);
      })
      .catch((err) => {
        console.error(err);
      });
  }, [getCourseById, courseId]);

  return (
    <Card sx={{ width: "32rem" }} raised>
      <CardContent>
        <Stack spacing={2}>
          <Typography variant="h5">Join Course</Typography>

          <Typography variant="body2">
            You are joining {course?.name}
          </Typography>

          <TextField
            fullWidth
            value={joinCode}
            label="Join code"
            onChange={(event) => {
              setSearchParams({
                code: event.target.value,
              });
            }}
          />
        </Stack>
        <CardActions>
          <Grid
            item
            xs
            display="flex"
            justifyContent="center"
            alignItems="center"
          >
            <Button
              disabled={course === undefined || joinCode === ""}
              onClick={joinHandler}
            >
              Join
            </Button>
          </Grid>
        </CardActions>
      </CardContent>
    </Card>
  );
};

export default JoinCourse;
