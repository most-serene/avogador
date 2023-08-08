import Grid from "@mui/material/Unstable_Grid2";
import CourseItem from "../courses/CourseItem.tsx";

export default function CoursesPreview() {
  return (
    <Grid container spacing={1}>
      <Grid xs={4}>
        <CourseItem
          course={{
            name: "PO1",
            id: 1,
            year: "2023/2024",
            isArchived: false,
          }}
        />
      </Grid>
      <Grid xs={4}>
        <CourseItem
          course={{
            name: "PO1",
            id: 1,
            year: "2023/2024",
            isArchived: false,
          }}
        />
      </Grid>
      <Grid xs={4}>
        <CourseItem
          course={{
            name: "PO1",
            id: 1,
            year: "2023/2024",
            isArchived: false,
          }}
        />
      </Grid>
    </Grid>
  );
}
