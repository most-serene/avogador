import CoursesPreview from "./CoursesPreview.tsx";
import Grid from "@mui/material/Unstable_Grid2";
import DeadlineStack from "./DeadlineStack.tsx";

export default function HomeScreen() {
  return (
    <>
      <Grid container spacing={2} style={{ height: "100%" }}>
        <Grid xs={9}>
          <CoursesPreview />
        </Grid>

        <Grid xs={3}>
          <DeadlineStack />
        </Grid>
      </Grid>
    </>
  );
}
