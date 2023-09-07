import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import { MicroService } from "../types";

export const useStatusService = () => {
  const avogadorApi = useAvogadorApi();

  const getMicroservicesStatus = useCallback(async () => {
    const { data }: { data: MicroService[] } = await avogadorApi.get("/status");
    return data;
  }, [avogadorApi]);

  const getBackendVersion = useCallback(async () => {
    const { data: version }: { data: string } = await avogadorApi.get("/");
    return version;
  }, [avogadorApi]);

  return {
    getMicroservicesStatus,
    getBackendVersion,
  };
};
