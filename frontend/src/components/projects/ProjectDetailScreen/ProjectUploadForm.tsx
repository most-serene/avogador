import {
  Backdrop,
  Box,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Divider,
  Typography,
} from "@mui/material";
import useProjectService from "../hooks/useProjectService.tsx";
import { ReactNode, useMemo, useRef, useState } from "react";
import { Project, ProjectSubmission } from "@components/projects/types.ts";
import { enqueueSnackbar } from "notistack";

interface ProjectUploadFormProps {
  project: Project;
  children: ReactNode;
  onUpload: (submission: ProjectSubmission) => void;
  disabled: boolean;
}

const ProjectUploadForm = ({
  project,
  children,
  onUpload,
  disabled,
}: ProjectUploadFormProps) => {
  const { uploadProject } = useProjectService();
  const [showFileInput, setShowFileInput] = useState(false);
  const [files, setFiles] = useState<FileList | null>(null);
  const [progress, setProgress] = useState<number>();
  const [eta, setEta] = useState<number>();
  const isProjectOver = useMemo(() => project.deadline < new Date(), [project]);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleUpload = () => {
    if (files == null) return;
    uploadProject(
      project.id,
      files,
      ({ loaded, total, estimated }) => {
        setEta(estimated);
        if (total != null) {
          setProgress((100 * loaded) / total);
        }
      },
      () => {
        setProgress(undefined);
        setFiles(null);
      },
    )
      .then((response) => {
        onUpload(response);
      })
      .catch((err: Error) => {
        console.error(err);
        enqueueSnackbar(err.message, {
          variant: "error",
        });
      });
  };

  return (
    <>
      <Backdrop open={progress != null} sx={{ zIndex: 10, flexFlow: "column" }}>
        <Box position={"relative"}>
          <CircularProgress
            color="primary"
            size={"5rem"}
            variant={
              Math.floor(progress ?? 0) == 100 ? "indeterminate" : "determinate"
            }
            value={progress}
          />
          <Box
            sx={{
              top: 0,
              left: 0,
              width: "100%",
              height: "100%",
              position: "absolute",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Typography variant="h6">{Math.floor(progress ?? 0)}%</Typography>
          </Box>
        </Box>
        {Math.floor(progress ?? 0) !== 100 && (
          <Typography textAlign={"center"}>
            Remaining: {Math.round(eta ?? 0)}s
          </Typography>
        )}
      </Backdrop>

      <Card
        sx={{ height: "100%", position: "relative" }}
        onDragOver={(event) => {
          if (disabled) return;
          event.preventDefault();
          const dt = event.dataTransfer;
          if (dt.types.includes("Files")) {
            setShowFileInput(true);
          }
        }}
        onDragLeave={() => {
          if (disabled) return;
          setShowFileInput(false);
        }}
      >
        <Box
          sx={{
            display: showFileInput ? "block" : "none",
            position: "absolute",
            left: 0,
            top: 0,
            width: "100%",
            height: "100%",
            backgroundColor: "rgba(0,0,0, 0.5)",
          }}
        >
          <Box
            sx={{
              borderRadius: 1,
              m: "1.5rem",
              border: "dashed grey 2px",
              width: "calc(100%- 3rem)",
              height: "calc(100% - 3rem)",
            }}
            display={"flex"}
            justifyContent={"center"}
            alignItems={"center"}
          >
            <Typography variant={"h3"} textAlign="center">
              Drop your submission here
            </Typography>
          </Box>
          <input
            type="file"
            id="project-input"
            ref={inputRef}
            style={{
              position: "absolute",
              top: 0,
              left: 0,
              width: "100%",
              height: "100%",
              opacity: 0,
            }}
            {...{
              webkitdirectory: "",
              mozdirectory: "",
              directory: "",
            }}
            onChange={(event) => {
              setFiles(event.target.files);
              setShowFileInput(false);
            }}
            onDrop={() => {
              setTimeout(() => {
                console.log();
                setShowFileInput((prev) => {
                  if (prev) {
                    enqueueSnackbar(
                      "Something went wrong. Did you load a folder?",
                      { variant: "error" },
                    );
                  }
                  return false;
                });
              }, 1000);
            }}
          ></input>
        </Box>

        <CardContent
          className={"hidden-scrollbar"}
          sx={{
            display: "flex",
            flexFlow: "column",
            height: "100%",
            maxHeight: "100%",
            overflowY: "scroll",
          }}
          onClick={() => {
            if (inputRef.current != null) {
              inputRef.current.click();
            } else {
              console.log("WHT");
            }
          }}
        >
          {children}
          {!disabled && (
            <>
              <Divider sx={{ my: 2 }} />
              <Typography variant={"h4"} sx={{ mb: 1 }}>
                Submit
              </Typography>
              <Box
                padding={1}
                borderRadius={1}
                height={"100%"}
                minHeight={"5rem"}
                maxHeight={"100%"}
                display={files == null ? "flex" : "block"}
                justifyContent={"center"}
                alignItems={"center"}
                className={"hidden-scrollbar"}
                sx={{
                  backgroundColor: "rgba(0,0,0,0.1)",
                  overflowY: "scroll",
                  my: 1,
                }}
              >
                {!showFileInput &&
                  (files == null ? (
                    <Typography variant={"h5"}>
                      Click here or Drag the submission folder and drop it here
                    </Typography>
                  ) : (
                    <>
                      <ul>
                        {Array.from(files).map((file, i) => (
                          <li key={i}>{file.name}</li>
                        ))}
                      </ul>
                    </>
                  ))}
              </Box>
            </>
          )}
          <Box display="flex" alignItems="center">
            <Button
              sx={{ mr: 2 }}
              onClick={handleUpload}
              variant={"outlined"}
              disabled={disabled || files == null}
            >
              Upload
            </Button>
            {isProjectOver && !disabled && (
              <Typography>Project overdue</Typography>
            )}
            {disabled && <Typography>Confirmed submission</Typography>}
          </Box>
        </CardContent>
      </Card>
    </>
  );
};

export default ProjectUploadForm;
