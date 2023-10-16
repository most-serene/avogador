import { Typography } from "@mui/material";
import Box from "@mui/material/Box";
import { Exercise, Testcase } from "@exercises/types.ts";
import Markdown from "react-markdown";
import SampleTestcaseCards from "@exercises/exerciseScreen/SampleTestcaseCards.tsx";

interface ExerciseStatementProps {
  exercise: Exercise;
  testcases: Testcase[];
}

const ExerciseStatement = ({ exercise, testcases }: ExerciseStatementProps) => {
  return (
    <Box
      padding={2}
      height="100%"
      style={{ overflow: "scroll" }}
      className="hidden-scrollbar"
    >
      <Typography variant="h3">{exercise.name}</Typography>
      <Markdown>{exercise.statement}</Markdown>
      <Box marginBottom="6rem">
        {testcases.map((testcase, i) => (
          <SampleTestcaseCards key={i} testcase={testcase} />
        ))}
      </Box>
    </Box>
  );
};

export default ExerciseStatement;
