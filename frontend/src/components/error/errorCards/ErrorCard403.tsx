import { Box, Button, Card, CardContent, Typography } from "@mui/material";

const ErrorCard403 = () => {
  const goBack = () => {
    history.back();
  };

  return (
    <Card sx={{ width: "32rem" }} raised>
      <CardContent>
        <Typography variant="h5" color="text.secondary" gutterBottom>
          403 - Forbidden
        </Typography>

        <Typography variant="body1" gutterBottom>
          You don&apos;t have the right permissions to view this page. If you
          think this is an error, please contact your service administrators.
        </Typography>

        <Box display="flex" justifyContent="center" marginTop=".5rem">
          <Button onClick={goBack} variant={"outlined"}>
            Go Back
          </Button>
        </Box>
      </CardContent>
    </Card>
  );
};

export default ErrorCard403;
