import ButtonWithConfirmation from "@structure/ButtonWithConfirmation/ButtonWithConfirmation";
import { enqueueSnackbar } from "notistack";
import ExitToAppIcon from "@mui/icons-material/ExitToApp";
import { Box } from "@mui/material";
import { Course } from "@courses/types";
import useCourseService from "@courses/hooks/useCourseService";
import { useNavigate } from "react-router-dom";

interface LeaveCourseProps {
  course: Course;
}

const LeaveCourse = ({ course }: LeaveCourseProps) => {
  const { leaveCourse } = useCourseService();
  const navigate = useNavigate();

  return (
    <Box style={{ position: "absolute", right: "1rem", top: "5rem" }}>
      <ButtonWithConfirmation
        onConfirm={() => {
          leaveCourse(course)
            .then(() => {
              enqueueSnackbar(`${course.name} left successfully`, {
                variant: "success",
              });
              navigate("/");
            })
            .catch((err: Error) => {
              enqueueSnackbar(err.message, { variant: "error" });
            });
        }}
        confirmColor={"error"}
        variant="outlined"
        color={"secondary"}
        confirmText="leave"
        description={`You are leaving ${course.name}, this operation cannot be reverted. If you will want to re-join this course you will need the join link.`}
      >
        <ExitToAppIcon />
      </ButtonWithConfirmation>
    </Box>
  );
};

export default LeaveCourse;
