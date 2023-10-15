import { Exam, Practice } from "@trials/types.ts";
import { Box, Button, Tab, Tabs, Typography } from "@mui/material";
import Container from "@mui/material/Container";
import TabPanel from "@structure/TabPanel.tsx";
import {
  SyntheticEvent,
  useCallback,
  useEffect,
  useRef,
  useState,
} from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import { CourseDetail } from "@courses/types.ts";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
import TrialDetailExercisesTab from "@trials/trialDetail/TrialDetailExercisesTab/TrialDetailExercisesTab.tsx";

const tabs = ["Exercises", "Users", "Settings"];

interface TrialDetailCollaboratorScreenProps {
  trial: Practice | Exam;
  course: CourseDetail;
}

const TrialDetailCollaboratorScreen = ({
  trial,
  course,
}: TrialDetailCollaboratorScreenProps) => {
  const trialTitleRef = useRef<HTMLElement>(null);
  const globalErrorSetter = useGlobalErrorSetter();
  const [searchParams, setSearchParams] = useSearchParams();
  const [openTab, setOpenTab] = useState(0);
  const navigate = useNavigate();

  const getInitialTab = useCallback(() => {
    const paramTab = Number(searchParams.get("tab"));
    return isNaN(paramTab) ? 0 : paramTab;
  }, [searchParams]);

  useEffect(() => {
    setOpenTab(getInitialTab());
  }, [globalErrorSetter, getInitialTab]);

  const handleTabChange = (event: SyntheticEvent, newValue: number) => {
    event.preventDefault();
    setOpenTab(newValue);
    setSearchParams({
      tab: newValue.toString(),
    });
  };

  return (
    <Box
      height="100%"
      sx={{
        flexGrow: 1,
        display: "flex",
      }}
    >
      <Tabs orientation="vertical" value={openTab} onChange={handleTabChange}>
        {tabs.map((tab, i) => (
          <Tab key={i} label={tab} />
        ))}
      </Tabs>
      <Container maxWidth={false}>
        <Box display={"flex"} justifyContent={"center"}>
          <Box>
            <Typography
              ref={trialTitleRef}
              id="trialTitle"
              variant="h3"
              align="center"
            >
              {trial.name}
            </Typography>
            <Box style={{ position: "absolute", left: "8rem", top: "5rem" }}>
              <Button
                variant={"outlined"}
                onClick={() => {
                  navigate(`/courses/${course.id}?tab=1`);
                }}
              >
                <ArrowBackIosNewIcon />
                {course.name.length > 25
                  ? course.name.substring(0, 25) + "..."
                  : course.name}
              </Button>
            </Box>
          </Box>
        </Box>
        <TabPanel
          value={openTab}
          index={0}
          occupiedHeight={trialTitleRef.current?.clientHeight ?? 0}
        >
          <TrialDetailExercisesTab trial={trial} course={course} />
        </TabPanel>
        <TabPanel
          value={openTab}
          index={1}
          occupiedHeight={trialTitleRef.current?.clientHeight ?? 0}
        >
          <> Users tab</>
        </TabPanel>
        <TabPanel
          value={openTab}
          index={2}
          occupiedHeight={trialTitleRef.current?.clientHeight ?? 0}
        >
          <>Settings tab</>
        </TabPanel>
      </Container>
    </Box>
  );
};

export default TrialDetailCollaboratorScreen;
