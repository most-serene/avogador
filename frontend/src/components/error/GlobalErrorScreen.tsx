import { Box } from "@mui/material";
import { useAtom } from "jotai";
import { globalErrorAtom } from "@error/GlobalErrorState";
import GlobalErrorCard from "@error/errorCards/GlobalErrorCard";
import ErrorCard404 from "@error/errorCards/ErrorCard404";
import {
  ArchivedCourseError,
  ForbiddenError,
  ResourceNotFoundError,
} from "@error/types";
import ErrorCard403 from "@error/errorCards/ErrorCard403.tsx";
import ArchivedCourseErrorCard from "./errorCards/ArchivedCourseErrorCard";

const getErrorCard = (error: Error) => {
  if (error instanceof ResourceNotFoundError) {
    return <ErrorCard404 error={error} />;
  }
  if (error instanceof ForbiddenError) {
    return <ErrorCard403 />;
  }
  if (error instanceof ArchivedCourseError) {
    return <ArchivedCourseErrorCard />;
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
