import { Card, IconButton, Tooltip, Typography } from "@mui/material";
import { CopyAll, Download, Replay } from "@mui/icons-material";
import Box from "@mui/material/Box";
import ButtonWithConfirmation from "@structure/ButtonWithConfirmation/ButtonWithConfirmation.tsx";
import { Strox } from "@exercises/types.ts";
import { enqueueSnackbar } from "notistack";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { useEffect, useState } from "react";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { useParams } from "react-router-dom";
import { CourseDetail } from "@courses/types.ts";

interface EditorToolbarProps {
  onReset: () => void;
  strox: Strox;
}

const EditorToolbar = ({ onReset: handleReset, strox }: EditorToolbarProps) => {
  const { trialId } = useParams();
  const [user] = useAtom(userAtom);
  const { getCourseById } = useCourseService();
  const { getTrialById } = useTrialService();
  const [userCourse, setUserCourse] = useState<CourseDetail>();

  const handleCopy = () => {
    const code = strox.cells.map((cell) => cell.content).join("\n");

    navigator.clipboard
      .writeText(code)
      .then(() => {
        enqueueSnackbar("Code copied successfully", { variant: "success" });
      })
      .catch(() => {
        enqueueSnackbar("Something went wrong", { variant: "error" });
      });
  };

  const handleDownload = () => {
    // TODO: Download handling
  };

  useEffect(() => {
    if (trialId == null) return;

    getTrialById(trialId)
      .then((trial) => {
        getCourseById(trial.courseId)
          .then((userCourse) => {
            setUserCourse(userCourse);
          })
          .catch((err: Error) => {
            enqueueSnackbar(err.message, { variant: "error" });
          });
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [trialId, getTrialById, getCourseById]);

  return (
    <Card
      sx={{
        borderBottom: 1,
        borderBottomColor: "secondary.dark",
        py: 0.5,
        px: 2,
        borderBottomRightRadius: 0,
        borderBottomLeftRadius: 0,
        ml: "3px",
        display: "flex",
        alignItems: "center",
      }}
    >
      <Typography fontFamily="monospace">{strox.sourceFileName}</Typography>
      <IconButton sx={{ ml: 1 }} onClick={handleCopy}>
        <CopyAll />
      </IconButton>
      {userCourse != null &&
        (userCourse.role === "COLLABORATOR" ||
          userCourse.role === "ADMIN" ||
          user?.isSuperuser === true) && (
          <IconButton onClick={handleDownload} disabled>
            <Download />
          </IconButton>
        )}
      <Box sx={{ marginLeft: "auto" }}>
        <Tooltip title="Reset code" placement="left">
          <ButtonWithConfirmation
            onConfirm={handleReset}
            as="IconButton"
            confirmColor="error"
            title="Reset Code?"
            description="By doing so you will lose all your changes"
            confirmText="Reset"
          >
            <Replay />
          </ButtonWithConfirmation>
        </Tooltip>
      </Box>
    </Card>
  );
};

export default EditorToolbar;
