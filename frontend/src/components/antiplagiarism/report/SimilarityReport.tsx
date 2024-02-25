import { Cluster, PlagiarismReport } from "@components/antiplagiarism/types.ts";
import { Exercise, Strox, StroxCell } from "@exercises/types.ts";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import useAntiPlagiarismService from "@components/antiplagiarism/hooks/useAntiPlagiarismService.tsx";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { enqueueSnackbar } from "notistack";
import {
  Box,
  Button,
  CircularProgress,
  Container,
  Divider,
  Drawer,
  Grid,
  List,
  ListItemButton,
  ListItemText,
  Modal,
  Stack,
  Typography,
} from "@mui/material";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
import SimilarityDistributionChart from "@components/antiplagiarism/report/SimilarityDistributionChart.tsx";
import SimilarityClustersCard from "@components/antiplagiarism/report/SimilarityClustersCard.tsx";
import SimilarityComparisonModal from "@components/antiplagiarism/report/SimilarityComparisonModal.tsx";
import SimilarityComparisonDetail from "@components/antiplagiarism/report/SimilarityComparisonDetail.tsx";
import SimilarityThresholdSetter from "@components/antiplagiarism/report/SimilarityThresholdSetter.tsx";

const CircularLoading = () => {
  return (
    <Box
      style={{
        display: "flex",
        height: "100%",
      }}
      justifyContent={"center"}
      alignItems={"center"}
    >
      <CircularProgress size={80} />
    </Box>
  );
};

const SimilarityReport = () => {
  const [exercise, setExercise] = useState<Exercise>();
  const { exerciseId } = useParams();
  const { getPlagiarismReport } = useAntiPlagiarismService();
  const { getExerciseById, getTemplateFromExercise, getSubmission } =
    useExerciseService();
  const navigate = useNavigate();
  const [report, setReport] = useState<PlagiarismReport>();
  const [selectedSubmission, setSelectedSubmission] = useState<string>();
  const [template, setTemplate] = useState<Strox>();
  const [comparedSubmission, setComparedSubmission] = useState<string>();
  const [firstSubmission, setFirstSubmission] = useState<StroxCell[]>();
  const [secondSubmission, setSecondSubmission] = useState<StroxCell[]>();
  const [threshold, setThreshold] = useState<number>(80);
  const [selectedCluster, setSelectedCluster] = useState<Cluster | null>(null);

  useEffect(() => {
    if (exercise != undefined || exerciseId == undefined) return;
    getExerciseById(exerciseId)
      .then((e) => {
        setExercise(e);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [exercise, exerciseId, getExerciseById]);

  useEffect(() => {
    if (exercise == null || selectedSubmission == null) return;
    getSubmission(exercise.id, selectedSubmission)
      .then((resSub) => {
        setFirstSubmission(resSub.stroxCells);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [exercise, getSubmission, selectedSubmission, template?.cells]);

  useEffect(() => {
    if (exercise == null || comparedSubmission == null) return;
    getSubmission(exercise.id, comparedSubmission)
      .then((resSub) => {
        setSecondSubmission(resSub.stroxCells);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [comparedSubmission, exercise, getSubmission, template?.cells]);

  useEffect(() => {
    if (exercise == undefined) return;
    getTemplateFromExercise(exercise.id)
      .then((t) => {
        setTemplate(t);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
    getPlagiarismReport(exercise.id)
      .then((reportResponse: PlagiarismReport) => {
        Object.entries(reportResponse.comparisons).forEach(
          ([firstSubmission, comparisons]) => {
            Object.entries(comparisons).forEach(
              ([secondSubmission, comparison]) => {
                // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
                if (reportResponse.comparisons[secondSubmission] == null) {
                  reportResponse.comparisons[secondSubmission] = {};
                }
                reportResponse.comparisons[secondSubmission][firstSubmission] =
                  {
                    similarity: comparison.similarity,
                    matches: comparison.matches.map((match) => {
                      return {
                        firstFile: match.secondFile,
                        secondFile: match.firstFile,
                        firstStart: match.secondStart,
                        firstEnd: match.secondEnd,
                        secondStart: match.firstStart,
                        secondEnd: match.firstEnd,
                        tokens: match.tokens,
                      };
                    }),
                  };
              },
            );
          },
        );

        setReport(reportResponse);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [exercise, getPlagiarismReport, getTemplateFromExercise]);

  if (exercise == null || report == null) return <CircularLoading />;

  return (
    <Container maxWidth={false} style={{ height: "100%" }}>
      <Box
        display="flex"
        position="relative"
        justifyContent={"center"}
        alignItems="center"
        sx={{ mb: 1 }}
      >
        <Typography variant="h3" align="center">
          {exercise.name} - Similarity Report
        </Typography>
        <Box style={{ position: "absolute", left: 0 }}>
          <Button
            variant={"outlined"}
            onClick={() => {
              navigate(`/trials/${exercise.trial.id}?tab=2`);
            }}
          >
            <ArrowBackIosNewIcon />
            {exercise.trial.name.length > 25
              ? exercise.trial.name.substring(0, 25) + "..."
              : exercise.trial.name}
          </Button>
        </Box>
      </Box>
      <Grid container spacing={2} style={{ height: "calc(100% - 57px)" }}>
        <Grid
          item
          xs={4}
          sx={{ mt: 2, pb: 2 }}
          style={{ paddingTop: "0px", height: "100%", overflowY: "scroll" }}
          className="hidden-scrollbar"
        >
          <Stack spacing={1}>
            <SimilarityThresholdSetter
              report={report}
              setThreshold={setThreshold}
            />
            <SimilarityDistributionChart report={report} />
            <SimilarityClustersCard
              report={report}
              onClick={(cluster) => {
                setSelectedCluster(cluster);
              }}
            />
          </Stack>
        </Grid>
        <Grid item xs={8} style={{ height: "100%" }}>
          <SimilarityComparisonDetail
            report={report}
            selectedSubmissionState={[
              selectedSubmission,
              setSelectedSubmission,
            ]}
            comparedSubmissionState={[
              comparedSubmission,
              setComparedSubmission,
            ]}
            threshold={threshold}
          />
        </Grid>
      </Grid>
      <Modal
        open={comparedSubmission != null}
        onClose={() => {
          setComparedSubmission(undefined);
          setSecondSubmission(undefined);
        }}
        keepMounted
      >
        <SimilarityComparisonModal
          exercise={exercise}
          report={report}
          template={template}
          firstSubmissionId={selectedSubmission}
          firstSubmission={firstSubmission}
          secondSubmissionId={comparedSubmission}
          secondSubmission={secondSubmission}
        />
      </Modal>
      <Drawer
        anchor="left"
        open={selectedCluster != null}
        onClose={() => {
          setSelectedCluster(null);
        }}
      >
        {selectedCluster && (
          <Box padding={2} sx={{ width: 300 }}>
            <Typography variant="h5">Cluster Data</Typography>
            <Divider />
            <Typography variant="h6">
              Average Similarity:{" "}
              {Math.round(selectedCluster.averageSimilarity * 10000) / 100}%
            </Typography>
            <Typography variant="h6">
              Strength: {Math.round(selectedCluster.strength * 10000) / 100}%
            </Typography>
            <Typography variant="h6">Members:</Typography>
            <List>
              {Array.from(selectedCluster.members).map((submissionId) => (
                <ListItemButton
                  key={submissionId}
                  onClick={() => {
                    setSelectedCluster(null);
                    setSelectedSubmission(submissionId);
                  }}
                >
                  <ListItemText>
                    {report.submissions[submissionId].givenName}{" "}
                    {report.submissions[submissionId].familyName}
                  </ListItemText>
                </ListItemButton>
              ))}
            </List>
          </Box>
        )}
      </Drawer>
    </Container>
  );
};

export default SimilarityReport;
