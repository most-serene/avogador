import {
  Badge,
  Box,
  Card,
  CardActionArea,
  CardContent,
  FormControl,
  Grid,
  Grow,
  IconButton,
  InputLabel,
  Select,
  Stack,
  Typography,
  useTheme,
} from "@mui/material";
import { PlagiarismReport } from "@components/antiplagiarism/types.ts";
import React, { useState } from "react";
import MenuItem from "@mui/material/MenuItem";
import { ArrowDownward, ArrowUpward } from "@mui/icons-material";

type SortingStrategy = "Given Name" | "Family Name" | "Similarity" | "";

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

interface SubmissionsListProps {
  report: PlagiarismReport;
  threshold: number;
  selectedSubmissionState: [
    string | undefined,
    React.Dispatch<React.SetStateAction<string | undefined>>,
  ];
  sorting: SortingStrategy;
  order: 1 | -1;
}

interface ComparisonsListProps {
  report: PlagiarismReport;
  threshold: number;
  selectedSubmission: string;
  setComparedSubmission: React.Dispatch<
    React.SetStateAction<string | undefined>
  >;
  sorting: SortingStrategy;
  order: 1 | -1;
}

const sortingStrategy: SortingStrategy[] = [
  "Given Name",
  "Family Name",
  "Similarity",
];

const getGivenNameComparator =
  (report: PlagiarismReport, order: 1 | -1) => (a: string, b: string) =>
    report.submissions[a].givenName.localeCompare(
      report.submissions[b].givenName,
    ) * order;

const getFamilyNameComparator =
  (report: PlagiarismReport, order: 1 | -1) => (a: string, b: string) =>
    report.submissions[a].familyName.localeCompare(
      report.submissions[b].familyName,
    ) * order;

const getSimilarityComparator =
  (report: PlagiarismReport, selectedSubmission: string, order: 1 | -1) =>
  (a: string, b: string) =>
    (report.comparisons[selectedSubmission][b].similarity -
      report.comparisons[selectedSubmission][a].similarity) *
    order;

const getSimilarityNumberComparator =
  (report: PlagiarismReport, order: 1 | -1) => (a: string, b: string) =>
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    (Object.keys(report.comparisons[b] ?? {}).length -
      // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
      Object.keys(report.comparisons[a] ?? {}).length) *
    order;

const SubmissionsList = ({
  report,
  threshold,
  selectedSubmissionState: [selectedSubmission, setSelectedSubmission],
  sorting,
  order,
}: SubmissionsListProps) => {
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

  const getComparator = (): ((a: string, b: string) => number) => {
    switch (sorting) {
      case "Given Name":
        return getGivenNameComparator(report, order);
      case "Family Name":
        return getFamilyNameComparator(report, order);
      case "Similarity":
        return getSimilarityNumberComparator(report, order);
      default:
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        return (_a, _b) => 0;
    }
  };

  return (
    <Stack spacing={1}>
      {Object.entries(report.submissions)
        .sort(([a], [b]) => getComparator()(a, b))
        .map(([submissionId, user]) => (
          <Card
            key={submissionId}
            raised
            style={getSelectableCardStyle(submissionId)}
          >
            <CardActionArea
              onClick={() => {
                setSelectedSubmission(submissionId);
              }}
            >
              <CardContent
                sx={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <Box>
                  <Typography>{user.email}</Typography>
                  <Typography>
                    {user.givenName} {user.familyName}
                  </Typography>
                </Box>

                <Box marginRight={2}>
                  <Badge
                    color={
                      Object.values(
                        /* eslint-disable-next-line @typescript-eslint/no-unnecessary-condition */
                        report.comparisons[submissionId] ?? {},
                      ).some(
                        (comparison) =>
                          comparison.similarity * 100 >= threshold,
                      )
                        ? "warning"
                        : "secondary"
                    }
                    badgeContent={
                      // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
                      Object.keys(report.comparisons[submissionId] ?? {}).length
                    }
                  ></Badge>
                </Box>
              </CardContent>
            </CardActionArea>
          </Card>
        ))}
    </Stack>
  );
};

const ComparisonsList = ({
  report,
  threshold,
  selectedSubmission,
  setComparedSubmission,
  sorting,
  order = 1,
}: ComparisonsListProps) => {
  const theme = useTheme();

  const getComparator = (): ((a: string, b: string) => number) => {
    switch (sorting) {
      case "Given Name":
        return getGivenNameComparator(report, order);
      case "Family Name":
        return getFamilyNameComparator(report, order);
      case "Similarity":
        return getSimilarityComparator(report, selectedSubmission, order);
      default:
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        return (_a, _b) => 0;
    }
  };

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
          <Typography variant="h6">No matches found!</Typography>
        </Box>
      ) : (
        <Stack spacing={1}>
          {Object.entries(report.comparisons[selectedSubmission])
            .sort(([a], [b]) => getComparator()(a, b))
            .map(([sid, comparison]) => {
              return (
                <Card
                  key={sid}
                  style={getThresholdStyle(comparison.similarity)}
                >
                  <CardActionArea
                    onClick={() => {
                      setComparedSubmission(sid);
                    }}
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
                  </CardActionArea>
                </Card>
              );
            })}
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
  const [sorting, setSorting] = useState<SortingStrategy>("");
  const [order, setOrder] = useState<1 | -1>(1);

  return (
    <Card style={{ height: "100%" }}>
      <CardContent style={{ height: "100%" }}>
        <Grid container spacing={1} style={{ height: "100%" }}>
          <Grid item xs={3} style={{ display: "flex", alignItems: "center" }}>
            <FormControl variant="standard" fullWidth>
              <InputLabel id="sorting-label">Sort by</InputLabel>
              <Select
                labelId="sorting-label"
                label="Sort by"
                value={sorting}
                onChange={(event) => {
                  setSorting(event.target.value as SortingStrategy);
                }}
              >
                <MenuItem value={""}>
                  <i>None</i>
                </MenuItem>
                {sortingStrategy.map((strategy, i) => (
                  <MenuItem key={i} value={strategy}>
                    {strategy}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <Grow in={sorting !== ""}>
              <IconButton
                sx={{ mt: 1 }}
                size="small"
                color="secondary"
                onClick={() => {
                  setOrder((order) => (order === 1 ? -1 : 1));
                }}
              >
                {order === 1 ? <ArrowDownward /> : <ArrowUpward />}
              </IconButton>
            </Grow>
          </Grid>
          <Grid item xs={9}>
            {selectedSubmission != null && (
              <Box
                display="flex"
                justifyContent="center"
                alignItems="center"
                height="100%"
              >
                <Typography variant="h5" align="center">
                  {report.submissions[selectedSubmission].givenName}{" "}
                  {report.submissions[selectedSubmission].familyName} &apos;s
                  report
                </Typography>
              </Box>
            )}
          </Grid>
          <Grid
            item
            xs={3}
            sx={{ pb: 5, mt: 1 }}
            style={{ height: "100%", overflowY: "scroll" }}
            className="hidden-scrollbar"
          >
            <SubmissionsList
              report={report}
              threshold={threshold}
              selectedSubmissionState={[
                selectedSubmission,
                setSelectedSubmission,
              ]}
              sorting={sorting}
              order={order}
            />
          </Grid>
          <Grid
            item
            xs={9}
            sx={{ pb: 5, mt: 1 }}
            style={{ height: "100%", overflowY: "scroll" }}
            className="hidden-scrollbar"
          >
            <Card raised>
              <CardContent>
                {selectedSubmission == undefined ? (
                  <Box
                    display={"flex"}
                    justifyContent={"center"}
                    alignItems={"center"}
                    height={"10rem"}
                  >
                    <Typography variant="h6">Select a User</Typography>
                  </Box>
                ) : (
                  <ComparisonsList
                    report={report}
                    threshold={threshold}
                    selectedSubmission={selectedSubmission}
                    setComparedSubmission={setComparedSubmission}
                    sorting={sorting}
                    order={order}
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
