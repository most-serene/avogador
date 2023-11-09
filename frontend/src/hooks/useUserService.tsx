import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import { User } from "@authentication/types.ts";

const useUserService = () => {
  const avogadorApi = useAvogadorApi();

  const getUserById: (userId: string) => Promise<User> = useCallback(
    async (userId: string) => {
      const { data: trial }: { data: User } = await avogadorApi.get(
        `/users/${userId}`,
      );
      return trial;
    },
    [avogadorApi],
  );

  return {
    getUserById,
  };
};

export default useUserService;
