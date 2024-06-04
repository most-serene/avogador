import {
  Project,
  ProjectSubmission,
  ProjectSubmissionDetail,
} from "@components/projects/types.ts";
import {
  DataGrid,
  GridActionsCellItem,
  // eslint-disable-next-line import/named
  GridCellParams,
  // eslint-disable-next-line import/named
  GridColDef,
  // eslint-disable-next-line import/named
  GridFilterItem,
  // eslint-disable-next-line import/named
  GridFilterModel,
  GridFooter,
  GridFooterContainer,
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
import { format, isAfter } from "date-fns";
import {
  alpha,
  Card,
  Chip,
  darken,
  lighten,
  styled,
  Tooltip,
} from "@mui/material";
import {
  Cancel,
  CheckCircle,
  Help,
  SettingsBackupRestore,
  Upload,
} from "@mui/icons-material";
import ColorModeAtom from "@theme/colorModeAtom.ts";
import { useNavigate } from "react-router-dom";
import { ChangeEvent, useCallback, useEffect, useRef, useState } from "react";
import { useAtom } from "jotai";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { enqueueSnackbar } from "notistack";
import { LoadingButton } from "@mui/lab";
import { AxiosError } from "axios";

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

const getBackgroundColor = (color: string, mode: string) =>
  alpha(mode === "dark" ? darken(color, 0.7) : lighten(color, 0.7), 0.5);

const getHoverBackgroundColor = (color: string, mode: string) =>
  alpha(mode === "dark" ? darken(color, 0.6) : lighten(color, 0.6), 0.5);

const getSelectedBackgroundColor = (color: string, mode: string) =>
  alpha(mode === "dark" ? darken(color, 0.5) : lighten(color, 0.5), 0.5);

const getSelectedHoverBackgroundColor = (color: string, mode: string) =>
  alpha(mode === "dark" ? darken(color, 0.4) : lighten(color, 0.4), 0.5);

const StyledDataGrid = styled(DataGrid<ProjectSubmissionDetail>)(
  ({ theme }) => ({
    "& .super-app-theme--overdue": {
      backgroundColor: getBackgroundColor(
        theme.palette.warning.main,
        theme.palette.mode,
      ),
      "&:hover": {
        backgroundColor: getHoverBackgroundColor(
          theme.palette.warning.main,
          theme.palette.mode,
        ),
      },
      "&.Mui-selected": {
        backgroundColor: getSelectedBackgroundColor(
          theme.palette.warning.main,
          theme.palette.mode,
        ),
        "&:hover": {
          backgroundColor: getSelectedHoverBackgroundColor(
            theme.palette.warning.main,
            theme.palette.mode,
          ),
        },
      },
    },
  }),
);

const columns: GridColDef<ProjectSubmissionDetail>[] = [
  {
    field: "enroll",
    headerName: "Enroll No.",
    valueGetter: (params) => params.row.user.email.split("@")[0],
    flex: 0.5,
    minWidth: 100,
  },
  {
    field: "fullName",
    headerName: "Name",
    valueGetter: (params) =>
      `${params.row.user.familyName ?? ""} ${params.row.user.givenName ?? ""}`,
    flex: 1,
    minWidth: 150,
  },
  {
    field: "mark",
    headerName: "Mark",
    valueGetter: (params) => `${params.row.mark ?? ""}`,
    flex: 1,
    maxWidth: 80,
    align: "center",
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
    maxWidth: 200,
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
  const {
    getProjectMembers,
    getMembersLastProjectSubmission,
    unconfirmSubmission,
    uploadMarksFile,
  } = useProjectService();
  const [isUploadingMarks, setIsUploadingMarks] = useState(false);
  const marksCsvFile = useRef<HTMLInputElement>(null);

  const [rows, setRows] = useState<GridRowsProp<ProjectSubmissionDetail>>();

  const apiRef = useGridApiRef();

  const getDatagridSettings = () => {
    return JSON.parse(
      sessionStorage.getItem(SESSION_STORAGE_SETTINGS_KEY) ?? "{}",
    ) as DataGridSettings;
  };

  const handleMarksUpload = (
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    setIsUploadingMarks(true);
    const fileList = (e.target as HTMLInputElement).files;
    if (fileList == null) return;
    const file = fileList.item(0);
    if (file == null) return;
    uploadMarksFile(project, file)
      .then(() => {
        enqueueSnackbar("Marks upload successfully", { variant: "success" });
        setIsUploadingMarks(false);
        if (marksCsvFile.current != null) {
          marksCsvFile.current.value = "";
        }
      })
      .catch((err: Error) => {
        if (err instanceof AxiosError && err.status === 400) {
          enqueueSnackbar("The domain of some emails is not valid", {
            variant: "error",
          });
        } else {
          enqueueSnackbar(err.message, { variant: "error" });
        }
        setIsUploadingMarks(false);
        if (marksCsvFile.current != null) {
          marksCsvFile.current.value = "";
        }
      })
      .finally(() => {
        setRows(undefined);
        fetchProjectMembers();
      });
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

  const fetchProjectMembers = useCallback(() => {
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
                  submissionId: usersSubmissions[userId]?.id,
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

  useEffect(() => {
    fetchProjectMembers();
  }, [fetchProjectMembers]);

  const handleUnconfirmSubmission = (
    submissionDetail: ProjectSubmissionDetail,
  ) => {
    if (submissionDetail.submissionId == null) return;
    unconfirmSubmission(
      submissionDetail.projectId,
      submissionDetail.submissionId,
    )
      .then(() => {
        enqueueSnackbar("Submission unconfirmed successfully", {
          variant: "success",
        });
        setRows(
          rows?.map((submission) =>
            submission.id === submissionDetail.id
              ? { ...submissionDetail, status: "SUCCESS" }
              : submission,
          ) ?? [],
        );
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  };

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
            handleUnconfirmSubmission(params.row);
          }}
          disabled={params.row.status !== "CONFIRMED"}
          showInMenu
        />,
      ];
    },
  };

  const CustomGridFooter = () => {
    return (
      <GridFooterContainer>
        <Tooltip title={"as email,mark csv file"} placement={"top"}>
          <LoadingButton
            sx={{ ml: "2rem" }}
            variant={"outlined"}
            loading={isUploadingMarks}
            component={"label"}
            loadingPosition={"start"}
            startIcon={<Upload />}
          >
            Upload marks
            <input
              hidden
              accept={".csv"}
              type={"file"}
              ref={marksCsvFile}
              onChange={handleMarksUpload}
            />
          </LoadingButton>
        </Tooltip>
        <GridFooter sx={{ border: "none" }} />
      </GridFooterContainer>
    );
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
      <StyledDataGrid
        apiRef={apiRef}
        rows={rows ?? []}
        loading={rows == null}
        columns={[...columns, actionsColumn]}
        slots={{
          toolbar: GridToolbar,
          footer: CustomGridFooter,
        }}
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
        getRowClassName={(params) =>
          `super-app-theme--${params.row.timestamp && isAfter(params.row.timestamp, project.deadline) ? "overdue" : ""}`
        }
        onPaginationModelChange={handlePaginationModelChange}
        onSortModelChange={handleSortModelChange}
        onFilterModelChange={handleFilterModelChange}
        onCellClick={(cell: GridCellParams<ProjectSubmissionDetail>) => {
          if (["actions", "__check__"].includes(cell.field)) return;
          if (cell.row.status == null) return;
          navigate(`/projects/${project.id}/users/${cell.id}`);
        }}
      />
    </Card>
  );
};

export default ProjectMembersTab;
