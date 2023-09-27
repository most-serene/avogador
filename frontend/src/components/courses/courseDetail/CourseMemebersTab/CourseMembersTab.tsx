import {
  DataGrid,
  GridActionsCellItem,
  // eslint-disable-next-line import/named
  GridColDef,
  // eslint-disable-next-line import/named
  GridRowParams,
  // eslint-disable-next-line import/named
  GridRowsProp,
  GridToolbar,
} from "@mui/x-data-grid";
import { ReactElement, useEffect, useState } from "react";
import { Card, Skeleton } from "@mui/material";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import { CourseMemberDetail, UserCourseDetail } from "@courses/types.ts";
import { useAtom } from "jotai";
import ColorModeAtom from "@theme/colorModeAtom.ts";
import { format } from "date-fns";
import { GetApp, Publish } from "@mui/icons-material";
import { enqueueSnackbar } from "notistack";
import { AxiosError } from "axios";

declare global {
  interface Array<T> {
    pushIfRole(o: T, role: "ADMIN" | "COLLABORATOR"): T[];
  }
}

interface CourseMembersTabProps {
  userCourse: UserCourseDetail | undefined;
}

const columns: GridColDef<CourseMemberDetail>[] = [
  {
    field: "enroll",
    headerName: "Enroll No.",
    valueGetter: (params) => params.row.user.email.split("@")[0],
    flex: 0.25,
    minWidth: 125,
  },
  {
    field: "email",
    headerName: "Email",
    valueGetter: (params) => params.row.user.email,
    flex: 1,
    minWidth: 200,
  },
  {
    field: "givenName",
    headerName: "Given Name",
    valueGetter: (params) => params.row.user.givenName,
    flex: 1,
  },
  {
    field: "familyName",
    headerName: "Family Name",
    valueGetter: (params) => params.row.user.familyName,
    flex: 1,
  },
  {
    field: "role",
    headerName: "Role",
    flex: 0.5,
  },
  {
    field: "joinDate",
    headerName: "Joined on",
    valueGetter: (params) => format(params.row.joinDate, "yyyy/MM/dd HH:mm"),
    flex: 1,
  },
];

export default function CourseMembersTab({
  userCourse,
}: CourseMembersTabProps) {
  const [colorMode] = useAtom(ColorModeAtom);
  const { getCourseMembers, promoteUser, demoteUser } = useCourseService();
  const [rows, setRows] = useState<GridRowsProp<CourseMemberDetail>>();

  useEffect(() => {
    if (userCourse == null) return;

    getCourseMembers(userCourse.id)
      .then((data) => {
        setRows(data);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [getCourseMembers, userCourse]);

  const handlePromote = (member: CourseMemberDetail) => {
    promoteUser(member.courseId, member.user.id)
      .then(() => {
        enqueueSnackbar(
          `${member.user.givenName} ${member.user.familyName} promoted to Collaborator`,
          { variant: "success" },
        );
        setRows(
          rows?.map((uc) =>
            uc.id === member.id ? { ...uc, role: "COLLABORATOR" } : uc,
          ) ?? [],
        );
      })
      .catch((err) => {
        if (err instanceof AxiosError) {
          enqueueSnackbar(err.name, { variant: "error" });
        }
      });
  };

  const handleDemote = (member: CourseMemberDetail) => {
    demoteUser(member.courseId, member.user.id)
      .then(() => {
        enqueueSnackbar(
          `${member.user.givenName} ${member.user.familyName} demoted to Student`,
          { variant: "success" },
        );
        setRows(
          rows?.map((uc) =>
            uc.id === member.id ? { ...uc, role: "STUDENT" } : uc,
          ) ?? [],
        );
      })
      .catch((err) => {
        if (err instanceof AxiosError) {
          enqueueSnackbar(err.name, { variant: "error" });
        }
      });
  };

  const actionsColumn = {
    field: "actions",
    type: "actions",
    width: 10,
    getActions: (params: GridRowParams<CourseMemberDetail>) => {
      const actions: ReactElement[] = [];
      actions.pushIfRole = function (element, role) {
        if (
          userCourse?.role === "ADMIN" ||
          (role === "COLLABORATOR" && userCourse?.role === "COLLABORATOR")
        ) {
          this.push(element);
        }
        return this;
      };

      actions.pushIfRole(getPromoteActionCellItem(params), "ADMIN");
      actions.pushIfRole(getDemoteActionCellItem(params), "ADMIN");

      return actions;
    },
  };

  const getPromoteActionCellItem = (
    params: GridRowParams<CourseMemberDetail>,
  ) => (
    <GridActionsCellItem
      key={"promote"}
      icon={<Publish />}
      label="Promote"
      onClick={() => {
        handlePromote(params.row);
      }}
      disabled={params.row.role !== "STUDENT"}
      showInMenu
    />
  );

  const getDemoteActionCellItem = (
    params: GridRowParams<CourseMemberDetail>,
  ) => (
    <GridActionsCellItem
      key="demote"
      icon={<GetApp />}
      label="Demote"
      onClick={() => {
        handleDemote(params.row);
      }}
      disabled={params.row.role !== "COLLABORATOR"}
      showInMenu
    />
  );

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
          columns={[...columns, actionsColumn]}
          slots={{ toolbar: GridToolbar }}
          showColumnVerticalBorder
          showCellVerticalBorder
          checkboxSelection
          disableRowSelectionOnClick
          density="compact"
          autoPageSize
          columnVisibilityModel={{
            actions:
              userCourse?.role !== "STUDENT" && userCourse?.role !== "EXTERNAL",
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
}
