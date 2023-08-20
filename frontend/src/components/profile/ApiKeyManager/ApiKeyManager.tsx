import {
  Button,
  Card,
  CardContent,
  Divider,
  Grid,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import { User } from "../../authentication/types";
import { ApiKey } from "./types";
import { DateField } from "@mui/x-date-pickers/DateField";
import { atom, useAtom } from "jotai";
import useApiKeyService from "./hooks/useApiKeyService";
import { useEffect, useState } from "react";
// import {format} from 'date-fns'
import DeleteForeverIcon from "@mui/icons-material/DeleteForever";
import ApiKeyModal from "./ApiKeyModal.tsx";

const userKeysAtom = atom<ApiKey[]>([]);

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

const CreateNewKey = ({ user }: { user?: User | null }) => {
  const { getUserApiKeys, createUserApiKey } = useApiKeyService();

  const [name, setName] = useState<string>();
  const [expiration, setExpiration] = useState<Date>();
  const [key, setKey] = useState<string>();
  const [, setUsersKeys] = useAtom(userKeysAtom);

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
              if (name === undefined || expiration === undefined) {
                return;
              }
              createUserApiKey(user, name, expiration)
                .then((res: string) => {
                  setName(undefined);
                  setExpiration(undefined);
                  setKey(res);
                  getUserApiKeys(user)
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

const KeysList = ({ user, keys }: { user?: User | null; keys: ApiKey[] }) => {
  const { deleteUserApiKey } = useApiKeyService();
  const [, setUserKeys] = useAtom(userKeysAtom);

  if (!user) return <>Skeleton todo</>;
  return (
    <Stack spacing={1}>
      {keys.map((k) => {
        console.log(k);

        return (
          <Card key={k.id}>
            <CardContent>
              <Grid container>
                <Grid item xs={10}>
                  <Typography variant="h6">{k.name}</Typography>

                  <Typography>
                    Creation: {k.creationTimestamp.toString()}
                  </Typography>

                  <Typography>
                    Expiration: {k.expirationTimestamp.toString()}
                  </Typography>
                </Grid>

                <Grid item xs={2} display={"flex"} alignContent={"center"}>
                  <Tooltip title="Delete key">
                    <Button
                      color="error"
                      onClick={() => {
                        deleteUserApiKey(user, k.name)
                          .then((res) => {
                            console.log(res);
                            setUserKeys((old) => {
                              return old.filter((e) => e.name !== k.name);
                            });
                          })
                          .catch((err) => {
                            console.log(err);
                          });
                      }}
                    >
                      <DeleteForeverIcon />
                    </Button>
                  </Tooltip>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        );
      })}
    </Stack>
  );
};

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
