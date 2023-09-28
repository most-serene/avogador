import { SpeedDial, SpeedDialAction, SpeedDialIcon } from "@mui/material";
import { PostAdd, School } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";

const actions = [
  { icon: <School />, name: "Course", path: "/courses/new" },
  { icon: <PostAdd />, name: "Trial", path: "/trials/new" },
];

const QuickCreationHome = () => {
  const navigate = useNavigate();

  return (
    <SpeedDial
      ariaLabel="SpeedDial basic example"
      sx={{ position: "absolute", bottom: 16, right: 16 }}
      icon={<SpeedDialIcon />}
    >
      {actions.map((action) => (
        <SpeedDialAction
          key={action.name}
          icon={action.icon}
          onClick={() => {
            navigate(action.path);
          }}
          tooltipOpen
          tooltipTitle={action.name}
        />
      ))}
    </SpeedDial>
  );
};

export default QuickCreationHome;
