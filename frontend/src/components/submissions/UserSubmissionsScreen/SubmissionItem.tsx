import {
  Exercise,
  Submission,
  SubmissionResult,
  SubmissionStatus,
} from "@exercises/types.ts";
import {
  Card,
  CardActionArea,
  CardContent,
  CircularProgress,
  Tooltip,
  Typography,
} from "@mui/material";
import Box from "@mui/material/Box";
import { format } from "date-fns";
import {
  Cancel,
  CheckCircle,
  Error,
  OfflineBolt,
  WatchLater,
} from "@mui/icons-material";

const getResultBadge = (status: SubmissionStatus) => {
  switch (status) {
    case "COMPILE_ERROR":
      return (
        <Tooltip placement={"top"} title={"Compile error"}>
          <Error color="error" />
        </Tooltip>
      );
    case "CORRECT":
      return (
        <Tooltip placement={"top"} title={"Correct"}>
          <CheckCircle color="success" />
        </Tooltip>
      );
    case "RUNTIME_ERROR":
      return (
        <Tooltip placement={"top"} title={"Runtime error"}>
          <OfflineBolt color="error" />
        </Tooltip>
      );
    case "TIME_LIMIT_EXCEEDED":
      return (
        <Tooltip placement={"top"} title={"Time limit exceeded"}>
          <WatchLater color="error" />
        </Tooltip>
      );
    case "WRONG_ANSWER":
      return (
        <Tooltip placement={"top"} title={"Wrong answer"}>
          <Cancel color="error" />
        </Tooltip>
      );
    default:
      return (
        <Tooltip placement={"top"} title={"Pending"}>
          <CircularProgress size="1.5rem" color="warning" />
        </Tooltip>
      );
  }
};

interface SubmissionItemProps {
  submission: Submission;
  exercise: Exercise;
  results: SubmissionResult[];
  onSelect: () => void;
  selected: boolean;
}

const SubmissionItem = ({
  submission,
  exercise,
  results,
  onSelect: handleSelect,
  selected,
}: SubmissionItemProps) => {
  return (
    <Card
      key={submission.id}
      sx={selected ? { border: 3, borderColor: "primary.main" } : {}}
    >
      <CardActionArea onClick={handleSelect}>
        <CardContent>
          <Box
            display="flex"
            alignItems="center"
            justifyContent="space-between"
          >
            <Typography variant="h6">{exercise.name}</Typography>
            <Typography variant="body1">
              {format(submission.timestamp, "yyyy/MM/dd HH:mm:ss")}
            </Typography>
          </Box>
          <Box display="flex" flexWrap="wrap">
            {results.map((result) => (
              <Box key={result.id} margin={0.25}>
                {getResultBadge(result.status)}
              </Box>
            ))}
          </Box>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};

export default SubmissionItem;
