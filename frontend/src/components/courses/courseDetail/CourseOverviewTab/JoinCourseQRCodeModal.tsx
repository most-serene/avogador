import { UserCourseDetail } from "@courses/types";
import Box from "@mui/material/Box";
import QRCode from "react-qr-code";
import { Button, Modal } from "@mui/material";
import { Download } from "@mui/icons-material";
import { getJoinLink } from "@courses/courseDetail/CourseOverviewTab/utils";

const modalStyle = {
  position: "absolute" as const,
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  backgroundColor: "white",
  borderRadius: 1,
  boxShadow: 24,
  p: 4,
};

interface JoinCodeQRCodeModalProps {
  course: UserCourseDetail;
  showQrJoinCode: boolean;
  setShowQrJoinCode: (
    value: ((prevState: boolean) => boolean) | boolean,
  ) => void;
}

const downloadQrCode = (course: UserCourseDetail) => {
  const svg = document.getElementById("QRCode") as Node;
  const svgData = new XMLSerializer().serializeToString(svg);
  const canvas = document.createElement("canvas");

  const ctx = canvas.getContext("2d");
  if (ctx == null) return;
  const img = new Image();
  img.onload = () => {
    canvas.width = img.width;
    canvas.height = img.height;
    ctx.drawImage(img, 0, 0);
    const pngFile = canvas.toDataURL("image/png");
    const downloadLink = document.createElement("a");
    downloadLink.download = `join ${course.name}`;
    downloadLink.href = `${pngFile}`;
    downloadLink.click();
  };
  img.src = `data:image/svg+xml;base64,${btoa(svgData)}`;
};

const JoinCodeQRCodeModal = ({
  course,
  showQrJoinCode,
  setShowQrJoinCode,
}: JoinCodeQRCodeModalProps) => {
  return (
    <Modal
      open={showQrJoinCode}
      onClose={() => {
        setShowQrJoinCode(false);
      }}
      aria-labelledby="modal-modal-title"
      aria-describedby="modal-modal-description"
    >
      <Box sx={modalStyle}>
        <QRCode
          id="QRCode"
          style={{ height: "auto", maxWidth: "100%", width: "100%" }}
          value={getJoinLink(course)}
        />
        <Box display={"flex"} justifyContent={"center"} mt={".5rem"}>
          <Button
            variant={"outlined"}
            onClick={() => {
              downloadQrCode(course);
            }}
          >
            <Download /> Download
          </Button>
        </Box>
      </Box>
    </Modal>
  );
};

export default JoinCodeQRCodeModal;
