import Typography from "@mui/material/Typography";
import { Button, Container, Grid, Paper, Stack } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { forwardRef } from "react";

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
        <Grid container display={"flex"}>
          <Grid item xs={4}></Grid>
          <Grid
            item
            xs={4}
            display={"flex"}
            justifyContent={"center"}
            alignContent={"center"}
          >
            <Button
              sx={{ justifyContent: "center", margin: 0 }}
              onClick={() => {
                navigate("/status");
              }}
            >
              <Typography align="center" margin={0}>
                System status
              </Typography>
            </Button>
          </Grid>
          <Grid
            item
            xs={4}
            display={"flex"}
            justifyContent={"flex-end"}
            alignContent={"center"}
          >
            <Stack direction="column" justifyContent="center">
              <Typography variant="body2" color="text.secondary">
                {import.meta.env.APP_VERSION}
              </Typography>
            </Stack>
          </Grid>
        </Grid>
      </Container>
    </Paper>
  );
});

export default Footer;
