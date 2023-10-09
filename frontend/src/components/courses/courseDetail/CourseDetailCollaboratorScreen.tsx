import useCourseService from "@courses/hooks/useCourseService.tsx";
import { useParams, useSearchParams } from "react-router-dom";
import {
  SyntheticEvent,
  useCallback,
  useEffect,
  useRef,
  useState,
} from "react";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import { courseDetailAtom } from "@courses/courseDetail/courseDetailAtom.ts";
import { Box, Skeleton, Tab, Tabs, Typography } from "@mui/material";
import Container from "@mui/material/Container";
import LeaveCourse from "@courses/courseDetail/LeaveCourse.tsx";
import TabPanel from "@structure/TabPanel.tsx";
import CourseOverviewTab from "@courses/courseDetail/CourseOverviewTab/CourseOverviewTab.tsx";
import CourseTrialsTab from "@courses/courseDetail/CourseTrialsTab/CourseTrialsTab.tsx";
import CourseMembersTab from "@courses/courseDetail/CourseMemebersTab/CourseMembersTab.tsx";
import CourseSettingsTab from "@courses/courseDetail/CourseSettingsTab/CourseSettingsTab.tsx";
import { useAtom } from "jotai";

const tabs = ["Overview", "Tests", "Members", "Settings"];

const CourseDetailCollaboratorScreen = () => {
  const { getCourseById } = useCourseService();
  const { courseId } = useParams();
  const courseTitleRef = useRef<HTMLElement>(null);
  const globalErrorSetter = useGlobalErrorSetter();
  const [searchParams, setSearchParams] = useSearchParams();
  const [course, setCourse] = useAtom(courseDetailAtom);
  const [openTab, setOpenTab] = useState(0);
  const [courseName, setCourseName] = useState<string>();

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
    if (courseId === undefined) return;
    setOpenTab(getInitialTab());
  }, [getCourseById, courseId, globalErrorSetter, getInitialTab, setCourse]);

  useEffect(() => {
    if (course) {
      setCourseName(`${course.name} (${course.year})`);
    }
  }, [course]);

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
              ref={courseTitleRef}
              id="courseTitle"
              variant="h3"
              align="center"
            >
              {courseName ?? <Skeleton width={"50rem"} />}
            </Typography>
          </Box>
          {course && course.role !== "ADMIN" && <LeaveCourse course={course} />}
        </Box>
        <TabPanel
          value={openTab}
          index={0}
          occupiedHeight={courseTitleRef.current?.clientHeight ?? 0}
        >
          <CourseOverviewTab course={course} />
        </TabPanel>
        <TabPanel
          value={openTab}
          index={1}
          occupiedHeight={courseTitleRef.current?.clientHeight ?? 0}
        >
          <CourseTrialsTab userCourse={course} />
        </TabPanel>
        <TabPanel
          value={openTab}
          index={2}
          occupiedHeight={courseTitleRef.current?.clientHeight ?? 0}
        >
          <CourseMembersTab userCourse={course} />
        </TabPanel>
        <TabPanel
          value={openTab}
          index={3}
          occupiedHeight={courseTitleRef.current?.clientHeight ?? 0}
        >
          <CourseSettingsTab />
        </TabPanel>
      </Container>
    </Box>
  );
};

export default CourseDetailCollaboratorScreen;
