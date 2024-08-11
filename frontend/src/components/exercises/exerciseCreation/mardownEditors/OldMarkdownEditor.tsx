import { useAtom } from "jotai";
import exerciseAtom from "@exercises/exerciseCreation/ExerciseAtom.ts";
import { Grid, TextField } from "@mui/material";
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { MathJax } from "better-react-mathjax";

const OldMarkdownEditor = () => {
  const [exercise, setExercise] = useAtom(exerciseAtom);

  return (
    <>
      <Grid item xs={6}>
        <TextField
          fullWidth
          multiline
          minRows={3}
          label="Problem statement"
          value={exercise.statement}
          onChange={(event) => {
            setExercise({ ...exercise, statement: event.target.value });
          }}
        />
      </Grid>
      <Grid item xs={6}>
        <MathJax>
          <Markdown remarkPlugins={[remarkGfm]}>
            {exercise.statement === ""
              ? "_Start writing to see the markdown preview of the problem statement_"
              : exercise.statement}
          </Markdown>
        </MathJax>
      </Grid>
    </>
  );
};

export default OldMarkdownEditor;
