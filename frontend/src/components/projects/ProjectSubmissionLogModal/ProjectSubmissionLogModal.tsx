import { ProjectSubmission } from "@components/projects/types.ts";
import { Button, Card, CardContent, IconButton, Modal } from "@mui/material";
import Box from "@mui/material/Box";
import { useEffect, useState } from "react";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { enqueueSnackbar } from "notistack";
import Ansi from "ansi-to-react";
import { saveAs } from "file-saver";
import { Close, Download } from "@mui/icons-material";

const style = {
  position: "absolute" as const,
  top: "50%",
  left: "50%",
  height: "90%",
  transform: "translate(-50%, -50%)",
  width: { md: "75%", xs: "90%" },
};

interface ProjectSubmissionLogModalProps {
  submission: ProjectSubmission;
  open: boolean;
  onClose: () => void;
}

const ProjectSubmissionLogModal = ({
  submission,
  open,
  onClose: handleClose,
}: ProjectSubmissionLogModalProps) => {
  const { getSubmissionExecutionLog } = useProjectService();
  const [log, setLog] = useState<string>();

  useEffect(() => {
    getSubmissionExecutionLog(submission)
      .then((result) => result.text())
      .then((content) => {
        setLog(content);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [getSubmissionExecutionLog, submission]);

  const handleDownload = () => {
    if (log == null) return;
    const file = new Blob([log], { type: "text/plain" });
    saveAs(file, `exec-${submission.id}.log`);
  };

  return (
    <Modal open={open} onClose={handleClose}>
      <Box sx={style}>
        <Card
          sx={{
            height: "100%",
          }}
        >
          <CardContent
            className="hidden-scrollbar"
            sx={{
              position: "relative",
              height: "100%",
              overflowY: "auto",
            }}
          >
            <IconButton
              onClick={handleClose}
              color={"secondary"}
              sx={{ position: "fixed", top: 0, right: 0 }}
            >
              <Close />
            </IconButton>
            <div style={{ whiteSpace: "pre-line" }}>
              <Ansi>{log ?? ""}</Ansi>
            </div>
            <Box display="flex" justifyContent="center">
              <Button
                variant={"outlined"}
                startIcon={<Download />}
                onClick={handleDownload}
              >
                Download
              </Button>
            </Box>
          </CardContent>
        </Card>
      </Box>
    </Modal>
  );
};

export default ProjectSubmissionLogModal;
