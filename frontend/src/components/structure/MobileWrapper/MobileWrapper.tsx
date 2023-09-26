import { PropsWithChildren } from "react";
import Box from "@mui/material/Box";
import { Card, CardContent, Container, Typography } from "@mui/material";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom";

const MobileScreenErrorMessage = () => {
  return (
    <Container>
      <Card sx={{ marginTop: "2rem" }} raised>
        <CardContent>
          <Typography variant="h5" color="text.secondary" gutterBottom>
            Your screen is too narrow!
          </Typography>

          <Typography variant="body1" gutterBottom>
            The mobile mode is not supported yet, please open this page from
            your laptop.
          </Typography>
        </CardContent>
      </Card>
    </Container>
  );
};

const MobileWrapper = ({ children }: PropsWithChildren) => {
  const mode: string = import.meta.env.MODE;
  const [user] = useAtom(userAtom);

  if (mode !== "production" || (user && user.isSuperuser)) return children;

  return (
    <>
      <Box display={{ xs: "block", sm: "none" }}>
        <MobileScreenErrorMessage />
      </Box>

      <Box display={{ xs: "none", sm: "block" }}>{children}</Box>
    </>
  );
};

export default MobileWrapper;
