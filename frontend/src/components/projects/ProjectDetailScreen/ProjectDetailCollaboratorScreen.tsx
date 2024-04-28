import { CourseDetail } from "@courses/types.ts";
import { Project } from "@components/projects/types.ts";
import ProjectOverviewTab from "@components/projects/ProjectDetailScreen/ProjectOverviewTab/ProjectOverviewTab.tsx";
import { useNavigate, useSearchParams } from "react-router-dom";
import {
  SyntheticEvent,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import { Box, Button, Tab, Tabs, Typography } from "@mui/material";
import Container from "@mui/material/Container";
import TabPanel from "@structure/TabPanel.tsx";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
import ProjectSettingsTab from "@components/projects/ProjectDetailScreen/ProjectSettingsTab/ProjectSettingsTab.tsx";

interface ProjectDetailCollaboratorScreenProps {
  project: Project;
  onUpdate: (project: Project) => void;
  course: CourseDetail;
}

const ProjectDetailCollaboratorScreen = ({
  project,
  onUpdate,
  course,
}: ProjectDetailCollaboratorScreenProps) => {
  const navigate = useNavigate();
  const courseTitleRef = useRef<HTMLElement>(null);
  const globalErrorSetter = useGlobalErrorSetter();
  const [searchParams, setSearchParams] = useSearchParams();

  const [openTab, setOpenTab] = useState(0);
  const tabs = useMemo(() => {
    return {
      Overview: <ProjectOverviewTab project={project} />,
      Settings: <ProjectSettingsTab project={project} onUpdate={onUpdate} />,
    };
  }, [onUpdate, project]);

  const getInitialTab = useCallback(() => {
    const paramTab = Number(searchParams.get("tab"));
    return isNaN(paramTab) ? 0 : paramTab;
  }, [searchParams]);

  useEffect(() => {
    setOpenTab(getInitialTab());
  }, [course, globalErrorSetter, getInitialTab]);

  const handleTabChange = (event: SyntheticEvent, newValue: number) => {
    event.preventDefault();
    setOpenTab(newValue);
    setSearchParams({
      tab: newValue.toString(),
    });
  };

  return (
    <Box
      width={"100%"}
      height="100%"
      sx={{
        display: "flex",
        flexGrow: 1,
      }}
    >
      <Tabs orientation="vertical" value={openTab} onChange={handleTabChange}>
        {Object.keys(tabs).map((tab, i) => (
          <Tab key={i} label={tab} />
        ))}
      </Tabs>
      <Container
        maxWidth={false}
        sx={{
          minWidth: 0,
          height: "100%",
          display: "flex",
          flexFlow: "column",
        }}
      >
        <Box display={"flex"} justifyContent={"center"}>
          <Box
            sx={{
              display: { md: "flex", xs: "block" },
              alignItems: "center",
              justifyContent: "center",
              width: "100%",
              position: "relative",
              mb: 1,
            }}
          >
            <Button
              sx={{ position: { md: "absolute", xs: "static" }, left: 0 }}
              variant={"outlined"}
              onClick={() => {
                navigate(`/courses/${course.id}?tab=2`);
              }}
            >
              <ArrowBackIosNewIcon />
              Back to{" "}
              {course.name.length > 20
                ? course.name.substring(0, 18) + "..."
                : course.name}
            </Button>
            <Typography
              ref={courseTitleRef}
              id="courseTitle"
              variant="h3"
              align="center"
              sx={{ typography: { md: "h3", xs: "h5" } }}
            >
              {project.name}
            </Typography>
          </Box>
        </Box>
        {Object.values(tabs).map((panel, i) => (
          <TabPanel padding={0} key={i} value={openTab} index={i}>
            {panel}
          </TabPanel>
        ))}
      </Container>
    </Box>
  );
};

export default ProjectDetailCollaboratorScreen;
