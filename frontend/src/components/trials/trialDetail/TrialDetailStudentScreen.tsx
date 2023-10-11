import { Box, Button, Typography } from "@mui/material";
import Container from "@mui/material/Container";
import { Exam, Practice } from "@trials/types.ts";
import { CourseDetail } from "@courses/types.ts";
import { useNavigate } from "react-router-dom";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
import TrialDetailExercisesTab from "@trials/trialDetail/TrialDetailExercisesTab/TrialDetailExercisesTab.tsx";

interface TrialDetailStudentScreenProps {
  trial: Practice | Exam;
  course: CourseDetail;
}

const TrialDetailStudentScreen = ({
  trial,
  course,
}: TrialDetailStudentScreenProps) => {
  const navigate = useNavigate();

  return (
    <Box
      height="100%"
      sx={{
        flexGrow: 1,
        display: "flex",
      }}
    >
      <Container maxWidth={false}>
        <Box display={"flex"} justifyContent={"center"}>
          <Typography variant="h3" align="center">
            {trial.name}
          </Typography>
          <Box style={{ position: "absolute", left: "2rem", top: "5rem" }}>
            <Button
              variant={"outlined"}
              onClick={() => {
                navigate(`/courses/${course.id}?tab=1`);
              }}
            >
              <ArrowBackIosNewIcon />
              Back to{" "}
              {course.name.length > 20
                ? course.name.substring(0, 20) + "..."
                : course.name}
            </Button>
          </Box>
        </Box>
        <TrialDetailExercisesTab trial={trial} course={course} />
      </Container>
    </Box>
  );
};

export default TrialDetailStudentScreen;
