import { Box } from "@mui/material";
import { useAtom } from "jotai";
import { globalErrorAtom } from "@error/GlobalErrorState";
import GlobalErrorCard from "./errorCards/GlobalErrorCard";
import ErrorCard404 from "./errorCards/ErrorCard404";
import { ResourceNotFoundError } from "./types";

const getErrorCard = (error: Error) => {
  if (error instanceof ResourceNotFoundError) {
    return <ErrorCard404 error={error} />;
  }

  return <GlobalErrorCard error={error} />;
};

const GlobalErrorScreen = () => {
  const [globalError] = useAtom(globalErrorAtom);

  if (globalError === undefined) return <> </>;

  return (
    <Box display={"flex"} justifyContent={"center"} marginTop={"2rem"}>
      {getErrorCard(globalError)}
    </Box>
  );
};

export default GlobalErrorScreen;
