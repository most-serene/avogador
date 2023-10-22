import { Card, CardContent, Typography } from "@mui/material";
import { isExam, isPractice, Trial } from "@trials/types.ts";
import PracticeItem from "@trials/TrialItem/PracticeItem.tsx";
import ExamItem from "@trials/TrialItem/ExamItem.tsx";
import ContextMenuWrapper from "@structure/ContextMenuWrapper/ContextMenuWrapper.tsx";
import MenuItem from "@mui/material/MenuItem";
import { enqueueSnackbar } from "notistack";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { useState } from "react";

interface TrialItemProps {
  trial: Trial;
}

const TrialItem = ({ trial }: TrialItemProps) => {
  const { updatePractice } = useTrialService();
  const [trialState, setTrialState] = useState(trial);

  if (isPractice(trialState)) {
    return (
      <ContextMenuWrapper
        menu={
          <>
            <MenuItem
              disabled={trialState.isVisible}
              onClick={() => {
                updatePractice({ ...trialState, isVisible: true })
                  .then((updatedTrial) => {
                    setTrialState(updatedTrial);
                  })
                  .catch((err: Error) => {
                    enqueueSnackbar(err.message, { variant: "error" });
                  });
              }}
            >
              Turn visible
            </MenuItem>
            <MenuItem
              disabled={!trialState.isVisible}
              onClick={() => {
                updatePractice({ ...trialState, isVisible: false })
                  .then((updatedTrial) => {
                    setTrialState(updatedTrial);
                  })
                  .catch((err: Error) => {
                    enqueueSnackbar(err.message, { variant: "error" });
                  });
              }}
            >
              Turn hidden
            </MenuItem>
          </>
        }
      >
        <PracticeItem practice={trialState} />
      </ContextMenuWrapper>
    );
  }
  if (isExam(trialState)) {
    return <ExamItem exam={trialState} />;
  }

  return (
    <Card>
      <CardContent>
        <Typography variant="h4">Unknown trial type</Typography>
      </CardContent>
    </Card>
  );
};

export default TrialItem;
