import ProjectSubmissionLogModal from "@components/projects/ProjectSubmissionLogModal/ProjectSubmissionLogModal.tsx";
import { Button } from "@mui/material";
import { Terminal } from "@mui/icons-material";
import { useState } from "react";
import { ProjectSubmission } from "@components/projects/types.ts";

interface ToggleProjectSubmissionLogButtonProps {
  submission: ProjectSubmission;
}
const ToggleProjectSubmissionLogButton = ({
  submission,
}: ToggleProjectSubmissionLogButtonProps) => {
  const [isLogModalOpen, setIsLogModalOpen] = useState(false);

  return (
    <>
      <Button
        sx={{ flex: "1 1 10rem", m: 0.5 }}
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
    </>
  );
};

export default ToggleProjectSubmissionLogButton;
