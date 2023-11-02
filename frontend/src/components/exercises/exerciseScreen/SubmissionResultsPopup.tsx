import {
  Card,
  CardActionArea,
  CardContent,
  CircularProgress,
  Collapse,
  Tooltip,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
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
import {
  SubmissionStatus,
  SubmissionResult,
  SubmissionResultMap,
} from "@exercises/types.ts";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { useParams } from "react-router-dom";
import { enqueueSnackbar } from "notistack";
import CopiableCard from "@structure/CopiableCard/CopiableCard.tsx";

interface SubmissionResultsPopupProps {
  exerciseId?: string;
  submissionResult: SubmissionResultMap;
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
          <CircularProgress size="1.5rem" color="warning" />
        </Tooltip>
      );
  }
};

interface ResultButtonProps {
  status: SubmissionStatus;
  output: string | undefined;
  onClick: () => void;
  selected: boolean;
}

const ResultButton = ({
  status,
  output,
  selected,
  onClick: handleClick,
}: ResultButtonProps) => {
  if (output == null || status === "PENDING") {
    return <Box margin={0.5}>{getResultBadge(status)}</Box>;
  }
  return (
    <Box
      margin={0.5}
      sx={{
        width: "1.75rem",
        height: "1.75rem",
        border: 6,
        borderColor: selected ? "primary.main" : "secondary.dark",
        borderRadius: "100%",
      }}
    >
      <CardActionArea
        sx={{
          ml: "-6px",
          mt: "-6px",
          width: "1.75rem",
          height: "1.75rem",
          display: "flex",
          justifyContent: "center",
          position: "relative",
        }}
        onClick={handleClick}
      >
        {getResultBadge(status)}
      </CardActionArea>
    </Box>
  );
};

const SubmissionResultsPopup = ({
  submissionResult,
}: SubmissionResultsPopupProps) => {
  const { getSubmissionOutputs } = useExerciseService();
  const { exerciseId } = useParams();
  const [visible, setVisible] = useState(false);
  const [results, setResults] = useState<SubmissionResult[]>([]);
  const [outputs, setOutputs] = useState<Record<string, string>>({});
  const [selectedResult, setSelectedResult] = useState("");

  const handleSelectResult = (id: string) => {
    if (selectedResult === id) {
      setSelectedResult("");
    } else {
      setSelectedResult(id);
    }
  };

  useEffect(() => {
    const keys = Object.keys(submissionResult);
    if (keys.length === 0) {
      setVisible(false);
      return;
    }

    setVisible(true);
    setResults(submissionResult[keys[0]]);
    if (
      !submissionResult[keys[0]].some(
        (submissionResult) => submissionResult.status === "PENDING",
      ) &&
      exerciseId != null
    ) {
      getSubmissionOutputs(exerciseId, keys[0])
        .then((outputs) => {
          setOutputs(outputs);
          if (outputs.compile !== "") {
            setSelectedResult("compile");
          }
        })
        .catch((err: Error) => {
          enqueueSnackbar(err.message, { variant: "error" });
        });
    } else {
      setSelectedResult("");
      setOutputs({});
    }

    return () => {
      setResults([]);
    };
  }, [exerciseId, getSubmissionOutputs, submissionResult]);

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
                  <ResultButton
                    key={result.id}
                    status={result.status}
                    output={outputs[result.testcaseId]}
                    onClick={() => {
                      handleSelectResult(result.testcaseId);
                    }}
                    selected={result.testcaseId === selectedResult}
                  />
                ))}
              </Box>
              <Collapse in={selectedResult !== ""}>
                <Typography variant="body1" fontWeight="bold">
                  Your output
                </Typography>
                <CopiableCard fontFamily="monospace">
                  {outputs[selectedResult]}
                </CopiableCard>
              </Collapse>
              {results.length === 0 && (
                <Typography variant="body1">
                  Solve the problem and make your first submission!
                </Typography>
              )}
            </CardContent>
          </Card>
        </Collapse>
      </Box>
    </>
  );
};

export default SubmissionResultsPopup;
