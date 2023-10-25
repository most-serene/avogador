import Grid from "@mui/material/Grid";
import KanbanColumn from "@courses/courseDetail/CourseTrialsTab/KanbanColumn.tsx";
import { useEffect, useMemo, useState } from "react";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { UserCourseDetail } from "@courses/types.ts";
import { isPractice, Trial } from "@trials/types.ts";
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
    const timeouts: NodeJS.Timeout[] = [];
    if (userCourse == null) {
      return;
    }
    getTrialsByCourseId(userCourse.id)
      .then((trials) => {
        setTrials(trials);

        trials
          .filter((trial) => isTrialScheduled(trial))
          .forEach((trial) => {
            timeouts.push(
              setTimeout(() => {
                setTrials([...trials]);
              }, trial.startTimestamp.getTime() - Date.now()),
            );
            if (isPractice(trial)) {
              timeouts.push(
                setTimeout(() => {
                  setTrials([...trials]);
                }, trial.deadline.getTime() - Date.now()),
              );
            }
          });

        trials
          .filter((trial) => isTrialOngoing(trial))
          .map((trial) => {
            if (isPractice(trial)) {
              return trial.deadline.getTime();
            }
          })
          .forEach((end) => {
            if (end == undefined) return;
            timeouts.push(
              setTimeout(() => {
                setTrials([...trials]);
              }, end - Date.now()),
            );
          });

        setIsLoading(false);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });

    return () => {
      timeouts.forEach((timeout) => {
        clearTimeout(timeout);
      });
    };
  }, [getTrialsByCourseId, isTrialOngoing, isTrialScheduled, userCourse]);

  const scheduledTrials = useMemo(() => {
    return trials
      .filter((trial) => isTrialScheduled(trial))
      .sort((a, b) => a.startTimestamp.getTime() - b.startTimestamp.getTime());
  }, [isTrialScheduled, trials]);

  const ongoingTrials = useMemo(() => {
    return trials
      .filter((trial) => isTrialOngoing(trial))
      .sort((a, b) => a.startTimestamp.getTime() - b.startTimestamp.getTime());
  }, [isTrialOngoing, trials]);

  const endedTrials = useMemo(() => {
    return trials
      .filter((trial) => isTrialEnded(trial))
      .sort((a, b) => b.startTimestamp.getTime() - a.startTimestamp.getTime());
  }, [isTrialEnded, trials]);

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
