import { useNavigate } from "react-router-dom";
import { Box, Button, Card, CardContent, Typography } from "@mui/material";

const ArchivedCourseErrorCard = () => {
  const navigate = useNavigate();

  const goHome = () => {
    navigate("/");
  };

  return (
    <Card sx={{ width: "32rem" }} raised>
      <CardContent>
        <Typography variant="h5" color="text.secondary" gutterBottom>
          Archived Course
        </Typography>

        <Typography variant="body1" gutterBottom>
          The course you are trying to open has been archived by its owner. You
          cannot access it anymore.
        </Typography>

        <Box display="flex" justifyContent="center" marginTop=".5rem">
          <Button onClick={goHome} variant={"outlined"}>
            Go Home
          </Button>
        </Box>
      </CardContent>
    </Card>
  );
};

export default ArchivedCourseErrorCard;
