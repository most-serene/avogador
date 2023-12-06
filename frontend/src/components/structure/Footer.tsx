import Typography from "@mui/material/Typography";
import { Button, Container, Paper } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { forwardRef } from "react";
import Box from "@mui/material/Box";

const Footer = forwardRef<HTMLElement>(function Footer(_, ref) {
  const navigate = useNavigate();

  return (
    <Paper
      ref={ref}
      sx={{
        backgroundColor: (theme) =>
          theme.palette.mode === "light"
            ? theme.palette.grey[200]
            : theme.palette.grey[800],
        width: "100%",
        position: "fixed",
        bottom: 0,
        marginTop: 0,
      }}
      component="footer"
      square
      variant="outlined"
    >
      <Container maxWidth={false}>
        <Box
          display="flex"
          justifyContent="center"
          alignItems="center"
          position="relative"
        >
          <Button
            sx={{ justifyContent: "center", margin: 0 }}
            onClick={() => {
              navigate("/status");
            }}
          >
            System status
          </Button>
          <Typography
            variant="body2"
            color="text.secondary"
            style={{ float: "right", position: "absolute", right: 0 }}
          >
            {import.meta.env.APP_VERSION}
          </Typography>
        </Box>
      </Container>
    </Paper>
  );
});

export default Footer;
