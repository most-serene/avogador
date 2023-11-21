import { Client, Message, StompSubscription } from "@stomp/stompjs";
import { atom, useAtom } from "jotai";
import { useCallback, useEffect } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi.tsx";
import { enqueueSnackbar } from "notistack";

const isSocketConnectedAtom = atom(false);
const websocketTokenAtom = atom(async () => {
  const avogadorApi = useAvogadorApi();
  try {
    const { data: token }: { data: string } = await avogadorApi.get(
      "/users/websocket-token",
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
