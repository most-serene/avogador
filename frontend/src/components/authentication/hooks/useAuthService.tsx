import { User } from "@authentication/types";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import { useCallback } from "react";
import { enqueueSnackbar } from "notistack";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";

export const useAuthService = () => {
  const avogadorApi = useAvogadorApi();
  const [, setUser] = useAtom(userAtom);

  const getCurrent = useCallback(async () => {
    try {
      const { data: responseUser }: { data: User } = await avogadorApi.get(
        "/users/current",
      );
      return responseUser;
    } catch {
      return null;
    }
  }, [avogadorApi]);

  const login: (googleToken: string) => Promise<User> = useCallback(
    async (googleToken: string) => {
      return avogadorApi
        .post("/users/google-auth", {
          googleToken: googleToken,
        })
        .then(
          ({
            data: user,
          }: {
            data: User & { hash: string; picture: string };
          }) => {
            avogadorApi.defaults.headers.common["Jwt-CSRF-Hash"] = user.hash;
            localStorage.setItem("Jwt-CSRF-Hash", user.hash);
            localStorage.setItem("profile-picture", user.picture);
            const u: User = {
              id: user.id,
              email: user.email,
              givenName: user.givenName,
              familyName: user.familyName,
              isProfessor: user.isProfessor,
              isSuperuser: user.isSuperuser,
            };
            return u;
          },
        );
    },
    [avogadorApi],
  );

  const logout = useCallback(() => {
    avogadorApi
      .get("/users/logout")
      .then(() => {
        setUser(null);
        localStorage.removeItem("profile-picture");
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [avogadorApi, setUser]);

  return {
    getCurrent,
    login,
    logout,
  };
};
