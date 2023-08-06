import { useEffect, useState } from "react";
import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";
import "./App.css";
import LoginGoogle from "./components/authentication/LoginGoogle";
import { avogadorApi } from "./utils/axiosConf";
import Button from "@mui/material/Button";

function App() {
  const [count, setCount] = useState(0);
  const [gatewayStatus, setGatewayStatus] = useState<string>("offline");
  const [usersStatus, setUsersStatus] = useState<string>("offline");
  const [coursesStatus, setCoursesStatus] = useState<string>("offline");

  useEffect(() => {
    const storedCSRF = localStorage.getItem("Jwt-CSRF-Hash");
    if (storedCSRF !== null) {
      avogadorApi.defaults.headers.common["Jwt-CSRF-Hash"] = storedCSRF;
    }
  }, []);

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
      <div>
        <a href="https://vitejs.dev" target="_blank" rel="noreferrer">
          <img src={viteLogo} className="logo" alt="Vite logo" />
        </a>

        <a href="https://react.dev" target="_blank" rel="noreferrer">
          <img src={reactLogo} className="logo react" alt="React logo" />
        </a>
      </div>
      <h1>Vite + React</h1>
      <p>gateway {gatewayStatus}</p>
      <p>users {usersStatus}</p>
      <p>gateway {coursesStatus}</p>
      <div className="card">
        <button
          onClick={() => {
            setCount((count) => count + 1);
          }}
        >
          count is {count}
        </button>
        <p>
          Edit <code>src/App.tsx</code> and save to test HMR
        </p>
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
        <LoginGoogle />
      </div>
      <p className="read-the-docs">
        Click on the Vite and React logos to learn more
      </p>
    </>
  );
}

export default App;
