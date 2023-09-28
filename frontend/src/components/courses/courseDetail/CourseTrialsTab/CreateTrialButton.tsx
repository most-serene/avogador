import { Card, CardActionArea, CardContent, Typography } from "@mui/material";
import Box from "@mui/material/Box";
import { Add } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useAtom } from "jotai";
import { courseDetailAtom } from "@courses/courseDetail/courseDetailAtom.ts";

const CreateTrialButton = () => {
  const navigate = useNavigate();
  const [course] = useAtom(courseDetailAtom);

  if (course === undefined) return <></>;

  return (
    <Card
      sx={{
        mb: 2,
        border: 2,
        borderColor: "primary.main",
        borderStyle: "dashed",
        minHeight: "7rem",
      }}
      elevation={0}
    >
      <CardActionArea style={{ height: "100%", minHeight: "7rem" }}>
        <CardContent
          onClick={() => {
            navigate("/trials/new", {
              state: {
                courseId: course.id,
              },
            });
          }}
        >
          <Box display="flex" justifyContent="center" alignItems="center">
            <Add sx={{ mr: 2 }} />
            <Typography variant="h5"> Create new Trial </Typography>
          </Box>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};

export default CreateTrialButton;
