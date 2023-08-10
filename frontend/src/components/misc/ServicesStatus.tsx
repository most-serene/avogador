import { useEffect, useState } from "react";
import { avogadorApi } from "../../utils/axiosConf.ts";
import Button from "@mui/material/Button";
import LoginGoogle from "../authentication/LoginGoogle.tsx";

export default function ServicesStatus() {
  const [gatewayStatus, setGatewayStatus] = useState<string>("offline");
  const [usersStatus, setUsersStatus] = useState<string>("offline");
  const [coursesStatus, setCoursesStatus] = useState<string>("offline");

  useEffect(() => {
    const i = setInterval(() => {
      avogadorApi
        .get("/status")
        .then(({ data }: { data: string }) => {
          setGatewayStatus(data);
        })
        .catch(() => {
          setGatewayStatus("offline");
        });
      avogadorApi
        .get("/users/status")
        .then(({ data }: { data: string }) => {
          setUsersStatus(data);
        })
        .catch(() => {
          setUsersStatus("offline");
        });
      avogadorApi
        .get("/courses/status")
        .then(({ data }: { data: string }) => {
          setCoursesStatus(data);
        })
        .catch(() => {
          setCoursesStatus("offline");
        });
    }, 2000);

    return () => {
      clearInterval(i);
    };
  }, []);

  return (
    <>
      <div>{import.meta.env.VITE_AVOGADOR_BACKEND_API_ADDRESS}</div>
      <h1>Avogador</h1>
      <p>gateway {gatewayStatus}</p>
      <p>users {usersStatus}</p>
      <p>gateway {coursesStatus}</p>
      <div className="card">
        <LoginGoogle />
        <Button
          variant="outlined"
          onClick={() => {
            avogadorApi
              .get("/users/logout")
              .then((res) => {
                console.log(res);
              })
              .catch((err) => {
                console.log(err);
              });
          }}
        >
          Logout
        </Button>
      </div>
      <p className="read-the-docs">
        Click on the Vite and React logos to learn more
      </p>
    </>
  );
}
