import { useEffect, useState } from "react";
import MicroServiceStatus from "./MicroServiceStatus/MicroServiceStatus";
import { useStatusService } from "./hooks/useStatusService";
import { MicroService } from "./types";
import {
  Card,
  CardContent,
  CircularProgress,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

const StatusPage = () => {
  const { getMicroservicesStatus } = useStatusService();
  const [microServices, setMicroServices] = useState<MicroService[]>([]);
  const [gatewayError, setGatewayError] = useState<boolean>(false);

  const GatewayAddressCard = () => {
    return (
      <>
        <Card sx={{ width: "32rem" }} raised>
          <CardContent>
            <Typography variant="h5">API gateway address:</Typography>
            <Grid container justifyContent="center">
              <Typography variant="body1">
                {import.meta.env.VITE_AVOGADOR_BACKEND_API_ADDRESS}
              </Typography>
            </Grid>
          </CardContent>
        </Card>
      </>
    );
  };

  useEffect(() => {
    const checkInterval = setInterval(() => {
      getMicroservicesStatus()
        .then((res) => {
          setMicroServices(res);
          setGatewayError(false);
        })
        .catch(() => {
          setGatewayError(true);
        });
    }, 2000);
    return () => {
      clearInterval(checkInterval);
    };
  }, [getMicroservicesStatus]);

  if (microServices.length === 0) {
    return (
      <>
        <Grid container style={{ marginTop: "2rem" }}>
          <Grid
            item
            xs={12}
            display="flex"
            justifyContent="center"
            alignItems="center"
          >
            <Stack spacing={2}>
              <GatewayAddressCard />
              <Card sx={{ width: "32rem" }} raised>
                <CardContent>
                  <Typography variant="h5">Services status:</Typography>
                  <Grid container justifyContent="center" alignContent="center">
                    <CircularProgress color="primary" />
                  </Grid>
                </CardContent>
              </Card>
            </Stack>
          </Grid>
        </Grid>
      </>
    );
  }

  return (
    <>
      <Grid container style={{ marginTop: "2rem" }}>
        <Grid
          item
          xs={12}
          display="flex"
          justifyContent="center"
          alignItems="center"
        >
          <Stack spacing={2}>
            <GatewayAddressCard />
            <Card sx={{ width: "32rem" }} raised>
              <CardContent>
                <Typography variant="h5">Services status:</Typography>
                {gatewayError ? (
                  <Typography variant="body1">
                    The gateway is offline
                  </Typography>
                ) : (
                  <Stack spacing={1}>
                    {microServices.map((service) => (
                      <MicroServiceStatus
                        key={service.name}
                        service={service}
                      />
                    ))}
                  </Stack>
                )}
              </CardContent>
            </Card>
          </Stack>
        </Grid>
      </Grid>
    </>
  );
};

export default StatusPage;
