import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import { User } from "@authentication/types.ts";

const useUserService = () => {
  const avogadorApi = useAvogadorApi();

  const getUserById: (userId: string) => Promise<User> = useCallback(
    async (userId: string) => {
      const { data: user }: { data: User } = await avogadorApi.get(
        `/users/${userId}`,
      );
      return user;
    },
    [avogadorApi],
  );

  return {
    getUserById,
  };
};

export default useUserService;
