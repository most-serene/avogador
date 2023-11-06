import { Card, IconButton, Typography } from "@mui/material";
import { CopyAll, Download, Replay } from "@mui/icons-material";
import Box from "@mui/material/Box";
import { Strox, StroxCell } from "@exercises/types.ts";
import { enqueueSnackbar } from "notistack";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import React, { useEffect, useState } from "react";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import { useParams } from "react-router-dom";
import { CourseDetail } from "@courses/types.ts";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import ButtonWithConfirmation from "@structure/ButtonWithConfirmation/ButtonWithConfirmation.tsx";

interface EditorToolbarProps {
  setStrox: (strox: Strox) => void;
  strox: Strox;
}

const EditorToolbar = ({ strox, setStrox }: EditorToolbarProps) => {
  const { trialId, exerciseId } = useParams();
  const { getTemplateFromExercise } = useExerciseService();
  const [user] = useAtom(userAtom);
  const { getCourseById } = useCourseService();
  const { getTrialById } = useTrialService();
  const [userCourse, setUserCourse] = useState<CourseDetail>();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleOpenMenu = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleCloseMenu = () => {
    setAnchorEl(null);
  };

  const handleReset = () => {
    if (exerciseId == undefined) return;
    getTemplateFromExercise(exerciseId)
      .then((template) => {
        setStrox(template);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  };

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

  const handleLoadLocal = () => {
    const storedCells = localStorage.getItem(`sub-${exerciseId}`);
    if (storedCells == null) return;
    const parsedStoredCells: StroxCell[] = JSON.parse(
      storedCells,
    ) as StroxCell[];

    if (localStorage.getItem(`sub-${exerciseId}`) != null) {
      setStrox({ ...strox, cells: parsedStoredCells });
    }
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
        <IconButton onClick={handleOpenMenu}>
          <Replay />
        </IconButton>
        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={handleCloseMenu}
          MenuListProps={{
            "aria-labelledby": "basic-button",
          }}
        >
          <MenuItem
            disabled={localStorage.getItem(`sub-${exerciseId}`) == null}
            onClick={handleLoadLocal}
          >
            Load un-submitted changes
          </MenuItem>
          <ButtonWithConfirmation
            as={"Plain"}
            confirmColor={"error"}
            variant={"contained"}
            confirmText={"Reset"}
            title={"Reset Code?"}
            description={"By doing so you will lose all your changes"}
            onConfirm={() => {
              handleReset();
            }}
          >
            <MenuItem>Reset to template</MenuItem>
          </ButtonWithConfirmation>
        </Menu>
      </Box>
    </Card>
  );
};

export default EditorToolbar;
