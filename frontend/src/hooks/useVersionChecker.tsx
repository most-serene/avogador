import { useAvogadorApi } from "@hooks/useAvogadorApi.tsx";
import { useCallback } from "react";
import { closeSnackbar, enqueueSnackbar } from "notistack";
import semver from "semver";
import { IconButton } from "@mui/material";
import RefreshIcon from "@mui/icons-material/Refresh";
import CloseIcon from "@mui/icons-material/Close";

const useVersionChecker = () => {
  const avogadorApi = useAvogadorApi();

  const checkWebAppVersion: () => void = useCallback(() => {
    avogadorApi
      .get(`/version/webapp`)
      .then(({ data: latestVersion }: { data: string }) => {
        if (semver.lt(import.meta.env.APP_VERSION as string, latestVersion)) {
          enqueueSnackbar("Reload to get the latest version of the app", {
            variant: "info",
            persist: true,
            action: (snackbarId) => (
              <>
                <IconButton
                  onClick={() => {
                    /*
                    caches
                      .keys()
                      .then((names) => {
                        console.log(names);
                        for (const name of names) {
                          caches
                            .delete(name)
                            .then((r) => {
                              console.log(r);
                            })
                            .catch((e) => {
                              console.error(e);
                            });
                        }
                      })
                      .catch((err) => {
                        console.error(err);
                      });

                     */
                    window.location.reload();
                  }}
                >
                  <RefreshIcon />
                </IconButton>
                <IconButton
                  onClick={() => {
                    closeSnackbar(snackbarId);
                  }}
                >
                  <CloseIcon />
                </IconButton>
              </>
            ),
          });
        }
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [avogadorApi]);

  return { checkWebAppVersion };
};

export default useVersionChecker;
