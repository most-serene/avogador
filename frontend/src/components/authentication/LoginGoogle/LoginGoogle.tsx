import { GoogleLogin } from "@react-oauth/google";
import { useAuthService } from "@authentication/hooks/useAuthService";
import {
  Card,
  CardActions,
  CardContent,
  Grid,
  Typography,
} from "@mui/material";
import { enqueueSnackbar } from "notistack";
import { AxiosError } from "axios";
import { useAtom } from "jotai";
import showSplashScreenAtom from "@structure/SplashScreen/showSplashScreenAtom.ts";

const LoginGoogle = () => {
  const { login } = useAuthService();
  const [, setShowSplashScreen] = useAtom(showSplashScreenAtom);

  return (
    <Card sx={{ maxWidth: "32rem" }} raised>
      <CardContent>
        <Typography variant="h5" color="text.secondary" gutterBottom>
          Login with Google
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
          <GoogleLogin
            onSuccess={(credentialResponse) => {
              if (credentialResponse.credential !== undefined) {
                setShowSplashScreen(true);
                login(credentialResponse.credential)
                  .then(() => {
                    setShowSplashScreen(false);
                  })
                  .catch((err) => {
                    if (
                      err instanceof AxiosError &&
                      err.response?.status === 400
                    ) {
                      enqueueSnackbar(
                        "Login failed: not an valid email domain",
                        {
                          variant: "error",
                        },
                      );
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
