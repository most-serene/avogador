import { UserCourseDetail } from "@courses/types.ts";
import { Card, CardContent, Grid } from "@mui/material";
import UserTrialProgressScreen from "@components/analytics/UserTrialProgress/UserTrialProgressScreen.tsx";

interface CourseOverviewTabProps {
  course: UserCourseDetail | undefined;
}

const CourseOverviewStudentTab = ({ course }: CourseOverviewTabProps) => {
  return (
    <Grid height={"100%"} container spacing={2}>
      <Grid item xs={12}>
        <Card raised>
          <CardContent>
            <UserTrialProgressScreen course={course} />
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
};

export default CourseOverviewStudentTab;
