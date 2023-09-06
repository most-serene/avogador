import { atom, useAtom } from "jotai";

export const globalErrorAtom = atom<Error | undefined>(undefined);

export const useGlobalErrorSetter = () => {
  const [, setGlobalError] = useAtom(globalErrorAtom);
  return (error: Error) => {
    setGlobalError(error);
  };
};
