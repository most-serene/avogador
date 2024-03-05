import Grid from "@mui/material/Grid";
import { lazy, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { Skeleton, Typography } from "@mui/material";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import { UserCourse } from "@courses/types.ts";
import { enqueueSnackbar } from "notistack";
const QuickCreationHome = lazy(() => import("@home/QuickCreationHome.tsx"));
const CoursesPreview = lazy(() => import("@home/CoursesPreview.tsx"));
const DeadlineStack = lazy(
  () => import("@home/DeadlineStack/DeadlineStack.tsx"),
);

export default function HomeScreen() {
  const [user] = useAtom(userAtom);
  const navigate = useNavigate();
  const { getUserCourses } = useCourseService();
  const [userCourses, setUserCourses] = useState<UserCourse[]>();

  useEffect(() => {
    if (user == null) {
      return;
    }

    getUserCourses(user.id)
      .then((resUserCourses) => {
        setUserCourses(resUserCourses);
      })
      .catch((err: Error) =>
        enqueueSnackbar(err.message, { variant: "error" }),
      );
  }, [user, navigate, getUserCourses]);

  return (
    <>
      <Grid container height="100%">
        <Grid item xs={9} paddingRight={1} position={"relative"}>
          <Typography variant={"h4"} marginY={2}>
            {user ? `Welcome back ${user.givenName}!` : <Skeleton />}
          </Typography>
          <CoursesPreview courses={userCourses?.map((elem) => elem.course)} />
          {userCourses && <QuickCreationHome userCourses={userCourses} />}
        </Grid>

        <Grid item xs={3} paddingLeft={1} height={"100%"}>
          <DeadlineStack userCourses={userCourses} />
        </Grid>
      </Grid>
    </>
  );
}
