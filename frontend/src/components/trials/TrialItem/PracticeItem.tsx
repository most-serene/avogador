import { Practice } from "@trials/types.ts";
import {
  alpha,
  Card,
  CardActionArea,
  CardContent,
  Typography,
  useTheme,
} from "@mui/material";
import { format, parseJSON } from "date-fns";
import { useNavigate } from "react-router-dom";

interface PracticeItemProps {
  practice: Practice;
}

const PracticeItem = ({ practice }: PracticeItemProps) => {
  const theme = useTheme();
  const navigate = useNavigate();

  const accent = alpha(theme.palette.secondary.main, 0.5);

  const getBorderImage = () => {
    if (practice.isVisible) return "";
    return `16 repeating-linear-gradient(-45deg, ${accent} 0, ${accent} 0.5rem, transparent 0, transparent 1rem)`;
  };

  return (
    <Card
      raised
      sx={{
        mb: 2,
      }}
    >
      <CardActionArea
        onClick={() => {
          navigate(`/trials/${practice.id}`);
        }}
      >
        <CardContent
          sx={{
            borderLeft: `1rem solid ${accent}`,
            borderImage: getBorderImage(),
          }}
        >
          <Typography variant="h5" marginBottom={1}>
            {practice.name}
          </Typography>
          <Typography variant="body1">
            Starts on:{" "}
            {format(parseJSON(practice.startTimestamp), "yyyy/MM/dd HH:mm")}
          </Typography>
          <Typography variant="body1">
            Deadline: {format(parseJSON(practice.deadline), "yyyy/MM/dd HH:mm")}
          </Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};

export default PracticeItem;
