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
import { courseDetailAtom } from "@courses/courseDetail/courseDetailAtom.ts";
import { Box, Skeleton, Tab, Tabs, Typography } from "@mui/material";
import Container from "@mui/material/Container";
import LeaveCourse from "@courses/courseDetail/LeaveCourse.tsx";
import TabPanel from "@structure/TabPanel.tsx";
import CourseTrialsTab from "@courses/courseDetail/CourseTrialsTab/CourseTrialsTab.tsx";
import CourseMembersTab from "@courses/courseDetail/CourseMemebersTab/CourseMembersTab.tsx";
import CourseSettingsTab from "@courses/courseDetail/CourseSettingsTab/CourseSettingsTab.tsx";
import { useAtom } from "jotai";
import CourseOverviewCollaboratorTab from "@courses/courseDetail/CourseOverviewTab/CourseOverviewCollaboratorTab.tsx";
import userAtom from "@authentication/userAtom.ts";
import CourseProjectsTab from "@courses/courseDetail/CourseProjectsTab/CourseProjectsTab.tsx";

const CourseDetailCollaboratorScreen = () => {
  const [user] = useAtom(userAtom);
  const { getCourseById } = useCourseService();
  const { courseId } = useParams();
  const courseTitleRef = useRef<HTMLElement>(null);
  const globalErrorSetter = useGlobalErrorSetter();
  const [searchParams, setSearchParams] = useSearchParams();
  const [course, setCourse] = useAtom(courseDetailAtom);
  const [openTab, setOpenTab] = useState(0);
  const [courseName, setCourseName] = useState<string>();
  const tabs = useMemo(() => {
    return {
      Overview: <CourseOverviewCollaboratorTab course={course} />,
      Tests: <CourseTrialsTab userCourse={course} />,
      Projects: <CourseProjectsTab userCourse={course} />,
      Members: <CourseMembersTab userCourse={course} />,
      Settings: <CourseSettingsTab />,
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
      <Container maxWidth={false} sx={{ minWidth: 0 }}>
        <Box display={"flex"} justifyContent={"center"}>
          <Box width={"85%"}>
            <Typography
              ref={courseTitleRef}
              id="courseTitle"
              variant="h3"
              align="center"
              sx={{ typography: { md: "h3", xs: "h5" } }}
            >
              {courseName ?? <Skeleton width={"50rem"} />}
            </Typography>
          </Box>
          {user && course && course.role !== "ADMIN" && !user.isSuperuser && (
            <LeaveCourse course={course} />
          )}
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

export default CourseDetailCollaboratorScreen;
