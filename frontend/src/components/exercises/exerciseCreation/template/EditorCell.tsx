import { Editor } from "@monaco-editor/react";
import { StroxCell } from "@exercises/types.ts";
import Box from "@mui/material/Box";
import { Divider, IconButton, Paper, Tooltip } from "@mui/material";
import { Delete, Edit, Visibility, VisibilityOff } from "@mui/icons-material";

interface EditorCellProps {
  cell: StroxCell;
  onChange: (cell: StroxCell) => void;
  onDelete: () => void;
}

const EditorCell = ({
  cell,
  onChange: handleChange,
  onDelete: handleDelete,
}: EditorCellProps) => {
  const handleTypeChange = (type: "HIDDEN" | "EDITABLE" | "VISIBLE") => {
    cell.type = type;
    handleChange(cell);
  };

  const handleContentChange = (value: string | undefined) => {
    if (value != null) {
      cell.content = value;
      handleChange(cell);
    }
  };

  return (
    <Box position="relative" className="show-on-hover-source">
      <Editor
        height={`${24 * (cell.content.split(/\r\n|\r|\n/).length + 1)}px`}
        defaultLanguage="javascript"
        theme="vs-dark"
        options={{
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
          lineNumbers: (n: number): string => `${n}`,
          minimap: {
            enabled: false,
          },
        }}
        value={cell.content}
        onChange={handleContentChange}
      />
      <div className="show-on-hover-target">
        <Paper
          sx={{ p: 0.5 }}
          style={{
            display: "flex",
            position: "absolute",
            top: 0,
            right: 0,
          }}
        >
          <Tooltip title="Hidden">
            <span>
              <IconButton
                size={"small"}
                disabled={cell.type === "HIDDEN"}
                onClick={() => {
                  handleTypeChange("HIDDEN");
                }}
              >
                <VisibilityOff />
              </IconButton>
            </span>
          </Tooltip>
          <Tooltip title="Editable">
            <span>
              <IconButton
                size={"small"}
                disabled={cell.type === "EDITABLE"}
                onClick={() => {
                  handleTypeChange("EDITABLE");
                }}
              >
                <Edit />
              </IconButton>
            </span>
          </Tooltip>
          <Tooltip title="Visible">
            <span>
              <IconButton
                size={"small"}
                disabled={cell.type === "VISIBLE"}
                onClick={() => {
                  handleTypeChange("VISIBLE");
                }}
              >
                <Visibility />
              </IconButton>
            </span>
          </Tooltip>
          <Divider orientation="vertical" flexItem sx={{ mx: 1 }} />
          <Tooltip title={"Delete"}>
            <IconButton size={"small"} color="error" onClick={handleDelete}>
              <Delete />
            </IconButton>
          </Tooltip>
        </Paper>
      </div>
    </Box>
  );
};

export default EditorCell;
