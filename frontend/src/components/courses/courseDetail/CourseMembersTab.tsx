// eslint-disable-next-line import/named
import { DataGrid, GridColDef, GridRowsProp } from "@mui/x-data-grid";
import { useEffect, useState } from "react";
import { Card, Skeleton } from "@mui/material";
import useCourseService from "../hooks/useCourseService.tsx";
import { CourseMemberDetail } from "../types.ts";

interface CourseMembersTabProps {
  courseId: string | undefined;
}

const columns: GridColDef<CourseMemberDetail>[] = [
  {
    field: "email",
    headerName: "Email",
    valueGetter: (params) => params.row.user.email,
    flex: 1,
    minWidth: 200,
  },
  {
    field: "matriculation",
    headerName: "Matriculation",
    valueGetter: (params) => params.row.user.email.split("@")[0],
    flex: 0.5,
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
    headerName: "Joined On",
    flex: 1,
  },
];

export default function CourseMembersTab({ courseId }: CourseMembersTabProps) {
  const { getCourseMembers } = useCourseService();
  const [rows, setRows] = useState<GridRowsProp<CourseMemberDetail>>();

  useEffect(() => {
    if (courseId == null) return;

    getCourseMembers(courseId)
      .then((data) => {
        setRows(data);
      })
      .catch((err) => {
        console.error(err);
      });
  }, [getCourseMembers, courseId]);

  return (
    <Card
      style={{
        width: "100%",
        height: "100%",
      }}
      elevation={0}
    >
      {rows != null ? (
        <DataGrid
          rows={rows}
          columns={columns}
          sx={{ borderColor: "secondary.main" }}
          showColumnVerticalBorder
          showCellVerticalBorder
          autoPageSize
        />
      ) : (
        <Skeleton variant="rectangular" width="100%" height="100%" />
      )}
    </Card>
  );
}
