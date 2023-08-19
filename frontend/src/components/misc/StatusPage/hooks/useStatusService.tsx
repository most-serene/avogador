import { useAvogadorApi } from "../../../../hooks/useAvogadorApi";
import { MicroService } from "../types";

export const useStatusService = () => {
  const avogadorApi = useAvogadorApi();

  const getMicroservicesStatus = async () => {
    const { data }: { data: MicroService[] } = await avogadorApi.get("/status");
    return data;
  };

  return {
    getMicroservicesStatus,
  };
};
