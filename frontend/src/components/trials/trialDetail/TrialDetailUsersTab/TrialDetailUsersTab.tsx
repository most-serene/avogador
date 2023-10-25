import {
  DataGrid,
  // eslint-disable-next-line import/named
  GridColDef,
  // eslint-disable-next-line import/named
  GridRowsProp,
  GridToolbar,
} from "@mui/x-data-grid";
import { useEffect, useState } from "react";
import { Trial, UserExerciseSummary, UserTrialSummary } from "@trials/types.ts";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { enqueueSnackbar } from "notistack";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { User } from "@authentication/types.ts";
import { Exercise } from "@exercises/types.ts";
import { format } from "date-fns";
import { Card, Chip, Skeleton } from "@mui/material";
import ColorModeAtom from "@theme/colorModeAtom.ts";
import { useAtom } from "jotai";
import { Cancel, CheckCircle, Help } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";

interface TrialDetailUsersTabProps {
  trial: Trial;
}

const columns: GridColDef<UserTrialSummary>[] = [
  {
    field: "enroll",
    headerName: "Enroll No.",
    valueGetter: (params) => params.row.user.email.split("@")[0],
    flex: 0.25,
    minWidth: 125,
  },
  {
    field: "fullName",
    headerName: "Name",
    valueGetter: (params) =>
      `${params.row.user.familyName} ${params.row.user.givenName}`,
    flex: 1,
  },
  {
    field: "startDate",
    headerName: "Started",
    valueGetter: (params) => format(params.row.startTime, "yyyy/MM/dd HH:mm"),
    flex: 1,
  },
];

const getMissingSubmission = (
  user: User,
  exercise: Exercise,
): UserExerciseSummary => {
  return {
    submissionId: undefined,
    userId: user.id,
    exerciseId: exercise.id,
    status: "MISSING",
  };
};

const getStatusIcon = (status: "CORRECT" | "WRONG" | "PENDING" | "MISSING") => {
  switch (status) {
    case "CORRECT":
      return <Chip icon={<CheckCircle />} label="Correct" color="success" />;
    case "WRONG":
      return <Chip icon={<Cancel />} label="Wrong" color="error" />;
    case "PENDING":
      return <Chip icon={<Help />} label="Pending" color="warning" />;
    case "MISSING":
      return <Chip icon={<Help />} label="Missing" color="secondary" />;
  }
};

const TrialDetailUsersTab = ({ trial }: TrialDetailUsersTabProps) => {
  const [colorMode] = useAtom(ColorModeAtom);
  const navigate = useNavigate();
  const { getExercisesByTrial, getExerciseResultSummary } =
    useExerciseService();
  const { getUsersFromTrial } = useTrialService();
  const [rows, setRows] = useState<GridRowsProp<UserTrialSummary>>();

  useEffect(() => {
    const generateRows = async () => {
      const users = await getUsersFromTrial(trial);
      const exercises = await getExercisesByTrial(trial);
      const summaries: Record<string, UserExerciseSummary[]> = {};

      for (const exercise of exercises) {
        summaries[exercise.id] = await getExerciseResultSummary(exercise.id);
      }

      setRows(
        users.map((userTrial) => {
          const userSummaries = exercises
            .map(
              (exercise) =>
                summaries[exercise.id].filter(
                  (ue) => ue.userId === userTrial.user.id,
                )[0] ?? getMissingSubmission(userTrial.user, exercise),
            )
            .map(({ exerciseId, status }) => {
              return { exerciseId, status };
            });
          return {
            startTime: userTrial.startTime,
            user: userTrial.user,
            summary: userSummaries,
          } as UserTrialSummary;
        }),
      );

      exercises.forEach((exercise) => {
        if (!columns.some((column) => column.field == exercise.id)) {
          columns.push({
            field: exercise.id,
            headerName: exercise.name,
            renderCell: (params) =>
              getStatusIcon(
                params.row.summary.filter(
                  (summary) => summary.exerciseId === exercise.id,
                )[0].status,
              ),
            flex: 1,
          });
        }
      });
    };

    generateRows().catch((err: Error) => {
      enqueueSnackbar(err.message, { variant: "error" });
    });
  }, [trial, getExercisesByTrial, getExerciseResultSummary, getUsersFromTrial]);

  return (
    <Card
      sx={{
        width: "100%",
        height: "100%",
        "& .MuiDataGrid-columnHeader": {
          backgroundColor: `primary.${colorMode}`,
          fontSize: "1.1rem",
        },
      }}
    >
      {rows != null ? (
        <DataGrid
          rows={rows}
          columns={[...columns]}
          slots={{ toolbar: GridToolbar }}
          getRowId={(row) => row.user.id}
          showColumnVerticalBorder
          showCellVerticalBorder
          checkboxSelection
          disableRowSelectionOnClick
          density="compact"
          autoPageSize
          onCellClick={(cell) => {
            if (!["enroll", "fullName", "startDate"].includes(cell.field)) {
              navigate(
                `/${trial.trialType === "PRACTICE" ? "practice" : "exam"}/${
                  trial.id
                }/exercises/${cell.field}/users/${cell.id}`,
              );
            }
          }}
        />
      ) : (
        <Skeleton
          variant="rectangular"
          width="100%"
          height="100%"
          animation="wave"
        />
      )}
    </Card>
  );
};

export default TrialDetailUsersTab;
