import {
  Card,
  CardActionArea,
  CardContent,
  CircularProgress,
  Collapse,
  Tooltip,
  Typography,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import {
  Cancel,
  CheckCircle,
  Error,
  ExpandLess,
  ExpandMore,
  OfflineBolt,
  WatchLater,
} from "@mui/icons-material";
import Box from "@mui/material/Box";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { enqueueSnackbar } from "notistack";
import {
  SubmissionStatus,
  SubmissionResult,
  SubmissionResultMap,
} from "@exercises/types.ts";

interface SubmissionResultsPopupProps {
  exerciseId?: string;
  submissionResult: [
    SubmissionResultMap,
    React.Dispatch<React.SetStateAction<SubmissionResultMap>>,
  ];
}

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
          <CircularProgress size="1rem" color="warning" />
        </Tooltip>
      );
  }
};

const SubmissionResultsPopup = ({
  exerciseId,
  submissionResult: [submissionRes],
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
  }, [exerciseId, getUserLastSubmissionFromExercise]);

  useEffect(() => {
    setVisible(true);
    const keys = Object.keys(submissionRes);
    if (keys.length === 0) return;

    setResults(submissionRes[keys[0]]);
  }, [submissionRes]);

  return (
    <>
      <Box
        style={{
          position: "absolute",
          bottom: -7,
          width: "100%",
        }}
      >
        <Box
          style={{ width: "100%", display: "flex", justifyContent: "center" }}
        >
          <Card
            sx={{
              width: "5rem",
              height: "2.5rem",
              borderRadius: "100px 100px 0 0",
            }}
          >
            <CardActionArea
              onClick={() => {
                setVisible(!visible);
              }}
              sx={{
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                height: "100%",
              }}
            >
              {visible ? <ExpandMore /> : <ExpandLess />}
            </CardActionArea>
          </Card>
        </Box>
        <Collapse in={visible} sx={{ width: "100%" }}>
          <Card
            sx={{
              borderBottomLeftRadius: 0,
              borderBottomRightRadius: 0,
            }}
          >
            <CardContent>
              <Typography variant="h5">Last Submission Results</Typography>
              <Box display="flex" flexWrap="wrap">
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
