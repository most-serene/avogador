import { GoogleLogin } from "@react-oauth/google";
import { useAuthService } from "@authentication/hooks/useAuthService";
import {
  Card,
  CardActions,
  CardContent,
  Grid,
  Typography,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { enqueueSnackbar } from "notistack";
import { AxiosError } from "axios";

const LoginGoogle = () => {
  const { login } = useAuthService();
  const navigate = useNavigate();

  return (
    <Card sx={{ maxWidth: "32rem" }} raised>
      <CardContent>
        <Typography variant="h5" color="text.secondary" gutterBottom>
          Login with Google
        </Typography>
        <Typography variant="body2">
          This application only supports the login through you academic account.
          <br />
          Please login using your @stud.unive.it or @unive.it account.
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
          <GoogleLogin
            onSuccess={(credentialResponse) => {
              if (credentialResponse.credential !== undefined) {
                login(credentialResponse.credential)
                  .then(() => {
                    navigate("/");
                  })
                  .catch((err) => {
                    if (
                      err instanceof AxiosError &&
                      err.response?.status === 400
                    ) {
                      enqueueSnackbar("Login failed: not an UniVe email", {
                        variant: "error",
                      });
                    } else {
                      enqueueSnackbar("Login failed", { variant: "error" });
                    }
                  });
              }
            }}
            onError={() => {
              enqueueSnackbar("Login failed", { variant: "error" });
            }}
            useOneTap
          />
        </Grid>
      </CardActions>
    </Card>
  );
};

export default LoginGoogle;
