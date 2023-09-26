import { atom } from "jotai";
import { UserCourseDetail } from "@courses/types";

export const courseDetailAtom = atom<UserCourseDetail | undefined>(undefined);
