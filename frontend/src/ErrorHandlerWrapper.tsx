import { ReactNode } from "react";
import { useAtom } from "jotai";
import ServerError from "./components/error/ServerError.tsx";
import { useLocation } from "react-router-dom";
import { globalErrorAtom } from "./components/error/serverErrorState.tsx";

interface ErrorHandlerWrapperProps {
  children: ReactNode;
}

export default function ErrorHandlerWrapper({
  children,
}: ErrorHandlerWrapperProps) {
  const [globalError] = useAtom(globalErrorAtom);
  const { pathname } = useLocation();

  if (globalError && pathname != "/status") {
    return <ServerError />;
  }
  return <> {children} </>;
}
