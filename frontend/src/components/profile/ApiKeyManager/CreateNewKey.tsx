import { Button, Grid, TextField, Typography } from "@mui/material";
import { User } from "../../authentication/types";
import { DateField } from "@mui/x-date-pickers/DateField";
import { PrimitiveAtom, useAtom } from "jotai";
import useApiKeyService from "./hooks/useApiKeyService";
import { useCallback, useState } from "react";
import ApiKeyModal from "./ApiKeyModal.tsx";
import { ApiKey } from "./types.ts";

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
      <Grid container>
        <Grid item xs={12}>
          <Typography>Create new API Key</Typography>
        </Grid>
        <Grid item xs={12}>
          <Grid container>
            <Grid item xs={5} sx={{ margin: "1rem" }}>
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
            <Grid item xs={5} sx={{ margin: "1rem" }}>
              <DateField
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
        </Grid>
        <Grid item xs={12} display={"flex"} justifyContent={"flex-end"}>
          <Button
            variant="outlined"
            disabled={!isValid(name, expiration)}
            onClick={() => {
              createKey(user);
            }}
          >
            Create
          </Button>
        </Grid>
      </Grid>
      <ApiKeyModal apiKey={key} />
    </>
  );
};

export default CreateNewKey;
