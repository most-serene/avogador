import { ReactNode, useEffect, useState } from "react";
import { useAtom } from "jotai";
import GlobalErrorScreen from "./components/error/GlobalErrorScreen.tsx";
import { useLocation } from "react-router-dom";
import { globalErrorAtom } from "./components/error/GlobalErrorState.tsx";

interface ErrorHandlerWrapperProps {
  children: ReactNode;
}

export default function ErrorHandlerWrapper({
  children,
}: ErrorHandlerWrapperProps) {
  const [globalError, setGlobalError] = useAtom(globalErrorAtom);
  const location = useLocation();
  const [previousLocation, setPreviousLocation] = useState(location);

  useEffect(() => {
    console.log(previousLocation);
    console.log(location);

    if (previousLocation.key !== location.key) {
      setGlobalError(undefined);
      setPreviousLocation(location);
    }
  }, [previousLocation, setPreviousLocation, location, setGlobalError]);

  if (globalError && location.pathname != "/status") {
    return <GlobalErrorScreen />;
  }
  return <> {children} </>;
}
