import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import { Box, Link } from "@mui/material";
import { useEffect, useState } from "react";
import { avogadorApi } from "../../utils/axiosConf.ts";

export default function Footer() {
  const [apiVersion, setApiVersion] = useState<string>("");

  useEffect(() => {
    avogadorApi
      .get("/")
      .then(({ data }) => {
        setApiVersion(data as string);
      })
      .catch(() => {
        setApiVersion("API Offline");
      });
  }, []);

  return (
    <Box
      sx={{
        backgroundColor: (theme) =>
          theme.palette.mode === "light"
            ? theme.palette.grey[200]
            : theme.palette.grey[800],
        p: 3,
      }}
      component="footer"
    >
      <Container maxWidth="sm">
        <Typography variant="body2" color="text.secondary" align="center">
          {apiVersion}
        </Typography>
        <Typography align="center">
          <Link href={"/status"}>Current API status</Link>
        </Typography>
      </Container>
    </Box>
  );
}
