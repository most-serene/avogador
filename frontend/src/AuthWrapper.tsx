import { ReactNode, useEffect } from "react";
import { User } from "@authentication/types.ts";
import { useAuthService } from "@authentication/hooks/useAuthService.tsx";
import { useLocation, useNavigate } from "react-router-dom";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { LoginScreen } from "@authentication/LoginPage/LoginScreen.tsx";
import { enqueueSnackbar } from "notistack";
import SplashScreen from "@structure/SplashScreen/SplashScreen.tsx";
import showSplashScreenAtom from "@structure/SplashScreen/showSplashScreenAtom.ts";

const allowedPaths = ["/status"];

interface AuthWrapperProps {
  children: ReactNode;
}

export default function AuthWrapper({ children }: AuthWrapperProps) {
  const { getCurrent } = useAuthService();
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const [user, setUser] = useAtom(userAtom);
  const [showSplashScreen] = useAtom(showSplashScreenAtom);

  useEffect(() => {
    if (user === undefined) {
      getCurrent()
        .then((u: User | null) => {
          setUser(u);
        })
        .catch((err: Error) => {
          setUser(null);
          enqueueSnackbar(err.message, { variant: "error" });
        });
    }
  }, [pathname, user, getCurrent, navigate, setUser]);

  if (user === undefined || showSplashScreen) return <SplashScreen />;

  if (user === null && !allowedPaths.includes(pathname)) {
    return <LoginScreen />;
  }

  return <>{children}</>;
}
