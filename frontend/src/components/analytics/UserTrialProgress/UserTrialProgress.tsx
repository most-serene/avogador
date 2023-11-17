import useAnalyticsService from "@components/analytics/hooks/useAnalyticsService.tsx";
import userAtom from "@authentication/userAtom.ts";
import { useEffect, useState } from "react";
import { Course } from "@courses/types.ts";
import { UserTrialProgress } from "@components/analytics/types.ts";
import { enqueueSnackbar } from "notistack";
import { useAtom } from "jotai";
import { CircularProgress, useTheme } from "@mui/material";
import { BarChart } from "@mui/x-charts";
import Box from "@mui/material/Box";

interface UserTrialProgressProps {
  course?: Course;
}

const UserTrialProgress = ({ course }: UserTrialProgressProps) => {
  const { getUserTrialProgress } = useAnalyticsService();
  const [userTrialProgress, setUserTrialProgress] =
    useState<UserTrialProgress[]>();
  const [user] = useAtom(userAtom);
  const theme = useTheme();

  useEffect(() => {
    if (user == null || course == null) {
      return;
    }
    getUserTrialProgress(user.id, course.id)
      .then((userTrialProgressResponse) => {
        setUserTrialProgress(userTrialProgressResponse);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [course, getUserTrialProgress, user]);

  if (userTrialProgress == null) {
    return (
      <Box
        style={{
          width: "100%",
          height: 350,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  return (
    <BarChart
      series={[
        {
          data: userTrialProgress.map((progress) => progress.passed),
          stack: "total",
          label: "Passed",
          color: theme.palette.success.main,
        },
        {
          data: userTrialProgress.map((progress) => progress.wrong),
          stack: "total",
          label: "Wrong",
          color: theme.palette.error.main,
        },
        {
          data: userTrialProgress.map((progress) => progress.missing),
          stack: "total",
          label: "Missing",
          color: theme.palette.secondary.main,
        },
      ]}
      xAxis={[
        {
          scaleType: "band" as const,
          data: userTrialProgress.map((progress) => progress.name),
        },
      ]}
      height={350}
    />
  );
};

export default UserTrialProgress;
