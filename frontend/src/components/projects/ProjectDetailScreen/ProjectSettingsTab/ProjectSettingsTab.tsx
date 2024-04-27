import { Project } from "@components/projects/types.ts";
import {
  Card,
  CardContent,
  Container,
  Divider,
  Grid,
  TextField,
  Typography,
} from "@mui/material";
import Box from "@mui/material/Box";
import { DateTimePicker } from "@mui/x-date-pickers";
import { setSeconds } from "date-fns";
import ButtonWithConfirmation from "@structure/ButtonWithConfirmation/ButtonWithConfirmation.tsx";
import { useMemo, useState } from "react";
import MarkdownEditor from "@structure/editors/MarkdownEditor.tsx";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";
import { enqueueSnackbar } from "notistack";

interface ProjectSettingsTabProps {
  project: Project;
  onUpdate: (project: Project) => void;
}
const ProjectSettingsTab = ({ project, onUpdate }: ProjectSettingsTabProps) => {
  const { updateProject } = useProjectService();

  const [name, setName] = useState<string>(project.name);
  const [deadline, setDeadline] = useState<Date>(project.deadline);
  const [description, setDescription] = useState<string>(project.description);

  const handleUpdate = () => {
    updateProject({
      ...project,
      name: name,
      deadline: deadline,
      description: description,
    })
      .then((project: Project) => {
        enqueueSnackbar(`Project ${project.name} updated successfully`, {
          variant: "success",
        });
        onUpdate(project);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, {
          variant: "error",
        });
      });
  };

  const handleDelete = () => {
    // empty function
  };

  const isEdited = useMemo(() => {
    return (
      project.name !== name ||
      project.deadline !== deadline ||
      project.description !== description
    );
  }, [project, name, description, deadline]);

  return (
    <Container>
      <Card>
        <CardContent>
          <Typography variant="h6" sx={{ mb: "1rem" }}>
            Update Practice
          </Typography>

          <Grid container spacing={2}>
            <Grid item xs={8}>
              <TextField
                error={name.trim() === ""}
                helperText={
                  name.trim() === "" ? "Name cannot be empty" : undefined
                }
                fullWidth
                label="Project name"
                value={name}
                onChange={(event) => {
                  setName(event.target.value);
                }}
              />
            </Grid>
            <Grid item xs={4}>
              <DateTimePicker
                sx={{ width: "100%" }}
                ampm={false}
                disablePast
                minDateTime={new Date()}
                value={setSeconds(deadline, 0)}
                onChange={(newVal) => {
                  if (newVal == null) return;
                  setDeadline(setSeconds(newVal, 0));
                }}
                label="Deadline"
              />
            </Grid>
            <Grid item xs={12}>
              <MarkdownEditor
                value={description}
                onChange={(markdown) => {
                  setDescription(markdown);
                }}
              />
            </Grid>
          </Grid>

          <Box display={"flex"} justifyContent={"center"} margin={"1rem"}>
            <ButtonWithConfirmation
              disabled={name.trim() === "" || !isEdited}
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
              onConfirm={handleDelete}
              disabled={true /*TODO: Implement delete*/}
              color="error"
              variant="outlined"
              confirmText="Delete"
              confirmColor="error"
              title={`You are deleting ${project.name}`}
              description={`Are you sure to delete the project ${project.name}?
                       All submissions and results will be lost.`}
            >
              Delete
            </ButtonWithConfirmation>
          </Box>
        </CardContent>
      </Card>
    </Container>
  );
};

export default ProjectSettingsTab;
