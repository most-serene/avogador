import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom.ts";
import { useEffect, useState } from "react";
import { useGlobalErrorSetter } from "@error/GlobalErrorState.tsx";
import {
  Button,
  Card,
  CardContent,
  CircularProgress,
  IconButton,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import { ChevronLeft, ChevronRight } from "@mui/icons-material";
import useCourseService from "@courses/hooks/useCourseService.tsx";
import { useNavigate } from "react-router-dom";

const getCourseYear = (delta = 0) => {
  const now = new Date();
  if (now.getMonth() < 6) {
    return `${getYearFromNow(delta - 1)}/${getYearFromNow(delta)}}`;
  }
  return `${getYearFromNow(delta)}/${getYearFromNow(delta + 1)}`;
};

const getYearFromNow = (delta: number) => {
  return new Date(
    new Date().setFullYear(new Date().getFullYear() + delta),
  ).getFullYear();
};

export default function CourseCreationScreen() {
  const navigate = useNavigate();
  const [user] = useAtom(userAtom);
  const globalErrorSetter = useGlobalErrorSetter();
  const { createCourse } = useCourseService();
  const [name, setName] = useState("");
  const [year, setYear] = useState(getCourseYear());

  const [isRequestProcessing, setIsRequestProcessing] = useState(false);

  const handleSubmit = () => {
    setIsRequestProcessing(true);
    createCourse(name, year)
      .then((course) => {
        navigate(`/courses/${course.id}`);
      })
      .catch((err) => {
        console.error(err);
      })
      .finally(() => {
        setIsRequestProcessing(false);
      });
  };

  useEffect(() => {
    if (user && !(user.isProfessor || user.isSuperuser)) {
      globalErrorSetter(new Error());
    }
  }, [user]);

  return (
    <Box display="flex" justifyContent="center" paddingTop={2}>
      <Card sx={{ width: "48rem" }}>
        <CardContent>
          <Stack spacing={2}>
            <Typography variant="h5" color="text.secondary">
              New Course
            </Typography>

            <Box>
              <Grid container spacing={1}>
                <Grid item xs={12} md={8}>
                  <TextField
                    fullWidth
                    label="Name"
                    value={name}
                    onChange={(event) => {
                      setName(event.target.value);
                    }}
                  />
                </Grid>
                <Grid item xs={12} md={4}>
                  <YearPicker value={year} onChange={setYear} />
                </Grid>
              </Grid>
            </Box>
            <Box display="flex" justifyContent="center">
              <Button
                variant="outlined"
                disabled={name === "" || isRequestProcessing}
                onClick={handleSubmit}
              >
                {isRequestProcessing && (
                  <CircularProgress size={"1rem"} sx={{ pr: 1 }} />
                )}
                Create
              </Button>
            </Box>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}

interface YearPickerProps {
  value: string;
  onChange: (val: string) => void;
}

const YearPicker = ({ value: year, onChange: setYear }: YearPickerProps) => {
  const [currentYearDelta, setCurrentYearDelta] = useState(0);
  const updateYearDelta = (newDelta: 1 | -1) => {
    setYear(getCourseYear(currentYearDelta + newDelta));
    setCurrentYearDelta(currentYearDelta + newDelta);
  };

  return (
    <TextField
      disabled
      fullWidth
      label="Year"
      value={year}
      inputProps={{ style: { textAlign: "center" } }}
      onChange={(event) => {
        setYear(event.target.value);
      }}
      InputProps={{
        startAdornment: (
          <IconButton
            disabled={currentYearDelta < 0}
            onClick={() => {
              updateYearDelta(-1);
            }}
          >
            <ChevronLeft fontSize="small" />
          </IconButton>
        ),
        endAdornment: (
          <IconButton
            onClick={() => {
              updateYearDelta(1);
            }}
          >
            <ChevronRight fontSize="small" />
          </IconButton>
        ),
      }}
    />
  );
};
