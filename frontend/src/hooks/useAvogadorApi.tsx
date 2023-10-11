import axios, { AxiosError } from "axios";
import { atom, useAtom } from "jotai";
import { useGlobalErrorSetter } from "@components/error/GlobalErrorState";

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
    (response) => {
      if (response.data instanceof Object) {
        handleDates(response.data);
      }
      return response;
    },
    (error: AxiosError) => {
      if (error.response && error.response.status >= 500) {
        globalErrorSetter(error);
      }
      throw error;
    },
  );

  return avogadorApi;
};

/*
const isoDateFormat = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)((-(\d{2}):(\d{2})|Z)?)$/;

function isIsoDateString(value: any): boolean {
  return value && typeof value === "string" && isoDateFormat.test(value);
}
*/

/* eslint-disable */
const handleDates = (body: any) => {
  if (body == null) return body;
  for (const key of Object.keys(body)) {
    const value = body[key];
    if (typeof value !== 'number' && !isNaN(Date.parse(value))) {
      body[key] = new Date(value);
    } else if (typeof value === "object") handleDates(value);
  }
};
/* eslint-enable */
