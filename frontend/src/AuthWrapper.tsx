import { ReactNode, useEffect } from "react";
import { User } from "@authentication/types.ts";
import { useAuthService } from "@authentication/hooks/useAuthService.tsx";
import { useLocation, useNavigate } from "react-router-dom";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";

interface AuthWrapperProps {
  children: ReactNode;
}

export default function AuthWrapper({ children }: AuthWrapperProps) {
  const { getCurrent } = useAuthService();
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const [user] = useAtom(userAtom);

  useEffect(() => {
    const allowedPaths = ["/", "/login", "/status"];
    if (user === undefined) {
      getCurrent()
        .then((u: User | null) => {
          if (u === null && !allowedPaths.includes(pathname)) {
            navigate("/login");
          }
        })
        .catch((err) => {
          console.log(err);
        });
    } else if (user === null && !allowedPaths.includes(pathname)) {
      navigate("/login");
    }
  }, [pathname, user, getCurrent, navigate]);

  return <>{children}</>;
}
