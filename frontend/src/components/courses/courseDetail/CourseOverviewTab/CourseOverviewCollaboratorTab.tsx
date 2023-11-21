import JoinCourseLinkCard from "@courses/courseDetail/CourseOverviewTab/JoinCourseLinkCard.tsx";
import { UserCourseDetail } from "@courses/types.ts";
import { CircularProgress, Grid } from "@mui/material";
import ExerciseResultsCard from "@components/analytics/ExerciseResultsChart/ExerciseResultsCard.tsx";

interface CourseOverviewTabProps {
  course: UserCourseDetail | undefined;
}

const CourseOverviewCollaboratorTab = ({ course }: CourseOverviewTabProps) => {
  if (course == undefined) {
    return <CircularProgress size={80} />;
  }

  return (
    <Grid container spacing={2}>
      <Grid item xs={12} md={4}>
        <ExerciseResultsCard courseId={course.id} />
      </Grid>
      <Grid item xs={12} md={4}>
        <JoinCourseLinkCard course={course} />
      </Grid>
    </Grid>
  );
};

export default CourseOverviewCollaboratorTab;
