//import { Card, CardActions, CardContent, Typography, Grid, Alert} from "@mui/material";
import { GoogleLogin } from "@react-oauth/google";
import { avogadorApi } from "../../utils/axiosConf";
import { useState } from "react";

const LoginGoogle = () => {
  const [user, setUser] = useState<{ givenName: string; familyName: string }>();

  const googleSuccess = (response: string) => {
    console.log(response);
    console.log(`${import.meta.env.VITE_AVOGADOR_BACKEND_API_ADDRESS}`);

    // avogadorApi.post('/users/google-auth', {'googleToken': response})
    avogadorApi
      .post("http://localhost:8083/users/google-auth", {
        googleToken: response,
      })
      .then(({ data }: { data: { givenName: string; familyName: string } }) => {
        console.log(data);
        setUser(data);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  return (
    <div>
      TODO: nice card for this
      {user ? (
        <p>
          Hello {user.givenName} {user.familyName}
        </p>
      ) : (
        <></>
      )}
      <GoogleLogin
        onSuccess={(credentialResponse) => {
          if (credentialResponse.credential !== undefined) {
            googleSuccess(credentialResponse.credential);
          }
        }}
        onError={() => {
          console.log("Login Failed");
        }}
        useOneTap
      />
    </div>
  );
};

export default LoginGoogle;
