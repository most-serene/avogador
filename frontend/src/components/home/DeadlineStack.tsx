import { Card, Stack, useTheme } from "@mui/material";
import TrialDeadline from "../trials/TrialDeadline.tsx";

export default function DeadlineStack() {
  const theme = useTheme();

  return (
    <>
      <Card
        style={{
          height: "100%",
          backgroundColor: theme.palette.secondary.light,
        }}
      >
        <Stack>
          <TrialDeadline />
          <TrialDeadline />
          <TrialDeadline />
          <TrialDeadline />
        </Stack>
      </Card>
    </>
  );
}
