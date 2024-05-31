import {
  ProjectStatus,
  ProjectSubmission,
} from "@components/projects/types.ts";
import { Chip, Typography } from "@mui/material";

interface ProjectSubmissionStatusProps {
  submission: ProjectSubmission;
}

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

const ProjectSubmissionStatus = ({
  submission,
}: ProjectSubmissionStatusProps) => {
  return (
    <Typography variant={"h6"}>
      Status: {getSubmissionStatusBadge(submission.status)}
    </Typography>
  );
};

export default ProjectSubmissionStatus;
