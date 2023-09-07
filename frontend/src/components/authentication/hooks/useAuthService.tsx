import { User } from "@authentication/types";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom";
import { useAvogadorApi } from "@hooks/useAvogadorApi";
import { useCallback } from "react";

export const useAuthService = () => {
  const [, setUser] = useAtom(userAtom);
  const avogadorApi = useAvogadorApi();

  const getCurrent = useCallback(async () => {
    try {
      const { data: responseUser }: { data: User } = await avogadorApi.get(
        "/users/current",
      );
      setUser(responseUser);
      return responseUser;
    } catch {
      setUser(null);
      return null;
    }
  }, [avogadorApi, setUser]);

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
            setUser(u);
            return u;
          },
        );
    },
    [avogadorApi, setUser],
  );

  const logout = useCallback(() => {
    avogadorApi
      .get("/users/logout")
      .then(() => {
        setUser(null);
        localStorage.removeItem("profile-picture");
      })
      .catch((err) => {
        console.log(err);
      });
  }, [avogadorApi, setUser]);

  return {
    getCurrent,
    login,
    logout,
  };
};
