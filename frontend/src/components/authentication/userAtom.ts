import { atom } from "jotai";
import { User } from "./types";

export default atom<User | null | undefined>(undefined);
