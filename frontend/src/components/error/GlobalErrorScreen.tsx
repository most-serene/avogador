import { Box, Button, Card, CardContent, Typography } from "@mui/material";
import { useAtom } from "jotai";
import { AxiosError } from "axios";
import { globalErrorAtom } from "./GlobalErrorState";

const getErrorCardTitle = (error: Error): string => {
  if (!(error instanceof AxiosError) || error.response === undefined) {
    return "An error has occurred";
  }
  if (error.response.status >= 500) {
    return `A server error has occurred - ${error.response.status}`;
  }
  return `An error has occurred - ${error.response.status}`;
};

const getErrorCardMessage = (error: Error): string => {
  if (error instanceof AxiosError && error.response) {
    return `Message: ${error.response.status} - ${
      (error.response.data as { error: string }).error
    }`;
  }
  return `Raw message: ${error.message}`;
};

const ServerError = () => {
  const [globalError, setGlobalError] = useAtom(globalErrorAtom);

  const dismissErrorPage = () => {
    setGlobalError(undefined);
  };

  if (globalError === undefined) return <> </>;

  return (
    <Box display={"flex"} justifyContent={"center"} marginTop={"2rem"}>
      <Card sx={{ width: "32rem" }} raised>
        <CardContent>
          <Typography variant="h5" color="text.secondary" gutterBottom>
            {getErrorCardTitle(globalError)}
          </Typography>

          <Typography variant="body1" gutterBottom>
            A major issue has occurred in the application. The developers will
            fix this as soon as possible. Check the system status page to stay
            updated.
          </Typography>

          <Typography variant="body1" gutterBottom>
            {getErrorCardMessage(globalError)}
          </Typography>
          <Box display="flex" justifyContent="center" marginTop=".5rem">
            <Button onClick={dismissErrorPage} variant={"outlined"}>
              Dismiss
            </Button>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default ServerError;
