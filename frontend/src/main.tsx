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
import ColorModeProvider from "./components/theme/ColorModeProvider.tsx";
import { MsalProvider } from "@azure/msal-react";
import { PublicClientApplication } from "@azure/msal-browser";
import { msalConfig } from "@authentication/LoginMicrosoft/msalConfig.ts";
import { enGB } from "date-fns/locale";

const root = document.getElementById("root");

const msalInstance = new PublicClientApplication(msalConfig);

if (root) {
  ReactDOM.createRoot(root).render(
    <React.StrictMode>
      <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={enGB}>
        <ColorModeProvider>
          <GoogleOAuthProvider
            clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID as string}
          >
            <MsalProvider instance={msalInstance}>
              <App />
            </MsalProvider>
          </GoogleOAuthProvider>
        </ColorModeProvider>
      </LocalizationProvider>
    </React.StrictMode>,
  );
}
