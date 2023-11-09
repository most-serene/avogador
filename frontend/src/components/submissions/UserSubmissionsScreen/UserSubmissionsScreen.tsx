import {
  Grid,
  IconButton,
  ListItemIcon,
  ListItemText,
  Stack,
  Typography,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import { useParams, useSearchParams } from "react-router-dom";
import { Exercise, Submission, SubmissionResult } from "@exercises/types.ts";
import useExerciseService from "@exercises/hooks/useExerciseService.tsx";
import Box from "@mui/material/Box";
import { User } from "@authentication/types.ts";
import useUserService from "@hooks/useUserService.tsx";
import { enqueueSnackbar } from "notistack";
import { Check, FilterList } from "@mui/icons-material";
import MenuItem from "@mui/material/MenuItem";
import Menu from "@mui/material/Menu";
import { compareDesc } from "date-fns";
import SubmissionItem from "@components/submissions/UserSubmissionsScreen/SubmissionItem.tsx";

const UserSubmissionsScreen = () => {
  const {
    getExercisesByTrialId,
    getUserSubmissionsFromExercise,
    getUserSubmissionsResultsFromExercise,
  } = useExerciseService();
  const { getUserById } = useUserService();
  const { trialId, userId } = useParams();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const [user, setUser] = useState<User>();
  const [searchParams] = useSearchParams();
  const [exercises, setExercises] = useState<Record<string, Exercise>>({});
  const [selectedExercises, setSelectedExercises] = useState<Set<string>>(
    new Set(),
  );
  const [selectedSubmission, setSelectedSubmission] = useState<string>("");
  const [submissions, setSubmissions] = useState<Submission[]>([]);
  const [results, setResults] = useState<
    Record<string, SubmissionResult[] | undefined>
  >({});

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleCloseMenu = () => {
    setAnchorEl(null);
  };

  const handleSelectExercise = (exercise: Exercise) => {
    setSelectedExercises((prevState) => {
      if (prevState.has(exercise.id)) {
        prevState.delete(exercise.id);
        return new Set([...prevState]);
      }
      return new Set([...prevState.add(exercise.id)]);
    });
  };

  useEffect(() => {
    if (trialId == null || userId == null) return;
    const queryExercise = searchParams.get("exerciseId");

    const getSubmissions = async (exercises: Exercise[]) => {
      return (
        await Promise.all(
          exercises.map((exercise) =>
            getUserSubmissionsFromExercise(exercise.id, userId),
          ),
        )
      ).flat();
    };

    const getResults = async (exercises: Exercise[]) => {
      return (
        await Promise.all(
          exercises.map((exercise) =>
            getUserSubmissionsResultsFromExercise(exercise.id, userId),
          ),
        )
      ).reduce((acc, result) => {
        return { ...acc, ...result };
      }, {});
    };

    getUserById(userId)
      .then((user) => {
        setUser(user);
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });

    getExercisesByTrialId(trialId)
      .then(async (exercises) => {
        setExercises(
          exercises.reduce(
            (acc: Record<string, Exercise>, exercise) => (
              (acc[exercise.id] = exercise), acc
            ),
            {},
          ),
        );

        setSubmissions(await getSubmissions(exercises));
        setResults(await getResults(exercises));

        if (queryExercise != null) {
          setSelectedExercises(new Set([queryExercise]));
        } else {
          setSelectedExercises(
            new Set(exercises.map((exercise) => exercise.id)),
          );
        }
      })
      .catch((err: Error) => {
        enqueueSnackbar(err.message, { variant: "error" });
      });
  }, [
    trialId,
    userId,
    searchParams,
    getUserById,
    getExercisesByTrialId,
    getUserSubmissionsFromExercise,
    getUserSubmissionsResultsFromExercise,
  ]);

  return (
    <Grid container sx={{ height: "100%" }}>
      <Grid
        item
        xs={6}
        sx={{ height: "100%", overflow: "scroll" }}
        className="hidden-scrollbar"
      >
        <Box paddingX={2}>
          <Box display="flex" alignItems="center">
            <Typography variant="h5">
              {user &&
                `${user.email.split("@")[0]} - ${user.givenName} ${
                  user.familyName
                }`}
              &apos;s submissions
            </Typography>
            <IconButton onClick={handleClick} sx={{ ml: 2 }}>
              <FilterList />
            </IconButton>
          </Box>
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleCloseMenu}
          >
            {Object.values(exercises).map((exercise) => (
              <MenuItem
                key={exercise.id}
                onClick={() => {
                  handleSelectExercise(exercise);
                }}
              >
                {selectedExercises.has(exercise.id) && (
                  <ListItemIcon>
                    <Check />
                  </ListItemIcon>
                )}
                <ListItemText inset={!selectedExercises.has(exercise.id)}>
                  {exercise.name}
                </ListItemText>
              </MenuItem>
            ))}
          </Menu>

          <Stack spacing={2}>
            {submissions
              .sort((a, b) => compareDesc(a.timestamp, b.timestamp))
              .filter((submission) =>
                selectedExercises.has(submission.exerciseId),
              )
              .map((submission) => (
                <SubmissionItem
                  key={submission.id}
                  submission={submission}
                  exercise={exercises[submission.exerciseId]}
                  results={results[submission.id] ?? []}
                  onSelect={() => {
                    setSelectedSubmission(submission.id);
                  }}
                  selected={selectedSubmission === submission.id}
                />
              ))}
          </Stack>
        </Box>
      </Grid>
      <Grid item xs={6}>
        Code
      </Grid>
    </Grid>
  );
};

export default UserSubmissionsScreen;
