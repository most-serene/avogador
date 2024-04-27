import { Box } from "@mui/material";
import { ReactNode } from "react";

interface TabPanelProps {
  children?: ReactNode;
  index: number;
  value: number;
  occupiedHeight?: number;
  padding?: number;
}

export default function TabPanel(props: TabPanelProps) {
  const {
    children,
    value,
    index,
    occupiedHeight = 0,
    padding,
    ...other
  } = props;

  return (
    <Box
      role="tabpanel"
      hidden={value !== index}
      id={`vertical-tabpanel-${index}`}
      aria-labelledby={`vertical-tab-${index}`}
      {...other}
      height={`calc(100% - ${occupiedHeight}px)`}
      sx={{ overflowY: "scroll" }}
      className={"hidden-scrollbar"}
    >
      {value === index && (
        <Box padding={padding ?? 3} height="100%">
          {children}
        </Box>
      )}
    </Box>
  );
}
