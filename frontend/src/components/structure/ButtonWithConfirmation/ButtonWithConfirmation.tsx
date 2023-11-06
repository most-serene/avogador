import { ReactNode, SyntheticEvent, useState } from "react";
import {
  Button,
  Card,
  CardActions,
  CardContent,
  IconButton,
  Modal,
  Typography,
} from "@mui/material";

interface ButtonWithConfirmationProps {
  as?: "IconButton" | "Button" | "Plain";
  confirmColor?: "primary" | "secondary" | "error" | "success";
  color?: "primary" | "secondary" | "error" | "success";
  variant?: "outlined" | "contained" | "text";
  confirmText?: string;
  title?: string;
  description?: string;
  disabled?: boolean;
  onConfirm: (event: SyntheticEvent) => void;
  children: ReactNode;
}

const style = {
  position: "absolute" as const,
  width: "30rem",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
};

const ButtonWithConfirmation = ({
  as: buttonType = "Button",
  confirmColor = "primary",
  color,
  variant = "text",
  confirmText = "Confirm",
  title = "Are you sure?",
  description,
  disabled = false,
  onConfirm,
  children,
}: ButtonWithConfirmationProps) => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleOpen = () => {
    setIsModalOpen(true);
  };

  const handleClose = () => {
    setIsModalOpen(false);
  };

  return (
    <>
      {buttonType === "Button" && (
        <Button
          color={color}
          disabled={disabled}
          variant={variant}
          onClick={handleOpen}
        >
          {children}
        </Button>
      )}
      {buttonType === "IconButton" ? (
        <IconButton color={color} disabled={disabled} onClick={handleOpen}>
          {children}
        </IconButton>
      ) : (
        // eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-static-element-interactions
        <div onClick={handleOpen}>{children}</div>
      )}
      {isModalOpen && (
        <Modal
          open={isModalOpen}
          onClose={handleClose}
          aria-labelledby="parent-modal-title"
          aria-describedby="parent-modal-description"
        >
          <Card style={{ ...style }}>
            <CardContent>
              <Typography variant="h4" id="parent-modal-title" sx={{ mb: 1 }}>
                {title}
              </Typography>
              <Typography variant="body1">{description}</Typography>
            </CardContent>
            <CardActions style={{ float: "right" }}>
              <Button onClick={handleClose}>Cancel</Button>

              <Button
                onClick={(event) => {
                  onConfirm(event);
                  handleClose();
                }}
                color={confirmColor}
                variant="contained"
              >
                {confirmText}
              </Button>
            </CardActions>
          </Card>
        </Modal>
      )}
    </>
  );
};

export default ButtonWithConfirmation;
