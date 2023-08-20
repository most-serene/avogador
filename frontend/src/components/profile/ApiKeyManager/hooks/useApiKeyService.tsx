import { useAvogadorApi } from "../../../../hooks/useAvogadorApi";
import { User } from "../../../authentication/types";
import { ApiKey } from "../types";

const useApiKeyService = () => {
  const avogadorApi = useAvogadorApi();

  const getUserApiKeys: (user: User) => Promise<ApiKey[]> = async (
    user: User,
  ) => {
    const { data }: { data: ApiKey[] } = await avogadorApi.get(
      `/public/users/${user.id}/api-key`,
    );
    return data;
  };

  const createUserApiKey: (
    user: User,
    keyName: string,
    expiration: Date,
  ) => Promise<string> = async (
    user: User,
    keyName: string,
    expiration: Date,
  ) => {
    const { data: key }: { data: string } = await avogadorApi.post(
      `/public/users/${user.id}/api-key`,
      {
        name: keyName,
        expiration: expiration,
      },
    );
    return key;
  };

  const deleteUserApiKey = (user: User, keyName: string) => {
    return avogadorApi.delete(`/public/users/${user.id}/api-key/${keyName}`);
  };

  return {
    getUserApiKeys,
    createUserApiKey,
    deleteUserApiKey,
  };
};

export default useApiKeyService;
