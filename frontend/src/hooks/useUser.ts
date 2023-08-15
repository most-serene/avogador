import { useContext } from "react";
import { UserContext } from "../App.tsx";

export default () => {
  return useContext(UserContext);
};
