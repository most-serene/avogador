import Grid from "@mui/material/Grid";
import KanbanColumn from "@courses/courseDetail/CourseTrialsTab/KanbanColumn.tsx";
import { useEffect, useState } from "react";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { UserCourseDetail } from "@courses/types.ts";
import { Exam, isExam, isPractice, Practice, Trial } from "@trials/types.ts";
import { addMinutes } from "date-fns";

interface CourseTrialsTabProps {
  userCourse: UserCourseDetail | undefined;
}

const isTrialScheduled = (trial: Trial) => {
  return trial.startTimestamp > new Date();
};

const isTrialOngoing = (trial: Trial) => {
  return (
    !isTrialScheduled(trial) &&
    ((isPractice(trial) && isPracticeOngoing(trial)) ||
      (isExam(trial) && isExamOngoing(trial)))
  );
};

const isExamOngoing = (exam: Exam) => {
  return (
    addMinutes(exam.startTimestamp, exam.duration + exam.extraTime) > new Date()
  );
};

const isPracticeOngoing = (practice: Practice) => {
  return practice.deadline > new Date();
};

const isTrialEnded = (trial: Trial) => {
  return !isTrialScheduled(trial) && !isTrialOngoing(trial);
};

const CourseTrialsTab = ({ userCourse }: CourseTrialsTabProps) => {
  const { getTrialsByCourseId } = useTrialService();
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
      .catch((err) => {
        console.error(err);
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
