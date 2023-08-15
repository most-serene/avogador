import CoursesPreview from "./CoursesPreview.tsx";
import Grid from "@mui/material/Grid";
import DeadlineStack from "./DeadlineStack.tsx";

export default function HomeScreen() {
  return (
    <>
      <Grid container spacing={2} style={{ height: "100%" }}>
        <Grid item xs={9}>
          <CoursesPreview />
        </Grid>

        <Grid item xs={3}>
          <DeadlineStack />
        </Grid>
      </Grid>
    </>
  );
}
