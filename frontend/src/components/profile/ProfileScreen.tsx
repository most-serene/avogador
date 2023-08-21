import { Grid } from "@mui/material";
import { useAtom } from "jotai";
import userAtom from "../authentication/userAtom";
import Profile from "./Profile";

const ProfileScreen = () => {
  const [user] = useAtom(userAtom);
  const profilePicture = localStorage.getItem("profile-picture");

  return (
    <>
      <Grid container style={{ marginTop: "2rem" }} spacing={2}>
        <Grid item xs display="flex" justifyContent="center">
          <Profile user={user} profilePicture={profilePicture ?? undefined} />
        </Grid>

        <Grid item xs display="flex" justifyContent="center">
          API Key manager
        </Grid>
      </Grid>
    </>
  );
};

export default ProfileScreen;
