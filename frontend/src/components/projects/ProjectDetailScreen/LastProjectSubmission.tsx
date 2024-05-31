import { Box, Button, Divider, Typography } from "@mui/material";
import ButtonWithConfirmation from "@structure/ButtonWithConfirmation/ButtonWithConfirmation.tsx";
import { format } from "date-fns";
import { ProjectSubmission } from "@components/projects/types.ts";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { useEffect, useState } from "react";
import { enqueueSnackbar } from "notistack";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { CourseDetail } from "@courses/types.ts";
import ProjectSubmissionButtons from "@components/projects/ProjectDetailScreen/ProjectSubmissionButtons/ProjectSubmissionButtons.tsx";
import ProjectSubmissionStatus from "@components/projects/ProjectDetailScreen/ProjectSubmissionStatus.tsx";

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
  const { getSubmissionTree, confirmSubmission, unconfirmSubmission } =
    useProjectService();
  const [tree, setTree] = useState<string>();
  const [user] = useAtom(userAtom);

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

  return (
    <>
      <Typography variant={"h4"}>Your last submission</Typography>
      <Box sx={{ my: 2 }}>
        <Box display={"flex"} justifyContent={"space-between"}>
          <ProjectSubmissionStatus submission={submission} />
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

        <ProjectSubmissionButtons submission={submission} />
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
