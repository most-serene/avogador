import CoursesPreview from "./CoursesPreview.tsx";
import Grid from "@mui/material/Grid";
import DeadlineStack from "./DeadlineStack.tsx";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAtom } from "jotai";
import userAtom from "../authentication/userAtom.ts";

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
