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
import { Card, useTheme } from "@mui/material";
import useUserService from "@components/users/hooks/useUserService.tsx";
import { useEffect, useState } from "react";
import { enqueueSnackbar } from "notistack";
import { User } from "@authentication/types.ts";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import { ForbiddenError } from "@error/types.ts";
import {
  CheckCircle,
  GetApp,
  Publish,
  RadioButtonUnchecked,
} from "@mui/icons-material";
import Box from "@mui/material/Box";

const columns: GridColDef<User>[] = [
  {
    field: "enroll",
    headerName: "Enroll No.",
    valueGetter: (params) => params.row.email.split("@")[0],
    flex: 0.25,
    minWidth: 125,
  },
  {
    field: "email",
    headerName: "Email",
    valueGetter: (params) => params.row.email,
    flex: 1,
    minWidth: 200,
  },
  {
    field: "givenName",
    headerName: "Given Name",
    valueGetter: (params) => params.row.givenName,
    flex: 1,
  },
  {
    field: "familyName",
    headerName: "Family Name",
    valueGetter: (params) => params.row.familyName,
    flex: 1,
  },
  {
    field: "isProfessor",
    headerName: "Professor",
    valueGetter: (params) => params.row.isProfessor,
    renderCell: (params) => (
      <Box width="100%" display="flex" justifyContent="center">
        {params.row.isProfessor ? <CheckCircle /> : <RadioButtonUnchecked />}
      </Box>
    ),
    flex: 0.5,
  },
  {
    field: "isSuperuser",
    headerName: "Superuser",
    valueGetter: (params) => params.row.isSuperuser,
    renderCell: (params) => (
      <Box width="100%" display="flex" justifyContent="center">
        {params.row.isSuperuser ? <CheckCircle /> : <RadioButtonUnchecked />}
      </Box>
    ),
    flex: 0.5,
  },
];

const UsersScreen = () => {
  const { getUsers, promoteToProfessor, demoteToStudent } = useUserService();
  const theme = useTheme();
  const [user] = useAtom(userAtom);
  const [rows, setRows] = useState<GridRowsProp<User>>();
  const globalErrorSetter = useGlobalErrorSetter();

  useEffect(() => {
    if (user == null) return;
    if (!user.isSuperuser) {
      globalErrorSetter(
        new ForbiddenError(location.pathname, `You cannot access this page`),
      );
    }
    getUsers()
      .then((responseUsers) => {
        setRows(responseUsers);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [getUsers, globalErrorSetter, user]);

  const handlePromote = (user: User) => {
    promoteToProfessor(user)
      .then((updatedUser) => {
        enqueueSnackbar(
          `${updatedUser.familyName} ${updatedUser.givenName} promoted to Professor`,
          { variant: "success" },
        );
        setRows((prevState) =>
          prevState == null
            ? prevState
            : prevState.map((user) =>
                user.id === updatedUser.id ? updatedUser : user,
              ),
        );
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  };

  const handleDemote = (user: User) => {
    demoteToStudent(user)
      .then((updatedUser) => {
        enqueueSnackbar(
          `${updatedUser.familyName} ${updatedUser.givenName} demoted to Student`,
          { variant: "success" },
        );
        setRows((prevState) =>
          prevState == null
            ? prevState
            : prevState.map((user) =>
                user.id === updatedUser.id ? updatedUser : user,
              ),
        );
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  };

  const getSetProfessorActionCellItem = (params: GridRowParams<User>) => (
    <GridActionsCellItem
      key={"promote"}
      icon={<Publish />}
      label="Set Professor"
      onClick={() => {
        handlePromote(params.row);
      }}
      disabled={params.row.isProfessor}
      showInMenu
    />
  );

  const getSetStudentActionCellItem = (params: GridRowParams<User>) => (
    <GridActionsCellItem
      key={"demote"}
      icon={<GetApp />}
      label="Set Student"
      onClick={() => {
        handleDemote(params.row);
      }}
      disabled={!params.row.isProfessor}
      showInMenu
    />
  );

  const actionsColumn = {
    field: "actions",
    type: "actions",
    width: 10,
    getActions: (params: GridRowParams<User>) => {
      return [
        getSetProfessorActionCellItem(params),
        getSetStudentActionCellItem(params),
      ];
    },
  };

  return (
    <Card
      sx={{
        width: "100%",
        height: "100%",
        "& .MuiDataGrid-columnHeader": {
          backgroundColor: `primary.${theme.palette.mode}`,
          fontSize: "1.1rem",
        },
      }}
    >
      <DataGrid
        rows={rows ?? []}
        loading={rows == null}
        columns={[...columns, actionsColumn]}
        slots={{ toolbar: GridToolbar }}
        slotProps={{
          toolbar: {
            csvOptions: { fileName: `avogador-users` },
            printOptions: {
              fileName: `avogador-users`,
              hideToolbar: true,
            },
          },
        }}
        showColumnVerticalBorder
        showCellVerticalBorder
        checkboxSelection
        disableRowSelectionOnClick
        density="compact"
        autoPageSize
      />
    </Card>
  );
};
/*
        columnVisibilityModel={{
          actions:
            userCourse?.role !== "STUDENT" && userCourse?.role !== "EXTERNAL",
        }}

 */

export default UsersScreen;
