import { Project } from "@components/projects/types.ts";
import { CourseDetail } from "@courses/types.ts";
import ProjectOverviewTab from "@components/projects/ProjectDetailScreen/ProjectOverviewTab/ProjectOverviewTab.tsx";
import { Box, Button, Container, Typography } from "@mui/material";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
import { useNavigate } from "react-router-dom";

interface ProjectDetailStudentScreenProps {
  project: Project;
  course: CourseDetail;
}

const ProjectDetailStudentScreen = ({
  project,
  course,
}: ProjectDetailStudentScreenProps) => {
  const navigate = useNavigate();

  return (
    <Container
      maxWidth={false}
      sx={{
        height: "100%",
        display: { md: "flex", xs: "block" },
        flexFlow: "column",
      }}
    >
      <Box
        sx={{
          display: { md: "flex", xs: "block" },
          alignItems: "center",
          justifyContent: "center",
          width: "100%",
          position: "relative",
          mb: 1,
          ml: 2,
        }}
      >
        <Button
          sx={{ position: { md: "absolute", xs: "static" }, left: 0 }}
          variant={"outlined"}
          onClick={() => {
            navigate(`/courses/${course.id}?tab=2`);
          }}
        >
          <ArrowBackIosNewIcon />
          Back to{" "}
          {course.name.length > 20
            ? course.name.substring(0, 18) + "..."
            : course.name}
        </Button>
        <Typography variant={"h3"} textAlign={"center"} sx={{ mb: 1 }}>
          {project.name}
        </Typography>
      </Box>
      <ProjectOverviewTab project={project} />
    </Container>
  );
};

export default ProjectDetailStudentScreen;
