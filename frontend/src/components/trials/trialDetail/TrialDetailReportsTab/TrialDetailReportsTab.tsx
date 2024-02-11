import { Trial } from "@trials/types.ts";
import { Grid } from "@mui/material";
import AntiPlagiarismReportCard from "@components/antiplagiarism/AntiPlagiarismReportCard.tsx";

const TrialDetailReportsTab = ({ trial }: { trial: Trial }) => {
  return (
    <Grid container>
      <Grid item xs={4}></Grid>
      <Grid item xs={4}>
        <AntiPlagiarismReportCard trial={trial} />
      </Grid>
    </Grid>
  );
};

export default TrialDetailReportsTab;
