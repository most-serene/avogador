import { Box, Grid } from "@mui/material";
import { useAtom } from "jotai";
import userAtom from "../authentication/userAtom";
import ApiKeyManager from "./ApiKeyManager/ApiKeyManager";
import Profile from "./Profile";

const ProfileScreen = () => {
  const [user] = useAtom(userAtom);
  const profilePicture = localStorage.getItem("profile-picture");

  return (
    <>
      <Grid container style={{ marginTop: "2rem" }} spacing={2}>
        <Grid item xs display="flex" justifyContent="center">
          <Box>
            <Profile user={user} profilePicture={profilePicture ?? undefined} />
          </Box>
        </Grid>

        <Grid item xs display="flex" justifyContent="center">
          <ApiKeyManager user={user} />
        </Grid>
      </Grid>
    </>
  );
};

export default ProfileScreen;
