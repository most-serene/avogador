import { UserCourseDetail } from "@courses/types.ts";
import { Card, CardContent, Grid } from "@mui/material";

interface CourseOverviewTabProps {
  course: UserCourseDetail | undefined;
}

const CourseOverviewStudentTab = ({ course }: CourseOverviewTabProps) => {
  console.log(course);
  return (
    <Grid height={"100%"} container spacing={2}>
      <Grid item xs={2}>
        <Card raised>
          <CardContent></CardContent>
        </Card>
      </Grid>
      <Grid item xs={10}>
        <Card raised>
          <CardContent></CardContent>
        </Card>
      </Grid>
    </Grid>
  );
};

export default CourseOverviewStudentTab;
