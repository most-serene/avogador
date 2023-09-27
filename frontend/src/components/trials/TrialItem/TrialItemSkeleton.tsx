import {
  Card,
  CardActionArea,
  CardContent,
  Skeleton,
  Typography,
} from "@mui/material";
const TrialItemSkeleton = () => {
  return (
    <Card raised sx={{ mb: 2 }}>
      <CardActionArea>
        <CardContent>
          <Typography variant="h5" marginBottom={1}>
            <Skeleton />
          </Typography>
          <Typography variant="body1">
            <Skeleton />
          </Typography>
          <Typography variant="body1">
            <Skeleton />
          </Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};

export default TrialItemSkeleton;
