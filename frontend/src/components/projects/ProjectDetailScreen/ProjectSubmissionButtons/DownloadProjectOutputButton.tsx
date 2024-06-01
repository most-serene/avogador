import { CircularProgress } from "@mui/material";
import { Description } from "@mui/icons-material";
import { LoadingButton } from "@mui/lab";
import { useState } from "react";
import { saveResponseToFile } from "../../../../utils/fileHandling.ts";
import { enqueueSnackbar } from "notistack";
import { ProjectSubmission } from "@components/projects/types.ts";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";

interface DownloadProjectOutputButtonProps {
  submission: ProjectSubmission;
}

const DownloadProjectOutputButton = ({
  submission,
}: DownloadProjectOutputButtonProps) => {
  const { downloadOutputFile } = useProjectService();

  const [progress, setProgress] = useState<number>();

  const handleDownload = () => {
    downloadOutputFile(submission, (progressEvent) => {
      if (progressEvent.total != null) {
        setProgress(
          Math.round((100 * progressEvent.loaded) / progressEvent.total),
        );
      }
    })
      .then((res) => {
        saveResponseToFile(res, `output.html`);
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
      disabled={
        submission.status !== "SUCCESS" && submission.status !== "CONFIRMED"
      }
      loadingPosition={"start"}
      variant={"outlined"}
      startIcon={<Description />}
      onClick={handleDownload}
    >
      Download Output
    </LoadingButton>
  );
};

export default DownloadProjectOutputButton;
