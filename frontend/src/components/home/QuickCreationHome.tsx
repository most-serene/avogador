import { SpeedDial, SpeedDialAction, SpeedDialIcon } from "@mui/material";
import { EditNote, PostAdd, School } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { UserCourse } from "@courses/types.ts";
import { useEffect, useMemo, useState } from "react";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { enqueueSnackbar } from "notistack";

interface QuickCreationHomeProps {
  userCourses: UserCourse[];
}

const QuickCreationHome = ({ userCourses }: QuickCreationHomeProps) => {
  const navigate = useNavigate();
  const { getTrialsByCourseId, isTrialEnded } = useTrialService();
  const [user] = useAtom(userAtom);
  const [canSeeCreateExercise, setCanSeeCreateExercise] = useState(false);

  const canSeeSpeedDial = useMemo(() => {
    if (user == null) return false;
    return (
      user.isProfessor ||
      user.isSuperuser ||
      userCourses.some((userCourse) => userCourse.role === "COLLABORATOR")
    );
  }, [user, userCourses]);

  const canSeeCreateCourse = useMemo(() => {
    if (user == null) return false;
    return user.isProfessor || user.isSuperuser;
  }, [user]);

  const canSeeCreateTrial = useMemo(() => {
    return userCourses.some(
      (userCourse) =>
        userCourse.role === "COLLABORATOR" || userCourse.role === "ADMIN",
    );
  }, [userCourses]);

  useEffect(() => {
    const privilegedCourses = userCourses
      .filter(
        (userCourse) =>
          userCourse.role === "COLLABORATOR" || userCourse.role === "ADMIN",
      )
      .map((uc) => uc.course);

    for (const course of privilegedCourses) {
      getTrialsByCourseId(course.id)
        .then((trials) => {
          if (trials.some((trial) => !isTrialEnded(trial))) {
            setCanSeeCreateExercise(true);
          }
        })
        .catch((err: Error) =>
          enqueueSnackbar(err.message, { variant: "error" }),
        );
    }
  }, [userCourses, getTrialsByCourseId, isTrialEnded]);

  const actions = useMemo(() => {
    return [
      {
        icon: <School />,
        name: "Course",
        path: "/courses/new",
        condition: canSeeCreateCourse,
      },
      {
        icon: <PostAdd />,
        name: "Trial",
        path: "/trials/new",
        condition: canSeeCreateTrial,
      },
      {
        icon: <EditNote />,
        name: "Exercise",
        path: "/exercises/new",
        condition: canSeeCreateExercise,
      },
    ];
  }, [canSeeCreateTrial, canSeeCreateCourse, canSeeCreateExercise]);

  if (!canSeeSpeedDial) return null;

  return (
    <SpeedDial
      ariaLabel="SpeedDial basic example"
      sx={{ position: "absolute", bottom: 16, right: 16 }}
      icon={<SpeedDialIcon />}
    >
      {actions.map(
        (action) =>
          action.condition && (
            <SpeedDialAction
              key={action.name}
              icon={action.icon}
              onClick={() => {
                navigate(action.path);
              }}
              tooltipOpen
              tooltipTitle={action.name}
            />
          ),
      )}
    </SpeedDial>
  );
};

export default QuickCreationHome;
