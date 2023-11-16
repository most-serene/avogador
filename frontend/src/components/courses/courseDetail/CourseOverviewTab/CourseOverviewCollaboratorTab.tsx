import JoinCourseLinkVisualizer from "@courses/courseDetail/CourseOverviewTab/JoinCourseLinkVisualizer.tsx";
import Box from "@mui/material/Box";
import { UserCourseDetail } from "@courses/types.ts";
import { CircularProgress } from "@mui/material";

interface CourseOverviewTabProps {
  course: UserCourseDetail | undefined;
}

const CourseOverviewCollaboratorTab = ({ course }: CourseOverviewTabProps) => {
  if (course == undefined) {
    return <CircularProgress size={80} />;
  }
  return (
    <Box display={"flex"} justifyContent={"center"}>
      <JoinCourseLinkVisualizer course={course} />
    </Box>
  );
};

export default CourseOverviewCollaboratorTab;
