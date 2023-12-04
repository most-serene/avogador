import { Course } from "@courses/types.ts";
import { Alert, Card, CardContent, CircularProgress } from "@mui/material";
import { useCallback, useEffect, useState } from "react";
import useAnalyticsService from "@components/analytics/hooks/useAnalyticsService.tsx";
import { enqueueSnackbar } from "notistack";
import { LineChart } from "@mui/x-charts";
import Box from "@mui/material/Box";

interface SubmissionsTrendChart {
  course: Course;
}

const SubmissionsTrendChart = ({ course }: SubmissionsTrendChart) => {
  const { getSubmissionTimeSeries } = useAnalyticsService();
  const [timeSeries, setTimeSeries] = useState<Record<number, number>>();

  const processTimeSeries: (
    timeSeriesResponse: Date[],
  ) => Record<string, number> = useCallback((timeSeriesResponse: Date[]) => {
    const roundingCoefficient = 1000 * 60 * 60;
    const record = timeSeriesResponse
      .map(
        (date) =>
          Math.round(date.getTime() / roundingCoefficient) *
          roundingCoefficient,
      )
      .sort()
      .map((t) => t.toString())
      .reduce<Record<string, number>>((occurrences, item) => {
        occurrences[item] = (occurrences[item] || 0) + 1;
        return occurrences;
      }, {});

    let cumulativeSum = 0;
    const updatedRecord: Record<string, number> = {};

    Object.keys(record).forEach((timestamp) => {
      cumulativeSum += record[timestamp];
      updatedRecord[timestamp] = cumulativeSum;
    });

    return updatedRecord;
  }, []);

  useEffect(() => {
    getSubmissionTimeSeries(course.id)
      .then(processTimeSeries)
      .then(setTimeSeries)
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [course, getSubmissionTimeSeries, processTimeSeries]);

  return (
    <Card raised>
      <CardContent>
        {timeSeries ? (
          Object.keys(timeSeries).length > 0 ? (
            <LineChart
              height={250}
              series={[
                {
                  data: Object.values(timeSeries),
                  label: "submissions",
                  curve: "monotoneY",
                  showMark: false,
                },
              ]}
              xAxis={[
                {
                  scaleType: "time",
                  data: Object.keys(timeSeries).map(
                    (value) => new Date(Number.parseInt(value)),
                  ),
                },
              ]}
            />
          ) : (
            <Box
              width={"100%"}
              display={"flex"}
              justifyContent={"center"}
              alignItems={"center"}
            >
              <Alert severity="info" variant={"outlined"}>
                No submission trend data
              </Alert>
            </Box>
          )
        ) : (
          <Box
            height={300}
            width={"100%"}
            display={"flex"}
            justifyContent={"center"}
            alignItems={"center"}
          >
            <CircularProgress />
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

export default SubmissionsTrendChart;
