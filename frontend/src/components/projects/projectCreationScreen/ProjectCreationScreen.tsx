import {
  Button,
  Card,
  CardContent,
  Divider,
  FormControl,
  InputLabel,
  Select,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useLocation, useNavigate } from "react-router-dom";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import MenuItem from "@mui/material/MenuItem";
import { DateTimePicker } from "@mui/x-date-pickers";
import { addDays, setSeconds } from "date-fns";
import { useEffect, useMemo, useState } from "react";
import { UserCourse } from "@courses/types.ts";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { ForbiddenError } from "@error/types.ts";
import { enqueueSnackbar } from "notistack";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import {
  Project,
  ProjectData,
  ProjectType,
} from "@components/projects/types.ts";
import MarkdownEditor from "@structure/editors/MarkdownEditor.tsx";
import NotebookCreationInfo from "@components/projects/projectCreationScreen/NotebookCreationInfo.tsx";
import useProjectService from "@components/projects/hooks/useProjectService.tsx";

const ProjectCreationScreen = () => {
  const [user] = useAtom(userAtom);
  const navigate = useNavigate();
  const { state }: { state: null | { courseId: string } } = useLocation() as {
    state: null | { courseId: string };
  };
  const { getUserCourses } = useCourseService();
  const { createProject } = useProjectService();
  const globalErrorSetter = useGlobalErrorSetter();

  const [courses, setCourses] = useState<UserCourse[]>([]);
  const [courseId, setCourseId] = useState<string>(state?.courseId ?? "");
  const [projectName, setProjectName] = useState<string>("");
  const [projectType, setProjectType] = useState<ProjectType>("NOTEBOOK");
  const [deadline, setDeadline] = useState<Date>(addDays(new Date(), 7));
  const [description, setDescription] = useState<string>("");
  const [projectData, setProjectData] = useState<ProjectData>({
    isValid: false,
  });
  const [canSubmit, setCanSubmit] = useState(true);

  const isFormValid = useMemo<boolean>(() => {
    return (
      courseId !== "" &&
      projectName.trim() !== "" &&
      projectData.isValid &&
      new Date() < deadline
    );
  }, [courseId, projectName, deadline, projectData]);

  useEffect(() => {
    if (user == null) return;

    getUserCourses(user.id)
      .then((userCourses: UserCourse[]) => {
        if (
          !userCourses.some(
            (userCourse) =>
              userCourse.role === "COLLABORATOR" || userCourse.role === "ADMIN",
          )
        ) {
          globalErrorSetter(
            new ForbiddenError("/projects/new", "You cannot create projects"),
          );
        }

        const fromCourseId = state === null ? undefined : state.courseId;

        if (fromCourseId == null) {
          setCourses(
            userCourses
              .filter(
                (userCourse) =>
                  userCourse.role === "COLLABORATOR" ||
                  userCourse.role === "ADMIN",
              )
              .filter((userCourse) => !userCourse.course.isArchived),
          );
          return;
        }

        const fromCourse = userCourses.find(
          (userCourse) => userCourse.course.id === fromCourseId,
        );

        if (
          fromCourse != null &&
          (fromCourse.role === "COLLABORATOR" || fromCourse.role === "ADMIN")
        ) {
          setCourses([fromCourse]);
        } else {
          globalErrorSetter(
            new ForbiddenError(
              "/projects/new",
              `You cannot create projects in ${fromCourseId}`,
            ),
          );
        }
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [getUserCourses, globalErrorSetter, state, user]);

  const handleProjectCreation = () => {
    const { isValid, ...data } = projectData;
    const project: Omit<Project, "id"> = {
      courseId: courseId,
      name: projectName,
      description: description,
      canSubmit: true,
      deadline: deadline,
      projectType: projectType,
      ...data,
    };
    setCanSubmit(false);
    createProject(project)
      .then((project) => {
        navigate(`/projects/${project.id}`);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      })
      .finally(() => {
        setCanSubmit(true);
      });
  };

  const getProjectDataCreationComponent = (projectType: ProjectType) => {
    switch (projectType) {
      case "NOTEBOOK":
        return (
          <NotebookCreationInfo
            onChange={(data) => {
              setProjectData(data);
            }}
          />
        );
    }
  };

  return (
    <Box display="flex" justifyContent="center" paddingTop={2}>
      <Card sx={{ maxWidth: "60rem" }}>
        <CardContent>
          <Stack spacing={2}>
            <Typography variant="h5" color="text.secondary">
              New Project
            </Typography>

            <Box>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <FormControl fullWidth>
                    <InputLabel id="courseId">Course</InputLabel>
                    <Select
                      labelId="courseId"
                      id="courseId"
                      label="Course"
                      disabled={courses.length === 1}
                      value={courses.length === 0 ? "" : courseId}
                      onChange={(event) => {
                        setCourseId(event.target.value);
                      }}
                    >
                      {courses.map((c) => (
                        <MenuItem key={c.course.id} value={c.course.id}>
                          {c.course.name} - {c.course.year}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={6}>
                  <FormControl fullWidth>
                    <InputLabel id="projectType">Project type</InputLabel>
                    <Select
                      labelId="projectType"
                      id="projectType"
                      label="Project type"
                      disabled
                      value={projectType}
                      onChange={(event) => {
                        setProjectType(event.target.value as ProjectType);
                      }}
                    >
                      <MenuItem value={"NOTEBOOK"}>Notebook</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={8}>
                  <TextField
                    fullWidth
                    label="Project name"
                    value={projectName}
                    onChange={(event) => {
                      setProjectName(event.target.value.trimStart());
                    }}
                  />
                </Grid>
                <Grid item xs={4}>
                  <DateTimePicker
                    sx={{ width: "100%" }}
                    ampm={false}
                    value={deadline}
                    format={"dd/MM/yyyy HH:mm"}
                    onChange={(newVal) => {
                      if (newVal) setDeadline(setSeconds(newVal, 0));
                    }}
                    disablePast
                    label="Deadline"
                    minDate={new Date()}
                  />
                </Grid>

                <Grid item xs={12}>
                  <MarkdownEditor
                    value={description}
                    onChange={(markdown) => {
                      setDescription(markdown);
                    }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <Divider>
                    {projectType[0] + projectType.toLowerCase().slice(1)}{" "}
                    Settings{" "}
                  </Divider>
                </Grid>
                {getProjectDataCreationComponent(projectType)}
              </Grid>
            </Box>

            <Box display="flex" justifyContent="center">
              <Button
                variant="outlined"
                disabled={!isFormValid || !canSubmit}
                onClick={handleProjectCreation}
              >
                Create
              </Button>
            </Box>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
};

export default ProjectCreationScreen;
