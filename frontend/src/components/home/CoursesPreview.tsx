import Grid from "@mui/material/Grid";
import { useEffect, useState } from "react";
import { avogadorApi } from "../../utils/axiosConf.ts";
import CourseItem from "../courses/CourseItem.tsx";
import { Course, GetCoursesResponse } from "../courses/types.ts";
import CourseItemSkeleton from "../courses/CourseItemSkeleton.tsx";
import useUser from "../../hooks/useUser.ts";

export default function CoursesPreview() {
  const { user } = useUser();
  const [courses, setCourses] = useState<Course[]>();

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
  }, [user]);

  if (user === null) {
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
    <Grid container spacing={1}>
      {gridContent}
    </Grid>
  );
}
