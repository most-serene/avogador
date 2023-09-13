import Grid from "@mui/material/Grid";
import { useEffect, useState } from "react";
import CourseItem from "@courses/CourseItem.tsx";
import { Course, UserCourse } from "@courses/types.ts";
import CourseItemSkeleton from "@courses/CourseItemSkeleton.tsx";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { Typography } from "@mui/material";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import Box from "@mui/material/Box";

const EmptyCoursesHome = () => {
  return (
    <Box
      display={"flex"}
      justifyContent={"center"}
      width="100%"
      alignItems="end"
      height="40%"
    >
      <Typography variant="h6">
        Your home is so empty! Time to join a course!
      </Typography>
    </Box>
  );
};

const CoursesGridContent = ({ courses }: { courses: Course[] | undefined }) => {
  if (courses === undefined) {
    const coursesNumber = parseInt(
      localStorage.getItem("coursesNumber") ?? "0",
    );

    return [...Array(coursesNumber).keys()].map((i) => (
      <Grid item key={i} xs={4}>
        <CourseItemSkeleton />
      </Grid>
    ));
  }

  return courses.map((course) => (
    <Grid item key={course.id} xs={4}>
      <CourseItem course={course} />
    </Grid>
  ));
};

export default function CoursesPreview() {
  const [user] = useAtom(userAtom);
  const [courses, setCourses] = useState<Course[]>();
  const { getUserCourses } = useCourseService();

  useEffect(() => {
    if (!user) {
      return;
    }
    getUserCourses(user.id)
      .then((data: UserCourse[]) => {
        const resCourses = data.map((elem) => elem.course);
        setCourses(resCourses);
      })
      .catch((err) => {
        console.error(err);
      });
  }, [user, getUserCourses]);

  if (user && courses && courses.length === 0) {
    return <EmptyCoursesHome />;
  }

  return (
    <Grid container spacing={2} display={"flex"}>
      <CoursesGridContent courses={courses} />
    </Grid>
  );
}
