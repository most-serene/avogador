import { GetCoursesDetailResponse } from "@courses/types";
import Box from "@mui/material/Box";
import {
  Button,
  Card,
  CardContent,
  Modal,
  Paper,
  Typography,
} from "@mui/material";
import { enqueueSnackbar } from "notistack";
import { useState } from "react";

interface JoinCourseLinkVisualizerProps {
  course: GetCoursesDetailResponse | undefined;
}

const JoinCourseLinkVisualizer = ({
  course,
}: JoinCourseLinkVisualizerProps) => {
  const [showJoinCode, setShowJoinCode] = useState(false);

  const modalStyle = {
    position: "absolute" as const,
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    bgcolor: "background.paper",
    borderRadius: 1,
    boxShadow: 24,
    p: 4,
  };

  return (
    <Card sx={{ width: "32rem" }} raised>
      <CardContent>
        <Box display="flex" justifyContent="center" marginTop=".5rem">
          <Button
            size={"large"}
            variant={"outlined"}
            onClick={() => {
              setShowJoinCode(!showJoinCode);
            }}
            sx={{ ml: ".5rem" }}
          >
            show join code
          </Button>
        </Box>
      </CardContent>

      <Modal
        open={showJoinCode}
        onClose={() => {
          setShowJoinCode(false);
        }}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={modalStyle}>
          <Typography variant="body1" gutterBottom>
            Share this join code with your students to let them join this
            course.
          </Typography>
          <Paper variant={"outlined"}>
            <Typography
              display={"flex"}
              justifyContent={"center"}
              variant="caption"
            >
              {course?.joinCode}
            </Typography>
          </Paper>
          <Box display="flex" justifyContent="center" marginTop=".5rem">
            <Button
              variant={"outlined"}
              sx={{ mr: ".5rem" }}
              onClick={() => {
                navigator.clipboard
                  .writeText(
                    `https://${window.location.hostname}/courses/${course?.id}/join?code=${course?.joinCode}`,
                  )
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
              }}
            >
              Copy join link
            </Button>
            <Button variant={"outlined"} sx={{ ml: ".5rem" }}>
              Generate QR code
            </Button>
          </Box>
        </Box>
      </Modal>
    </Card>
  );
};

export default JoinCourseLinkVisualizer;
