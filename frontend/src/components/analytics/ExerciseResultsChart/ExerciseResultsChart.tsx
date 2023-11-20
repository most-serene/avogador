import { PieChart } from "@mui/x-charts";
import { ExerciseResults } from "@components/analytics/types.ts";
import { SubmissionStatus } from "@exercises/types.ts";
import { CircularProgress, Typography } from "@mui/material";
import Box from "@mui/material/Box";

interface ExerciseResultsChartProps {
  results: ExerciseResults | undefined;
}

const getStatusColor = (status: SubmissionStatus) => {
  switch (status) {
    case "CORRECT":
      return "green";
    case "WRONG_ANSWER":
      return "red";
    case "TIME_LIMIT_EXCEEDED":
      return "orange";
    case "COMPILE_ERROR":
      return "purple";
    case "RUNTIME_ERROR":
      return "cyan";
    default:
      return "gray";
  }
};

const getStatusName = (status: SubmissionStatus) => {
  switch (status) {
    case "CORRECT":
      return "Correct";
    case "WRONG_ANSWER":
      return "Wrong Answer";
    case "TIME_LIMIT_EXCEEDED":
      return "Time Limit";
    case "COMPILE_ERROR":
      return "Compile";
    case "RUNTIME_ERROR":
      return "Runtime";
    default:
      return "gray";
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
            return {
              id: type,
              value,
              label: getStatusName(type as SubmissionStatus),
              color: getStatusColor(type as SubmissionStatus),
            };
          }),
        },
      ]}
      height={200}
    />
  );
};

export default ExerciseResultsChart;
