import Grid from "@mui/material/Grid";
import { useEffect, useState } from "react";
import CourseItem from "../courses/CourseItem.tsx";
import { Course, GetCoursesResponse } from "../courses/types.ts";
import CourseItemSkeleton from "../courses/CourseItemSkeleton.tsx";
import { useAtom } from "jotai";
import userAtom from "../authentication/userAtom.ts";
import { useAvogadorApi } from "../../hooks/useAvogadorApi.tsx";
import { Typography } from "@mui/material";
import { User } from "../authentication/types.ts";

const EmptyCoursesHome = ({ user }: { user: User }) => {
  return (
    <Grid container alignContent={"center"}>
      <Grid item xs={12} display={"flex"} justifyContent={"center"}>
        <Typography variant="h6">
          Hi {user.givenName}! <br />
          Your home is so empty! time to join a course!
        </Typography>
      </Grid>
    </Grid>
  );
};

export default function CoursesPreview() {
  const [user] = useAtom(userAtom);
  const [courses, setCourses] = useState<Course[]>();
  const avogadorApi = useAvogadorApi();

  useEffect(() => {
    if (!user) {
      return;
    }
    avogadorApi
      .get(`/courses/users/${user.id}`)
      .then(({ data }: { data: GetCoursesResponse[] }) => {
        const resCourses = data.map((elem) => elem.course);
        setCourses(resCourses);
        localStorage.setItem("coursesNumber", String(resCourses.length));
      })
      .catch((err) => {
        console.error(err);
      });
  }, [user, avogadorApi]);

  if (user === null || user === undefined) {
    return <h1>Log in to view this content</h1>;
  }

  const coursesNumber = parseInt(localStorage.getItem("coursesNumber") ?? "0");
  let gridContent = [...Array(coursesNumber).keys()].map((i) => (
    <Grid item key={i} xs={4}>
      <CourseItemSkeleton />
    </Grid>
  ));

  if (courses) {
    gridContent = courses.map((course) => (
      <Grid item key={course.id} xs={4}>
        <CourseItem course={course} />
      </Grid>
    ));
  }

  return (
    <Grid container spacing={1} display={"flex"} style={{ height: "100%" }}>
      {gridContent.length === 0 ? (
        <EmptyCoursesHome user={user} />
      ) : (
        gridContent
      )}
    </Grid>
  );
}
