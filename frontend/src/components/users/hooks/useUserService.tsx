import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi.tsx";
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

  const getUsers: () => Promise<User[]> = useCallback(async () => {
    const { data: users }: { data: User[] } = await avogadorApi.get(`/users`);
    return users;
  }, [avogadorApi]);

  return {
    getUserById,
    getUsers,
  };
};

export default useUserService;
