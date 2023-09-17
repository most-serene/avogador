import { atom, useAtom } from "jotai";
import { useCallback } from "react";

export const globalErrorAtom = atom<Error | undefined>(undefined);

export const useGlobalErrorSetter = () => {
  const [, setGlobalError] = useAtom(globalErrorAtom);
  return useCallback(
    (error: Error) => {
      setGlobalError(error);
    },
    [setGlobalError],
  );
};
