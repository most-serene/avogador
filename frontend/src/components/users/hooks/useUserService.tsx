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

  const promoteToProfessor: (user: User) => Promise<User> = useCallback(
    async (user: User) => {
      const { data: updatedUser }: { data: User } = await avogadorApi.put(
        `/users/professors/${user.id}`,
      );
      return updatedUser;
    },
    [avogadorApi],
  );

  const demoteToStudent: (user: User) => Promise<User> = useCallback(
    async (user: User) => {
      const { data: updatedUser }: { data: User } = await avogadorApi.put(
        `/users/students/${user.id}`,
      );
      return updatedUser;
    },
    [avogadorApi],
  );

  return {
    getUserById,
    getUsers,
    promoteToProfessor,
    demoteToStudent,
  };
};

export default useUserService;
