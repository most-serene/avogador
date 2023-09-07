import { useNavigate } from "react-router-dom";
import { Card, CardActionArea, CardContent, Typography } from "@mui/material";
import { Course } from "@courses/types.ts";

interface CourseItemProps {
  course: Course;
}

export default function CourseItem({ course }: CourseItemProps) {
  const navigate = useNavigate();

  console.log(course);

  return (
    <Card
      onClick={() => {
        navigate(`/courses/${course.id}`);
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
