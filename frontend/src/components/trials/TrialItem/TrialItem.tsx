import { Card, CardContent, Typography } from "@mui/material";
import { isExam, isPractice, Trial } from "@trials/types.ts";
import PracticeItem from "@trials/TrialItem/PracticeItem.tsx";
import ExamItem from "@trials/TrialItem/ExamItem.tsx";
import ContextMenuWrapper from "@structure/ContextMenuWrapper/ContextMenuWrapper.tsx";
import MenuItem from "@mui/material/MenuItem";
import { enqueueSnackbar } from "notistack";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { useEffect, useState } from "react";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { CourseDetail } from "@courses/types.ts";
import Box from "@mui/material/Box";

interface TrialItemProps {
  trial: Trial;
}

const TrialItem = ({ trial }: TrialItemProps) => {
  const { updatePractice } = useTrialService();
  const { getCourseById } = useCourseService();
  const [user] = useAtom(userAtom);
  const [course, setCourse] = useState<CourseDetail>();
  const [trialState, setTrialState] = useState(trial);

  useEffect(() => {
    getCourseById(trial.courseId)
      .then((courseResponse) => {
        setCourse(courseResponse);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [getCourseById, trial.courseId]);

  if (
    isPractice(trialState) &&
    (course?.role === "COLLABORATOR" ||
      course?.role === "ADMIN" ||
      (user && user.isSuperuser))
  ) {
    return (
      <ContextMenuWrapper
        menu={
          <Box>
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
          </Box>
        }
      >
        <PracticeItem practice={trialState} />
      </ContextMenuWrapper>
    );
  }
  if (isPractice(trialState)) {
    return <PracticeItem practice={trialState} />;
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
