import { Box } from "@mui/material";
import { ReactNode, useLayoutEffect, useState } from "react";

interface TabPanelProps {
  children?: ReactNode;
  index: number;
  value: number;
}

export default function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;
  const [occupiedHeight, setOccupiedHeight] = useState(0);

  useLayoutEffect(() => {
    const courseTitle =
      document.getElementById("courseTitle")?.getBoundingClientRect().height ??
      0;
    setOccupiedHeight(courseTitle);
  }, []);

  return (
    <Box
      role="tabpanel"
      hidden={value !== index}
      id={`vertical-tabpanel-${index}`}
      aria-labelledby={`vertical-tab-${index}`}
      {...other}
      height={`calc(100% - ${occupiedHeight}px)`}
    >
      {value === index && (
        <Box padding={3} height="100%">
          {children}
        </Box>
      )}
    </Box>
  );
}
