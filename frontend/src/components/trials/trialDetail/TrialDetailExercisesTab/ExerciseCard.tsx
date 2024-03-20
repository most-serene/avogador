import { Exercise } from "@exercises/types.ts";
import { useNavigate } from "react-router-dom";
import {
  Card,
  CardActionArea,
  CardContent,
  Tooltip,
  Typography,
} from "@mui/material";
import { Exam, isPractice, Practice } from "@trials/types.ts";
import ContextMenuWrapper from "@structure/ContextMenuWrapper/ContextMenuWrapper.tsx";
import Box from "@mui/material/Box";
import MenuItem from "@mui/material/MenuItem";
import { enqueueSnackbar } from "notistack";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import DeleteIcon from "@mui/icons-material/Delete";
import ButtonWithConfirmation from "@structure/ButtonWithConfirmation/ButtonWithConfirmation.tsx";

interface ExerciseCardProps {
  exercise: Exercise;
  onChange: (e: Exercise | null) => void;
  trial: Practice | Exam;
}

const ExerciseCard = ({
  exercise,
  onChange: handleChange,
  trial,
}: ExerciseCardProps) => {
  const navigate = useNavigate();
  const { updateExercise, deleteExercise } = useExerciseService();

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
          component="span"
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
            <Box
              sx={{
                display: "flex",
                position: "absolute",
                justifyContent: "flex-end",
                alignItems: "center",
                top: "50%",
                right: 0,
                transform: "translate(-50%, -50%)",
              }}
            >
              <Box
                onClick={(event) => {
                  event.stopPropagation();
                }}
                onMouseDown={(event) => {
                  event.stopPropagation();
                }}
              >
                <Tooltip
                  title={exercise.isVisible ? "Hide to delete" : undefined}
                >
                  <span>
                    <ButtonWithConfirmation
                      as={"IconButton"}
                      disabled={exercise.isVisible}
                      color={"error"}
                      title={`You are deleting ${exercise.name}`}
                      confirmColor={"error"}
                      description={`All the submissions, results and testcases in it will be lost.`}
                      confirmText={"Delete"}
                      onConfirm={() => {
                        deleteExercise(exercise)
                          .then(() => {
                            enqueueSnackbar(
                              `${exercise.name} deleted successfully`,
                              {
                                variant: "success",
                              },
                            );
                          })
                          .catch((err: Error) => {
                            enqueueSnackbar(err.message, { variant: "error" });
                          });
                        handleChange(null);
                      }}
                    >
                      <DeleteIcon />
                    </ButtonWithConfirmation>
                  </span>
                </Tooltip>
              </Box>
            </Box>
          </CardContent>
        </CardActionArea>
      </Card>
    </ContextMenuWrapper>
  );
};

export default ExerciseCard;
