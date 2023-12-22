import { Skeleton, Typography } from "@mui/material";
import Box from "@mui/material/Box";
import { Exercise, Testcase } from "@exercises/types.ts";
import Markdown from "react-markdown";
import SampleTestcaseCards from "@exercises/exerciseScreen/SampleTestcaseCards.tsx";
import remarkGfm from "remark-gfm";

interface ExerciseStatementProps {
  exercise: Exercise | undefined;
  testcases: Testcase[];
}

const ExerciseStatement = ({ exercise, testcases }: ExerciseStatementProps) => {
  if (exercise == null) {
    return (
      <Box
        padding={2}
        height="100%"
        style={{ overflow: "scroll" }}
        className="hidden-scrollbar"
      >
        <Typography variant="h3">
          <Skeleton />
        </Typography>
        {[...Array(3).keys()].map((i) => (
          <Box key={i}>
            <Skeleton animation="wave" />
            <Skeleton width="80%" animation="wave" />
            <Skeleton animation="wave" />
          </Box>
        ))}
      </Box>
    );
  }

  return (
    <Box
      padding={2}
      height="100%"
      style={{ overflow: "scroll" }}
      className="hidden-scrollbar"
    >
      <Typography variant="h3">{exercise.name}</Typography>
      <Markdown remarkPlugins={[remarkGfm]}>{exercise.statement}</Markdown>
      <Box marginBottom="6rem">
        {testcases.map((testcase, i) => (
          <SampleTestcaseCards key={i} testcase={testcase} />
        ))}
      </Box>
    </Box>
  );
};

export default ExerciseStatement;
