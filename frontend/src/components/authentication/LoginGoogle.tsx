//import { Card, CardActions, CardContent, Typography, Grid, Alert} from "@mui/material";
import { GoogleLogin } from "@react-oauth/google";

const LoginGoogle = () => {
  const googleSuccess = (response: string) => {
    console.log(response);
  };

  return (
    <div>
      TODO: nice card for this
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
