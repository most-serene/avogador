import { Exercise } from "@exercises/types.ts";
import { useNavigate } from "react-router-dom";
import { Card, CardActionArea, CardContent, Typography } from "@mui/material";
import { Exam, isPractice, Practice } from "@trials/types.ts";

interface ExerciseCardProps {
  exercise: Exercise;
  trial: Practice | Exam;
}

const ExerciseCard = ({ exercise, trial }: ExerciseCardProps) => {
  const navigate = useNavigate();

  return (
    <Card>
      <CardActionArea
        onClick={() => {
          if (isPractice(trial)) {
            navigate(`/practices/${trial.id}/exercises/${exercise.id}`);
          }
        }}
      >
        <CardContent>
          <Typography display={"flex"} variant={"h5"} justifyContent={"center"}>
            {exercise.name}
          </Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};

export default ExerciseCard;
