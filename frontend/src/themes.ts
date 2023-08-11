import { createTheme } from "@mui/material/styles";

export const darkTheme = createTheme({
  palette: {
    primary: {
      light: "#98ee99",
      main: "#42b883",
      dark: "#338a3e",
      contrastText: "#fff",
    },
    secondary: {
      light: "#cfcfcf",
      main: "#9e9e9e",
      dark: "#707070",
      contrastText: "#000",
    },
    mode: "dark",
  },
});

export const lightTheme = createTheme({
  palette: {
    primary: {
      light: "#98ee99",
      main: "#42b883",
      dark: "#338a3e",
      contrastText: "#eee",
    },
    secondary: {
      light: "#cfcfcf",
      main: "#9e9e9e",
      dark: "#707070",
      contrastText: "#000",
    },
    background: {
      default: "#e0e0e0",
    },
    mode: "light",
  },
});
