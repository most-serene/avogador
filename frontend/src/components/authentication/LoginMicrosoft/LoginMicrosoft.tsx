import { useAuthService } from "@authentication/hooks/useAuthService.tsx";
import userAtom from "@authentication/userAtom.ts";
import showSplashScreenAtom from "@structure/SplashScreen/showSplashScreenAtom.ts";
import { enqueueSnackbar } from "notistack";
import { useAtom } from "jotai";
import { AxiosError } from "axios";
import MicrosoftLoginIconLight from "@assets/ms-symbollockup_signin_light.svg";
import Box from "@mui/material/Box";
import { useIsAuthenticated, useMsal } from "@azure/msal-react";
import { loginRequest } from "@authentication/LoginMicrosoft/msalConfig.ts";
import { useCallback } from "react";

const LoginMicrosoft = () => {
  const { instance, accounts } = useMsal();
  const [, setUser] = useAtom(userAtom);
  const [, setShowSplashScreen] = useAtom(showSplashScreenAtom);
  const isAuthenticated = useIsAuthenticated();
  const { loginWithMicrosoft } = useAuthService();

  const processLoginRequest = useCallback(() => {
    if (!isAuthenticated) return;

    setShowSplashScreen(true);
    return instance
      .acquireTokenSilent({
        ...loginRequest,
        account: instance.getActiveAccount() ?? accounts[0],
      })
      .then((response) => {
        loginWithMicrosoft(response.account.tenantId, response.accessToken)
          .then(setUser)
          .catch((err: Error) => {
            setUser(null);
            if (err instanceof AxiosError && err.response?.status === 400) {
              enqueueSnackbar("Login failed: not an valid email domain", {
                variant: "error",
              });
            } else {
              enqueueSnackbar("Login failed", { variant: "error" });
            }
          })
          .finally(() => {
            setShowSplashScreen(false);
          });
      });
  }, [
    accounts,
    instance,
    isAuthenticated,
    loginWithMicrosoft,
    setShowSplashScreen,
    setUser,
  ]);

  return (
    <Box
      component="img"
      onClick={() => {
        instance
          .loginPopup(loginRequest)
          .then((authenticationResult) => {
            instance.setActiveAccount(authenticationResult.account);
          })
          .then(processLoginRequest)
          .catch((err: Error) => {
            enqueueSnackbar(err.message, { variant: "error" });
          });
      }}
      sx={{
        width: "100%",
        display: "flex",
        cursor: "pointer",
      }}
      src={MicrosoftLoginIconLight}
    />
  );
};

export default LoginMicrosoft;
