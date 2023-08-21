import { Box, Skeleton, Tab, Tabs, Typography } from "@mui/material";
import { useParams } from "react-router-dom";
import { SyntheticEvent, useEffect, useState } from "react";
import { useAvogadorApi } from "../../hooks/useAvogadorApi.tsx";
import { CourseDetail, GetCoursesDetailResponse } from "./types.ts";
import Container from "@mui/material/Container";
import TabPanel from "../misc/TabPanel.tsx";

function a11yProps(index: number) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

export default function CourseDetailScreen() {
  const avogadorApi = useAvogadorApi();
  const { courseId } = useParams();

  const [course, setCourse] = useState<CourseDetail>();
  const [openTab, setOpenTab] = useState(0);

  const tabs = ["Overview", "Tests", "Members", "Settings"];

  useEffect(() => {
    avogadorApi
      .get(`/courses/${courseId}`)
      .then(({ data }: { data: GetCoursesDetailResponse }) => {
        setCourse(data);
        console.log(data);
      })
      .catch((err) => {
        console.error(err);
      });
  }, [courseId]);

  const handleTabChange = (event: SyntheticEvent, newValue: number) => {
    event.preventDefault();
    setOpenTab(newValue);
  };

  return (
    <Box
      className={"full-page-without-header"}
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
          <Tab label={tab} {...a11yProps(i)} />
        ))}
      </Tabs>
      <Container maxWidth={false}>
        <Typography variant={"h3"} align={"center"}>
          {course ? `${course.name} (${course.year})` : <Skeleton />}
        </Typography>
        <TabPanel value={openTab} index={0}>
          <Typography variant={"h4"} color={"secondary"} align={"center"}>
            Tab Coming Soon
          </Typography>
        </TabPanel>
      </Container>
    </Box>
  );
}
