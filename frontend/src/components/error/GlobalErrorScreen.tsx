import { Box } from "@mui/material";
import { useAtom } from "jotai";
import { globalErrorAtom } from "@error/GlobalErrorState";
import GlobalErrorCard from "@error/errorCards/GlobalErrorCard";
import ErrorCard404 from "@error/errorCards/ErrorCard404";
import { ForbiddenError, ResourceNotFoundError } from "@error/types";
import ErrorCard403 from "@error/errorCards/ErrorCard403.tsx";

const getErrorCard = (error: Error) => {
  if (error instanceof ResourceNotFoundError) {
    return <ErrorCard404 error={error} />;
  }
  if (error instanceof ForbiddenError) {
    return <ErrorCard403 />;
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
