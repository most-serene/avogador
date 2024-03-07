import {
  Button,
  Card,
  CardContent,
  CircularProgress,
  Divider,
  TextField,
  Typography,
} from "@mui/material";
import ButtonWithConfirmation from "@structure/ButtonWithConfirmation/ButtonWithConfirmation";
import Box from "@mui/material/Box";
import { useEffect, useState } from "react";
import useCourseService from "@courses/hooks/useCourseService";
import { enqueueSnackbar } from "notistack";
import { useAtom } from "jotai";
import { courseDetailAtom } from "@courses/courseDetail/courseDetailAtom";
import { UserCourseDetail } from "@courses/types";
import userAtom from "@authentication/userAtom.ts";
import { useNavigate } from "react-router-dom";

const CourseSettingsTab = () => {
  const { updateCourse, archiveCourse, downloadCourseArchive, deleteCourse } =
    useCourseService();
  const [course, setCourse] = useAtom(courseDetailAtom);
  const [rename, setRename] = useState("");
  const [isError, setIsError] = useState(false);
  const [user] = useAtom(userAtom);
  const [downloading, setDownloading] = useState<boolean>(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (course) {
      setRename(course.name);
    }
  }, [course]);

  return (
    <Box display={"flex"} justifyContent={"center"}>
      <Card sx={{ maxWidth: "32rem" }} raised>
        <CardContent>
          <Typography>Rename course</Typography>

          <TextField
            sx={{ marginTop: "1rem" }}
            error={isError}
            fullWidth
            label="New course name"
            value={rename}
            onChange={(event) => {
              setIsError(false);
              setRename(event.target.value);
            }}
          />
          <Box display={"flex"} justifyContent={"center"} margin={"1rem"}>
            <ButtonWithConfirmation
              onConfirm={() => {
                if (course === undefined) return;
                updateCourse({
                  id: course.id,
                  name: rename,
                  year: course.year,
                  isArchived: course.isArchived,
                })
                  .then((c) => {
                    setCourse((old: UserCourseDetail | undefined) => {
                      if (old === undefined) throw new Error("Illegal state");
                      const updated: UserCourseDetail = { ...old, ...c };
                      return updated;
                    });
                    enqueueSnackbar(`Course ${c.name} updated successfully`, {
                      variant: "success",
                    });
                  })
                  .catch((r: Error) => {
                    setIsError(true);
                    enqueueSnackbar(r.message, { variant: "error" });
                  });
              }}
              variant="outlined"
              confirmText="rename"
              description="You are changing the name of this course"
            >
              Rename
            </ButtonWithConfirmation>
          </Box>
          <Divider />
          <Typography>
            Delete this course and all its data for you and your students.
          </Typography>
          <Box display={"flex"} justifyContent={"center"} margin={"1rem"}>
            <ButtonWithConfirmation
              title={`You are deleting the course ${course?.name}`}
              description={`Are you sure to delete the course ${course?.name}?
                       All the trials, exercises, submissions, results and testcases in it will be lost.`}
              onConfirm={() => {
                if (!course) return;
                navigate("/");
                deleteCourse(course)
                  .then(() => {
                    enqueueSnackbar(
                      `Course ${course.name} deleted successfully`,
                      { variant: "success" },
                    );
                  })
                  .catch((err: Error) => {
                    enqueueSnackbar(err.message, { variant: "error" });
                  });
              }}
              variant={"outlined"}
              confirmColor={"error"}
              confirmText={"Delete"}
              color={"error"}
              disabled={
                !course ||
                (course.role !== "ADMIN" && (!user || !user.isSuperuser))
              }
            >
              Delete course
            </ButtonWithConfirmation>
          </Box>
          <Divider />
          <Typography>
            Only you will be able to access the course, that will be saved as a
            zip file. This action is irreversible.
          </Typography>
          {course != null && !course.isArchived && (
            <Box display={"flex"} justifyContent={"center"} marginTop={"1rem"}>
              <ButtonWithConfirmation
                onConfirm={() => {
                  archiveCourse(course)
                    .then((updatedCourse) => {
                      enqueueSnackbar(
                        "archiving procedure has been dispatched",
                        {
                          variant: "info",
                        },
                      );
                      setCourse({
                        ...course,
                        isArchived: updatedCourse.isArchived,
                      });
                    })
                    .catch((err: Error) => {
                      enqueueSnackbar(err.message, { variant: "error" });
                    });
                }}
                title={`You are archiving the course ${course.name}`}
                description={
                  "An archived course is not accessible by students and cannot be modified in any way. This action is irreversible."
                }
                confirmText={"Archive"}
                variant={"outlined"}
                color={"error"}
                disabled={
                  (course.role !== "ADMIN" && (!user || !user.isSuperuser)) ||
                  course.isArchived
                }
                confirmColor={"error"}
              >
                Archive course
              </ButtonWithConfirmation>
            </Box>
          )}
          {course != null && course.isArchived && (
            <Box display={"flex"} justifyContent={"center"} marginTop={"1rem"}>
              {downloading ? (
                <CircularProgress />
              ) : (
                <Button
                  variant={"outlined"}
                  disabled={!course.isArchived}
                  onClick={() => {
                    setDownloading(true);
                    void downloadCourseArchive(course, (progressEvent) => {
                      console.log(progressEvent);
                    }).finally(() => {
                      setDownloading(false);
                    });
                  }}
                >
                  Download archive
                </Button>
              )}
            </Box>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default CourseSettingsTab;
