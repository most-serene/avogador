import Grid from "@mui/material/Grid";
import KanbanColumn from "@courses/courseDetail/CourseTrialsTab/KanbanColumn.tsx";
import { useEffect, useState } from "react";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { UserCourseDetail } from "@courses/types.ts";
import { Trial } from "@trials/types.ts";
import { enqueueSnackbar } from "notistack";

interface CourseTrialsTabProps {
  userCourse: UserCourseDetail | undefined;
}

const CourseTrialsTab = ({ userCourse }: CourseTrialsTabProps) => {
  const {
    getTrialsByCourseId,
    isTrialScheduled,
    isTrialEnded,
    isTrialOngoing,
  } = useTrialService();
  const [trials, setTrials] = useState<Trial[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (userCourse == null) {
      return;
    }
    getTrialsByCourseId(userCourse.id)
      .then((trials) => {
        setTrials(trials);
        setIsLoading(false);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.name, { variant: "error" });
      });
  }, [getTrialsByCourseId, userCourse]);

  const scheduledTrials = trials
    .filter((trial) => isTrialScheduled(trial))
    .sort((a, b) => a.startTimestamp.getTime() - b.startTimestamp.getTime());

  const ongoingTrials = trials
    .filter((trial) => isTrialOngoing(trial))
    .sort((a, b) => a.startTimestamp.getTime() - b.startTimestamp.getTime());

  const endedTrials = trials
    .filter((trial) => isTrialEnded(trial))
    .sort((a, b) => b.startTimestamp.getTime() - a.startTimestamp.getTime());

  return (
    <Grid container height="100%" width={"100%"} spacing={2}>
      <Grid item height="100%" xs={4}>
        <KanbanColumn
          title="Scheduled"
          trials={scheduledTrials}
          isLoading={isLoading}
          hasCreateButton={
            userCourse?.role === "COLLABORATOR" || userCourse?.role === "ADMIN"
          }
        />
      </Grid>
      <Grid item height="100%" xs={4}>
        <KanbanColumn
          title="In progress"
          trials={ongoingTrials}
          isLoading={isLoading}
        />
      </Grid>
      <Grid item height="100%" xs={4}>
        <KanbanColumn
          title="Completed"
          trials={endedTrials}
          isLoading={isLoading}
        />
      </Grid>
    </Grid>
  );
};

export default CourseTrialsTab;
