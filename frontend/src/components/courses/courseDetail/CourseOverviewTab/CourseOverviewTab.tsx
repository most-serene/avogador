import { GetCoursesDetailResponse } from "@courses/types";
import Box from "@mui/material/Box";
import JoinCourseLinkVisualizer from "@courses/courseDetail/CourseOverviewTab/JoinCourseLinkVisualizer";

interface CourseOverviewTabProps {
  course: GetCoursesDetailResponse | undefined;
}

const CourseOverviewTab = ({ course }: CourseOverviewTabProps) => {
  return (
    <Box display={"flex"} justifyContent={"center"}>
      {course &&
        (course.role === "COLLABORATOR" || course.role === "ADMIN") && (
          <JoinCourseLinkVisualizer course={course} />
        )}
    </Box>
  );
};

export default CourseOverviewTab;
