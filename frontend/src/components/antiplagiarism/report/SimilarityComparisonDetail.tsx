import {
  Box,
  Card,
  CardContent,
  Grid,
  Stack,
  Typography,
  useTheme,
} from "@mui/material";
import { PlagiarismReport } from "@components/antiplagiarism/types.ts";
import React from "react";

interface SimilarityComparisonDetailProps {
  report: PlagiarismReport;
  selectedSubmissionState: [
    string | undefined,
    React.Dispatch<React.SetStateAction<string | undefined>>,
  ];
  comparedSubmissionState: [
    string | undefined,
    React.Dispatch<React.SetStateAction<string | undefined>>,
  ];
  threshold: number;
}

const SubmissionsList = ({
  report,
  selectedSubmissionState: [selectedSubmission, setSelectedSubmission],
}: {
  report: PlagiarismReport;
  selectedSubmissionState: [
    string | undefined,
    React.Dispatch<React.SetStateAction<string | undefined>>,
  ];
}) => {
  const theme = useTheme();
  const getSelectableCardStyle = (subId: string) => {
    return subId === selectedSubmission
      ? {
          border: 2,
          borderColor: theme.palette.primary.main,
          borderStyle: "solid",
        }
      : {};
  };

  return (
    <Stack
      spacing={1}
      style={{ height: "100%", overflow: "scroll" }}
      className={"hidden-scrollbar"}
    >
      {Object.entries(report.submissions).map(([submissionId, user]) => {
        return (
          <Card
            key={submissionId}
            raised
            onClick={() => {
              setSelectedSubmission(submissionId);
            }}
            style={getSelectableCardStyle(submissionId)}
          >
            <CardContent>
              <Typography>{user.email}</Typography>
              <Typography>
                {user.givenName} {user.familyName}
              </Typography>
            </CardContent>
          </Card>
        );
      })}
    </Stack>
  );
};

const ComparisonsList = ({
  report,
  threshold,
  selectedSubmission,
  setComparedSubmission,
}: {
  report: PlagiarismReport;
  threshold: number;
  selectedSubmission: string;
  setComparedSubmission: React.Dispatch<
    React.SetStateAction<string | undefined>
  >;
}) => {
  const theme = useTheme();

  const getThresholdStyle = (similarity: number) => {
    return similarity * 100 >= threshold
      ? {
          border: 2,
          borderColor: theme.palette.warning.main,
          borderStyle: "solid",
        }
      : {};
  };

  return (
    <>
      {!Object.keys(report.comparisons).includes(selectedSubmission) ? (
        <Box
          display={"flex"}
          justifyContent={"center"}
          alignItems={"center"}
          height={"10rem"}
        >
          <Typography>No matches!</Typography>
        </Box>
      ) : (
        <Stack spacing={1}>
          {Object.entries(report.comparisons[selectedSubmission]).map(
            ([sid, comparison]) => {
              return (
                <Card
                  key={sid}
                  onClick={() => {
                    setComparedSubmission(sid);
                  }}
                  style={getThresholdStyle(comparison.similarity)}
                >
                  <CardContent>
                    <Typography>
                      {report.submissions[sid].email} -{" "}
                      {report.submissions[sid].givenName}{" "}
                      {report.submissions[sid].familyName}
                    </Typography>
                    <Typography>
                      Similarity:{" "}
                      {Math.round(comparison.similarity * 10000) / 100}%
                    </Typography>
                  </CardContent>
                </Card>
              );
            },
          )}
        </Stack>
      )}
    </>
  );
};

const SimilarityComparisonDetail = ({
  report,
  selectedSubmissionState: [selectedSubmission, setSelectedSubmission],
  comparedSubmissionState: [, setComparedSubmission],
  threshold,
}: SimilarityComparisonDetailProps) => {
  return (
    <Card
      style={{ height: "100%", overflow: "scroll" }}
      className={"hidden-scrollbar"}
    >
      <CardContent>
        <Grid container spacing={1}>
          <Grid
            item
            xs={3}
            style={{ overflow: "scroll" }}
            className={"hidden-scrollbar"}
          >
            <Box
              width="100%"
              style={{ height: "100%", overflow: "scroll" }}
              className={"hidden-scrollbar"}
            >
              <SubmissionsList
                report={report}
                selectedSubmissionState={[
                  selectedSubmission,
                  setSelectedSubmission,
                ]}
              />
            </Box>
          </Grid>
          <Grid item xs={9}>
            <Card raised>
              <CardContent>
                {selectedSubmission == undefined ? (
                  <Box
                    display={"flex"}
                    justifyContent={"center"}
                    alignItems={"center"}
                    height={"10rem"}
                  >
                    <Typography>Select one</Typography>
                  </Box>
                ) : (
                  <ComparisonsList
                    report={report}
                    threshold={threshold}
                    selectedSubmission={selectedSubmission}
                    setComparedSubmission={setComparedSubmission}
                  />
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};
export default SimilarityComparisonDetail;
