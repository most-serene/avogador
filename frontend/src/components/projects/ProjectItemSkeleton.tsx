import { Card, CardContent, Skeleton, Typography } from "@mui/material";
import Box from "@mui/material/Box";

const ProjectItemSkeleton = () => {
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
      <CardContent sx={{ height: "100%" }}>
        <Box
          display={"flex"}
          justifyContent="space-between"
          alignItems={"center"}
          sx={{ mb: 1 }}
        >
          <Typography variant="h5" fontWeight="bold" width={"60%"} noWrap>
            <Skeleton />
          </Typography>
          <div>
            <Typography variant="body1">
              <Skeleton sx={{ ml: "4rem" }} />
            </Typography>
            <Typography variant="body1" width={"8rem"}>
              <Skeleton />
            </Typography>
          </div>
        </Box>
        <Typography variant="body1" fontStyle="italic">
          <Skeleton />
          <Skeleton />
          <Skeleton />
        </Typography>
      </CardContent>
    </Card>
  );
};

export default ProjectItemSkeleton;
