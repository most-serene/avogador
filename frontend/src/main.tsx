import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";

import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";
import { connectToGlitchTip } from "./conf/Glitchtip.ts";
import ColorModeProvider from "./components/theme/ColorModeProvider.tsx";

const root = document.getElementById("root");

if (root) {
  connectToGlitchTip();

  ReactDOM.createRoot(root).render(
    <React.StrictMode>
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <ColorModeProvider>
          <GoogleOAuthProvider
            clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID as string}
          >
            <App />
          </GoogleOAuthProvider>
        </ColorModeProvider>
      </LocalizationProvider>
    </React.StrictMode>,
  );
}
