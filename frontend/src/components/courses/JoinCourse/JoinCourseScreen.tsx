import { Grid } from "@mui/material";
import JoinCourse from "./JoinCourse";

const JoinCourseScreen = () => {
  return (
    <>
      <Grid container style={{ marginTop: "2rem" }}>
        <Grid
          item
          xs
          display="flex"
          justifyContent="center"
          alignItems="center"
        >
          <JoinCourse />
        </Grid>
      </Grid>
    </>
  );
};

export default JoinCourseScreen;
