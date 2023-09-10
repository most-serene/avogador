import { Box, Button, Card, CardContent, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { ResourceNotFoundError } from "@error/types";

export interface ErrorCard404Props {
  error: ResourceNotFoundError;
}

const ErrorCard404 = ({ error }: ErrorCard404Props) => {
  const navigate = useNavigate();

  const goHome = () => {
    navigate("/");
  };

  return (
    <Card sx={{ width: "32rem" }} raised>
      <CardContent>
        <Typography variant="h5" color="text.secondary" gutterBottom>
          404 - {error.resourceName} not found
        </Typography>

        <Typography variant="body1" gutterBottom>
          The resource you were looking for is not present right now. Click on
          the button below to go back to your home page.
        </Typography>

        <Box display="flex" justifyContent="center" marginTop=".5rem">
          <Button onClick={goHome} variant={"outlined"}>
            Go Home
          </Button>
        </Box>
      </CardContent>
    </Card>
  );
};

export default ErrorCard404;
