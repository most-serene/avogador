import {
  DataGrid,
  // eslint-disable-next-line import/named
  GridColDef,
  // eslint-disable-next-line import/named
  GridFilterItem,
  // eslint-disable-next-line import/named
  GridFilterModel,
  // eslint-disable-next-line import/named
  GridPaginationModel,
  // eslint-disable-next-line import/named
  GridRowsProp,
  // eslint-disable-next-line import/named
  GridSortDirection,
  // eslint-disable-next-line import/named
  GridSortModel,
  GridToolbar,
  useGridApiRef,
} from "@mui/x-data-grid";
import { useEffect, useState } from "react";
import { Trial, UserExerciseSummary, UserTrialSummary } from "@trials/types.ts";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { enqueueSnackbar } from "notistack";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { User } from "@authentication/types.ts";
import { Exercise } from "@exercises/types.ts";
import { format } from "date-fns";
import { Card, Chip } from "@mui/material";
import ColorModeAtom from "@theme/colorModeAtom.ts";
import { useAtom } from "jotai";
import { Cancel, CheckCircle, Help } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";

interface TrialDetailUsersTabProps {
  trial: Trial;
}

interface DataGridSettings {
  trialId?: string;
  page?: number;
  order?: {
    field: string;
    sort: GridSortDirection;
  };
  quickFilterValues?: string[];
  filter?: GridFilterItem[];
}

const staticColumns: GridColDef<UserTrialSummary>[] = [
  {
    field: "enroll",
    headerName: "Enroll No.",
    valueGetter: (params) => params.row.user.email.split("@")[0],
    flex: 0.25,
    minWidth: 75,
  },
  {
    field: "fullName",
    headerName: "Name",
    valueGetter: (params) =>
      `${params.row.user.familyName} ${params.row.user.givenName}`,
    flex: 1,
    minWidth: 150,
  },
  {
    field: "startDate",
    headerName: "Started",
    valueGetter: (params) => format(params.row.startTime, "yyyy/MM/dd HH:mm"),
    flex: 1,
    minWidth: 125,
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

const getStatusIcon = (
  status: "CORRECT" | "WRONG" | "PENDING" | "MISSING" | undefined,
) => {
  switch (status) {
    case "CORRECT":
      return <Chip icon={<CheckCircle />} label="Correct" color="success" />;
    case "WRONG":
      return <Chip icon={<Cancel />} label="Wrong" color="error" />;
    case "PENDING":
      return <Chip icon={<Help />} label="Pending" color="warning" />;
    default:
      return <Chip icon={<Help />} label="Missing" color="secondary" />;
  }
};

const SESSION_STORAGE_SETTINGS_KEY = "trial-table-settings";

const TrialDetailUsersTab = ({ trial }: TrialDetailUsersTabProps) => {
  const [colorMode] = useAtom(ColorModeAtom);
  const navigate = useNavigate();
  const { getExercisesByTrial, getExerciseResultSummary } =
    useExerciseService();
  const { getUsersFromTrial } = useTrialService();
  const [rows, setRows] = useState<GridRowsProp<UserTrialSummary>>();
  const [columns, setColumns] = useState(staticColumns);
  const apiRef = useGridApiRef();

  const getDatagridSettings = () => {
    return JSON.parse(
      sessionStorage.getItem(SESSION_STORAGE_SETTINGS_KEY) ?? "{}",
    ) as DataGridSettings;
  };

  useEffect(() => {
    const tableSettings = getDatagridSettings();

    if (tableSettings.trialId == null || tableSettings.trialId != trial.id) {
      sessionStorage.setItem(
        SESSION_STORAGE_SETTINGS_KEY,
        JSON.stringify({
          trialId: trial.id,
        }),
      );
      return;
    }

    if (tableSettings.page != null) {
      apiRef.current.setPage(tableSettings.page);
    }
    if (tableSettings.order != null) {
      apiRef.current.setSortModel([tableSettings.order]);
    }
    if (tableSettings.filter != null) {
      apiRef.current.setFilterModel({ items: tableSettings.filter });
    }
    if (tableSettings.quickFilterValues != null) {
      apiRef.current.setQuickFilterValues(tableSettings.quickFilterValues);
    }
  }, [apiRef, trial]);

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

      if (Object.keys(summaries).length > 0) {
        exercises.forEach((exercise) => {
          setColumns((columns) => {
            if (columns.some((column) => column.field == exercise.id))
              return [...columns];
            return [
              ...columns,
              {
                field: exercise.id,
                align: "center",
                headerName: exercise.name,
                valueGetter: (params) =>
                  params.row.summary.filter(
                    (summary) => summary.exerciseId === exercise.id,
                  )[0].status,
                renderCell: (params) =>
                  getStatusIcon(
                    params.row.summary.filter(
                      (summary) => summary.exerciseId === exercise.id,
                    )[0].status,
                  ),
                flex: 1,
                minWidth: 150,
              },
            ];
          });
        });
      }
    };

    generateRows().catch((err: Error) => {
      enqueueSnackbar(err.message, { variant: "error" });
    });
  }, [trial, getExercisesByTrial, getExerciseResultSummary, getUsersFromTrial]);

  const handlePaginationModelChange = ({ page }: GridPaginationModel) => {
    const tableSettings = getDatagridSettings();
    tableSettings.page = page;

    sessionStorage.setItem(
      SESSION_STORAGE_SETTINGS_KEY,
      JSON.stringify(tableSettings),
    );
  };

  const handleSortModelChange = (model: GridSortModel) => {
    const tableSettings = getDatagridSettings();

    tableSettings.order = model.length === 0 ? undefined : model[0];

    sessionStorage.setItem(
      SESSION_STORAGE_SETTINGS_KEY,
      JSON.stringify(tableSettings),
    );
  };

  const handleFilterModelChange = (model: GridFilterModel) => {
    const tableSettings = getDatagridSettings();

    tableSettings.quickFilterValues = model.quickFilterValues;
    tableSettings.filter = model.items;

    sessionStorage.setItem(
      SESSION_STORAGE_SETTINGS_KEY,
      JSON.stringify(tableSettings),
    );
  };

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
      <DataGrid
        apiRef={apiRef}
        rows={rows ?? []}
        loading={rows == null}
        columns={[...columns]}
        slots={{ toolbar: GridToolbar }}
        slotProps={{
          toolbar: {
            showQuickFilter: true,
            csvOptions: { fileName: `${trial.name}-results` },
            printOptions: {
              fileName: `${trial.name}-results`,
              hideToolbar: true,
            },
          },
        }}
        getRowId={(row) => row.user.id}
        showColumnVerticalBorder
        showCellVerticalBorder
        checkboxSelection
        disableRowSelectionOnClick
        density="compact"
        autoPageSize
        onPaginationModelChange={handlePaginationModelChange}
        onSortModelChange={handleSortModelChange}
        onFilterModelChange={handleFilterModelChange}
        onCellClick={(cell) => {
          if (["startDate", "__check__"].includes(cell.field)) {
            return;
          } else if (["enroll", "fullName"].includes(cell.field)) {
            navigate(`/trials/${trial.id}/users/${cell.id}`);
          } else {
            navigate(
              `/trials/${trial.id}/users/${cell.id}?exerciseId=${cell.field}`,
            );
          }
        }}
      />
    </Card>
  );
};

export default TrialDetailUsersTab;
