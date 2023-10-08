import Box from "@mui/material/Box";
import { useAtom } from "jotai";
import templateAtom, {
  getInitializedTemplate,
} from "@exercises/exerciseCreation/TemplateAtom.ts";
import EditorCell from "@exercises/exerciseCreation/template/EditorCell.tsx";
import { Button } from "@mui/material";
import { StroxCell } from "@exercises/types.ts";

const ExerciseCreationTemplate = () => {
  const [template, setTemplate] = useAtom(templateAtom);

  const handleOnDelete = (i: number) => {
    template.splice(i, 1);
    if (template.length === 0) {
      setTemplate([...getInitializedTemplate()]);
    } else {
      setTemplate([...template]);
    }
  };

  const handleOnChange = (i: number) => (cell: StroxCell) => {
    template[i] = cell;
    setTemplate([...template]);
  };

  const handleInsertCell = (i: number) => {
    template.splice(i, 0, { content: "", type: "VISIBLE" });
    setTemplate([...template]);
  };

  return (
    <Box
      style={{ overflow: "scroll", height: "100%" }}
      className="hidden-scrollbar"
    >
      <Box display="flex" justifyContent="center">
        <Button
          onClick={() => {
            handleInsertCell(0);
          }}
        >
          +
        </Button>
      </Box>
      {template.map((cell, i) => (
        <div key={i}>
          <EditorCell
            cell={cell}
            onChange={handleOnChange(i)}
            onDelete={() => {
              handleOnDelete(i);
            }}
          />
          <Box display="flex" justifyContent="center">
            <Button
              onClick={() => {
                handleInsertCell(i + 1);
              }}
            >
              +
            </Button>
          </Box>
        </div>
      ))}
    </Box>
  );
};

export default ExerciseCreationTemplate;
