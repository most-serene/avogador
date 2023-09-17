import { Alert, Box, Button, IconButton, Modal } from "@mui/material";
import Typography from "@mui/material/Typography";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import { useCallback, useEffect, useState } from "react";

const style = {
  position: "absolute" as const,
  top: "50%",
  left: "50%",
  border: 1,
  transform: "translate(-50%, -50%)",
  bgcolor: "background.paper",
  boxShadow: 24,
  p: 4,
};

interface ApiKeyModalProps {
  apiKey: string | undefined;
}

export default function ApiKeyModal({ apiKey }: ApiKeyModalProps) {
  const [keyState, setKeyState] = useState<string | undefined>();

  useEffect(() => {
    setKeyState(apiKey);
  }, [apiKey]);

  const handleOnClose = () => {
    setKeyState(undefined);
  };

  const copyKeyToClipboard = useCallback(() => {
    if (keyState !== undefined) {
      navigator.clipboard
        .writeText(keyState)
        .then((res) => {
          console.log(res);
        })
        .catch((err) => {
          console.log(err);
        });
    }
  }, [keyState]);

  return (
    <Modal open={keyState !== undefined} onClose={handleOnClose}>
      <Box sx={style}>
        <Typography fontWeight={"bold"} align={"center"}>
          Once you close this modal, the key will disappear forever (a very long
          time!)
        </Typography>
        <Alert
          sx={{ m: 2 }}
          action={
            <IconButton
              onClick={(event) => {
                event.preventDefault();
                copyKeyToClipboard();
              }}
            >
              <ContentCopyIcon />
            </IconButton>
          }
          severity="info"
        >
          Key Generated: {apiKey}
        </Alert>
        <Box display={"flex"} justifyContent={"flex-end"}>
          <Button onClick={handleOnClose} color={"error"}>
            Close
          </Button>
        </Box>
      </Box>
    </Modal>
  );
}
