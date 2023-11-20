import { PieChart } from "@mui/x-charts";
import { ExerciseResults } from "@components/analytics/types.ts";
import { SubmissionStatus } from "@exercises/types.ts";
import { CircularProgress, Typography } from "@mui/material";
import Box from "@mui/material/Box";

interface ExerciseResultsChartProps {
  results: ExerciseResults | undefined;
}

const getStatusMetadata = (status: SubmissionStatus) => {
  switch (status) {
    case "CORRECT":
      return { color: "green", label: "Correct" };
    case "WRONG_ANSWER":
      return { color: "red", label: "Wrong Answer" };
    case "TIME_LIMIT_EXCEEDED":
      return { color: "orange", label: "Time Limit" };
    case "COMPILE_ERROR":
      return { color: "purple", label: "Compile err." };
    case "RUNTIME_ERROR":
      return { color: "cyan", label: "Runtime err." };
    default:
      return { color: "gray", label: "Unknown" };
  }
};

const ExerciseResultsChart = ({ results }: ExerciseResultsChartProps) => {
  if (results == null) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        height={200}
      >
        <CircularProgress />
      </Box>
    );
  }

  if (Object.values(results).reduce((a, b) => a + b, 0) === 0) {
    return (
      <Typography textAlign="center" sx={{ height: 200 }}>
        No data to show
      </Typography>
    );
  }

  return (
    <PieChart
      series={[
        {
          data: Object.entries(results).map(([type, value]) => {
            const { color, label } = getStatusMetadata(
              type as SubmissionStatus,
            );
            return {
              id: type,
              value,
              label,
              color,
            };
          }),
          paddingAngle: 2,
          cornerRadius: 5,
          innerRadius: 25,
        },
      ]}
      height={200}
    />
  );
};

export default ExerciseResultsChart;
