import { Box, Container, Grid } from "@mui/material";
import { useAtom } from "jotai";
import userAtom from "@components/authentication/userAtom";
import ApiKeyManager from "@profile/ApiKeyManager/ApiKeyManager";
import Profile from "@profile/Profile";

const ProfileScreen = () => {
  const [user] = useAtom(userAtom);
  const profilePicture = localStorage.getItem("profile-picture");

  return (
    <>
      <Container>
        <Grid container style={{ marginTop: "2rem" }} spacing={2}>
          <Grid item lg={6} xs={12} display="flex" justifyContent="center">
            <Box>
              <Profile
                user={user}
                profilePicture={profilePicture ?? undefined}
              />
            </Box>
          </Grid>

          <Grid item lg={6} xs={12} display="flex" justifyContent="center">
            <ApiKeyManager user={user} />
          </Grid>
        </Grid>
      </Container>
    </>
  );
};

export default ProfileScreen;
