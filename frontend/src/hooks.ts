import { useContext } from "react";
import { UserContext } from "./App.tsx";

export const useUser = () => {
  return useContext(UserContext);
};
