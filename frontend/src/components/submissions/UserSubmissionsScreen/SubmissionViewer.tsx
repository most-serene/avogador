import { Strox, StroxCell } from "@exercises/types.ts";
import { Editor } from "@monaco-editor/react";
import Box from "@mui/material/Box";
import { useEffect, useState } from "react";
import { CircularProgress, useTheme } from "@mui/material";

interface SubmissionViewerProps {
  template: Strox;
  submissionCode: StroxCell[];
  language: string;
}

const SubmissionViewer = ({
  template,
  submissionCode,
  language,
}: SubmissionViewerProps) => {
  const theme = useTheme();
  const [cellsSize, setCellsSize] = useState<number[]>([]);
  const [code, setCode] = useState<StroxCell[]>([]);

  useEffect(() => {
    let i = 0;
    const code = template.cells.map((cell) =>
      cell.type === "EDITABLE" ? submissionCode[i++] : cell,
    );
    setCode(code);

    const sizes = [0];
    code.forEach((cell) =>
      sizes.push(sizes.slice(-1)[0] + cell.content.split("\n").length),
    );
    setCellsSize([...sizes]);
  }, [submissionCode, template.cells]);

  if (code.length === 0 || cellsSize.length === 0) {
    return <CircularProgress />;
  }

  return (
    <Box height="100%" overflow="scroll">
      {code.map((cell, i) => (
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
            height={`${24 * (cell.content.split(/\r\n|\r|\n/).length + 0.3)}px`}
            theme={theme.palette.mode === "dark" ? "vs-dark" : "light"}
            language={language.toLowerCase()}
            value={cell.content}
            options={{
              readOnly: true,
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
          />
        </Box>
      ))}
    </Box>
  );
};

export default SubmissionViewer;
