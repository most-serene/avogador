import {
  Card,
  CardActionArea,
  CardContent,
  Stack,
  Typography,
} from "@mui/material";
import { Cluster, PlagiarismReport } from "@components/antiplagiarism/types.ts";

interface SimilarityClustersCardProps {
  report: PlagiarismReport;
  onClick: (cluster: Cluster) => void;
}

const SimilarityClustersCard = ({
  report,
  onClick: handleClick,
}: SimilarityClustersCardProps) => {
  return (
    <Card>
      <CardContent>
        <Typography sx={{ mb: 1 }}>Clusters:</Typography>
        <Stack spacing={1}>
          {report.clusters.map((cluster, i) => (
            <Card key={i} raised>
              <CardActionArea
                onClick={() => {
                  handleClick(cluster);
                }}
              >
                <CardContent>
                  <Typography>
                    Similarity:{" "}
                    {Math.round(cluster.averageSimilarity * 10000) / 100}%
                  </Typography>
                  <Typography>
                    Strength: {Math.round(cluster.strength * 10000) / 100}%
                  </Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          ))}
        </Stack>
      </CardContent>
    </Card>
  );
};

export default SimilarityClustersCard;
