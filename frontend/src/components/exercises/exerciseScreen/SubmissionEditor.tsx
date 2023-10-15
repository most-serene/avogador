import { Strox, StroxCell } from "@exercises/types.ts";
import { Editor } from "@monaco-editor/react";
import { useEffect, useState } from "react";
import { enqueueSnackbar } from "notistack";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import { Button, CircularProgress } from "@mui/material";
import Box from "@mui/material/Box";
import useTrialService from "@trials/hooks/useTrialService.tsx";

interface SubmissionEditorProps {
  exerciseId: string;
  trialId: string;
}

const SubmissionEditor = ({ exerciseId, trialId }: SubmissionEditorProps) => {
  const { getTemplateFromExercise, createSubmission } = useExerciseService();
  const { getTrialById } = useTrialService();
  const [strox, setStrox] = useState<Strox>();
  const [language, setLanguage] = useState<"C" | "CPP" | "PYTHON" | "JAVA">();
  const [cellsSize, setCellsSize] = useState<number[]>([]);
  const [isSubmitted, setIsSubmitted] = useState(false);

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
      .then(() => {
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

  useEffect(() => {
    getTemplateFromExercise(exerciseId)
      .then((template) => {
        setStrox(template);
        updateCellsSize(template.cells);
        console.log(template);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });

    getTrialById(trialId)
      .then((trial) => {
        setLanguage(trial.language);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [trialId, exerciseId, getTemplateFromExercise, getTrialById]);

  if (strox == null) {
    return <CircularProgress />;
  }

  return (
    <Box position="relative" height="100%">
      <Box
        style={{ overflow: "scroll", height: "100%" }}
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
              theme={"vs-dark"}
              language={language?.toLowerCase()}
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

      <Button
        variant="contained"
        style={{
          position: "absolute",
          bottom: 16,
          right: 16,
        }}
        onClick={handleSubmit}
        disabled={isSubmitted}
      >
        SUBMIT
      </Button>
    </Box>
  );
};

export default SubmissionEditor;
