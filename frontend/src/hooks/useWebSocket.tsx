import { useMemo } from "react";
import { Client } from "@stomp/stompjs";

const useWebSocket = () => {
  const client = useMemo(() => {
    const client = new Client({
      brokerURL: "ws://localhost:8080/ws",
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
