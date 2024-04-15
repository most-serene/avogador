import { FormControl, InputLabel, Select } from "@mui/material";
import MenuItem from "@mui/material/MenuItem";
import Grid from "@mui/material/Grid";
import { NotebookData, NotebookKernel } from "@components/projects/types.ts";
import { useState } from "react";

interface NotebookCreationInfoProps {
  onChange: (value: NotebookData) => void;
}

const NotebookCreationInfo = ({
  onChange: handleChange,
}: NotebookCreationInfoProps) => {
  const [notebookData, setNotebookData] = useState<NotebookData>({
    kernel: undefined,
    isValid: false,
  });

  const isValid = (data: NotebookData) => {
    return data.kernel !== undefined;
  };

  return (
    <Grid item xs={12}>
      <FormControl fullWidth>
        <InputLabel id="kernel">Kernel</InputLabel>
        <Select
          labelId="lernel"
          value={notebookData.kernel ?? ""}
          onChange={(event) => {
            const updatedState = {
              ...notebookData,
              kernel: event.target.value as NotebookKernel,
            };
            updatedState.isValid = isValid(updatedState);
            setNotebookData(updatedState);
            handleChange(updatedState);
          }}
          id="kernel"
          label="kernel"
        >
          <MenuItem value={"IPYKERNEL"}>IPyKernel</MenuItem>
        </Select>
      </FormControl>
    </Grid>
  );
};

export default NotebookCreationInfo;
