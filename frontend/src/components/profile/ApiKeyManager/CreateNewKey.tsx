import { Button, Grid, TextField, Typography } from "@mui/material";
import { User } from "@authentication/types";
import { DateField } from "@mui/x-date-pickers/DateField";
import { PrimitiveAtom, useAtom } from "jotai";
import useApiKeyService from "@profile/ApiKeyManager/hooks/useApiKeyService";
import { useCallback, useState } from "react";
import ApiKeyModal from "@profile/ApiKeyManager/ApiKeyModal.tsx";
import { ApiKey } from "@profile/ApiKeyManager/types.ts";
import Box from "@mui/material/Box";

const isValid: (name?: string, expiration?: Date) => boolean = (
  name?: string,
  expiration?: Date,
) => {
  return (
    isNameValid(name) &&
    name !== undefined &&
    expiration !== undefined &&
    expiration > new Date()
  );
};

const isNameValid: (name?: string) => boolean = (name?: string) => {
  console.log(name);

  return name === undefined || (name.split(" ").length == 1 && name !== "");
};

const CreateNewKey = ({
  user,
  userKeysAtom,
}: {
  user?: User | null;
  userKeysAtom: PrimitiveAtom<ApiKey[]>;
}) => {
  const { getUserApiKeys, createUserApiKey } = useApiKeyService();

  const [name, setName] = useState<string>();
  const [expiration, setExpiration] = useState<Date>();
  const [key, setKey] = useState<string>();
  const [, setUsersKeys] = useAtom(userKeysAtom);

  const createKey = useCallback(
    (u: User) => {
      if (name === undefined || expiration === undefined) {
        return;
      }
      createUserApiKey(u, name, expiration)
        .then((res: string) => {
          setName(undefined);
          setExpiration(undefined);
          setKey(res);
          getUserApiKeys(u)
            .then((res) => {
              setUsersKeys(res);
            })
            .catch((err) => {
              console.log(err);
            });
        })
        .catch((err) => {
          console.log(err);
        });
    },
    [
      createUserApiKey,
      setName,
      setExpiration,
      setKey,
      getUserApiKeys,
      expiration,
      name,
      setUsersKeys,
    ],
  );

  if (!user) return <>skeleton todo</>;

  return (
    <>
      <Typography>Create new API Key</Typography>
      <Grid container spacing={2} sx={{ py: 2 }}>
        <Grid item xs={6}>
          <TextField
            error={!isNameValid(name)}
            fullWidth
            label="Key name"
            id="keyName"
            onChange={(newValue) => {
              setName(newValue.target.value);
            }}
          />
        </Grid>
        <Grid item xs={6}>
          <DateField
            fullWidth
            label="Expiration date"
            value={expiration}
            disablePast
            format="dd/MM/yyyy"
            onChange={(newValue) => {
              setExpiration(newValue ?? undefined);
            }}
          />
        </Grid>
      </Grid>
      <Box sx={{ display: "flex", justifyContent: "end" }}>
        <Button
          variant="outlined"
          disabled={!isValid(name, expiration)}
          onClick={() => {
            createKey(user);
          }}
        >
          Create
        </Button>
      </Box>

      <ApiKeyModal apiKey={key} />
    </>
  );
};

export default CreateNewKey;
