import { Box, Skeleton, Tab, Tabs, Typography } from "@mui/material";
import { useParams } from "react-router-dom";
import { SyntheticEvent, useEffect, useRef, useState } from "react";
import { CourseDetail } from "../types.ts";
import Container from "@mui/material/Container";
import TabPanel from "@structure/TabPanel.tsx";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import CourseMembersTab from "@courses/courseDetail/CourseMembersTab.tsx";
import { AxiosError } from "axios";
import { useGlobalErrorSetter } from "@components/error/GlobalErrorState.tsx";
import { ResourceNotFoundError } from "@components/error/types.ts";

function a11yProps(index: number) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

const tabs = ["Overview", "Tests", "Members", "Settings"];

export default function CourseDetailScreen() {
  const { getCourseById } = useCourseService();
  const { courseId } = useParams();
  const courseTitleRef = useRef<HTMLElement>(null);
  const globalErrorSetter = useGlobalErrorSetter();

  const [course, setCourse] = useState<CourseDetail>();
  const [openTab, setOpenTab] = useState(2);

  useEffect(() => {
    if (courseId === undefined) return;
    getCourseById(courseId)
      .then((c) => {
        setCourse(c);
      })
      .catch((err) => {
        console.error(err);
        if (err instanceof AxiosError && err.response?.status === 404) {
          globalErrorSetter(
            new ResourceNotFoundError(
              { id: courseId },
              "Course",
              `Course ${courseId} not found`,
            ),
          );
        }
      });
  }, [getCourseById, courseId, globalErrorSetter]);

  const handleTabChange = (event: SyntheticEvent, newValue: number) => {
    event.preventDefault();
    setOpenTab(newValue);
  };

  return (
    <Box
      height="100%"
      sx={{
        flexGrow: 1,
        display: "flex",
      }}
    >
      <Tabs
        orientation="vertical"
        value={openTab}
        onChange={handleTabChange}
        aria-label="basic tabs example"
      >
        {tabs.map((tab, i) => (
          <Tab key={i} label={tab} {...a11yProps(i)} />
        ))}
      </Tabs>
      <Container maxWidth={false}>
        <Typography
          ref={courseTitleRef}
          id="courseTitle"
          variant="h3"
          align="center"
        >
          {course ? `${course.name} (${course.year})` : <Skeleton />}
        </Typography>
        <TabPanel
          value={openTab}
          index={0}
          occupiedHeight={courseTitleRef.current?.clientHeight ?? 0}
        >
          <Typography variant={"h4"} color={"secondary"} align={"center"}>
            Overview Tab Coming Soon
          </Typography>
        </TabPanel>
        <TabPanel
          value={openTab}
          index={1}
          occupiedHeight={courseTitleRef.current?.clientHeight ?? 0}
        >
          <Typography variant={"h4"} color={"secondary"} align={"center"}>
            Tests Tab Coming Soon
          </Typography>
        </TabPanel>
        <TabPanel
          value={openTab}
          index={2}
          occupiedHeight={courseTitleRef.current?.clientHeight ?? 0}
        >
          <CourseMembersTab courseId={courseId} />
        </TabPanel>
        <TabPanel
          value={openTab}
          index={3}
          occupiedHeight={courseTitleRef.current?.clientHeight ?? 0}
        >
          <Typography variant={"h4"} color={"secondary"} align={"center"}>
            Settings Tab Coming Soon
          </Typography>
        </TabPanel>
      </Container>
    </Box>
  );
}
