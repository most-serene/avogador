import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import { Box, Button } from "@mui/material";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAvogadorApi } from "../../hooks/useAvogadorApi";

export default function Footer() {
  const [apiVersion, setApiVersion] = useState<string>("");
  const navigate = useNavigate();
  const avogadorApi = useAvogadorApi();

  useEffect(() => {
    avogadorApi
      .get("/")
      .then(({ data }) => {
        setApiVersion(data as string);
      })
      .catch(() => {
        setApiVersion("API Offline");
      });
  }, [avogadorApi]);

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
      <Container
        maxWidth="sm"
        style={{ display: "flex", justifyContent: "center" }}
      >
        <div>
          <Typography variant="body2" color="text.secondary" align="center">
            {apiVersion}
          </Typography>

          <Button
            sx={{ justifyContent: "center" }}
            onClick={() => {
              navigate("/status");
            }}
          >
            <Typography align="center">Current API status</Typography>
          </Button>
        </div>
      </Container>
    </Box>
  );
}
