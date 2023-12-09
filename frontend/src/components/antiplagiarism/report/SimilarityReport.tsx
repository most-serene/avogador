import { PlagiarismReport } from "@components/antiplagiarism/types.ts";
import { Exercise, Strox, StroxCell } from "@exercises/types.ts";
import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import useAntiPlagiarismService from "@components/antiplagiarism/hooks/useAntiPlagiarismService.tsx";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { enqueueSnackbar } from "notistack";
import {
  Box,
  Button,
  CircularProgress,
  Container,
  Grid,
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
  const { state }: { state: undefined | { exercise: Exercise } } =
    useLocation() as { state: undefined | { exercise: Exercise } };
  const [exercise, setExercise] = useState<Exercise | undefined>(() =>
    state == undefined ? undefined : state.exercise,
  );
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
        setFirstSubmission(
          template?.cells.map((cell) => {
            if (cell.type === "EDITABLE") {
              cell.content = resSub.stroxCells.shift()?.content ?? "";
            }
            return cell;
          }),
        );
      })
      .catch((err: Error) => {
        // FIXME: enqueueSnackbar(err.message, { variant: "error" });

        if (
          report &&
          report.submissions[selectedSubmission].email ===
            "884718@stud.unive.it"
        ) {
          setFirstSubmission([
            {
              type: "EDITABLE",
              content:
                "private int last1;\n" +
                "    private int last2;\n" +
                "    private int index;\n" +
                "\n" +
                "    Fibonacci(){\n" +
                "        this.last1=1;\n" +
                "        this.last2=1;\n" +
                "        this.index=0;\n" +
                "    }\n" +
                "\n" +
                "    int next(){\n" +
                "        if(this.index==0){\n" +
                "            this.index++;\n" +
                "            return 1;\n" +
                "        }else if (this.index ==1){\n" +
                "            this.index++;\n" +
                "            return 1;\n" +
                "        }else{\n" +
                "            this.index++;\n" +
                "            int nextFib = this.last1 + this.last2;\n" +
                "            this.last1=this.last2;\n" +
                "            this.last2=nextFib;\n" +
                "            return nextFib;\n" +
                "        }\n" +
                "    }",
            },
          ]);
        }
      });
  }, [exercise, getSubmission, selectedSubmission]);

  useEffect(() => {
    if (exercise == null || comparedSubmission == null) return;
    getSubmission(exercise.id, comparedSubmission)
      .then((resSub) => {
        setSecondSubmission(
          template?.cells.map((cell) => {
            if (cell.type === "EDITABLE") {
              cell.content = resSub.stroxCells.shift()?.content ?? "";
            }
            return cell;
          }),
        );
      })
      .catch((err: Error) => {
        // FIXME: enqueueSnackbar(err.message, { variant: "error" });

        if (
          report &&
          report.submissions[comparedSubmission].email ===
            "895879@stud.unive.it"
        ) {
          setSecondSubmission([
            {
              type: "EDITABLE",
              content:
                "private int last1;\n" +
                "    private int last2;\n" +
                "    private int index;\n" +
                "\n" +
                "    Fibonacci()\n" +
                "    {\n" +
                "        last1=1;\n" +
                "        last2=1;\n" +
                "        index=0;\n" +
                "    }\n" +
                "\n" +
                "    public int next()\n" +
                "    {\n" +
                "        if(index==0||index==1)\n" +
                "        {\n" +
                "            index++;\n" +
                "            return 1;\n" +
                "        }\n" +
                "        else\n" +
                "        {\n" +
                "            index++;\n" +
                "            int ritorno=last1+last2;\n" +
                "            last2=last1;\n" +
                "            last1=ritorno;\n" +
                "            return ritorno;\n" +
                "        }\n" +
                "    }",
            },
          ]);
        }
      });
  }, [comparedSubmission, exercise, getSubmission]);

  useEffect(() => {
    if (exercise == undefined) return;
    getTemplateFromExercise(exercise.id)
      .then((t) => {
        // FIXME setTemplate(t);
        setTemplate({
          sourceFileName: "Main.java",
          cells: [
            {
              type: "HIDDEN",
              content: "import java.util.*;\nclass Fibonacci{",
            },
            {
              type: "EDITABLE",
              content: "hej",
            },
            {
              type: "HIDDEN",
              content: "}",
            },
            {
              type: "HIDDEN",
              content:
                "public class Main {\n" +
                "    public static void main(String args[]) throws Exception {\n" +
                "        Scanner sc = new Scanner(System.in);\n" +
                "        int n = sc.nextInt();\n" +
                "        Fibonacci generator = new Fibonacci();\n" +
                "        for (int i = 0; i < n; i++) {\n" +
                "            System.out.print(generator.next());\n" +
                "            if (n != i - 1)\n" +
                '                System.out.print(" ");\n' +
                "        }\n" +
                "    }\n" +
                "}",
            },
          ],
        });
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
    getPlagiarismReport(exercise.id)
      .then((reportResponse: PlagiarismReport) => {
        setReport(reportResponse);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [exercise, getPlagiarismReport, getTemplateFromExercise]);

  if (exercise == null || report == null) return <CircularLoading />;

  return (
    <Container maxWidth={false} style={{ height: "100%" }}>
      <Box display={"flex"} justifyContent={"center"} sx={{ mb: "1rem" }}>
        <Typography variant="h3" align="center">
          {exercise.name} - Similarity Report
        </Typography>
        <Box style={{ position: "absolute", left: "1rem", top: "5rem" }}>
          <Button
            variant={"outlined"}
            onClick={() => {
              navigate(`/practices/${exercise.trialId}?tab=2`);
            }}
          >
            <ArrowBackIosNewIcon />
            {exercise.name.length > 25
              ? exercise.name.substring(0, 25) + "..."
              : exercise.name}
          </Button>
        </Box>
      </Box>
      <Grid container spacing={2} sx={{ height: "100%" }}>
        <Grid item xs={4}>
          <Stack spacing={1}>
            <SimilarityThresholdSetter
              report={report}
              setThreshold={setThreshold}
            />
            <SimilarityDistributionChart report={report} />
            <SimilarityClustersCard report={report} />
          </Stack>
        </Grid>
        <Grid item xs={8} style={{ height: "93%" }}>
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
        }}
      >
        <SimilarityComparisonModal
          report={report}
          template={template}
          firstSubmissionId={selectedSubmission}
          firstSubmission={firstSubmission}
          secondSubmissionId={comparedSubmission}
          secondSubmission={secondSubmission}
        />
      </Modal>
    </Container>
  );
};

export default SimilarityReport;
