import Grid from "@mui/material/Grid";
import TestcaseStack from "@exercises/exerciseCreation/testcases/TestcaseStack.tsx";
import { useState } from "react";
import IOEditor from "@exercises/exerciseCreation/testcases/IOEditor.tsx";

const ExerciseCreationTestcases = () => {
  const [selected, setSelected] = useState<number>();

  const handleSelectedChange = (i: number | undefined) => {
    setSelected(i);
  };

  return (
    <Grid container spacing={2} style={{ height: "100%" }}>
      <Grid item xs={6} style={{ height: "100%" }}>
        <TestcaseStack selected={selected} onSelect={handleSelectedChange} />
      </Grid>
      <Grid item xs={6}>
        {selected != null ? (
          <IOEditor selected={selected} />
        ) : (
          "Select a testcase"
        )}
      </Grid>
    </Grid>
  );
};

export default ExerciseCreationTestcases;
