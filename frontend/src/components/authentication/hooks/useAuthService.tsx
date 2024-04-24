import { User } from "@authentication/types";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import { useCallback } from "react";
import { enqueueSnackbar } from "notistack";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import axios from "axios";

export const useAuthService = () => {
  const avogadorApi = useAvogadorApi();
  const [, setUser] = useAtom(userAtom);

  const getCurrent = useCallback(async () => {
    try {
      const { data: responseUser }: { data: User } =
        await avogadorApi.get("/users/current");
      return responseUser;
    } catch {
      return null;
    }
  }, [avogadorApi]);

  const loginWithGoogle: (googleToken: string) => Promise<User> = useCallback(
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

  const getMicrosoftUserPicture: (microsoftToken: string) => Promise<string> =
    useCallback(async (microsoftToken) => {
      const { data }: { data: Blob } = await axios.get(
        `https://graph.microsoft.com/v1.0/me/photo/$value`,
        {
          headers: { Authorization: `Bearer ${microsoftToken}` },
          responseType: "blob",
        },
      );

      return URL.createObjectURL(data);
    }, []);

  const loginWithMicrosoft: (
    microsoftUserId: string,
    microsoftToken: string,
  ) => Promise<User> = useCallback(
    async (microsoftUserId, microsoftToken) => {
      return avogadorApi
        .post("/users/microsoft-auth", {
          microsoftUserId: microsoftUserId,
          microsoftToken: microsoftToken,
        })
        .then(
          async ({
            data: user,
          }: {
            data: User & { hash: string; picture: string };
          }) => {
            try {
              user.picture = await getMicrosoftUserPicture(microsoftToken);
            } catch (e) {
              user.picture = "";
            }

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
    [avogadorApi, getMicrosoftUserPicture],
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
    loginWithGoogle,
    loginWithMicrosoft,
    logout,
  };
};
