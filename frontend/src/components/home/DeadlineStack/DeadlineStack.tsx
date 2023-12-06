import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Divider,
  Stack,
  Typography,
} from "@mui/material";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { useEffect, useState } from "react";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { enqueueSnackbar } from "notistack";
import { isPractice, Trial, UserTrial } from "@trials/types.ts";
import { UserCourse } from "@courses/types.ts";
import TrialItemSkeleton from "@trials/TrialItem/TrialItemSkeleton.tsx";
import Box from "@mui/material/Box";
import { ExpandMore } from "@mui/icons-material";
import DeadlineItem from "@home/DeadlineStack/DeadlineItem.tsx";

interface DeadlineStackProps {
  userCourses?: UserCourse[];
}

export default function DeadlineStack({ userCourses }: DeadlineStackProps) {
  const { getUserTrials, getTrialsByCourseId, isTrialOngoing } =
    useTrialService();
  const [user] = useAtom(userAtom);
  const [userTrials, setUserTrials] = useState<UserTrial[]>();
  const [collaboratorTrials, setCollaboratorTrials] = useState<Trial[]>();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    setIsLoading(true);
    if (user == null) return;

    getUserTrials(user)
      .then((userTrials) => {
        setUserTrials(
          userTrials
            .filter(
              (ut) =>
                ut.finishTime == null &&
                ut.deadline &&
                ut.deadline.getTime() > new Date().getTime(),
            )
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
      });
    return () => {
      setUserTrials(undefined);
    };
  }, [getUserTrials, user]);

  useEffect(() => {
    if (user == null) return;

    userCourses
      ?.filter((userCourse) =>
        ["COLLABORATOR", "ADMIN"].includes(userCourse.role),
      )
      .forEach((userCourse) => {
        getTrialsByCourseId(userCourse.course.id)
          .then((trials) => {
            trials = trials.filter((trial) => isTrialOngoing(trial));
            setCollaboratorTrials((prev) => {
              if (prev == null) return trials;

              return [...prev, ...trials]
                .sort((a, b) => {
                  const startA = a.startTimestamp;
                  const startB = b.startTimestamp;

                  return startB.getTime() - startA.getTime();
                })
                .sort((a, b) => {
                  const deadlineA = isPractice(a) ? a.deadline : undefined;
                  const deadlineB = isPractice(b) ? b.deadline : undefined;

                  return (
                    (deadlineA?.getTime() ?? new Date().getTime()) -
                    (deadlineB?.getTime() ?? new Date().getTime())
                  );
                });
            });
          })
          .catch((err: Error) => {
            enqueueSnackbar(err.message, { variant: "error" });
          });
      });

    return () => {
      setCollaboratorTrials(undefined);
    };
  }, [getTrialsByCourseId, isTrialOngoing, user, userCourses]);

  return (
    <Box display={"flex"} height="100%">
      <Divider
        orientation="vertical"
        sx={{ mx: 1 }}
        variant="middle"
        flexItem
      />

      <Box
        width="100%"
        style={{ height: "100%", overflow: "scroll" }}
        className={"hidden-scrollbar"}
      >
        {(isLoading || (userTrials != null && userTrials.length > 0)) && (
          <Accordion disableGutters defaultExpanded>
            <AccordionSummary expandIcon={<ExpandMore />}>
              <Typography variant="h6">Deadlines</Typography>
            </AccordionSummary>
            <AccordionDetails>
              <Stack spacing={2}>
                {isLoading ? (
                  <TrialItemSkeleton />
                ) : (
                  userTrials?.map((userTrial) => (
                    <DeadlineItem
                      key={userTrial.id}
                      trial={userTrial.trial}
                      course={
                        userCourses?.find(
                          (uc) => uc.course.id === userTrial.trial.courseId,
                        )?.course
                      }
                    />
                  ))
                )}
              </Stack>
            </AccordionDetails>
          </Accordion>
        )}
        {collaboratorTrials != null && collaboratorTrials.length > 0 && (
          <Accordion disableGutters defaultExpanded>
            <AccordionSummary expandIcon={<ExpandMore />}>
              <Typography variant="h6">Your Trials</Typography>
            </AccordionSummary>
            <AccordionDetails>
              <Stack spacing={2}>
                {isLoading ? (
                  <TrialItemSkeleton />
                ) : (
                  collaboratorTrials.map((trial) => (
                    <DeadlineItem
                      key={trial.id}
                      trial={trial}
                      course={
                        userCourses?.find(
                          (uc) => uc.course.id === trial.courseId,
                        )?.course
                      }
                    />
                  ))
                )}
              </Stack>
            </AccordionDetails>
          </Accordion>
        )}
      </Box>
    </Box>
  );
}
