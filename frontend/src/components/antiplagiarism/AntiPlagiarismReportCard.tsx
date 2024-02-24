import { Trial } from "@trials/types.ts";
import {
  Button,
  Card,
  CardContent,
  CircularProgress,
  Divider,
  Stack,
  Typography,
} from "@mui/material";
import { useCallback, useEffect, useState } from "react";
import useAntiPlagiarismService from "@components/antiplagiarism/hooks/useAntiPlagiarismService.tsx";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { enqueueSnackbar } from "notistack";
import { Exercise } from "@exercises/types.ts";
import Box from "@mui/material/Box";
import { useNavigate } from "react-router-dom";
import useWebSocket from "@hooks/useWebSocket.tsx";
import userAtom from "@authentication/userAtom.ts";
import { useAtom } from "jotai";
import { Course } from "@courses/types.ts";

interface AntiPlagiarismReportCardProps {
  trial: Trial;
  course: Course;
}

const AntiPlagiarismReportCard = ({
  trial,
  course,
}: AntiPlagiarismReportCardProps) => {
  const [exercisesReports, setExercisesReports] =
    useState<Record<string, { exercise: Exercise; isPresent: boolean }>>();
  const { getExercisesByTrial } = useExerciseService();
  const { runAntiPlagiarismTool, checkReportPresence } =
    useAntiPlagiarismService();
  const navigate = useNavigate();
  const { subscribe } = useWebSocket();
  const [user] = useAtom(userAtom);

  const fetchReports = useCallback(() => {
    getExercisesByTrial(trial)
      .then((exercisesResponse) => {
        exercisesResponse.forEach((e) => {
          checkReportPresence(e.id)
            .then((presence) => {
              setExercisesReports((reports) => {
                return {
                  ...reports,
                  [e.id]: {
                    exercise: e,
                    isPresent: presence,
                  },
                };
              });
            })
            .catch((err: Error) => {
              enqueueSnackbar(err.message, { variant: "error" });
            });
        });
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [checkReportPresence, getExercisesByTrial, trial]);

  const runTool = useCallback(() => {
    runAntiPlagiarismTool(trial);
    subscribe(
      `/users/${user?.id}/trials/${trial.id}/similarity-report`,
      (message) => {
        enqueueSnackbar(message.body, { variant: "info" });
        fetchReports();
      },
    )
      .then(() => {
        // empty-function
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [fetchReports, runAntiPlagiarismTool, subscribe, trial, user?.id]);

  const visualizeReport = useCallback(
    (exerciseId: string) => {
      navigate(`/exercises/${exerciseId}/similarity-report`);
    },
    [navigate],
  );

  useEffect(() => {
    fetchReports();
  }, [fetchReports]);

  return (
    <Card raised>
      <CardContent>
        <Typography variant={"h6"} sx={{ mb: 1 }}>
          Code similarity report
        </Typography>
        <Box display={"flex"} justifyContent={"center"} marginBottom={2}>
          <Button
            variant={"outlined"}
            onClick={runTool}
            disabled={course.isArchived}
          >
            Run tool
          </Button>
        </Box>
        <Divider sx={{ my: 1 }} />
        {exercisesReports == null ? (
          <Box display="flex" justifyContent="center">
            <CircularProgress />
          </Box>
        ) : (
          <Stack spacing={2}>
            {Object.entries(exercisesReports).map(
              ([id, { exercise, isPresent }]) => (
                <Button
                  key={id}
                  variant={"outlined"}
                  onClick={() => {
                    visualizeReport(id);
                  }}
                  disabled={!isPresent}
                >
                  <Typography sx={{ fontStyle: "italic" }}>
                    {exercise.name}
                  </Typography>
                  <Typography sx={{ mx: 1 }}>Report</Typography>
                </Button>
              ),
            )}
          </Stack>
        )}
      </CardContent>
    </Card>
  );
};

export default AntiPlagiarismReportCard;
