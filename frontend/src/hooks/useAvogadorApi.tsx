import axios from "axios";
import { atom, useAtom } from "jotai";

const avogadorApiClientAtom = atom(() => {
  return axios.create({
    baseURL: `${import.meta.env.VITE_AVOGADOR_BACKEND_API_ADDRESS}`,
    withCredentials: true,
  });
});

export const useAvogadorApi = () => {
  const [avogadorApi] = useAtom(avogadorApiClientAtom);

  return avogadorApi;
};
