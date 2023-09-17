import { InfoOutlined } from "@mui/icons-material";
import { Tooltip } from "@mui/material";

const InfoPoint = ({ message }: { message: string }) => {
  return (
    <Tooltip title={message}>
      <InfoOutlined color={"primary"} />
    </Tooltip>
  );
};

export default InfoPoint;
