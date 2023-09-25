import { Box, Skeleton, Tab, Tabs, Typography } from "@mui/material";
import { useParams, useSearchParams } from "react-router-dom";
import {
  SyntheticEvent,
  useCallback,
  useEffect,
  useRef,
  useState,
} from "react";
import { UserCourseDetail } from "@courses/types.ts";
import Container from "@mui/material/Container";
import TabPanel from "@structure/TabPanel.tsx";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import CourseMembersTab from "@courses/courseDetail/CourseMemebersTab/CourseMembersTab.tsx";
import { AxiosError } from "axios";
import { useGlobalErrorSetter } from "@components/error/GlobalErrorState.tsx";
import { ResourceNotFoundError } from "@components/error/types.ts";
import CourseOverviewTab from "@courses/courseDetail/CourseOverviewTab/CourseOverviewTab";
import CourseSettingsTab from "@courses/courseDetail/CourseSettingsTab/CourseSettingsTab";
import CourseTrialsTab from "@courses/courseDetail/CourseTrialsTab/CourseTrialsTab";
import LeaveCourse from "@courses/courseDetail/LeaveCourse";

const tabs = ["Overview", "Tests", "Members", "Settings"];

export default function CourseDetailScreen() {
  const { getCourseById } = useCourseService();
  const { courseId } = useParams();
  const courseTitleRef = useRef<HTMLElement>(null);
  const globalErrorSetter = useGlobalErrorSetter();
  const [searchParams, setSearchParams] = useSearchParams();
  const [course, setCourse] = useState<UserCourseDetail>();
  const [openTab, setOpenTab] = useState(0);

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
    getCourseById(courseId)
      .then((c) => {
        setCourse(c);
      })
      .catch((err) => {
        if (
          err instanceof AxiosError &&
          (err.response?.status === 404 || err.response?.status === 400)
        ) {
          globalErrorSetter(
            new ResourceNotFoundError(
              { id: courseId },
              "Course",
              `Course ${courseId} not found`,
            ),
          );
        }
      });
  }, [getCourseById, courseId, globalErrorSetter, getInitialTab]);

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
              {course ? `${course.name} (${course.year})` : <Skeleton />}
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
          <CourseTrialsTab />
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
}
