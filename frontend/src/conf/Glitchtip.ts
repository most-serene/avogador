import * as Sentry from "@sentry/browser";

export const connectToGlitchTip = () => {
  if (!import.meta.env.PROD) return;

  Sentry.init({
    dsn: import.meta.env.VITE_GLITCHTIP_DSN as string,
    release: import.meta.env.APP_VERSION as string,
    environment: import.meta.env.MODE,
    autoSessionTracking: false,
  });
};
