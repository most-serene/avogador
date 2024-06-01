import { Button } from "@mui/material";
import { useNavigate } from "react-router-dom";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";

interface BackButtonProps {
  children: string;
  destination: string;
}
const BackButton = ({ children, destination }: BackButtonProps) => {
  const navigate = useNavigate();
  return (
    <Button
      sx={{ position: { md: "absolute", xs: "static" }, left: 0, ml: 2 }}
      variant={"outlined"}
      startIcon={<ArrowBackIosNewIcon />}
      onClick={() => {
        navigate(destination);
      }}
    >
      {children.length > 20 ? children.substring(0, 18) + "..." : children}
    </Button>
  );
};

export default BackButton;
