import { atom } from "jotai";
import { ApiKey } from "./types";

export const userKeysAtom = atom<ApiKey[]>([]);
