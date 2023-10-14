import { useMemo } from "react";
import { Client } from "@stomp/stompjs";

const useWebSocket = () => {
  const client = useMemo(() => {
    const client = new Client({
      brokerURL: `${import.meta.env.VITE_AVOGADOR_BACKEND_WEBSOCKET_ADDRESS}`,
      onConnect: (frame) => {
        console.log(frame);
      },
      onStompError: (frame) => {
        console.log(frame);
      },
      onWebSocketError: (e) => {
        console.log(e);
      },
    });
    client.activate();
    return client;
  }, []);

  return {
    client,
  };
};

export default useWebSocket;
