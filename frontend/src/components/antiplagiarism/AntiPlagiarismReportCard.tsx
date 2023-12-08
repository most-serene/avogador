import { Trial } from "@trials/types.ts";
import { Button, Card, CardContent, Stack, Typography } from "@mui/material";
import { useCallback, useEffect, useState } from "react";
import useAntiPlagiarismService from "@components/antiplagiarism/hooks/useAntiPlagiarismService.tsx";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { enqueueSnackbar } from "notistack";
import { Exercise } from "@exercises/types.ts";
import Box from "@mui/material/Box";
import { useNavigate } from "react-router-dom";

const AntiPlagiarismReportCard = ({ trial }: { trial: Trial }) => {
  const [exercises, setExercises] = useState<Exercise[]>();
  const { getExercisesByTrial } = useExerciseService();
  const { runAntiPlagiarismTool } = useAntiPlagiarismService();
  const navigate = useNavigate();

  const runTool = useCallback(() => {
    runAntiPlagiarismTool(trial);
  }, [runAntiPlagiarismTool, trial]);

  const visualizeReport = useCallback(
    (e: Exercise) => {
      navigate(`/exercises/${e.id}/similarity-report`, {
        state: { exercise: e },
      });
    },
    [navigate],
  );

  useEffect(() => {
    getExercisesByTrial(trial)
      .then((e) => {
        setExercises(e);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [getExercisesByTrial, trial]);

  return (
    <Card raised>
      <CardContent>
        <Typography variant={"h6"} sx={{ mb: 1 }}>
          Code similarity report
        </Typography>
        <Box display={"flex"} justifyContent={"center"} marginBottom={2}>
          <Button variant={"outlined"} onClick={runTool}>
            Run tool
          </Button>
        </Box>
        <Stack spacing={2}>
          {exercises?.map((e) => (
            <Button
              key={e.id}
              variant={"outlined"}
              onClick={() => {
                visualizeReport(e);
              }}
            >
              Visualize {e.name} report
            </Button>
          ))}
        </Stack>
      </CardContent>
    </Card>
  );
};

export default AntiPlagiarismReportCard;
