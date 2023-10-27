import {
  Strox,
  StroxCell,
  SubmissionResult,
  SubmissionResultMap,
} from "@exercises/types.ts";
import { Editor } from "@monaco-editor/react";
import React, { useEffect, useState } from "react";
import { enqueueSnackbar } from "notistack";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { Button, CircularProgress, useTheme } from "@mui/material";
import Box from "@mui/material/Box";
import useTrialService from "@trials/hooks/useTrialService.tsx";
import useWebSocket from "@hooks/useWebSocket.tsx";
import EditorToolbar from "@exercises/exerciseScreen/EditorToolbar.tsx";
import { Trial } from "@trials/types.ts";

interface SubmissionEditorProps {
  submissionDisabled: boolean;
  exerciseId: string;
  trialId: string;
  setSubmissionResult: React.Dispatch<
    React.SetStateAction<SubmissionResultMap | undefined>
  >;
}

const SubmissionEditor = ({
  submissionDisabled,
  exerciseId,
  trialId,
  setSubmissionResult,
}: SubmissionEditorProps) => {
  const { getTemplateFromExercise, createSubmission } = useExerciseService();
  const { getTrialById, isTrialEnded } = useTrialService();
  const { subscribe } = useWebSocket();
  const [strox, setStrox] = useState<Strox>();
  // const [language, setLanguage] = useState<"C" | "CPP" | "PYTHON" | "JAVA">();
  const [trial, setTrial] = useState<Trial>();
  const [cellsSize, setCellsSize] = useState<number[]>([]);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const theme = useTheme();

  const handleChange = (value: string | undefined, i: number) => {
    if (value == null || strox == null) {
      return;
    }
    const cells = strox.cells;
    cells[i].content = value;
    setStrox({ ...strox, cells: cells });
    updateCellsSize(cells);
  };

  const updateCellsSize = (cells: StroxCell[]) => {
    const sizes = [0];
    cells.forEach((cell) =>
      sizes.push(sizes.slice(-1)[0] + cell.content.split("\n").length),
    );
    setCellsSize([...sizes]);
  };

  const handleSubmit = () => {
    if (strox == null) {
      enqueueSnackbar("Keep calm!", { variant: "error" });
      return;
    }
    setIsSubmitted(true);
    createSubmission(exerciseId, strox.cells)
      .then((submission) => {
        setSubmissionResult(undefined);
        subscribe(`/${submission.id}/results`, (message) => {
          const result = JSON.parse(message.body) as SubmissionResult;
          setSubmissionResult((prev) => {
            const submissionResultCopy = { ...prev };
            // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
            if (submissionResultCopy[result.submissionId] == null) {
              submissionResultCopy[result.submissionId] = [];
            }
            const index = submissionResultCopy[result.submissionId].findIndex(
              (element) => element.id === result.id,
            );

            if (index > -1) {
              submissionResultCopy[result.submissionId][index] = result;
            } else {
              submissionResultCopy[result.submissionId].push(result);
            }
            return submissionResultCopy;
          });
        })
          .then(() => {
            // empty-function
          })
          .catch((err) => {
            console.error(err);
          });

        enqueueSnackbar("Submission submitted successfully!", {
          variant: "success",
        });
      })
      .catch((err: Error) => {
        enqueueSnackbar(
          "Something went wrong when submitting your solution\n" + err.message,
          {
            variant: "error",
          },
        );
      })
      .finally(() => {
        setIsSubmitted(false);
      });
  };

  const handleReset = () => {
    getTemplateFromExercise(exerciseId)
      .then((template) => {
        setStrox(template);
        updateCellsSize(template.cells);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  };

  useEffect(() => {
    getTemplateFromExercise(exerciseId, true)
      .then((template) => {
        setStrox(template);
        updateCellsSize(template.cells);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });

    getTrialById(trialId)
      .then((trial) => {
        setTrial(trial);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
    return () => {
      setStrox(undefined);
    };
  }, [trialId, exerciseId, getTemplateFromExercise, getTrialById]);

  if (strox == null) {
    return <CircularProgress />;
  }

  return (
    <Box position="relative" height="100%">
      <EditorToolbar onReset={handleReset} strox={strox} />
      <Box
        style={{ overflow: "scroll", height: "calc(100% - 42px)" }}
        className="hidden-scrollbar"
      >
        {strox.cells.map((cell, i) => (
          <Box
            key={i}
            sx={{
              borderLeft: 3,
              borderLeftColor:
                cell.type === "EDITABLE" ? "primary.main" : "rgba(0,0,0,0)",
              borderLeftStyle: "solid",
            }}
          >
            <Editor
              height={`${
                24 * (cell.content.split(/\r\n|\r|\n/).length + 0.3)
              }px`}
              theme={theme.palette.mode === "dark" ? "vs-dark" : "light"}
              language={trial?.language.toLowerCase()}
              value={cell.content}
              options={{
                readOnly: cell.type !== "EDITABLE",
                inlineSuggest: true,
                scrollBeyondLastLine: false,
                fontSize: "16px",
                formatOnType: true,
                autoClosingBrackets: true,
                automaticLayout: true,
                scrollbar: {
                  vertical: "hidden",
                  horizontal: "hidden",
                  handleMouseWheel: false,
                },
                lineNumbers: (n: number): string => `${cellsSize[i] + n}`,
                minimap: {
                  enabled: false,
                },
              }}
              onChange={(value) => {
                if (cell.type === "EDITABLE") {
                  handleChange(value, i);
                }
              }}
            />
          </Box>
        ))}
      </Box>

      {trial != null && !isTrialEnded(trial) && (
        <Button
          variant="contained"
          style={{
            position: "absolute",
            bottom: 16,
            right: 16,
          }}
          onClick={handleSubmit}
          disabled={isSubmitted || submissionDisabled}
        >
          SUBMIT
        </Button>
      )}
    </Box>
  );
};

export default SubmissionEditor;
