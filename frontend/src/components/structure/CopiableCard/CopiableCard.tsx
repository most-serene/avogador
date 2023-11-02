import { Card, IconButton, Typography } from "@mui/material";
import Box from "@mui/material/Box";
import { CopyAll } from "@mui/icons-material";

interface CopiableCardProps {
  children: string;
  fontFamily?: string;
}

const CopiableCard = ({ children: text, fontFamily }: CopiableCardProps) => {
  return (
    <Box position="relative">
      <IconButton
        onClick={() => void navigator.clipboard.writeText(text)}
        style={{ position: "absolute", top: 1, right: 1 }}
      >
        <CopyAll />
      </IconButton>
      <Card sx={{ p: 2 }}>
        <Typography
          fontFamily={fontFamily ?? ""}
          className="hidden-scrollbar"
          sx={{
            whiteSpace: "pre-wrap",
            maxHeight: "15rem",
            overflowY: "scroll",
          }}
        >
          {text}
        </Typography>
      </Card>
    </Box>
  );
};

export default CopiableCard;
