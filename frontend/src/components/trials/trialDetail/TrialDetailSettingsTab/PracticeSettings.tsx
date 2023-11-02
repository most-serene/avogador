import { Practice } from "@trials/types.ts";
import { useState } from "react";
import {
  Card,
  CardContent,
  Container,
  Divider,
  Grid,
  TextField,
  Typography,
} from "@mui/material";
import { DateTimePicker } from "@mui/x-date-pickers";
import { min, setSeconds } from "date-fns";
import Box from "@mui/material/Box";
import ButtonWithConfirmation from "@structure/ButtonWithConfirmation/ButtonWithConfirmation.tsx";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { enqueueSnackbar } from "notistack";

interface PracticeSettingsProps {
  practice: Practice;
}

const PracticeSettings = ({ practice }: PracticeSettingsProps) => {
  const { updatePractice } = useTrialService();
  const [name, setName] = useState(practice.name);
  const [startDate, setStartDate] = useState(practice.startTimestamp);
  const [deadline, setDeadline] = useState(practice.deadline);
  const [isUpdateDisabled, setIsUpdateDisabled] = useState(false);

  const handleUpdate = () => {
    setIsUpdateDisabled(true);
    updatePractice({
      ...practice,
      name: name,
      startTimestamp: startDate,
      deadline: deadline,
    })
      .then(() => {
        enqueueSnackbar("Practice updated successfully", {
          variant: "success",
        });
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      })
      .finally(() => {
        setIsUpdateDisabled(false);
      });
  };

  const handleDelete = () => {
    // TODO: implement delete
  };

  return (
    <Container sx={{ height: "100%" }}>
      <Card>
        <CardContent sx={{ height: "100%" }}>
          <Typography variant="h6">Update Practice</Typography>

          <TextField
            fullWidth
            sx={{ marginTop: "1rem" }}
            label="Trial name"
            value={name}
            onChange={(event) => {
              setName(event.target.value);
            }}
          />

          <Box marginY={2} />

          <Grid container spacing={2}>
            <Grid item xs={6}>
              <DateTimePicker
                sx={{ width: "100%" }}
                ampm={false}
                minDateTime={min([new Date(), practice.startTimestamp])}
                value={setSeconds(startDate, 0)}
                onChange={(newVal) => {
                  if (newVal) setStartDate(setSeconds(newVal, 0));
                }}
                label="Start timestamp"
                maxDate={deadline}
              />
            </Grid>
            <Grid item xs={6}>
              <DateTimePicker
                sx={{ width: "100%" }}
                ampm={false}
                value={setSeconds(deadline, 0)}
                onChange={(newVal) => {
                  if (newVal) setDeadline(setSeconds(newVal, 0));
                }}
                disablePast
                label="Deadline"
                minDate={startDate}
              />
            </Grid>
          </Grid>

          <Box display={"flex"} justifyContent={"center"} margin={"1rem"}>
            <ButtonWithConfirmation
              disabled={
                isUpdateDisabled ||
                ((name === practice.name || name.trim() === "") &&
                  startDate === practice.startTimestamp &&
                  deadline === practice.deadline)
              }
              onConfirm={handleUpdate}
              variant="outlined"
              confirmText="Update"
              description="You are about to update this practice"
            >
              Update
            </ButtonWithConfirmation>
          </Box>

          <Divider sx={{ mt: 6 }} />
          <Typography variant="h6">Delete Practice</Typography>
          <Box display={"flex"} justifyContent={"center"}>
            <ButtonWithConfirmation
              disabled
              onConfirm={handleDelete}
              color="error"
              variant="outlined"
              confirmText="Delete"
              confirmColor="error"
              description="You are about to delete this practice"
            >
              Delete
            </ButtonWithConfirmation>
          </Box>
        </CardContent>
      </Card>
    </Container>
  );
};

export default PracticeSettings;
