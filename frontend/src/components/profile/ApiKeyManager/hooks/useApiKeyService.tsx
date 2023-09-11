import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import { User } from "@authentication/types";
import { ApiKey } from "@profile/ApiKeyManager/types";

const useApiKeyService = () => {
  const avogadorApi = useAvogadorApi();

  const getUserApiKeys: (user: User) => Promise<ApiKey[]> = useCallback(
    async (user: User) => {
      const { data }: { data: ApiKey[] } = await avogadorApi.get(
        `/users/${user.id}/api-key`,
      );

      return [
        ...data.map((k) => {
          const key: ApiKey = {
            id: k.id,
            name: k.name,
            userId: k.userId,
            creationTimestamp: k.creationTimestamp,
            expirationTimestamp: k.expirationTimestamp,
          };
          return key;
        }),
      ];
    },
    [avogadorApi],
  );

  const createUserApiKey: (
    user: User,
    keyName: string,
    expiration: Date,
  ) => Promise<string> = useCallback(
    async (user: User, keyName: string, expiration: Date) => {
      const { data: key }: { data: string } = await avogadorApi.post(
        `/users/${user.id}/api-key`,
        {
          name: keyName,
          expiration: expiration,
        },
      );
      return key;
    },
    [avogadorApi],
  );

  const deleteUserApiKey = useCallback(
    (user: User, keyName: string) => {
      return avogadorApi.delete(`/users/${user.id}/api-key/${keyName}`);
    },
    [avogadorApi],
  );

  return {
    getUserApiKeys,
    createUserApiKey,
    deleteUserApiKey,
  };
};

export default useApiKeyService;
