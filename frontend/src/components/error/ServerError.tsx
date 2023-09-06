import { Box, Button, Card, CardContent, Typography } from "@mui/material";
import { useAtom } from "jotai";
import { AxiosError } from "axios";
import { globalErrorAtom } from "./serverErrorState";

const ServerError = () => {
  const [globalError, setGlobalError] = useAtom(globalErrorAtom);

  const dismissErrorPage = () => {
    setGlobalError(undefined);
  };

  return (
    <Box display={"flex"} justifyContent={"center"} marginTop={"2rem"}>
      <Card sx={{ width: "32rem" }} raised>
        <CardContent>
          <Typography variant="h5" color="text.secondary" gutterBottom>
            An error has occurred
          </Typography>
          <Typography variant="body1" gutterBottom>
            A major issue has occurred in the application. The developers will
            fix this as soon as possible. Check the system status page to stay
            updated.
          </Typography>
          {globalError instanceof AxiosError ? (
            <Typography variant="body1" gutterBottom>
              Message: {globalError.response?.status} -{" "}
              {(globalError.response?.data as { error: string }).error}
            </Typography>
          ) : (
            <Typography variant="body1" gutterBottom>
              Raw message: {globalError?.message}
            </Typography>
          )}

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
