import {
  Button,
  Card,
  CardContent,
  Grid,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import { User } from "../../authentication/types";
import { ApiKey } from "./types";
import { useAtom } from "jotai";
import useApiKeyService from "./hooks/useApiKeyService";
import { format } from "date-fns";
import DeleteForeverIcon from "@mui/icons-material/DeleteForever";
import { useCallback } from "react";
import { userKeysAtom } from "./userKeysAtom";

const KeysList = ({ user, keys }: { user?: User | null; keys: ApiKey[] }) => {
  const { deleteUserApiKey } = useApiKeyService();
  const [, setUserKeys] = useAtom(userKeysAtom);

  const deleteKey = useCallback(
    (user: User, k: ApiKey) => {
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
    },
    [deleteUserApiKey, setUserKeys],
  );

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
                    Creation: {format(k.creationTimestamp, "dd/MM/yyyy")}
                  </Typography>

                  <Typography>
                    Expiration: {format(k.expirationTimestamp, "dd/MM/yyyy")}
                  </Typography>
                </Grid>

                <Grid item xs={2} display={"flex"} alignContent={"center"}>
                  <Tooltip title="Delete key">
                    <Button
                      color="error"
                      onClick={() => {
                        deleteKey(user, k);
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

export default KeysList;
