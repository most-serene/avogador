import axios from "axios";

export const avogadorApi = axios.create({
  baseURL: `${import.meta.env.VITE_AVOGADOR_BACKEND_API_ADDRESS}`,
  withCredentials: true,
});
