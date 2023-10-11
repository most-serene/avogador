import { useLocation, useParams } from "react-router-dom";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { useEffect, useState } from "react";
import { Exam, Practice, UserTrial } from "@trials/types.ts";
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

interface TrialDetailScreenProps {
  trialType: "PRACTICE" | "EXAM";
}

const TrialDetailScreen = ({ trialType }: TrialDetailScreenProps) => {
  const { trialId } = useParams();
  const { getPracticeById, getUserTrial, joinPractice } = useTrialService();
  const { getCourseById } = useCourseService();
  const [user] = useAtom(userAtom);
  const globalErrorSetter = useGlobalErrorSetter();
  const { pathname } = useLocation();
  const [userCourse, setUserCourse] = useState<CourseDetail>();
  const [trial, setTrial] = useState<Practice | Exam>();
  const [userTrial, setUserTrial] = useState<UserTrial | undefined | null>();

  useEffect(() => {
    if (trialId != null && user) {
      if (trialType === "PRACTICE") {
        getPracticeById(trialId)
          .then((trialResponse) => {
            setTrial(trialResponse);
            getCourseById(trialResponse.courseId)
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
                console.error(err);
              });

            getUserTrial(user, trialResponse)
              .then((userTrialResponse) => {
                setUserTrial(userTrialResponse);
              })
              .catch((err: Error) => {
                if (err instanceof AxiosError && err.response?.status === 404) {
                  setUserTrial(null);
                } else {
                  enqueueSnackbar(err.message, { variant: "error" });
                  console.error(err);
                }
              });
          })
          .catch((err: Error) => {
            enqueueSnackbar(err.message, { variant: "error" });
            console.error(err);
          });
      } else {
        console.error("Exam not implemented");
      }
    }
  }, [
    getCourseById,
    getPracticeById,
    getUserTrial,
    globalErrorSetter,
    pathname,
    trialId,
    trialType,
    user,
  ]);

  if (!trial || !user || !userCourse || userCourse.role === "EXTERNAL") {
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
          if (trial.trialType === "PRACTICE") {
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
