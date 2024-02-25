import { Trial } from "@trials/types.ts";
import { Grid } from "@mui/material";
import AntiPlagiarismReportCard from "@components/antiplagiarism/AntiPlagiarismReportCard.tsx";
import { Course } from "@courses/types.ts";

const TrialDetailReportsTab = ({
  trial,
  course,
}: {
  trial: Trial;
  course: Course;
}) => {
  return (
    <Grid container>
      <Grid item xs={4}></Grid>
      <Grid item xs={4}>
        <AntiPlagiarismReportCard trial={trial} course={course} />
      </Grid>
    </Grid>
  );
};

export default TrialDetailReportsTab;
