import Grid from "@mui/material/Grid";
import { Card, CardActionArea, Typography } from "@mui/material";
import Box from "@mui/material/Box";
import { Add } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";

const CreateCourseButton = () => {
  const navigate = useNavigate();

  return (
    <Grid item xs={6}>
      <Card
        sx={{
          height: "100%",
          border: 2,
          borderColor: "primary.main",
          borderStyle: "dashed",
          minHeight: "5rem",
        }}
        elevation={0}
      >
        <CardActionArea
          sx={{ height: "100%" }}
          onClick={() => {
            navigate("new");
          }}
        >
          <Box
            display="flex"
            justifyContent="center"
            alignItems="center"
            height="100%"
          >
            <Add sx={{ mr: 2 }} />
            <Typography variant="h5"> Create new course </Typography>
          </Box>
        </CardActionArea>
      </Card>
    </Grid>
  );
};

export default CreateCourseButton;
