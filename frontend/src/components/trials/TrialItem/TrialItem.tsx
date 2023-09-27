import { Card, CardContent, Typography } from "@mui/material";
import { isExam, isPractice, Trial } from "@trials/types.ts";
import PracticeItem from "@trials/TrialItem/PracticeItem.tsx";
import ExamItem from "@trials/TrialItem/ExamItem.tsx";

interface TrialItemProps {
  trial: Trial;
}

const TrialItem = ({ trial }: TrialItemProps) => {
  if (isPractice(trial)) {
    return <PracticeItem practice={trial} />;
  }
  if (isExam(trial)) {
    return <ExamItem exam={trial} />;
  }

  return (
    <Card>
      <CardContent>
        <Typography variant="h4">Unknown trial type</Typography>
      </CardContent>
    </Card>
  );
};

export default TrialItem;
