import LoginGoogle from "../LoginGoogle/LoginGoogle";
import Box from "@mui/material/Box";

export const LoginPage = () => {
  return (
    <Box display="flex" justifyContent="center" marginTop={"2rem"}>
      <LoginGoogle />
    </Box>
  );
};
