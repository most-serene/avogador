import { Grid } from "@mui/material";

const ProfileScreen = () => {
  return (
    <>
      <Grid container style={{ marginTop: "2rem" }} spacing={2}>
        <Grid item xs display="flex" justifyContent="center">
          profile card
        </Grid>

        <Grid item xs display="flex" justifyContent="center">
          API Key manager
        </Grid>
      </Grid>
    </>
  );
};

export default ProfileScreen;
