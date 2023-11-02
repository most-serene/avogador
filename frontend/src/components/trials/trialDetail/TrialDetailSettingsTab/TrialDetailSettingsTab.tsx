import { isExam, isPractice, Trial } from "@trials/types.ts";
import { Typography } from "@mui/material";
import PracticeSettings from "@trials/trialDetail/TrialDetailSettingsTab/PracticeSettings.tsx";

interface TrialDetailSettingsTabProps {
  trial: Trial;
}

const TrialDetailSettingsTab = ({ trial }: TrialDetailSettingsTabProps) => {
  if (isPractice(trial)) {
    return <PracticeSettings practice={trial} />;
  }
  if (isExam(trial)) {
    return <Typography>Not implemented</Typography>;
  }
};

export default TrialDetailSettingsTab;
