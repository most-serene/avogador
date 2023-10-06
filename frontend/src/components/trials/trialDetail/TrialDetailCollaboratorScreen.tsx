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
import { UserCourseDetail } from "@courses/types.ts";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";

const tabs = ["Exercises", "Users", "Settings"];

interface TrialDetailCollaboratorScreenProps {
  trial: Practice | Exam;
  user: UserCourseDetail;
}

const TrialDetailCollaboratorScreen = ({
  trial,
  user,
}: TrialDetailCollaboratorScreenProps) => {
  const trialTitleRef = useRef<HTMLElement>(null);
  const globalErrorSetter = useGlobalErrorSetter();
  const [searchParams, setSearchParams] = useSearchParams();
  const [openTab, setOpenTab] = useState(0);
  const navigate = useNavigate();

  const getInitialTab = useCallback(() => {
    const paramTab = Number(searchParams.get("tab"));
    if (isNaN(paramTab) || paramTab >= tabs.length) {
      setSearchParams({
        tab: "0",
      });
      return 0;
    }
    setSearchParams({
      tab: paramTab.toString(),
    });
    return paramTab;
  }, [searchParams, setSearchParams]);

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
                  navigate(`/courses/${user.id}?tab=1`);
                }}
              >
                <ArrowBackIosNewIcon />
                Back to {user.name}
              </Button>
            </Box>
          </Box>
        </Box>
        <TabPanel
          value={openTab}
          index={0}
          occupiedHeight={trialTitleRef.current?.clientHeight ?? 0}
        >
          <>Trial exercises tab</>
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
