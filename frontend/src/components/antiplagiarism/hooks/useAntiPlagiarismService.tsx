import { useCallback } from "react";
import { useAvogadorApi } from "@hooks/useAvogadorApi.tsx";
import { Trial } from "@trials/types.ts";
import { enqueueSnackbar } from "notistack";
import { PlagiarismReport } from "@components/antiplagiarism/types.ts";

const useAntiPlagiarismService = () => {
  const avogadorApi = useAvogadorApi();

  const getPlagiarismReport: (exerciseId: string) => Promise<PlagiarismReport> =
    useCallback(
      async (exerciseId: string) => {
        const { data: report }: { data: PlagiarismReport } =
          await avogadorApi.get(`/exercises/${exerciseId}/similarity-report`);
        return report;
      },
      [avogadorApi],
    );

  const runAntiPlagiarismTool: (trial: Trial) => void = useCallback(
    (trial: Trial) => {
      avogadorApi
        .put(`/trials/${trial.id}/similarity`)
        .then(() => {
          enqueueSnackbar("Code similarity tool run scheduled", {
            variant: "info",
          });
        })
        .catch((err: Error) => {
          enqueueSnackbar(err.message, {
            variant: "error",
          });
        });
    },
    [avogadorApi],
  );

  return { runAntiPlagiarismTool, getPlagiarismReport };
};

export default useAntiPlagiarismService;
