import { Card, CardContent, Stack, Typography } from "@mui/material";
import { PlagiarismReport } from "@components/antiplagiarism/types.ts";

const SimilarityClustersCard = ({ report }: { report: PlagiarismReport }) => {
  return (
    <Card>
      <CardContent>
        <Typography sx={{ mb: 1 }}>Clusters:</Typography>
        <Stack spacing={1}>
          {report.clusters.map((cluster, i) => (
            <Card key={i} raised>
              <CardContent>
                <Typography>
                  Similarity:{" "}
                  {Math.round(cluster.averageSimilarity * 10000) / 100}%
                </Typography>
                <Typography>
                  Strength: {Math.round(cluster.strength * 10000) / 100}%
                </Typography>
              </CardContent>
            </Card>
          ))}
        </Stack>
      </CardContent>
    </Card>
  );
};

export default SimilarityClustersCard;
