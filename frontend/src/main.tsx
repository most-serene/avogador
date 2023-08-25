import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { CssBaseline, ThemeProvider } from "@mui/material";
import { lightTheme } from "./themes.ts";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";

import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";
import { connectToGlitchTip } from "./conf/Glitchtip.ts";

const root = document.getElementById("root");

if (root) {
  connectToGlitchTip();

  ReactDOM.createRoot(root).render(
    <React.StrictMode>
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <ThemeProvider theme={lightTheme}>
          <CssBaseline />
          <GoogleOAuthProvider
            clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID as string}
          >
            <App />
          </GoogleOAuthProvider>
        </ThemeProvider>
      </LocalizationProvider>
    </React.StrictMode>,
  );
}
