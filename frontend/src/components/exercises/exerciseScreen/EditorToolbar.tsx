import { Card, IconButton, Tooltip, Typography } from "@mui/material";
import { Replay } from "@mui/icons-material";
import Box from "@mui/material/Box";

interface EditorToolbarProps {
  onReset: () => void;
  fileName: string;
}

const EditorToolbar = ({
  onReset: handleReset,
  fileName,
}: EditorToolbarProps) => {
  return (
    <Card
      sx={{
        borderBottom: 1,
        borderBottomColor: "secondary.dark",
        py: 0.5,
        px: 2,
        borderBottomRightRadius: 0,
        borderBottomLeftRadius: 0,
        ml: "3px",
        display: "flex",
        alignItems: "center",
      }}
    >
      <Typography fontFamily="monospace">{fileName}</Typography>
      <Box sx={{ marginLeft: "auto" }}>
        <Tooltip title="Reset code" placement="left">
          <IconButton onClick={handleReset}>
            <Replay />
          </IconButton>
        </Tooltip>
      </Box>
    </Card>
  );
};

export default EditorToolbar;
