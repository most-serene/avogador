import { useNavigate } from "react-router-dom";
import { Card, CardActionArea, CardContent, Typography } from "@mui/material";
import { Course } from "./types.ts";

interface CourseItemProps {
  course: Course;
}

export default function CourseItem({ course }: CourseItemProps) {
  const navigator = useNavigate();

  console.log(course);

  return (
    <Card
      onClick={() => {
        navigator(`/courses/${course.id}`);
      }}
    >
      <CardActionArea>
        <CardContent>
          <Typography variant={"h5"}>{course.name}</Typography>
          <Typography variant={"body2"}>({course.year})</Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
}
