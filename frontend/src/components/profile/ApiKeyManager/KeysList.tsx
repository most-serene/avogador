import {
  Card,
  CardContent,
  Grid,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import { User } from "@authentication/types";
import { ApiKey } from "@profile/ApiKeyManager/types";
import { PrimitiveAtom, useAtom } from "jotai";
import useApiKeyService from "@profile/ApiKeyManager/hooks/useApiKeyService";
import { format } from "date-fns";
import DeleteForeverIcon from "@mui/icons-material/DeleteForever";
import { useCallback } from "react";
import ButtonWithConfirmation from "@structure/ButtonWithConfirmation/ButtonWithConfirmation.tsx";

const KeysList = ({
  user,
  userKeysAtom,
}: {
  user?: User | null;
  userKeysAtom: PrimitiveAtom<ApiKey[]>;
}) => {
  const { deleteUserApiKey } = useApiKeyService();
  const [keys, setUserKeys] = useAtom(userKeysAtom);

  const deleteKey = useCallback(
    (user: User, k: ApiKey) => {
      deleteUserApiKey(user, k.name)
        .then(() => {
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
        return (
          <Card key={k.id}>
            <CardContent>
              <Grid container>
                <Grid item xs={10}>
                  <Typography variant="h6">{k.name}</Typography>

                  <Typography>
                    Creation: {format(k.creationTimestamp, "dd/MM/yyyy HH:mm")}
                  </Typography>

                  <Typography>
                    Expiration:{" "}
                    {format(k.expirationTimestamp, "dd/MM/yyyy HH:mm")}
                  </Typography>
                </Grid>

                <Grid item xs={2} display={"flex"} alignContent={"center"}>
                  <Tooltip title="Delete key">
                    <ButtonWithConfirmation
                      color="error"
                      confirmText="Delete"
                      confirmColor="error"
                      description="If you delete this key, you won't be able to use it anymore"
                      onConfirm={() => {
                        deleteKey(user, k);
                      }}
                    >
                      <DeleteForeverIcon />
                    </ButtonWithConfirmation>
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
