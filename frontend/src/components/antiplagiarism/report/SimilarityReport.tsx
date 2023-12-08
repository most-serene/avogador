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
  Card,
  CardContent,
  Container,
  Divider,
  Grid,
  Modal,
  Slider,
  Stack,
  Typography,
  useTheme,
} from "@mui/material";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
import { format } from "date-fns";
import { BarChart } from "@mui/x-charts";
import SubmissionViewer from "@components/submissions/UserSubmissionsScreen/SubmissionViewer.tsx";

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
  const theme = useTheme();
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
        // enqueueSnackbar(err.message, { variant: "error" });

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
        // enqueueSnackbar(err.message, { variant: "error" });

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
        // setTemplate(t);
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

  const getStyle = (subId: string) => {
    if (subId === selectedSubmission) {
      return {
        border: 2,
        borderColor: theme.palette.primary.main,
        borderStyle: "solid",
      };
    }
    return {};
  };

  if (exercise == null || report == null) return;

  function activateThreshold(similarity: number) {
    if (similarity * 100 >= threshold) {
      return {
        border: 2,
        borderColor: theme.palette.warning.main,
        borderStyle: "solid",
      };
    }
    return {};
  }

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
            <Card>
              <CardContent>
                <Typography>
                  Execution date:{" "}
                  {format(report.executionDate, "dd/MM/yyyy HH:mm:ss")}
                </Typography>
                <Divider />
                <Typography>Alert threshold:</Typography>
                <Box display={"flex"} justifyContent={"center"}>
                  <Slider
                    onChange={(event, newVal) => {
                      setThreshold(newVal as number);
                    }}
                    defaultValue={80}
                    valueLabelDisplay="auto"
                    min={0}
                    style={{
                      width: "70%",
                    }}
                    max={100}
                    marks={[
                      {
                        value: 0,
                        label: "0%",
                      },
                      {
                        value: 100,
                        label: "100%",
                      },
                    ]}
                  />
                </Box>
              </CardContent>
            </Card>
            <Card>
              <CardContent>
                <BarChart
                  height={250}
                  series={[
                    {
                      label: "average similarity distribution",
                      data: report.averageMetrics.distribution,
                    },
                    {
                      label: "max similarity distribution",
                      data: report.maxMetrics.distribution,
                    },
                  ]}
                  xAxis={[
                    {
                      scaleType: "band",
                      data: [
                        "0-10",
                        "11-20",
                        "21-30",
                        "31-40",
                        "41-50",
                        "51-60",
                        "61-70",
                        "71-80",
                        "81-90",
                        "91-100",
                      ],
                    },
                  ]}
                />
              </CardContent>
            </Card>
            <Card>
              <CardContent>
                <Typography>Clusters:</Typography>
                <Stack spacing={1}>
                  {report.clusters.map((cluster, i) => {
                    return (
                      <Card key={i} raised>
                        <CardContent>
                          <Typography>
                            Similarity:{" "}
                            {Math.round(cluster.averageSimilarity * 10000) /
                              100}
                            %
                          </Typography>
                          <Typography>
                            Strength:{" "}
                            {Math.round(cluster.strength * 10000) / 100}%
                          </Typography>
                        </CardContent>
                      </Card>
                    );
                  })}
                </Stack>
              </CardContent>
            </Card>
          </Stack>
        </Grid>
        <Grid item xs={8} style={{ height: "93%" }}>
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
                    <Stack
                      spacing={1}
                      style={{ height: "100%", overflow: "scroll" }}
                      className={"hidden-scrollbar"}
                    >
                      {Object.entries(report.submissions).map(
                        ([submissionId, user]) => {
                          return (
                            <Card
                              key={submissionId}
                              raised
                              onClick={() => {
                                setSelectedSubmission(submissionId);
                              }}
                              style={getStyle(submissionId)}
                            >
                              <CardContent>
                                <Typography>{user.email}</Typography>
                                <Typography>
                                  {user.givenName} {user.familyName}
                                </Typography>
                              </CardContent>
                            </Card>
                          );
                        },
                      )}
                    </Stack>
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
                        <>
                          {!Object.keys(report.comparisons).includes(
                            selectedSubmission,
                          ) ? (
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
                              {Object.entries(
                                report.comparisons[selectedSubmission],
                              ).map(([sid, comparison]) => {
                                return (
                                  <Card
                                    key={sid}
                                    onClick={() => {
                                      setComparedSubmission(sid);
                                    }}
                                    style={activateThreshold(
                                      comparison.similarity,
                                    )}
                                  >
                                    <CardContent>
                                      <Typography>
                                        {report.submissions[sid].email} -{" "}
                                        {report.submissions[sid].givenName}{" "}
                                        {report.submissions[sid].familyName}
                                      </Typography>
                                      <Typography>
                                        Similarity:{" "}
                                        {Math.round(
                                          comparison.similarity * 10000,
                                        ) / 100}
                                        %
                                      </Typography>
                                    </CardContent>
                                  </Card>
                                );
                              })}
                            </Stack>
                          )}
                        </>
                      )}
                    </CardContent>
                  </Card>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
      <Modal
        open={comparedSubmission != null}
        onClose={() => {
          setComparedSubmission(undefined);
        }}
      >
        <Box
          sx={{
            position: "absolute" as const,
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: "80%",
            bgcolor: "background.paper",
            height: "80%",
            overflow: "scroll",
            border: "2px solid " + theme.palette.primary.main,
            boxShadow: 24,
            p: 4,
          }}
          className={"hidden-scrollbar"}
        >
          <Grid container spacing={1}>
            <Grid item xs={6}>
              <Card>
                <CardContent>
                  {selectedSubmission != null && (
                    <Typography>
                      {report.submissions[selectedSubmission].email} -{" "}
                      {report.submissions[selectedSubmission].givenName}{" "}
                      {report.submissions[selectedSubmission].familyName}
                    </Typography>
                  )}

                  <Divider />

                  {template && firstSubmission && (
                    <SubmissionViewer
                      template={template}
                      submissionCode={firstSubmission}
                      language={"JAVA"}
                    />
                  )}
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={6}>
              <Card>
                <CardContent>
                  {comparedSubmission != null && (
                    <Typography>
                      {report.submissions[comparedSubmission].email} -{" "}
                      {report.submissions[comparedSubmission].givenName}{" "}
                      {report.submissions[comparedSubmission].familyName}
                    </Typography>
                  )}
                  <Divider />
                  {template && secondSubmission && (
                    <SubmissionViewer
                      template={template}
                      submissionCode={secondSubmission}
                      language={"JAVA"}
                    />
                  )}
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </Box>
      </Modal>
    </Container>
  );
};

export default SimilarityReport;
