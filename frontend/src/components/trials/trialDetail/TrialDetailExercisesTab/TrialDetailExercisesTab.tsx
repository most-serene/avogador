import { Exam, Practice } from "@trials/types.ts";
import { useEffect, useState } from "react";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { Exercise } from "@exercises/types.ts";
import { enqueueSnackbar } from "notistack";
import { CourseDetail } from "@courses/types.ts";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import {
  Card,
  CardActionArea,
  CardContent,
  Container,
  Divider,
  Skeleton,
  Stack,
  Typography,
  useTheme,
} from "@mui/material";
import Box from "@mui/material/Box";
import ExerciseCard from "@trials/trialDetail/TrialDetailExercisesTab/ExerciseCard.tsx";
import { Add } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";

interface TrialDetailExercisesTabProps {
  trial: Practice | Exam;
  course: CourseDetail;
}

const TrialDetailExercisesTab = ({
  trial,
  course,
}: TrialDetailExercisesTabProps) => {
  const { getExercisesByTrialId } = useExerciseService();
  const [exercises, setExercises] = useState<Exercise[]>();
  const [user] = useAtom(userAtom);
  const theme = useTheme();
  const navigate = useNavigate();

  useEffect(() => {
    getExercisesByTrialId(trial)
      .then((exercises) => {
        setExercises(exercises);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [getExercisesByTrialId, trial]);

  if (exercises === undefined) {
    return (
      <Card raised>
        <CardContent>
          <Box display={"flex"} justifyContent={"center"}>
            <Typography variant={"h3"} width={"50%"}>
              <Skeleton />
            </Typography>
          </Box>
        </CardContent>
      </Card>
    );
  }

  return (
    <Container
      style={{ height: "100%", overflow: "scroll" }}
      className={"hidden-scrollbar"}
    >
      <Stack spacing={2}>
        {((user != null && user.isSuperuser) ||
          course.role === "COLLABORATOR" ||
          course.role === "ADMIN") && (
          <Card
            sx={{
              mb: 2,
              border: 2,
              borderColor: theme.palette.primary.main,
              borderStyle: "dashed",
            }}
            elevation={0}
          >
            <CardActionArea style={{ height: "100%" }}>
              <CardContent
                onClick={() => {
                  navigate("/exercises/new", {
                    state: {
                      courseId: course.id,
                      trialId: trial.id,
                    },
                  });
                }}
              >
                <Box display="flex" justifyContent="center" alignItems="center">
                  <Add sx={{ mr: 2 }} />
                  <Typography variant="h5"> Create new Exercise </Typography>
                </Box>
              </CardContent>
            </CardActionArea>
          </Card>
        )}
        {exercises
          .filter((exercise) => exercise.isVisible)
          .map((exercise) => (
            <ExerciseCard key={exercise.id} exercise={exercise} trial={trial} />
          ))}
      </Stack>
      {((user != null && user.isSuperuser) ||
        course.role === "COLLABORATOR" ||
        course.role === "ADMIN") && (
        <>
          <Divider
            sx={{
              "&::before, &::after": {
                borderColor: "primary.main",
              },
              mt: "2rem",
            }}
          >
            <Typography variant="body2" color="primary.main">
              Hidden exercises
            </Typography>
          </Divider>
          <Stack spacing={2}>
            {exercises
              .filter((exercise) => !exercise.isVisible)
              .map((exercise) => (
                <ExerciseCard
                  key={exercise.id}
                  exercise={exercise}
                  trial={trial}
                />
              ))}
          </Stack>
        </>
      )}
    </Container>
  );
};

export default TrialDetailExercisesTab;
