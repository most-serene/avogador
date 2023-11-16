import { Tab, Tabs } from "@mui/material";
import SubmissionViewer from "@components/submissions/UserSubmissionsScreen/SubmissionViewer.tsx";
import { SyntheticEvent, useState } from "react";
import { Strox, Submission } from "@exercises/types.ts";
import Box from "@mui/material/Box";
import SubmissionDetailIOTab from "@components/submissions/UserSubmissionsScreen/SubmissionDetailIOTab.tsx";

interface SubmissionDetailProps {
  submission: Submission;
  language: string;
  template: Strox;
}

const SubmissionDetail = ({
  submission,
  language,
  template,
}: SubmissionDetailProps) => {
  const [openTab, setOpenTab] = useState<number>(0);

  const handleTabChange = (event: SyntheticEvent, newValue: number) => {
    event.preventDefault();

    setOpenTab(newValue);
  };

  return (
    <Box height="100%" overflow="scroll" className="hidden-scrollbar">
      <Tabs centered value={openTab} onChange={handleTabChange}>
        <Tab label="Code" />
        <Tab label="I/O" />
      </Tabs>
      {openTab === 0 && (
        <SubmissionViewer
          template={template}
          submissionCode={submission.stroxCells}
          language={language}
        />
      )}
      {openTab === 1 && (
        <SubmissionDetailIOTab
          exerciseId={submission.exerciseId}
          submissionId={submission.id}
        />
      )}
    </Box>
  );
};

export default SubmissionDetail;
