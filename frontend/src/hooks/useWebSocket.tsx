import { Client, Message, StompSubscription } from "@stomp/stompjs";
import { atom, useAtom } from "jotai";
import { useCallback, useEffect } from "react";

const isSocketConnectedAtom = atom(false);
const socketClientAtom = atom<Client>(() => {
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
});

const useWebSocket = () => {
  const [socketClient] = useAtom(socketClientAtom);
  const [isSocketConnected, setIsSocketConnected] = useAtom(
    isSocketConnectedAtom,
  );

  useEffect(() => {
    socketClient.onConnect = (frame) => {
      console.log(frame);
      setIsSocketConnected(true);
    };
  }, [setIsSocketConnected, socketClient]);

  const subscribe: (
    topic: string,
    cb: (message: Message) => void,
  ) => Promise<StompSubscription> = useCallback(
    (topic: string, cb: (message: Message) => void) => {
      return new Promise<StompSubscription>((resolve, reject) => {
        if (isSocketConnected) {
          resolve(socketClient.subscribe(topic, cb));
        } else {
          reject("Socket not connected");
        }
      });
    },
    [isSocketConnected, socketClient],
  );

  return {
    isSocketConnected,
    socketClient,
    subscribe,
  };
};

export default useWebSocket;
