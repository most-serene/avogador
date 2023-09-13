import { useState } from "react";
import { IconButton, TextField } from "@mui/material";
import { ChevronLeft, ChevronRight } from "@mui/icons-material";
import { getCourseYear } from "@structure/AcademicYearPicker/utils.ts";

interface YearPickerProps {
  value: string;
  onChange: (val: string) => void;
  error: boolean;
}

const AcademicYearPicker = ({
  value: year,
  onChange: update,
  error,
}: YearPickerProps) => {
  const [currentYearDelta, setCurrentYearDelta] = useState(0);
  const updateYearDelta = (newDelta: 1 | -1) => {
    update(getCourseYear(currentYearDelta + newDelta));
    setCurrentYearDelta(currentYearDelta + newDelta);
  };

  return (
    <TextField
      error={error}
      fullWidth
      label="Year"
      value={year}
      inputProps={{ style: { textAlign: "center" } }}
      onChange={(event) => {
        event.preventDefault();
      }}
      InputProps={{
        startAdornment: (
          <IconButton
            disabled={currentYearDelta < 0}
            onClick={() => {
              updateYearDelta(-1);
            }}
          >
            <ChevronLeft fontSize="small" />
          </IconButton>
        ),
        endAdornment: (
          <IconButton
            onClick={() => {
              updateYearDelta(1);
            }}
          >
            <ChevronRight fontSize="small" />
          </IconButton>
        ),
      }}
    />
  );
};

export default AcademicYearPicker;
