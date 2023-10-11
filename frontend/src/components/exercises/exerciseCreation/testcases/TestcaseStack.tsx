import {
  Card,
  CardActionArea,
  CardContent,
  Divider,
  IconButton,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import { useAtom } from "jotai";
import testcasesAtom from "@exercises/exerciseCreation/TestcasesAtom.ts";
import Box from "@mui/material/Box";
import { Delete, Visibility, VisibilityOff } from "@mui/icons-material";
import { PartialTestcase } from "@exercises/types.ts";

interface TestcasePreviewCardProps {
  testcase: PartialTestcase;
  selected: boolean;
  onDelete: () => void;
  onClick: () => void;
  onVisibilityChange: () => void;
}

const TestcasePreviewCard = ({
  testcase,
  selected,
  onDelete: handleDelete,
  onClick: handleClick,
  onVisibilityChange: handleVisibilityChange,
}: TestcasePreviewCardProps) => {
  return (
    <Card
      sx={{
        border: selected ? 2 : 0,
        borderColor: "primary.main",
        borderStyle: "solid",
      }}
    >
      <CardActionArea
        onClick={() => {
          handleClick();
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
          <Box display="flex">
            <Tooltip title={testcase.isVisible ? "Set hidden" : "Set visible"}>
              <IconButton
                onClick={(event) => {
                  event.stopPropagation();
                  handleVisibilityChange();
                }}
                onMouseDown={(event) => {
                  event.stopPropagation();
                }}
              >
                {testcase.isVisible ? <Visibility /> : <VisibilityOff />}
              </IconButton>
            </Tooltip>
            <Divider orientation="vertical" sx={{ mx: 1 }} flexItem />
            <IconButton
              color="error"
              onClick={(event) => {
                event.stopPropagation();
                handleDelete();
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
  );
};

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

  const handleVisibilityChange = (i: number) => {
    testcases[i].isVisible = !testcases[i].isVisible;
    setTestcases([...testcases]);
  };

  return (
    <Box
      style={{ height: "100%", overflow: "scroll" }}
      className="hidden-scrollbar"
    >
      <Stack spacing={2}>
        {testcases.map((testcase, i) => (
          <TestcasePreviewCard
            key={i}
            testcase={testcase}
            selected={selected === i}
            onClick={() => {
              handleSelect(i);
            }}
            onDelete={() => {
              handleDelete(i);
            }}
            onVisibilityChange={() => {
              handleVisibilityChange(i);
            }}
          />
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
