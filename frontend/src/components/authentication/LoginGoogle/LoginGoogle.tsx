import { GoogleLogin } from "@react-oauth/google";
import { useAuthService } from "@authentication/hooks/useAuthService";
import { enqueueSnackbar } from "notistack";
import { AxiosError } from "axios";
import { useAtom } from "jotai";
import showSplashScreenAtom from "@structure/SplashScreen/showSplashScreenAtom.ts";
import userAtom from "@authentication/userAtom.ts";
import { User } from "@authentication/types.ts";

const LoginGoogle = () => {
  const { loginWithGoogle } = useAuthService();
  const [, setUser] = useAtom(userAtom);
  const [, setShowSplashScreen] = useAtom(showSplashScreenAtom);

  return (
    <GoogleLogin
      onSuccess={(credentialResponse) => {
        if (credentialResponse.credential !== undefined) {
          setShowSplashScreen(true);
          loginWithGoogle(credentialResponse.credential)
            .then((u: User) => {
              setUser(u);
            })
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
        }
      }}
      onError={() => {
        enqueueSnackbar("Login failed", { variant: "error" });
      }}
      useOneTap
    />
  );
};

export default LoginGoogle;
