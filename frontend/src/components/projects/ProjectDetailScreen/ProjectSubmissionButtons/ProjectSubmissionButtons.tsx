import DownloadProjectSubmissionButton from "@components/projects/ProjectDetailScreen/ProjectSubmissionButtons/DownloadProjectSubmissionButton.tsx";
import DownloadProjectOutputButton from "@components/projects/ProjectDetailScreen/ProjectSubmissionButtons/DownloadProjectOutputButton.tsx";
import Box from "@mui/material/Box";
import ToggleProjectSubmissionLogButton from "@components/projects/ProjectDetailScreen/ProjectSubmissionButtons/ToggleProjectSubmissionLogButton.tsx";
import { ProjectSubmission } from "@components/projects/types.ts";

interface ProjectSubmissionButtonsProps {
  submission: ProjectSubmission;
}

const ProjectSubmissionButtons = ({
  submission,
}: ProjectSubmissionButtonsProps) => {
  return (
    <Box
      sx={{
        display: "flex",
        flexWrap: "wrap",
      }}
    >
      <DownloadProjectOutputButton submission={submission} />
      <DownloadProjectSubmissionButton submission={submission} />
      <ToggleProjectSubmissionLogButton submission={submission} />
    </Box>
  );
};

export default ProjectSubmissionButtons;
