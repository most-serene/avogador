import {
  Alert,
  Button,
  Card,
  CardContent,
  LinearProgress,
  Stack,
} from "@mui/material";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import useProjectService from "../hooks/useProjectService.tsx";
import { useState } from "react";

const ProjectUploadForm = () => {
  const { uploadProject } = useProjectService();
  const [files, setFiles] = useState<FileList | null>(null);
  const [progress, setProgress] = useState(0);

  return (
    <Card style={{ width: "32rem" }}>
      <CardContent>
        <Stack spacing={2}>
          <Alert severity="warning">
            This feature is still under heavy development
          </Alert>
          <input
            type={"file"}
            {...{
              webkitdirectory: "",
              mozdirectory: "",
              directory: "",
            }}
            onChange={(event) => {
              setFiles(event.target.files);
            }}
          />
          <Button
            variant="outlined"
            startIcon={<CloudUploadIcon />}
            disabled={files == null}
            onClick={() => {
              if (files == null) return;
              uploadProject(
                "85b7515c-9e82-43f1-99ab-9c21e9fe1a1d",
                files,
                (progressEvent) => {
                  if (progressEvent.total == null) return;
                  setProgress(
                    (100 * progressEvent.loaded) / progressEvent.total,
                  );
                },
              );
              setFiles(null);
            }}
          >
            Upload Project
          </Button>
          <LinearProgress variant="determinate" value={progress} />
        </Stack>
      </CardContent>
    </Card>
  );
};

export default ProjectUploadForm;
