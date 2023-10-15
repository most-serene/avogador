import {
  Card,
  CardActionArea,
  CardContent,
  CircularProgress,
  Collapse,
  Fade,
  IconButton,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
import {
  Cancel,
  CheckCircle,
  Close,
  Error,
  ExpandLess,
  OfflineBolt,
  WatchLater,
} from "@mui/icons-material";
import Box from "@mui/material/Box";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { enqueueSnackbar } from "notistack";
import { ResultStatus, SubmissionResult } from "@exercises/types.ts";

interface SubmissionResultsPopupProps {
  exerciseId?: string;
}

const getResultBadge = (status: ResultStatus) => {
  switch (status) {
    case "COMPILE_ERROR":
      return <Error color="error" />;
    case "CORRECT":
      return <CheckCircle color="success" />;
    case "RUNTIME_ERROR":
      return <OfflineBolt color="error" />;
    case "TIME_LIMIT_EXCEEDED":
      return <WatchLater color="error" />;
    case "WRONG_ANSWER":
      return <Cancel color="error" />;
    default:
      return <CircularProgress size="1rem" color="secondary" />;
  }
};

const SubmissionResultsPopup = ({
  exerciseId,
}: SubmissionResultsPopupProps) => {
  const { getUserLastSubmissionFromExercise } = useExerciseService();
  const [visible, setVisible] = useState(false);
  const [results, setResults] = useState<SubmissionResult[]>([]);

  useEffect(() => {
    if (exerciseId == null) {
      return;
    }
    getUserLastSubmissionFromExercise(exerciseId)
      .then((results) => {
        const keys = Object.keys(results);
        if (keys.length === 0) return;

        setResults(results[keys[0]]);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [exerciseId, visible, getUserLastSubmissionFromExercise]);

  return (
    <>
      <Box sx={{ position: "absolute", bottom: 0, width: "100%" }}>
        <Fade
          in={!visible}
          style={{ width: "100%", display: "flex", justifyContent: "center" }}
        >
          <Box>
            <Card
              sx={{
                width: "5rem",
                height: "2.5rem",
                borderRadius: "100px 100px 0 0",
              }}
            >
              <CardActionArea
                onClick={() => {
                  setVisible(true);
                }}
                sx={{
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
                  height: "100%",
                }}
              >
                <ExpandLess />
              </CardActionArea>
            </Card>
          </Box>
        </Fade>
        <Collapse in={visible} sx={{ width: "100%" }}>
          <Card sx={{ position: "relative" }}>
            <IconButton
              onClick={() => {
                setVisible(false);
              }}
              sx={{ position: "absolute", top: 1, right: 1 }}
            >
              <Close />
            </IconButton>
            <CardContent>
              <Typography variant="h5">Last Submission Results</Typography>
              <Box display="flex" flexWrap="wrap" justifyContent="center">
                {results.map((result) => (
                  <Box key={result.id} margin={0.5}>
                    {getResultBadge(result.status)}
                  </Box>
                ))}
              </Box>
            </CardContent>
          </Card>
        </Collapse>
      </Box>
    </>
  );
};

export default SubmissionResultsPopup;
