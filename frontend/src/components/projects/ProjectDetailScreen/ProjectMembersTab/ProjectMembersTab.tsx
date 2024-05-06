import {
  Project,
  ProjectSubmission,
  ProjectSubmissionDetail,
} from "@components/projects/types.ts";
import {
  DataGrid,
  GridActionsCellItem,
  // eslint-disable-next-line import/named
  GridColDef,
  // eslint-disable-next-line import/named
  GridFilterItem,
  // eslint-disable-next-line import/named
  GridFilterModel,
  // eslint-disable-next-line import/named
  GridPaginationModel,
  // eslint-disable-next-line import/named
  GridRowParams,
  // eslint-disable-next-line import/named
  GridRowsProp,
  // eslint-disable-next-line import/named
  GridSortDirection,
  // eslint-disable-next-line import/named
  GridSortModel,
  GridToolbar,
  useGridApiRef,
} from "@mui/x-data-grid";
import { format } from "date-fns";
import { Card, Chip } from "@mui/material";
import {
  Cancel,
  CheckCircle,
  Help,
  SettingsBackupRestore,
} from "@mui/icons-material";
import ColorModeAtom from "@theme/colorModeAtom.ts";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { useAtom } from "jotai";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { enqueueSnackbar } from "notistack";

interface ProjectMembersTabProps {
  project: Project;
}

interface DataGridSettings {
  projectId?: string;
  page?: number;
  order?: {
    field: string;
    sort: GridSortDirection;
  };
  quickFilterValues?: string[];
  filter?: GridFilterItem[];
}

const columns: GridColDef<ProjectSubmissionDetail>[] = [
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
    field: "timestamp",
    headerName: "Submitted at",
    valueGetter: (params) =>
      params.row.timestamp == null
        ? ""
        : format(params.row.timestamp, "yyyy/MM/dd HH:mm"),
    flex: 1,
    minWidth: 125,
  },
  {
    field: "submissionStatus",
    headerName: "Status",
    align: "center",
    valueGetter: (params) => params.row.status,
    renderCell: (params) => getStatusIcon(params.row.status),
    flex: 1,
    minWidth: 125,
  },
];

const getStatusIcon = (
  status: "SUCCESS" | "ERROR" | "PENDING" | "CONFIRMED" | undefined,
) => {
  switch (status) {
    case "SUCCESS":
      return <Chip icon={<CheckCircle />} label="Success" color="success" />;
    case "ERROR":
      return <Chip icon={<Cancel />} label="Error" color="error" />;
    case "CONFIRMED":
      return <Chip icon={<CheckCircle />} label="Confirmed" color="info" />;
    case "PENDING":
      return <Chip icon={<Help />} label="Pending" color="warning" />;
    default:
      return <Chip icon={<Help />} label="Missing" color="secondary" />;
  }
};

const SESSION_STORAGE_SETTINGS_KEY = "project-table-settings";

const ProjectMembersTab = ({ project }: ProjectMembersTabProps) => {
  const [colorMode] = useAtom(ColorModeAtom);
  const navigate = useNavigate();
  const { getProjectMembers, getMembersLastProjectSubmission } =
    useProjectService();

  const [rows, setRows] = useState<GridRowsProp<ProjectSubmissionDetail>>();

  const apiRef = useGridApiRef();

  const getDatagridSettings = () => {
    return JSON.parse(
      sessionStorage.getItem(SESSION_STORAGE_SETTINGS_KEY) ?? "{}",
    ) as DataGridSettings;
  };

  useEffect(() => {
    const tableSettings = getDatagridSettings();

    if (
      tableSettings.projectId == null ||
      tableSettings.projectId != project.id
    ) {
      sessionStorage.setItem(
        SESSION_STORAGE_SETTINGS_KEY,
        JSON.stringify({
          projectId: project.id,
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
  }, [apiRef, project]);

  useEffect(() => {
    getProjectMembers(project)
      .then((usersProjects) => {
        getMembersLastProjectSubmission(project)
          .then((submissions) => {
            const usersSubmissions: Record<
              string,
              ProjectSubmission | undefined
            > = {};
            submissions.forEach(
              (submission) =>
                (usersSubmissions[submission.userId] = submission),
            );
            setRows(
              usersProjects.map((usersProject) => {
                const userId = usersProject.user.id;
                if (usersSubmissions[userId] == undefined) {
                  return usersProject as ProjectSubmissionDetail;
                }
                return {
                  ...usersProject,
                  status: usersSubmissions[userId]?.status,
                  timestamp: usersSubmissions[userId]?.timestamp,
                };
              }),
            );
          })
          .catch((err: Error) => {
            enqueueSnackbar(err.message, { variant: "error" });
          });
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [getMembersLastProjectSubmission, getProjectMembers, project]);

  const actionsColumn = {
    field: "actions",
    type: "actions",
    width: 10,
    getActions: (params: GridRowParams<ProjectSubmissionDetail>) => {
      return [
        <GridActionsCellItem
          key={"unconfirm"}
          icon={<SettingsBackupRestore />}
          label="Unconfirm"
          onClick={() => {
            console.log("REVOKE");
          }}
          disabled={params.row.status !== "CONFIRMED"}
          showInMenu
        />,
      ];
    },
  };

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
        columns={[...columns, actionsColumn]}
        slots={{ toolbar: GridToolbar }}
        slotProps={{
          toolbar: {
            showQuickFilter: true,
            csvOptions: { fileName: `${project.name}-results` },
            printOptions: {
              fileName: `${project.name}-results`,
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
          if (cell.field === "actions") return;
          navigate(`/projects/${project.id}/users/${cell.id}`);
        }}
      />
    </Card>
  );
};

export default ProjectMembersTab;
