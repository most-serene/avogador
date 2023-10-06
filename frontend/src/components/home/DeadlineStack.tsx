import {
  Card,
  CardActionArea,
  CardContent,
  Stack,
  Typography,
  useTheme,
} from "@mui/material";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { CSSProperties, useEffect, useState } from "react";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { enqueueSnackbar } from "notistack";
import { UserTrial } from "@trials/types.ts";
import { format, subHours } from "date-fns";
import { useNavigate } from "react-router-dom";
import { Course, UserCourse } from "@courses/types.ts";
import TrialItemSkeleton from "@trials/TrialItem/TrialItemSkeleton.tsx";

interface DeadlineItemProps {
  userTrial: UserTrial;
  course: Course | undefined;
}

const DeadlineItem = ({ userTrial, course }: DeadlineItemProps) => {
  const navigate = useNavigate();
  const theme = useTheme();

  const getStyle = (): CSSProperties => {
    if (userTrial.deadline && subHours(userTrial.deadline, 24) < new Date()) {
      return {
        border: 2,
        borderColor: theme.palette.warning.main,
        borderStyle: "solid",
      };
    }
    return {};
  };

  return (
    <Card
      raised
      style={getStyle()}
      onClick={() => {
        if (userTrial.trial.trialType === "PRACTICE") {
          navigate(`/practices/${userTrial.trial.id}`);
        } else {
          navigate(`/exams/${userTrial.trial.id}`);
        }
      }}
    >
      <CardActionArea>
        <CardContent>
          <Typography>
            {userTrial.trial.name} - {course?.name} {course?.year}
          </Typography>
          <Typography variant={"body2"}>
            Language: {userTrial.trial.language}
          </Typography>
          <Typography>
            Deadline:{" "}
            {userTrial.deadline &&
              format(userTrial.deadline, "dd/MM/yyyy HH:mm")}
          </Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};

interface DeadlineStackProps {
  userCourses?: UserCourse[];
}

export default function DeadlineStack({ userCourses }: DeadlineStackProps) {
  const { getUserTrials } = useTrialService();
  const [user] = useAtom(userAtom);
  const [userTrials, setUserTrials] = useState<UserTrial[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    setIsLoading(true);
    if (user) {
      getUserTrials(user)
        .then((userTrialsResponse) => {
          setUserTrials(
            userTrialsResponse
              .filter((ut) => !ut.finishTime)
              .sort(
                (a, b) =>
                  (a.deadline?.getTime() ?? new Date().getTime()) -
                  (b.deadline?.getTime() ?? new Date().getTime()),
              ),
          );
          setIsLoading(false);
        })
        .catch((err: Error) => {
          enqueueSnackbar(err.message, { variant: "error" });
          console.error(err);
        });
    }
  }, [getUserTrials, user]);

  return (
    <>
      <Card
        style={{ height: "100%", overflow: "scroll" }}
        className={"hidden-scrollbar"}
      >
        <CardContent>
          <Stack spacing={2}>
            {isLoading ? (
              <TrialItemSkeleton />
            ) : (
              userTrials.map((userTrial) => (
                <DeadlineItem
                  key={userTrial.id}
                  userTrial={userTrial}
                  course={
                    userCourses?.find(
                      (uc) => uc.course.id === userTrial.trial.courseId,
                    )?.course
                  }
                />
              ))
            )}
          </Stack>
        </CardContent>
      </Card>
    </>
  );
}
