import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { CssBaseline, ThemeProvider } from "@mui/material";
import { lightTheme } from "./themes.ts";

const root = document.getElementById("root");

if (root) {
  ReactDOM.createRoot(root).render(
    <React.StrictMode>
      <ThemeProvider theme={lightTheme}>
        <CssBaseline />
        <GoogleOAuthProvider
          clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID as string}
        >
          <App />
        </GoogleOAuthProvider>
      </ThemeProvider>
    </React.StrictMode>,
  );
}
