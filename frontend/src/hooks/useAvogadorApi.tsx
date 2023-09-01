import axios from "axios";
import { atom, useAtom } from "jotai";

const avogadorApiClientAtom = atom(() => {
  const client = axios.create({
    baseURL: `${import.meta.env.VITE_AVOGADOR_BACKEND_API_ADDRESS}`,
    withCredentials: true,
  });

  const storedCSRF = localStorage.getItem("Jwt-CSRF-Hash");
  if (storedCSRF !== null) {
    client.defaults.headers.common["Jwt-CSRF-Hash"] = storedCSRF;
  }

  return client;
});

export const useAvogadorApi = () => {
  const [avogadorApi] = useAtom(avogadorApiClientAtom);

  return avogadorApi;
};
