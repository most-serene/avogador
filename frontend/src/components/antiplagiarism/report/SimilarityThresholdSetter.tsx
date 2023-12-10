import {
  Box,
  Card,
  CardContent,
  Divider,
  Slider,
  Typography,
} from "@mui/material";
import { format } from "date-fns";
import { PlagiarismReport } from "@components/antiplagiarism/types.ts";
import React from "react";

interface SimilarityThresholdSetterProps {
  report: PlagiarismReport;
  setThreshold: React.Dispatch<React.SetStateAction<number>>;
}

const SimilarityThresholdSetter = ({
  report,
  setThreshold,
}: SimilarityThresholdSetterProps) => {
  return (
    <Card>
      <CardContent>
        <Typography>
          Execution date: {format(report.executionDate, "dd/MM/yyyy HH:mm:ss")}
        </Typography>
        <Divider sx={{ mb: 1 }} />
        <Typography>Alert threshold:</Typography>
        <Box display={"flex"} justifyContent={"center"}>
          <Slider
            onChange={(_event, newVal) => {
              setThreshold(newVal as number);
            }}
            defaultValue={80}
            valueLabelDisplay="auto"
            min={0}
            style={{
              width: "70%",
            }}
            max={100}
            marks={[
              {
                value: 0,
                label: "0%",
              },
              {
                value: 100,
                label: "100%",
              },
            ]}
          />
        </Box>
      </CardContent>
    </Card>
  );
};

export default SimilarityThresholdSetter;
