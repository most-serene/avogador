import Grid from "@mui/material/Grid";
import CourseItem from "@courses/CourseItem.tsx";
import { Course } from "@courses/types.ts";
import CourseItemSkeleton from "@courses/CourseItemSkeleton.tsx";
import { Typography } from "@mui/material";
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

interface CoursePreviewProps {
  courses: Course[] | undefined;
}

export default function CoursesPreview({ courses }: CoursePreviewProps) {
  if (courses && courses.length === 0) {
    return <EmptyCoursesHome />;
  }

  return (
    <Grid container spacing={2} display={"flex"}>
      <CoursesGridContent courses={courses?.filter((c) => !c.isArchived)} />
    </Grid>
  );
}
