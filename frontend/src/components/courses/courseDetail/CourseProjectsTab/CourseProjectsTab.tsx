import { Card, CardContent, Divider, Stack, Typography } from "@mui/material";
import Box from "@mui/material/Box";

const ProjectItem = () => {
  return (
    <Card
      sx={{
        width: "30%",
        minWidth: { xl: "30%", lg: "45%", xs: "90%" },
        flexBasis: "auto",
        textOverflow: "ellipsis",
        position: "relative",
      }}
      raised
    >
      <CardContent>
        <Box
          display={"flex"}
          justifyContent="space-between"
          alignItems={"center"}
          sx={{ mb: 1 }}
        >
          <Typography variant="h5" fontWeight="bold" width={"70%"} noWrap>
            very very Very long Project Name
          </Typography>
          <Typography variant="body1" textAlign={"right"}>
            Due to
            <br /> YYYY/MM/DD
          </Typography>
        </Box>
        <Typography
          variant="body1"
          fontStyle="italic"
          sx={{
            display: "-webkit-box",
            textOverflow: "ellipsis",
            wordWrap: "break-word",
            overflow: "hidden",
            WebkitLineClamp: 4,
            lineClamp: 4,
            WebkitBoxOrient: "vertical",
          }}
        >
          Description? Ideally this is a long text that should explain what the
          Description? Ideally this is a long text that should explain what the
          Description? Ideally this is a long text that should explain what the
          Description? Ideally this is a long text that should explain what the
          project is about
        </Typography>
      </CardContent>
      <Box
        position={"absolute"}
        width={"100%"}
        style={{
          backgroundColor: "rgba(128, 128, 128, 0.5)",
          bottom: 0,
        }}
      >
        <Divider />
        <Box padding={1}>Last submission: YYYY/MM/DD hh:mm</Box>
      </Box>
    </Card>
  );
};

const CourseProjectsTab = () => {
  return (
    <Box width={"100%"} height="100%" paddingBottom={0}>
      <Card sx={{ height: "50%", m: 1, minHeight: "20rem", width: "100%" }}>
        <CardContent
          sx={{
            height: "100%",
            display: "flex",
            flexFlow: "column",
            width: "100%",
          }}
        >
          <Typography variant="h4" sx={{ mb: 1 }}>
            Ongoing
          </Typography>
          <Stack
            direction={"row"}
            spacing={2}
            sx={{
              width: "100%",
              py: 1,
              overflowX: "scroll",
              height: "100%",
            }}
            className={"hidden-scrollbar"}
          >
            <ProjectItem />
            <ProjectItem />
            <ProjectItem />
            <ProjectItem />
          </Stack>
        </CardContent>
      </Card>

      <Card sx={{ height: "50%", m: 1, minHeight: "20rem", width: "100%" }}>
        <CardContent
          sx={{
            height: "100%",
            display: "flex",
            flexFlow: "column",
            width: "100%",
          }}
        >
          <Typography variant="h4" sx={{ mb: 1 }}>
            Finished
          </Typography>
          <Stack
            direction={"row"}
            spacing={2}
            sx={{
              width: "100%",
              py: 1,
              overflowX: "scroll",
              height: "100%",
            }}
            className={"hidden-scrollbar"}
          >
            <ProjectItem />
            <ProjectItem />
            <ProjectItem />
            <ProjectItem />
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
};

export default CourseProjectsTab;
