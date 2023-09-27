import { Exam } from "@trials/types.ts";
import { Card, CardActionArea, CardContent, Typography } from "@mui/material";
import { format, parseJSON } from "date-fns";

interface ExamItemProps {
  exam: Exam;
}

const ExamItem = ({ exam }: ExamItemProps) => {
  return (
    <Card elevation={2} sx={{ mb: 2 }}>
      <CardActionArea>
        <CardContent>
          <Typography variant="h5" marginBottom={1}>
            {exam.name}
          </Typography>
          <Typography variant="body1">
            Starts on:{" "}
            {format(parseJSON(exam.startTimestamp), "yyyy/MM/dd HH:mm")}
          </Typography>
          <Typography variant="body1">
            Duration: {exam.duration} mins
          </Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};

export default ExamItem;
