import { PropsWithChildren, useEffect } from "react";
import useWebSocket from "@hooks/useWebSocket.tsx";
import { enqueueSnackbar } from "notistack";
import SplashScreen from "@structure/SplashScreen/SplashScreen.tsx";
import { useLocation } from "react-router-dom";
import userAtom from "@authentication/userAtom.ts";
import { useAtom } from "jotai";

const allowedPaths = ["/status"];

const WebSocketWrapper = ({ children }: PropsWithChildren) => {
  const { isSocketConnected, subscribe } = useWebSocket();
  const { pathname } = useLocation();
  const [user] = useAtom(userAtom);

  useEffect(() => {
    if (isSocketConnected) {
      subscribe("/broadcast", (message) => {
        enqueueSnackbar(message.body, { variant: "info" });
      })
        .then(() => {
          // empty
        })
        .catch((err: Error) => {
          enqueueSnackbar(err.message, { variant: "error" });
        });
    }
  }, [isSocketConnected, subscribe]);

  useEffect(() => {
    if (isSocketConnected && user) {
      subscribe(`/users/${user.id}`, (message) => {
        enqueueSnackbar(message.body, { variant: "info" });
      })
        .then(() => {
          // empty
        })
        .catch((err: Error) => {
          enqueueSnackbar(err.message, { variant: "error" });
        });
    }
  }, [isSocketConnected, subscribe, user]);

  return isSocketConnected || allowedPaths.includes(pathname) ? (
    children
  ) : (
    <SplashScreen />
  );
};

export default WebSocketWrapper;
