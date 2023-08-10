import { Card, Stack, useTheme } from "@mui/material";

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
        <Stack></Stack>
      </Card>
    </>
  );
}
