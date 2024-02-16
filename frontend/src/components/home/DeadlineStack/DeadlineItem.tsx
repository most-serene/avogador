import { isPractice, Trial } from "@trials/types.ts";
import { Course } from "@courses/types.ts";
import { useNavigate } from "react-router-dom";
import {
  Card,
  CardActionArea,
  CardContent,
  Typography,
  useTheme,
} from "@mui/material";
import { CSSProperties } from "react";
import { format, subHours } from "date-fns";

interface DeadlineItemProps {
  trial: Trial;
  course: Course | undefined;
}

const DeadlineItem = ({ trial, course }: DeadlineItemProps) => {
  const navigate = useNavigate();
  const theme = useTheme();

  const getStyle = (): CSSProperties => {
    if (isPractice(trial) && subHours(trial.deadline, 24) < new Date()) {
      return {
        border: 2,
        borderColor: theme.palette.warning.main,
        borderStyle: "solid",
      };
    }
    return {};
  };

  return (
    <Card
      raised
      style={getStyle()}
      onClick={() => {
        navigate(`/trials/${trial.id}`);
      }}
    >
      <CardActionArea>
        <CardContent>
          <Typography>
            {trial.name} -{" "}
            <b>
              {course?.name} {course?.year}
            </b>
          </Typography>
          <Typography variant={"body2"}>Language: {trial.language}</Typography>
          {isPractice(trial) && (
            <Typography>
              Deadline: {format(trial.deadline, "dd/MM/yyyy HH:mm")}
            </Typography>
          )}
        </CardContent>
      </CardActionArea>
    </Card>
  );
};

export default DeadlineItem;
