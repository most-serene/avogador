import {
  Card,
  CardActionArea,
  CardContent,
  IconButton,
  Stack,
  Typography,
} from "@mui/material";
import { useAtom } from "jotai";
import testcasesAtom from "@exercises/exerciseCreation/TestcasesAtom.ts";
import Box from "@mui/material/Box";
import { Delete } from "@mui/icons-material";

interface TestcaseStackProps {
  readonly selected: number | undefined;
  onSelect: (i: number | undefined) => void;
}

const TestcaseStack = ({
  selected,
  onSelect: handleSelect,
}: TestcaseStackProps) => {
  const [testcases, setTestcases] = useAtom(testcasesAtom);

  const handleCreate = () => {
    testcases.push({ input: "", output: "", isVisible: false });
    setTestcases([...testcases]);
    handleSelect(testcases.length - 1);
  };

  const handleDelete = (i: number) => {
    if (i === selected) {
      handleSelect(undefined);
    }
    if (selected != null && i < selected) {
      handleSelect(selected - 1);
    }
    testcases.splice(i, 1);
    setTestcases([...testcases]);
  };

  return (
    <Box
      style={{ height: "100%", overflow: "scroll" }}
      className="hidden-scrollbar"
    >
      <Stack spacing={2}>
        {testcases.map((testcase, i) => (
          <Card
            key={i}
            sx={{
              border: selected === i ? 2 : 0,
              borderColor: "primary.main",
              borderStyle: "solid",
            }}
          >
            <CardActionArea
              onClick={() => {
                handleSelect(i);
              }}
            >
              <CardContent
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <Box>
                  <Typography>
                    Input : {testcase.input.slice(0, 50)}{" "}
                    {testcase.input.length > 50 ? "..." : ""}
                  </Typography>
                  <Typography>
                    Output : {testcase.output.slice(0, 50)}{" "}
                    {testcase.output.length > 50 ? "..." : ""}
                  </Typography>
                </Box>
                <Box>
                  <IconButton
                    color="error"
                    onClick={(event) => {
                      event.stopPropagation();
                      handleDelete(i);
                    }}
                    onMouseDown={(event) => {
                      event.stopPropagation();
                    }}
                  >
                    <Delete />
                  </IconButton>
                </Box>
              </CardContent>
            </CardActionArea>
          </Card>
        ))}
        <Card
          elevation={0}
          sx={{
            border: 2,
            borderColor: "primary.main",
            borderStyle: "dashed",
          }}
        >
          <CardActionArea onClick={handleCreate}>
            <CardContent>
              <Typography>New Testcase </Typography>
            </CardContent>
          </CardActionArea>
        </Card>
      </Stack>
    </Box>
  );
};

export default TestcaseStack;
