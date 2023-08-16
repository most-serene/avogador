import { Grid } from "@mui/material";
import LoginGoogle from "../LoginGoogle/LoginGoogle";

export const LoginPage = () => {
  return (
    <Grid container style={{ marginTop: "2rem" }}>
      <Grid item xs display="flex" justifyContent="center" alignItems="center">
        <LoginGoogle />
      </Grid>
    </Grid>
  );
};
