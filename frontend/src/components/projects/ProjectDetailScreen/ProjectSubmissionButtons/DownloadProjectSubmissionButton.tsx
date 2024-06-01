import { CircularProgress } from "@mui/material";
import { Download } from "@mui/icons-material";
import { LoadingButton } from "@mui/lab";
import { saveResponseToFile } from "../../../../utils/fileHandling.ts";
import { enqueueSnackbar } from "notistack";
import { ProjectSubmission } from "@components/projects/types.ts";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { useState } from "react";

interface DownloadProjectSubmissionButtonProps {
  submission: ProjectSubmission;
}

const DownloadProjectSubmissionButton = ({
  submission,
}: DownloadProjectSubmissionButtonProps) => {
  const { downloadSubmissionArchive } = useProjectService();
  const [progress, setProgress] = useState<number>();

  const handleDownload = () => {
    setProgress(0);
    downloadSubmissionArchive(submission, (progressEvent) => {
      if (progressEvent.total != null) {
        setProgress(
          Math.round((100 * progressEvent.loaded) / progressEvent.total),
        );
      }
    })
      .then((res) => {
        saveResponseToFile(res, "submission.tar.gz");
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      })
      .finally(() => {
        setProgress(undefined);
      });
  };

  return (
    <LoadingButton
      sx={{ flex: "1 1 10rem", m: 0.5 }}
      loading={progress != null}
      loadingIndicator={
        <CircularProgress
          variant="determinate"
          value={progress}
          color="inherit"
          size={16}
        />
      }
      loadingPosition={"start"}
      variant={"outlined"}
      startIcon={<Download />}
      onClick={handleDownload}
    >
      Download ZIP
    </LoadingButton>
  );
};

export default DownloadProjectSubmissionButton;
