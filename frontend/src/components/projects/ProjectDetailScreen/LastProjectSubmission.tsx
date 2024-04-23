import { Box, Button, Chip, Divider, Grid, Typography } from "@mui/material";
import ButtonWithConfirmation from "@structure/ButtonWithConfirmation/ButtonWithConfirmation.tsx";
import { format } from "date-fns";
import { Description, Download, Terminal } from "@mui/icons-material";
import {
  ProjectStatus,
  ProjectSubmission,
} from "@components/projects/types.ts";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { useEffect, useState } from "react";
import { enqueueSnackbar } from "notistack";
import { saveResponseToFile } from "../../../utils/fileHandling.ts";
import { LoadingButton } from "@mui/lab";

const getSubmissionStatusBadge = (status: ProjectStatus) => {
  const getProps = (
    status: ProjectStatus,
  ): { label: string; color?: "info" | "success" | "error" | "warning" } => {
    switch (status) {
      case "CONFIRMED":
        return { label: "CONFIRMED", color: "info" };
      case "SUCCESS":
        return { label: "SUCCESS", color: "success" };
      case "ERROR":
        return { label: "ERROR", color: "error" };
      case "PENDING":
        return { label: "PENDING", color: "warning" };
      default:
        return {
          label: "Unexpected status",
        };
    }
  };
  const { label, color } = getProps(status);
  return <Chip sx={{ ml: 1 }} label={label} color={color ?? "secondary"} />;
};

interface LastProjectSubmissionProps {
  submission: ProjectSubmission;
}

const LastProjectSubmission = ({ submission }: LastProjectSubmissionProps) => {
  const { getSubmissionTree, downloadSubmissionArchive } = useProjectService();
  const [tree, setTree] = useState<string>();
  const [downloadingSubmission, setDownloadingSubmission] = useState<string>();

  const downloadSubmission = () => {
    setDownloadingSubmission("0%");
    downloadSubmissionArchive(submission, (progressEvent) => {
      if (progressEvent.total != null) {
        setDownloadingSubmission(
          `${Math.round(
            (100 * progressEvent.loaded) / progressEvent.total,
          ).toString()}%`,
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
        setDownloadingSubmission(undefined);
      });
  };

  useEffect(() => {
    getSubmissionTree(submission)
      .then((result) => result.text())
      .then((content) => {
        setTree(content);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [submission, getSubmissionTree]);

  return (
    <>
      <Typography variant={"h4"}>Your last submission</Typography>
      <Box sx={{ my: 2 }}>
        <Box display={"flex"} justifyContent={"space-between"}>
          <Typography variant={"h6"}>
            Status: {getSubmissionStatusBadge(submission.status)}
          </Typography>
          {submission.status === "SUCCESS" && (
            <ButtonWithConfirmation
              variant={"outlined"}
              title={"Confirm submission"}
              description={
                "If you confirm the submission, you won't be able to modify it anymore. This action is irreversible."
              }
              onConfirm={() => {
                // TODO: Confirm submission code
              }}
            >
              Confirm Submission
            </ButtonWithConfirmation>
          )}
        </Box>
        <Typography variant={"h6"} display={"inline"}>
          Submitted at:{" "}
        </Typography>
        <Typography display={"inline"}>
          {format(submission.timestamp, "dd/MM/yyyy HH:mm")}
        </Typography>
        <Grid container spacing={1} sx={{ mt: 0 }}>
          <Grid
            item
            xl={4}
            lg={6}
            xs={12}
            sx={{ display: "flex", justifyContent: "center" }}
          >
            <LoadingButton
              loading={downloadingSubmission != null}
              loadingIndicator={downloadingSubmission}
              variant={"outlined"}
              startIcon={<Download />}
              onClick={downloadSubmission}
            >
              Download ZIP
            </LoadingButton>
          </Grid>
          <Grid
            item
            xl={4}
            lg={6}
            xs={12}
            sx={{ display: "flex", justifyContent: "center" }}
          >
            <Button variant={"outlined"} startIcon={<Description />}>
              Output
            </Button>
          </Grid>
          <Grid
            item
            xl={4}
            xs={12}
            sx={{ display: "flex", justifyContent: "center" }}
          >
            <Button variant={"outlined"} startIcon={<Terminal />}>
              Execution Log
            </Button>
          </Grid>
        </Grid>
      </Box>
      <Divider sx={{ mb: 2 }} />
      <Typography variant={"h5"}>Structure</Typography>
      <Box overflow={"auto"}>
        <Typography
          fontFamily={"monospace"}
          sx={{
            whiteSpace: "pre",
            display: "inline",
          }}
        >
          {tree}
        </Typography>
      </Box>
    </>
  );
};

export default LastProjectSubmission;
