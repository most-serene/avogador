import { ReactNode, useEffect } from "react";
import { User } from "./components/authentication/types.ts";
import { useAuthService } from "./components/authentication/hooks/useAuthService.tsx";
import { useLocation, useNavigate } from "react-router-dom";

interface AuthWrapperProps {
  children: ReactNode;
}

export default function AuthWrapper({ children }: AuthWrapperProps) {
  const { getCurrent } = useAuthService();
  const navigate = useNavigate();
  const { pathname } = useLocation();

  useEffect(() => {
    getCurrent()
      .then((u: User | null) => {
        if (u === null && pathname !== "/login") {
          navigate("/login");
        }
      })
      .catch((err) => {
        console.log(err);
      });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pathname]);

  return <>{children}</>;
}
