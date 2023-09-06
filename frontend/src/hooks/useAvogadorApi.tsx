import axios, { AxiosError } from "axios";
import { atom, useAtom } from "jotai";
import { useGlobalErrorSetter } from "../components/error/GlobalErrorState";

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
  const globalErrorSetter = useGlobalErrorSetter();

  avogadorApi.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
      if (error.response && error.response.status >= 500) {
        globalErrorSetter(error);
      }
      throw error;
    },
  );

  return avogadorApi;
};
