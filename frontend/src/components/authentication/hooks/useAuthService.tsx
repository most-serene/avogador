import { User } from "../types";
import { useAtom } from "jotai";
import userAtom from "../userAtom";
import { useAvogadorApi } from "../../../hooks/useAvogadorApi";

export const useAuthService = () => {
  const [user, setUser] = useAtom(userAtom);
  const avogadorApi = useAvogadorApi();

  const getCurrent = async () => {
    try {
      const { data: responseUser }: { data: User } = await avogadorApi.get(
        "/users/current",
      );
      setUser(responseUser);
      const storedCSRF = localStorage.getItem("Jwt-CSRF-Hash");
      if (storedCSRF !== null) {
        avogadorApi.defaults.headers.common["Jwt-CSRF-Hash"] = storedCSRF;
      }
      return responseUser;
    } catch {
      console.log("not logged");
      setUser(null);
      return null;
    }
  };

  const login: (googleToken: string) => Promise<User> = async (
    googleToken: string,
  ) => {
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
          console.log(user);
          avogadorApi.defaults.headers.common["Jwt-CSRF-Hash"] = user.hash;
          localStorage.setItem("Jwt-CSRF-Hash", user.hash);
          localStorage.setItem("picture", user.picture);
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
  };

  const logout = () => {
    avogadorApi
      .get("/users/logout")
      .then((res) => {
        console.log(res);
        console.log(user);
        setUser(null);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  return {
    getCurrent,
    login,
    logout,
  };
};
