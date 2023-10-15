import { PropsWithChildren, useEffect } from "react";
import useWebSocket from "@hooks/useWebSocket.tsx";
import { enqueueSnackbar } from "notistack";
import SplashScreen from "@structure/SplashScreen/SplashScreen.tsx";

const WebSocketWrapper = ({ children }: PropsWithChildren) => {
  const { isSocketConnected, socketClient } = useWebSocket();

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

  return isSocketConnected ? children : <SplashScreen />;
};

export default WebSocketWrapper;
