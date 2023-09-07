import { atom } from "jotai";
import { User } from "@authentication/types";

export default atom<User | null | undefined>(undefined);
