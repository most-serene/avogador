import { useContext } from "react";
import { UserContext } from "../App.tsx";

export default () => {
  const { user, setUser } = useContext(UserContext);
  return {
    user,
    setUser,
  };
};
