import * as Sentry from "@sentry/browser";
import { useCallback } from "react";
import { User } from "@components/authentication/types";

const useGlitchTip = () => {
  const connectToGlitchTip = useCallback((appUser: User) => {
    if (!import.meta.env.PROD) return;

    Sentry.init({
      dsn: import.meta.env.VITE_GLITCHTIP_DSN as string,
      release: import.meta.env.APP_VERSION as string,
      environment: import.meta.env.MODE,
      autoSessionTracking: false,
      beforeSend(event) {
        if (event.exception) {
          Sentry.showReportDialog({
            eventId: event.event_id,
            user: {
              name: `${appUser.givenName} ${appUser.familyName}`,
              email: appUser.email,
            },
          });
        }
        return event;
      },
      initialScope: function (scope) {
        scope.setUser({
          id: appUser.id,
          email: appUser.email,
          username: `${appUser.givenName}-${appUser.familyName}`,
        });
        return scope;
      },
    });
  }, []);

  return {
    connectToGlitchTip,
  };
};

export default useGlitchTip;
