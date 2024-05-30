import {
  Box,
  Button,
  Chip,
  CircularProgress,
  Divider,
  Grid,
  Typography,
} from "@mui/material";
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
import ProjectSubmissionLogModal from "@components/projects/ProjectSubmissionLogModal/ProjectSubmissionLogModal.tsx";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { CourseDetail } from "@courses/types.ts";

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
  onConfirm: (submission: ProjectSubmission) => void;
  course: CourseDetail;
}

const LastProjectSubmission = ({
  submission,
  onConfirm,
  course,
}: LastProjectSubmissionProps) => {
  const {
    getSubmissionTree,
    downloadSubmissionArchive,
    confirmSubmission,
    downloadOutputFile,
    unconfirmSubmission,
  } = useProjectService();
  const [tree, setTree] = useState<string>();
  const [downloadingSubmission, setDownloadingSubmission] = useState<number>();
  const [downloadOutputProgress, setDownloadOutputProgress] =
    useState<number>();
  const [isLogModalOpen, setIsLogModalOpen] = useState(false);
  const [user] = useAtom(userAtom);

  const downloadSubmission = () => {
    setDownloadingSubmission(0);
    downloadSubmissionArchive(submission, (progressEvent) => {
      if (progressEvent.total != null) {
        setDownloadingSubmission(
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
        setDownloadingSubmission(undefined);
      });
  };

  useEffect(() => {
    getSubmissionTree(submission)
      .then((result) => result.text())
      .then((content) => {
        setTree(content);
      })
      .catch(() => {
        setTree(undefined);
      });
  }, [submission, getSubmissionTree]);

  const handleConfirm = () => {
    confirmSubmission(submission)
      .then(onConfirm)
      .then(() => {
        enqueueSnackbar("Submission confirmed successfully", {
          variant: "success",
        });
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  };

  const handleUnConfirm = () => {
    unconfirmSubmission(submission.project.id, submission.id)
      .then(onConfirm)
      .then(() => {
        enqueueSnackbar("Submission unconfirmed successfully", {
          variant: "success",
        });
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  };

  const handleOutputDownload = () => {
    downloadOutputFile(submission, (progressEvent) => {
      if (progressEvent.total != null) {
        setDownloadOutputProgress(
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
        setDownloadOutputProgress(undefined);
      });
  };

  return (
    <>
      <Typography variant={"h4"}>Your last submission</Typography>
      <Box sx={{ my: 2 }}>
        <Box display={"flex"} justifyContent={"space-between"}>
          <Typography variant={"h6"}>
            Status: {getSubmissionStatusBadge(submission.status)}
          </Typography>
          {submission.status !== "CONFIRMED" && (
            <ButtonWithConfirmation
              disabled={submission.status !== "SUCCESS"}
              variant={"outlined"}
              title={"Confirm submission"}
              description={
                "If you confirm the submission, you won't be able to modify it anymore. This action is irreversible."
              }
              onConfirm={handleConfirm}
            >
              Confirm Submission
            </ButtonWithConfirmation>
          )}
          {(course.role === "COLLABORATOR" ||
            course.role === "ADMIN" ||
            user?.isSuperuser === true) &&
            submission.status === "CONFIRMED" && (
              <Button variant={"outlined"} onClick={handleUnConfirm}>
                unconfirm
              </Button>
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
              loadingIndicator={
                <CircularProgress
                  variant="determinate"
                  value={downloadingSubmission}
                  color="inherit"
                  size={16}
                />
              }
              loadingPosition={"start"}
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
            <LoadingButton
              loading={downloadOutputProgress != null}
              loadingIndicator={
                <CircularProgress
                  variant="determinate"
                  value={downloadOutputProgress}
                  color="inherit"
                  size={16}
                />
              }
              disabled={
                submission.status !== "SUCCESS" &&
                submission.status !== "CONFIRMED"
              }
              loadingPosition={"start"}
              variant={"outlined"}
              startIcon={<Description />}
              onClick={handleOutputDownload}
            >
              Output
            </LoadingButton>
          </Grid>
          <Grid
            item
            xl={4}
            xs={12}
            sx={{ display: "flex", justifyContent: "center" }}
          >
            <Button
              variant={"outlined"}
              startIcon={<Terminal />}
              disabled={submission.status === "PENDING"}
              onClick={() => {
                setIsLogModalOpen(true);
              }}
            >
              Execution Log
            </Button>
            <ProjectSubmissionLogModal
              submission={submission}
              open={isLogModalOpen}
              onClose={() => {
                setIsLogModalOpen(false);
              }}
            />
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
