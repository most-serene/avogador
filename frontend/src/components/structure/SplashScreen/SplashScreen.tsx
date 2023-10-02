import { LinearProgress, useTheme } from "@mui/material";
import Box from "@mui/material/Box";
import Logo from "@assets/images/logo.png";
import TextLogo from "@assets/textLogo.svg";
import TextLogoDark from "@assets/textLogoDark.svg";

const SplashScreen = () => {
  const theme = useTheme();
  return (
    <Box
      display={"flex"}
      justifyContent={"center"}
      alignContent={"center"}
      alignItems={"center"}
      className={"full-page-without-header-and-footer"}
    >
      <Box height={"80%"}>
        <Box
          component="img"
          style={{ overflow: "hidden", width: "auto", height: "60%" }}
          src={Logo}
        />
        <Box
          component="img"
          src={theme.palette.mode === "light" ? TextLogo : TextLogoDark}
        />
        <LinearProgress />
      </Box>
    </Box>
  );
};

export default SplashScreen;
