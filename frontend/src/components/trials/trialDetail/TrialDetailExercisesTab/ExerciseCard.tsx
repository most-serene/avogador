import { Exercise } from "@exercises/types.ts";
import { useNavigate } from "react-router-dom";
import { Card, CardActionArea, CardContent, Typography } from "@mui/material";
import { Exam, isPractice, Practice } from "@trials/types.ts";
import ContextMenuWrapper from "@structure/ContextMenuWrapper/ContextMenuWrapper.tsx";
import Box from "@mui/material/Box";
import MenuItem from "@mui/material/MenuItem";
import { enqueueSnackbar } from "notistack";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";

interface ExerciseCardProps {
  exercise: Exercise;
  onChange: (e: Exercise) => void;
  trial: Practice | Exam;
}

const ExerciseCard = ({
  exercise,
  onChange: handleChange,
  trial,
}: ExerciseCardProps) => {
  const navigate = useNavigate();
  const { updateExercise } = useExerciseService();

  return (
    <ContextMenuWrapper
      menu={
        <Box>
          <MenuItem
            disabled={exercise.isVisible}
            onClick={() => {
              updateExercise({
                ...exercise,
                isVisible: true,
              })
                .then((updatedExercise) => {
                  handleChange(updatedExercise);
                })
                .catch((err: Error) => {
                  enqueueSnackbar(err.message, { variant: "error" });
                });
            }}
          >
            Set visible
          </MenuItem>
          <MenuItem
            disabled={!exercise.isVisible}
            onClick={() => {
              updateExercise({ ...exercise, isVisible: false })
                .then((updatedExercise) => {
                  handleChange(updatedExercise);
                })
                .catch((err: Error) => {
                  enqueueSnackbar(err.message, { variant: "error" });
                });
            }}
          >
            Set hidden
          </MenuItem>
          <MenuItem
            onClick={() => {
              navigate(`/exercises/${exercise.id}/edit`);
            }}
          >
            Edit
          </MenuItem>
        </Box>
      }
    >
      <Card>
        <CardActionArea
          onClick={() => {
            if (isPractice(trial)) {
              navigate(`/trials/${trial.id}/exercises/${exercise.id}`);
            }
          }}
        >
          <CardContent>
            <Typography
              display={"flex"}
              variant={"h5"}
              justifyContent={"center"}
            >
              {exercise.name}
            </Typography>
          </CardContent>
        </CardActionArea>
      </Card>
    </ContextMenuWrapper>
  );
};

export default ExerciseCard;
