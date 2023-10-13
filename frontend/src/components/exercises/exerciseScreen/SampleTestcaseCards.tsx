import { Testcase } from "@exercises/types.ts";
import { Typography } from "@mui/material";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import CopiableCard from "@structure/CopiableCard/CopiableCard.tsx";

interface SampleTestcaseCardsProps {
  testcase: Testcase;
}

const SampleTestcaseCards = ({ testcase }: SampleTestcaseCardsProps) => {
  return (
    <Box marginTop={2}>
      <Typography variant="h5">Sample Testcase {testcase.index}</Typography>
      <Grid container spacing={2}>
        <Grid item xs={6}>
          <CopiableCard fontFamily="monospace">{testcase.input}</CopiableCard>
        </Grid>
        <Grid item xs={6}>
          <CopiableCard fontFamily="monospace">{testcase.input}</CopiableCard>
        </Grid>
      </Grid>
    </Box>
  );
};

export default SampleTestcaseCards;
