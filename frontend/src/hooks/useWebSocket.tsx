import { Client, Message, StompSubscription } from "@stomp/stompjs";
import { atom, useAtom } from "jotai";
import { useCallback, useEffect } from "react";
import { enqueueSnackbar } from "notistack";
import axios from "axios";

const isSocketConnectedAtom = atom(false);
const websocketTokenAtom = atom(async () => {
  try {
    const { data: token }: { data: string } = await axios.get(
      `${
        import.meta.env.VITE_AVOGADOR_BACKEND_API_ADDRESS
      }/users/websocket-token`,
      {
        withCredentials: true,
        headers: {
          "Jwt-CSRF-Hash": localStorage.getItem("Jwt-CSRF-Hash"),
        },
      },
    );
    return token;
  } catch (err) {
    enqueueSnackbar((err as Error).message, { variant: "error" });
  }
});

const socketClientAtom = atom(async (get) => {
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

  const websocketToken = await get(websocketTokenAtom);
  if (websocketToken != undefined) {
    client.connectHeaders = {
      token: websocketToken,
    };
  }
  client.activate();
  return client;
});

const useWebSocket = () => {
  const [socketClient] = useAtom(socketClientAtom);
  const [websocketToken] = useAtom(websocketTokenAtom);
  const [isSocketConnected, setIsSocketConnected] = useAtom(
    isSocketConnectedAtom,
  );

  useEffect(() => {
    socketClient.onConnect = () => {
      setIsSocketConnected(true);
    };
  }, [setIsSocketConnected, socketClient]);

  const subscribe: (
    topic: string,
    cb: (message: Message) => void,
  ) => Promise<StompSubscription> = useCallback(
    (topic: string, cb: (message: Message) => void) => {
      return new Promise<StompSubscription>((resolve, reject) => {
        if (isSocketConnected && websocketToken != undefined) {
          resolve(socketClient.subscribe(topic, cb, { token: websocketToken }));
        } else {
          reject("Socket not connected");
        }
      });
    },
    [isSocketConnected, socketClient, websocketToken],
  );

  return {
    isSocketConnected,
    socketClient,
    subscribe,
  };
};

export default useWebSocket;
