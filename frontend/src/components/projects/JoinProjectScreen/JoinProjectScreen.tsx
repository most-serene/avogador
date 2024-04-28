import Box from "@mui/material/Box";
import { Button, Card, CardContent, Typography } from "@mui/material";
import { Project } from "@components/projects/types.ts";

interface JoinProjectScreenProps {
  project: Project;
  joinHandler: () => void;
}

const JoinProjectScreen = ({
  project,
  joinHandler,
}: JoinProjectScreenProps) => {
  return (
    <Box display={"flex"} justifyContent={"center"} marginTop={"2rem"}>
      <Card sx={{ width: "32rem" }} raised>
        <CardContent>
          <Typography variant="h5" color="text.secondary" gutterBottom>
            {`Join the project: ${project.name}`}
          </Typography>

          <Box display="flex" justifyContent="center" marginTop=".5rem">
            <Button onClick={joinHandler} variant={"outlined"}>
              Join
            </Button>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default JoinProjectScreen;
