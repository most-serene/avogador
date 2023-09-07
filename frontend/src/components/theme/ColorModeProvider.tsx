import { useAtom } from "jotai";
import colorModeAtom from "@theme/colorModeAtom.ts";
import { CssBaseline, ThemeProvider } from "@mui/material";
import { ReactNode } from "react";
import { createTheme } from "@mui/material/styles";

interface ColorModeProviderProps {
  children: ReactNode;
}

export default function ColorModeProvider({
  children,
}: ColorModeProviderProps) {
  const [colorMode] = useAtom(colorModeAtom);

  return (
    <ThemeProvider theme={theme(colorMode)}>
      <CssBaseline />
      {children}
    </ThemeProvider>
  );
}

const theme = (mode: "dark" | "light") =>
  createTheme({
    palette: {
      primary: {
        main: "#009393",
        contrastText: "#fff",
      },
      secondary: {
        light: "#cfcfcf",
        main: "#9e9e9e",
        dark: "#707070",
        contrastText: "#000",
      },
      background: {
        default: mode === "light" ? "#e0e0e0" : "#111111",
      },
      mode: mode,
    },
    shape: {
      borderRadius: 16,
    },
  });
