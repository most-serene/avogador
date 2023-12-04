import { UserCourseDetail } from "@courses/types";
import Box from "@mui/material/Box";
import { Button, Card, CardContent, Typography } from "@mui/material";
import { enqueueSnackbar } from "notistack";
import { useState } from "react";
import { QrCode, Share } from "@mui/icons-material";
import { getJoinLink } from "@courses/courseDetail/CourseOverviewTab/utils";
import JoinCourseQRCodeModal from "@courses/courseDetail/CourseOverviewTab/JoinCourseQRCodeModal";
import InfoPoint from "@structure/InfoPoint/InfoPoint";

interface JoinCourseLinkCardProps {
  course: UserCourseDetail | undefined;
}
const copyJoinLink = (course: UserCourseDetail) => {
  navigator.clipboard
    .writeText(getJoinLink(course))
    .then(() =>
      enqueueSnackbar({
        message: "Join code copied to the clipboard",
        variant: "success",
      }),
    )
    .catch(() => {
      enqueueSnackbar({
        message: "Failed to copy join link",
        variant: "error",
      });
    });
};

const JoinCourseLinkCard = ({ course }: JoinCourseLinkCardProps) => {
  const [showQrJoinCode, setShowQrJoinCode] = useState(false);

  return (
    <Card sx={{ width: "100%" }} raised>
      <CardContent>
        <Box display={"flex"} justifyContent={"space-between"}>
          <Typography
            variant="h5"
            color="text.secondary"
            display={"inline"}
            gutterBottom
          >
            Join course link
          </Typography>
          <InfoPoint
            message={
              "Share this join code with your students to let them join this course."
            }
          />
        </Box>
        <Box display="flex" justifyContent="center" marginTop=".5rem">
          <Button
            variant={"outlined"}
            sx={{ mr: ".5rem" }}
            disabled={course === undefined}
            onClick={() => {
              if (course) copyJoinLink(course);
            }}
          >
            <Share /> Copy join link
          </Button>

          <Button
            variant={"outlined"}
            sx={{ ml: ".5rem" }}
            disabled={course === undefined}
            onClick={() => {
              setShowQrJoinCode(true);
            }}
          >
            <QrCode /> Generate QR code
          </Button>
        </Box>
      </CardContent>

      {course && (
        <JoinCourseQRCodeModal
          course={course}
          showQrJoinCode={showQrJoinCode}
          setShowQrJoinCode={setShowQrJoinCode}
        />
      )}
    </Card>
  );
};

export default JoinCourseLinkCard;
