import { Editor } from "@monaco-editor/react";
import Box from "@mui/material/Box";
import testcasesAtom from "@exercises/exerciseCreation/TestcasesAtom.ts";
import { useAtom } from "jotai";
import { useEffect, useState } from "react";
import { PartialTestcase } from "@exercises/types.ts";

interface IOEditorProps {
  selected: number;
}

const IOEditor = ({ selected }: IOEditorProps) => {
  const [testcases, setTestcases] = useAtom(testcasesAtom);
  const [testcase, setTestcase] = useState<PartialTestcase>({
    input: "",
    output: "",
    isVisible: false,
  });

  useEffect(() => {
    setTestcase(testcases[selected]);
  }, [testcases, selected]);

  const handleChange = (value: string | undefined, io: "input" | "output") => {
    if (value == null) {
      return;
    }
    testcase[io] = value;
    setTestcase(testcase);
    testcases[selected][io] = value;
    setTestcases([...testcases]);
  };

  return (
    <>
      <Box
        height="49%"
        sx={{
          borderBottom: 1,
          borderBottomColor: "primary.main",
          borderBottomStyle: "solid",
        }}
      >
        <Editor
          height="100%"
          theme="vs-dark"
          value={testcase.input}
          onChange={(value) => {
            handleChange(value, "input");
          }}
        />
      </Box>
      <Editor
        height="49%"
        theme="vs-dark"
        value={testcase.output}
        onChange={(value) => {
          handleChange(value, "output");
        }}
      />
    </>
  );
};

export default IOEditor;
