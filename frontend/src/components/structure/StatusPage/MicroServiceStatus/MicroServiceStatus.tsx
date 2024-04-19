import { Card, CardContent, Tooltip, Typography } from "@mui/material";
import { MicroService } from "@structure/StatusPage/types";
import { CheckCircle } from "@mui/icons-material";
import ErrorIcon from "@mui/icons-material/Error";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import Box from "@mui/material/Box";

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
          <Box
            display={"flex"}
            justifyContent={"space-between"}
            alignItems={"center"}
          >
            <Typography>{service.name}</Typography>
            {getStatusIcon(service.status)}
          </Box>
        </CardContent>
      </Card>
    </>
  );
};

export default MicroServiceStatus;
