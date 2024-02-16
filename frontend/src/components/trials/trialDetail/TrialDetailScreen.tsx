import { useParams } from "react-router-dom";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { useEffect, useState } from "react";
import { Exam, isPractice, Practice, UserTrial } from "@trials/types.ts";
import { CourseDetail } from "@courses/types.ts";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import { ForbiddenError } from "@error/types.ts";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import TrialDetailCollaboratorScreen from "@trials/trialDetail/TrialDetailCollaboratorScreen.tsx";
import TrialDetailStudentScreen from "@trials/trialDetail/TrialDetailStudentScreen.tsx";
import { AxiosError } from "axios";
import JoinTrialScreen from "@trials/JoinTrialScreen/JoinTrialScreen.tsx";
import { enqueueSnackbar } from "notistack";
import Box from "@mui/material/Box";
import { CircularProgress } from "@mui/material";

const TrialDetailScreen = () => {
  const { trialId } = useParams();
  const { getUserTrial, joinPractice, getTrialById } = useTrialService();
  const { getCourseById } = useCourseService();
  const [user] = useAtom(userAtom);
  const globalErrorSetter = useGlobalErrorSetter();
  const [userCourse, setUserCourse] = useState<CourseDetail>();
  const [trial, setTrial] = useState<Practice | Exam>();
  const [userTrial, setUserTrial] = useState<UserTrial | undefined | null>();

  useEffect(() => {
    if (trialId == null || !user) return;

    getTrialById(trialId)
      .then((trial) => {
        setTrial(trial);
      })
      .catch((err: AxiosError) => {
        if (err.response && err.response.status === 403) {
          globalErrorSetter(
            new ForbiddenError(
              location.pathname,
              `${user.email} does not belong to the associated course`,
            ),
          );
        } else {
          enqueueSnackbar(err.message, { variant: "error" });
        }
      });

    return () => {
      setTrial(undefined);
    };
  }, [getTrialById, globalErrorSetter, trialId, user]);

  useEffect(() => {
    if (trial == null || !user) return;
    getCourseById(trial.courseId)
      .then((userCourseResponse) => {
        if (userCourseResponse.role === "EXTERNAL") {
          globalErrorSetter(
            new ForbiddenError(
              location.pathname,
              `${user.email} does not belong to the associated course`,
            ),
          );
        }
        setUserCourse(userCourseResponse);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });

    getUserTrial(user, trial)
      .then((userTrialResponse) => {
        setUserTrial(userTrialResponse);
      })
      .catch((err: Error) => {
        if (err instanceof AxiosError && err.response?.status === 404) {
          setUserTrial(null);
        } else {
          enqueueSnackbar(err.message, { variant: "error" });
        }
      });
  }, [getCourseById, getUserTrial, globalErrorSetter, trial, user]);

  if (
    !trial ||
    !user ||
    !userCourse ||
    userTrial === undefined ||
    userCourse.role === "EXTERNAL"
  ) {
    return (
      <Box
        style={{
          display: "flex",
          height: "100%",
        }}
        justifyContent={"center"}
        alignItems={"center"}
      >
        <CircularProgress size={80} />
      </Box>
    );
  }

  if (userCourse.role === "STUDENT" && !user.isSuperuser) {
    if (userTrial?.startTime !== undefined) {
      return <TrialDetailStudentScreen trial={trial} course={userCourse} />;
    }
    return (
      <JoinTrialScreen
        trial={trial}
        joinHandler={() => {
          if (isPractice(trial)) {
            joinPractice(trial.id)
              .then((ut) => {
                setUserTrial(ut);
              })
              .catch((err: Error) => {
                console.error(err);
                enqueueSnackbar(err.message, { variant: "error" });
              });
          }
        }}
      />
    );
  }

  return <TrialDetailCollaboratorScreen trial={trial} course={userCourse} />;
};

export default TrialDetailScreen;
