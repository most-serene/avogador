import Grid from "@mui/material/Grid";
import { useEffect, useState } from "react";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { Course, UserCourse } from "@courses/types.ts";
import CourseItem from "@courses/CourseItem.tsx";
import CourseItemSkeleton from "@courses/CourseItemSkeleton.tsx";
import { Typography } from "@mui/material";
import Box from "@mui/material/Box";
import CreateCourseButton from "@courses/coursesPage/CreateCourseButton.tsx";

const CoursesGridContent = ({ courses }: { courses: Course[] | undefined }) => {
  if (courses === undefined) {
    const coursesNumber = parseInt(
      localStorage.getItem("coursesNumber") ?? "0",
    );

    return [...Array(coursesNumber).keys()].map((i) => (
      <Grid item key={i} xs={6}>
        <CourseItemSkeleton />
      </Grid>
    ));
  }

  if (courses.length === 0) {
    return <EmptyCoursesPage />;
  }

  return courses.map((course) => (
    <Grid item key={course.id} xs={6}>
      <CourseItem course={course} />
    </Grid>
  ));
};

const EmptyCoursesPage = () => {
  return (
    <Box
      display={"flex"}
      justifyContent={"center"}
      width="100%"
      alignItems="end"
      height="40%"
      marginTop={2}
    >
      <Typography variant="h6">
        This page is so empty! Time to join a course!
      </Typography>
    </Box>
  );
};

export default function CoursesScreen() {
  const { getUserCourses } = useCourseService();
  const [courses, setCourses] = useState<Course[]>();
  const [user] = useAtom(userAtom);

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

  return (
    <Grid container spacing={2} sx={{ pt: "2rem" }}>
      {user && (user.isProfessor || user.isSuperuser) && <CreateCourseButton />}
      <CoursesGridContent courses={courses} />
    </Grid>
  );
}
