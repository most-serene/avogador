import { Card, CardContent, Grid, Tooltip, Typography } from "@mui/material";
import { MicroService } from "@structure/StatusPage/types";
import { CheckCircle } from "@mui/icons-material";
import ErrorIcon from "@mui/icons-material/Error";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";

const getStatusIcon: (status: string) => JSX.Element = (status: string) => {
  switch (status) {
    case "online":
      return (
        <Tooltip title={"Online"} placement="left">
          <CheckCircle color="success" />
        </Tooltip>
      );

    case "offline":
      return (
        <Tooltip title={"Offline"} placement="left">
          <ErrorIcon color="error" />
        </Tooltip>
      );

    default:
      return (
        <Tooltip title={status} placement="left">
          <HelpOutlineIcon color="warning" />
        </Tooltip>
      );
  }
};

const MicroServiceStatus = ({ service }: { service: MicroService }) => {
  return (
    <>
      <Card>
        <CardContent>
          <Grid container>
            <Grid item xs={8}>
              <Typography>{service.name}</Typography>
            </Grid>

            <Grid container xs={4} justifyContent="flex-end">
              {getStatusIcon(service.status)}
            </Grid>
          </Grid>
        </CardContent>
      </Card>
    </>
  );
};

export default MicroServiceStatus;
