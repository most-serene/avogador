import { Box, Button, Card, CardContent, Typography } from "@mui/material";
import { AxiosError } from "axios";

const getErrorCardTitle = (error: Error): string => {
  if (!(error instanceof AxiosError) || error.response === undefined) {
    return "An error has occurred";
  }
  if (error.response.status >= 500) {
    return `${error.response.status} - A server error has occurred`;
  }
  return `${error.response.status} - An error has occurred`;
};

const getErrorCardMessage = (error: Error): string => {
  if (error instanceof AxiosError && error.response) {
    return `Message: ${error.response.status} - ${
      (error.response.data as { error: string }).error
    }`;
  }
  return `Raw message: ${error.message}`;
};

export interface GlobalErrorCardProps {
  error: Error;
}

const GlobalErrorCard = ({ error }: GlobalErrorCardProps) => {
  const reloadApplication = () => {
    window.location.reload();
  };

  return (
    <Card sx={{ width: "32rem" }} raised>
      <CardContent>
        <Typography variant="h5" color="text.secondary" gutterBottom>
          {getErrorCardTitle(error)}
        </Typography>

        <Typography variant="body1" gutterBottom>
          A major issue has occurred in the application. The developers will fix
          this as soon as possible. Check the system status page to stay
          updated.
        </Typography>

        <Typography variant="body1" gutterBottom>
          {getErrorCardMessage(error)}
        </Typography>
        <Box display="flex" justifyContent="center" marginTop=".5rem">
          <Button onClick={reloadApplication} variant={"outlined"}>
            Reload
          </Button>
        </Box>
      </CardContent>
    </Card>
  );
};

export default GlobalErrorCard;
