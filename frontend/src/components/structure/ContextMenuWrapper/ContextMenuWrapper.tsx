import React, { PropsWithChildren, ReactNode, useState } from "react";
import Menu from "@mui/material/Menu";
import Box from "@mui/material/Box";

interface ContextMenuWrapperProps extends PropsWithChildren {
  menu: ReactNode;
}

const ContextMenuWrapper = ({ children, menu }: ContextMenuWrapperProps) => {
  const [contextMenu, setContextMenu] = useState<{
    mouseX: number;
    mouseY: number;
  } | null>(null);

  const handleContextMenu = (event: React.MouseEvent) => {
    event.preventDefault();
    setContextMenu(
      contextMenu === null
        ? {
            mouseX: event.clientX + 2,
            mouseY: event.clientY - 6,
          }
        : null,
    );
  };

  const handleClose = () => {
    setContextMenu(null);
  };

  return (
    <>
      <Menu
        open={contextMenu !== null}
        onClose={handleClose}
        anchorReference="anchorPosition"
        onClickCapture={() => {
          handleClose();
        }}
        onContextMenu={(event) => {
          event.preventDefault();
          handleClose();
        }}
        anchorPosition={
          contextMenu !== null
            ? { top: contextMenu.mouseY, left: contextMenu.mouseX }
            : undefined
        }
      >
        {menu}
      </Menu>
      <Box onContextMenu={handleContextMenu} style={{ cursor: "context-menu" }}>
        {children}
      </Box>
    </>
  );
};

export default ContextMenuWrapper;
