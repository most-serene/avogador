import { PropsWithChildren, useEffect } from "react";
import useWebSocket from "@hooks/useWebSocket.tsx";
import { enqueueSnackbar } from "notistack";
import SplashScreen from "@structure/SplashScreen/SplashScreen.tsx";
import { useLocation } from "react-router-dom";

const allowedPaths = ["/status"];

const WebSocketWrapper = ({ children }: PropsWithChildren) => {
  const { isSocketConnected, socketClient } = useWebSocket();
  const { pathname } = useLocation();

  useEffect(() => {
    console.log(isSocketConnected);
    if (isSocketConnected) {
      console.log("subscribing");
      socketClient.subscribe("/broadcast", (message) => {
        console.log(message);
        enqueueSnackbar(message.body, { variant: "info" });
      });
    }
  }, [socketClient, isSocketConnected]);

  return isSocketConnected || allowedPaths.includes(pathname) ? (
    children
  ) : (
    <SplashScreen />
  );
};

export default WebSocketWrapper;
