import {
  Box,
  Card,
  CardContent,
  CircularProgress,
  Divider,
  Grid,
  Typography,
  useTheme,
} from "@mui/material";
import SubmissionViewer from "@components/submissions/UserSubmissionsScreen/SubmissionViewer.tsx";
import { PlagiarismReport } from "@components/antiplagiarism/types.ts";
import { Exercise, Strox, StroxCell } from "@exercises/types.ts";

interface SimilarityComparisonModalProps {
  exercise: Exercise;
  report: PlagiarismReport;
  template?: Strox;
  firstSubmissionId?: string;
  firstSubmission?: StroxCell[];
  secondSubmissionId?: string;
  secondSubmission?: StroxCell[];
}

const SimilarityComparisonModal = ({
  exercise,
  report,
  template,
  firstSubmissionId,
  firstSubmission,
  secondSubmissionId,
  secondSubmission,
}: SimilarityComparisonModalProps) => {
  const theme = useTheme();

  const loading =
    template == null ||
    firstSubmissionId == null ||
    firstSubmission == null ||
    secondSubmissionId == null ||
    secondSubmission == null;

  return (
    <Box
      sx={{
        position: "absolute" as const,
        top: "50%",
        left: "50%",
        transform: "translate(-50%, -50%)",
        width: "80%",
        bgcolor: "background.paper",
        height: "80%",
        overflow: "scroll",
        border: "2px solid " + theme.palette.primary.main,
        boxShadow: 24,
        p: 4,
      }}
      className={"hidden-scrollbar"}
    >
      {loading ? (
        <CircularProgress />
      ) : (
        <Grid container spacing={1}>
          <Grid item xs={6}>
            <Card>
              <CardContent>
                <Typography>
                  {report.submissions[firstSubmissionId].email} -{" "}
                  {report.submissions[firstSubmissionId].givenName}{" "}
                  {report.submissions[firstSubmissionId].familyName}
                </Typography>
                )
                <Divider />
                <SubmissionViewer
                  template={template}
                  submissionCode={firstSubmission}
                  language={exercise.trial.language}
                />
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={6}>
            <Card>
              <CardContent>
                <Typography>
                  {report.submissions[secondSubmissionId].email} -{" "}
                  {report.submissions[secondSubmissionId].givenName}{" "}
                  {report.submissions[secondSubmissionId].familyName}
                </Typography>
                <Divider />
                <SubmissionViewer
                  template={template}
                  submissionCode={secondSubmission}
                  language={exercise.trial.language}
                />
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}
    </Box>
  );
};

export default SimilarityComparisonModal;
