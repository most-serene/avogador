import { Card, CardContent, Divider, Typography } from "@mui/material";
import { User } from "../../authentication/types";
import { atom, useAtom } from "jotai";
import useApiKeyService from "./hooks/useApiKeyService";
import { useEffect } from "react";
import CreateNewKey from "./CreateNewKey.tsx";
import KeysList from "./KeysList.tsx";
import { ApiKey } from "./types.ts";

const userKeysAtom = atom<ApiKey[]>([]);

const ApiKeyManager = ({ user }: { user?: User | null }) => {
  const [, setUserKeys] = useAtom(userKeysAtom);
  const { getUserApiKeys } = useApiKeyService();

  useEffect(() => {
    if (user) {
      getUserApiKeys(user)
        .then((keys) => {
          setUserKeys(keys);
        })
        .catch((err) => {
          console.log(err);
        });
    }
  }, [user, getUserApiKeys, setUserKeys]);

  return (
    <>
      <Card raised sx={{ width: "32rem" }}>
        <CardContent>
          <Typography variant="h5">Your API Keys</Typography>
          <Divider sx={{ mb: ".5rem" }} />
          <CreateNewKey user={user} userKeysAtom={userKeysAtom} />
          <Divider sx={{ my: "1rem" }} />
          <KeysList user={user} userKeysAtom={userKeysAtom} />
        </CardContent>
      </Card>
    </>
  );
};

export default ApiKeyManager;
