import { Card, CardContent, Typography } from "@mui/material";
import TrialItem from "@trials/TrialItem/TrialItem.tsx";
import { Trial } from "@trials/types.ts";
import Box from "@mui/material/Box";
import ColorModeAtom from "@theme/colorModeAtom.ts";
import { useAtom } from "jotai";
import CreateTrialButton from "@courses/courseDetail/CourseTrialsTab/CreateTrialButton.tsx";
import TrialItemSkeleton from "@trials/TrialItem/TrialItemSkeleton.tsx";

interface KanbanColumnProps {
  title: string;
  trials: Trial[];
  hasCreateButton?: boolean;
  isLoading: boolean;
}

const KanbanColumn = ({
  title,
  trials,
  hasCreateButton = false,
  isLoading,
}: KanbanColumnProps) => {
  const [colorMode] = useAtom(ColorModeAtom);

  return (
    <Card
      style={{
        height: "100%",
        backgroundColor: colorMode === "light" ? "#f0f0f0" : " ",
      }}
    >
      <CardContent style={{ height: "100%" }}>
        <Typography variant={"h5"} align={"center"} paddingBottom={1}>
          {title}
        </Typography>
        <Box overflow="scroll" height="100%" className="hidden-scrollbar">
          {isLoading ? (
            <TrialItemSkeleton />
          ) : (
            <>
              {hasCreateButton && <CreateTrialButton />}
              {trials.map((trial) => (
                <TrialItem key={trial.id} trial={trial} />
              ))}
            </>
          )}
        </Box>
      </CardContent>
    </Card>
  );
};

export default KanbanColumn;
