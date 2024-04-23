import { UserCourseDetail } from "@courses/types.ts";
import { Card, CardContent, Grid } from "@mui/material";
import UserTrialProgressChart from "@components/analytics/UserTrialProgress/UserTrialProgressChart.tsx";

interface CourseOverviewTabProps {
  course: UserCourseDetail | undefined;
}

const CourseOverviewStudentTab = ({ course }: CourseOverviewTabProps) => {
  return (
    <Grid height={"100%"} container spacing={2}>
      <Grid item xs={12}>
        <Card raised>
          <CardContent>
            <UserTrialProgressChart course={course} />
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
};

export default CourseOverviewStudentTab;
