import { Card, CardContent, Divider, Typography } from "@mui/material";
import { User } from "../../authentication/types";
import { useAtom } from "jotai";
import useApiKeyService from "./hooks/useApiKeyService";
import { useEffect } from "react";
import CreateNewKey from "./CreateNewKey.tsx";
import KeysList from "./KeysList.tsx";
import { userKeysAtom } from "./userKeysAtom.ts";

const ApiKeyManager = ({ user }: { user?: User | null }) => {
  const [userKeys, setUserKeys] = useAtom(userKeysAtom);
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
          <Divider />
          <CreateNewKey user={user} />
          <Divider sx={{ marginTop: "1rem" }} />
          <KeysList user={user} keys={userKeys} />
        </CardContent>
      </Card>
    </>
  );
};

export default ApiKeyManager;
