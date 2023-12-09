import { Card, CardContent } from "@mui/material";
import { BarChart } from "@mui/x-charts";
import { PlagiarismReport } from "@components/antiplagiarism/types.ts";

const SimilarityDistributionChart = ({
  report,
}: {
  report: PlagiarismReport;
}) => {
  return (
    <Card>
      <CardContent>
        <BarChart
          height={250}
          series={[
            {
              label: "average similarity distribution",
              data: report.averageMetrics.distribution,
            },
            {
              label: "max similarity distribution",
              data: report.maxMetrics.distribution,
            },
          ]}
          xAxis={[
            {
              scaleType: "band",
              data: [
                "0-10",
                "11-20",
                "21-30",
                "31-40",
                "41-50",
                "51-60",
                "61-70",
                "71-80",
                "81-90",
                "91-100",
              ],
            },
          ]}
        />
      </CardContent>
    </Card>
  );
};

export default SimilarityDistributionChart;
