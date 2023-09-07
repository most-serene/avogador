import Grid from "@mui/material/Grid";
import { useEffect, useState } from "react";
import CourseItem from "@components/courses/CourseItem.tsx";
import { Course, UserCourse } from "@components/courses/types.ts";
import CourseItemSkeleton from "@components/courses/CourseItemSkeleton.tsx";
import { useAtom } from "jotai";
import userAtom from "@components/authentication/userAtom.ts";
import { useAvogadorApi } from "@hooks/useAvogadorApi.tsx";
import { Typography } from "@mui/material";

const EmptyCoursesHome = () => {
  return (
    <Grid container alignContent={"center"}>
      <Grid item xs={12} display={"flex"} justifyContent={"center"}>
        <Typography variant="h6">
          Your home is so empty! Time to join a course!
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
      .then(({ data }: { data: UserCourse[] }) => {
        const resCourses = data.map((elem) => elem.course);
        setCourses(resCourses);
        localStorage.setItem("coursesNumber", String(resCourses.length));
      })
      .catch((err) => {
        console.error(err);
      });
  }, [user, avogadorApi]);

  if (user === null) {
    return <h1>Log in to view this content</h1>;
  }

  let gridContent;
  if (!courses) {
    const coursesNumber = parseInt(
      localStorage.getItem("coursesNumber") ?? "0",
    );

    gridContent = [...Array(coursesNumber).keys()].map((i) => (
      <Grid item key={i} xs={4}>
        <CourseItemSkeleton />
      </Grid>
    ));
  } else {
    gridContent = courses.map((course) => (
      <Grid item key={course.id} xs={4}>
        <CourseItem course={course} />
      </Grid>
    ));
  }

  return (
    <Grid container spacing={2} display={"flex"}>
      {user && gridContent.length === 0 ? <EmptyCoursesHome /> : gridContent}
    </Grid>
  );
}
