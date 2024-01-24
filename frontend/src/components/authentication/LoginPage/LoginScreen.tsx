import LoginGoogle from "@authentication/LoginGoogle/LoginGoogle";
import Box from "@mui/material/Box";
import {
  Card,
  CardActions,
  CardContent,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import LoginMicrosoft from "@authentication/LoginMicrosoft/LoginMicrosoft.tsx";

export const LoginScreen = () => {
  return (
    <Box display="flex" justifyContent="center" marginTop={"2rem"}>
      <Card sx={{ maxWidth: "32rem" }} raised>
        <CardContent>
          <Typography variant="h5" color="text.secondary" gutterBottom>
            Avogador Login
          </Typography>
          <Typography variant="body2">
            This application only supports the login through your organization
            account.
            <br />
            Please login using your{" "}
            {(import.meta.env.VITE_CUSTOMER_DOMAINS as string)
              .split(",")
              .join("/")}{" "}
            account.
          </Typography>
        </CardContent>
        <CardActions>
          <Grid
            item
            xs
            display="flex"
            justifyContent="center"
            alignItems="center"
          >
            <Stack spacing={1}>
              <LoginGoogle />
              <LoginMicrosoft />
            </Stack>
          </Grid>
        </CardActions>
      </Card>
    </Box>
  );
};
