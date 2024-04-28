import useCourseService from "@courses/hooks/useCourseService.tsx";
import { useParams, useSearchParams } from "react-router-dom";
import {
  SyntheticEvent,
  useCallback,
  useEffect,
  useMemo,
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
import CourseTrialsTab from "@courses/courseDetail/CourseTrialsTab/CourseTrialsTab.tsx";
import CourseOverviewStudentTab from "@courses/courseDetail/CourseOverviewTab/CourseOverviewStudentTab.tsx";
import CourseProjectsTab from "@courses/courseDetail/CourseProjectsTab/CourseProjectsTab.tsx";

const CourseDetailStudentScreen = () => {
  const { getCourseById } = useCourseService();
  const { courseId } = useParams();
  const courseTitleRef = useRef<HTMLElement>(null);
  const globalErrorSetter = useGlobalErrorSetter();
  const [searchParams, setSearchParams] = useSearchParams();
  const [course, setCourse] = useAtom(courseDetailAtom);
  const [openTab, setOpenTab] = useState(0);
  const tabs = useMemo(() => {
    return {
      Overview: <CourseOverviewStudentTab course={course} />,
      Tests: <CourseTrialsTab userCourse={course} />,
      Projects: <CourseProjectsTab userCourse={course} />,
    };
  }, [course]);

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
      width="100%"
      height="100%"
      sx={{
        flexGrow: 1,
        display: "flex",
      }}
    >
      <Tabs orientation="vertical" value={openTab} onChange={handleTabChange}>
        {Object.keys(tabs).map((tab, i) => (
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
        {Object.values(tabs).map((panel, i) => (
          <TabPanel
            key={i}
            value={openTab}
            index={i}
            occupiedHeight={courseTitleRef.current?.clientHeight ?? 0}
          >
            {panel}
          </TabPanel>
        ))}
      </Container>
    </Box>
  );
};

export default CourseDetailStudentScreen;
