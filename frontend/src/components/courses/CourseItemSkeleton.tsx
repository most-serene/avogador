import {
  Card,
  CardActionArea,
  CardContent,
  Skeleton,
  Typography,
} from "@mui/material";

export default function CourseItemSkeleton() {
  return (
    <Card>
      <CardActionArea disabled={true}>
        <CardContent>
          <Typography variant={"h5"}>
            <Skeleton />
          </Typography>
          <Typography variant={"body2"}>
            <Skeleton />
          </Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
}
