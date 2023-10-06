import Box from "@mui/material/Box";
import { Alert, Button, Card, CardContent, Typography } from "@mui/material";
import { Trial } from "@trials/types.ts";
import { useEffect, useState } from "react";

interface JoinTrialScreenProps {
  trial: Trial;
  joinHandler: () => void;
}

const JoinTrialScreen = ({ trial, joinHandler }: JoinTrialScreenProps) => {
  const [isTrialStarted, setIsTrialStarted] = useState<boolean>(
    trial.startTimestamp < new Date(),
  );

  useEffect(() => {
    const checkInterval = setInterval(() => {
      setIsTrialStarted(trial.startTimestamp < new Date());
    }, 2000);

    if (isTrialStarted) {
      clearInterval(checkInterval);
    }

    return () => {
      clearInterval(checkInterval);
    };
  }, [isTrialStarted, trial.startTimestamp]);

  return (
    <Box display={"flex"} justifyContent={"center"} marginTop={"2rem"}>
      <Card sx={{ width: "32rem" }} raised>
        <CardContent>
          <Typography variant="h5" color="text.secondary" gutterBottom>
            {`Join the practice: ${trial.name}`}
          </Typography>
          <Typography gutterBottom>
            {`Once joined, there's no turning back`}
          </Typography>

          {!isTrialStarted && (
            <Alert severity={"error"}>The test has yet to begin</Alert>
          )}

          <Box display="flex" justifyContent="center" marginTop=".5rem">
            <Button
              onClick={joinHandler}
              variant={"outlined"}
              disabled={!isTrialStarted}
            >
              Join
            </Button>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default JoinTrialScreen;
