import { ReactNode, useEffect } from "react";
import { User } from "@authentication/types.ts";
import { useAuthService } from "@authentication/hooks/useAuthService.tsx";
import { useLocation, useNavigate } from "react-router-dom";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { LoginPage } from "@authentication/LoginPage/LoginPage.tsx";
import { enqueueSnackbar } from "notistack";

const allowedPaths = ["/status"];

interface AuthWrapperProps {
  children: ReactNode;
}

export default function AuthWrapper({ children }: AuthWrapperProps) {
  const { getCurrent } = useAuthService();
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const [user, setUser] = useAtom(userAtom);

  useEffect(() => {
    if (user === undefined) {
      getCurrent()
        .then((u: User | null) => {
          setUser(u);
        })
        .catch((err: Error) => {
          console.log(err);
          enqueueSnackbar(err.message, { variant: "error" });
        });
    }
  }, [pathname, user, getCurrent, navigate, setUser]);

  if (user === undefined) {
    return <>splashscreen</>;
  }

  if (user === null && !allowedPaths.includes(pathname)) {
    return <LoginPage />;
  }

  return <>{children}</>;
}
