import { SpeedDial, SpeedDialAction, SpeedDialIcon } from "@mui/material";
import { EditNote, PostAdd, School } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { UserCourse } from "@courses/types.ts";
import { useMemo } from "react";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";

interface QuickCreationHomeProps {
  userCourses: UserCourse[];
}

const QuickCreationHome = ({ userCourses }: QuickCreationHomeProps) => {
  const navigate = useNavigate();
  const [user] = useAtom(userAtom);

  const canSeeSpeedDial = useMemo(() => {
    if (user == null) return false;
    return (
      user.isProfessor ||
      user.isSuperuser ||
      userCourses.some((usercourse) => usercourse.role === "COLLABORATOR")
    );
  }, [user, userCourses]);

  const canSeeCreateCourse = useMemo(() => {
    if (user == null) return false;
    return user.isProfessor || user.isSuperuser;
  }, [user]);

  const canSeeCreateTrial = useMemo(() => {
    return userCourses.some(
      (usercourse) =>
        usercourse.role === "COLLABORATOR" || usercourse.role === "ADMIN",
    );
  }, [userCourses]);

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
        condition: canSeeCreateTrial,
      },
    ];
  }, [canSeeCreateTrial, canSeeCreateCourse]);

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
