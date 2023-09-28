import CoursesPreview from "@home/CoursesPreview.tsx";
import Grid from "@mui/material/Grid";
import DeadlineStack from "@home/DeadlineStack.tsx";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { Skeleton, Typography } from "@mui/material";
import QuickCreationHome from "@home/QuickCreationHome.tsx";

export default function HomeScreen() {
  const [user] = useAtom(userAtom);
  const navigate = useNavigate();

  useEffect(() => {
    console.log(user);
    if (user === null) {
      navigate("/login");
    }
  }, [user, navigate]);

  return (
    <>
      <Grid container height="100%">
        <Grid item xs={9} paddingRight={1} position={"relative"}>
          <Typography variant={"h4"} marginY={2}>
            {user ? `Welcome back ${user.givenName}!` : <Skeleton />}
          </Typography>
          <CoursesPreview />
          <QuickCreationHome />
        </Grid>

        <Grid item xs={3} paddingLeft={1}>
          <DeadlineStack />
        </Grid>
      </Grid>
    </>
  );
}
