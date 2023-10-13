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
import { useAtom } from "jotai/index";
import { courseDetailAtom } from "@courses/courseDetail/courseDetailAtom.ts";
import { Box, Skeleton, Tab, Tabs, Typography } from "@mui/material";
import Container from "@mui/material/Container";
import LeaveCourse from "@courses/courseDetail/LeaveCourse.tsx";
import TabPanel from "@structure/TabPanel.tsx";
import CourseOverviewTab from "@courses/courseDetail/CourseOverviewTab/CourseOverviewTab.tsx";
import CourseTrialsTab from "@courses/courseDetail/CourseTrialsTab/CourseTrialsTab.tsx";

const tabs = ["Overview", "Tests"];

const CourseDetailStudentScreen = () => {
  const { getCourseById } = useCourseService();
  const { courseId } = useParams();
  const courseTitleRef = useRef<HTMLElement>(null);
  const globalErrorSetter = useGlobalErrorSetter();
  const [searchParams, setSearchParams] = useSearchParams();
  const [course, setCourse] = useAtom(courseDetailAtom);
  const [openTab, setOpenTab] = useState(0);

  const getInitialTab = useCallback(() => {
    const paramTab = Number(searchParams.get("tab"));
    return isNaN(paramTab) ? 0 : paramTab;
  }, [searchParams]);

  useEffect(() => {
    if (courseId === undefined) return;
    setOpenTab(getInitialTab());
  }, [getCourseById, courseId, globalErrorSetter, getInitialTab, setCourse]);

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
              {course?.name ?? <Skeleton width={"50rem"} />}
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
      </Container>
    </Box>
  );
};

export default CourseDetailStudentScreen;
