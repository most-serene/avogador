import {
  Avatar,
  Card,
  CardContent,
  Chip,
  Divider,
  Grid,
  Skeleton,
  Typography,
} from "@mui/material";
import SecurityIcon from "@mui/icons-material/Security";
import { User } from "@authentication/types";
import { useEffect, useState } from "react";

const Profile = ({
  user,
  profilePicture,
}: {
  user?: User | null;
  profilePicture?: string;
}) => {
  const [roles, setRoles] = useState<string[]>([]);

  useEffect(() => {
    if (!user) return;

    if (!user.isProfessor && !user.isSuperuser) {
      setRoles(["Student"]);
      return;
    }
    const r: string[] = [];
    if (user.isProfessor) {
      r.push("Professor");
    }
    if (user.isSuperuser) {
      r.push("Superuser");
    }
    setRoles(r);
  }, [user, setRoles]);

  return (
    <>
      <Card raised sx={{ width: "32rem" }}>
        <CardContent>
          <Typography variant="h5">Your profile</Typography>
          <Divider sx={{ marginBottom: "1rem" }} />
          <Grid container>
            <Grid item xs={4} display={"flex"} justifyContent={"center"}>
              <Avatar
                src={profilePicture ?? ""}
                sx={{ width: 100, height: 100 }}
              />
            </Grid>
            <Grid item xs={8}>
              <Grid item xs={12}>
                {user ? (
                  <Typography>
                    Name: {user.givenName} {user.familyName}
                  </Typography>
                ) : (
                  <>
                    <Skeleton variant="text" sx={{ fontSize: 24 }} />
                  </>
                )}

                {user ? (
                  <>
                    <Typography display={"inline"}>Email:&nbsp;</Typography>
                    <Typography fontFamily={"monospace"} display={"inline"}>
                      {user.email}
                    </Typography>
                  </>
                ) : (
                  <Skeleton variant="text" sx={{ fontSize: 24 }} />
                )}
              </Grid>
              <Grid item xs={12}>
                {user ? (
                  <Grid container sx={{ marginTop: "1rem" }}>
                    {roles.map((r) => (
                      <Grid item xs={6} key={r}>
                        <Chip
                          label={r}
                          color="primary"
                          icon={<SecurityIcon />}
                        />
                      </Grid>
                    ))}
                  </Grid>
                ) : (
                  <Grid container sx={{ marginTop: "1rem" }}>
                    <Skeleton variant="rounded" width={93} height={32} />
                  </Grid>
                )}
              </Grid>
            </Grid>
          </Grid>
        </CardContent>
      </Card>
    </>
  );
};

export default Profile;
