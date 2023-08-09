import { Card, CardActionArea, CardContent, Typography } from "@mui/material";
import { AccessTime } from "@mui/icons-material";

export default function TrialDeadline() {
  return (
    <Card style={{ margin: 16, marginBottom: 0 }}>
      <CardActionArea>
        <CardContent>
          <Typography variant="h5">Trial Deadline</Typography>
          <div>
            <Typography
              style={{
                textAlign: "start",
                display: "flex",
                alignItems: "center",
              }}
            >
              <AccessTime style={{ margin: 8 }} /> dd/mm/yyyy - hh:mm{" "}
            </Typography>
          </div>
        </CardContent>
      </CardActionArea>
    </Card>
  );
}
