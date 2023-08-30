import * as React from "react";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import Menu from "@mui/material/Menu";
import MenuIcon from "@mui/icons-material/Menu";
import Container from "@mui/material/Container";
import Avatar from "@mui/material/Avatar";
import Tooltip from "@mui/material/Tooltip";
import MenuItem from "@mui/material/MenuItem";
import { Code, DarkMode, LightMode } from "@mui/icons-material";
import { useAuthService } from "../authentication/hooks/useAuthService";
import { Button, ButtonGroup, Divider } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useAtom } from "jotai";
import userAtom from "../authentication/userAtom";
import colorModeAtom from "../theme/colorModeAtom.ts";
import { useEffect } from "react";

interface PageItem {
  name: string;
  callback: () => void;
}

interface SettingsItem {
  name: string;
  callback: () => void;
}

export default function Navbar() {
  const { logout } = useAuthService();
  const navigate = useNavigate();
  const [user] = useAtom(userAtom);
  const [colorMode, setColorMode] = useAtom(colorModeAtom);
  const profilePicture = localStorage.getItem("profile-picture");

  useEffect(() => {
    const mode = localStorage.getItem("colorMode");
    setColorMode(mode === "dark" || mode === "light" ? mode : "light");
  }, [setColorMode]);

  const pages: PageItem[] = [
    {
      name: "Courses",
      callback: () => {
        navigate("/courses");
      },
    },
  ];
  const settings: SettingsItem[] = [
    {
      name: "Profile",
      callback: () => {
        navigate("/profile");
      },
    },
    { name: "Logout", callback: logout },
  ];

  const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(
    null,
  );
  const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(
    null,
  );

  const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElNav(event.currentTarget);
  };
  const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  const handleChangeColorMode = (mode: "dark" | "light") => {
    setColorMode(mode);
    localStorage.setItem("colorMode", mode);
  };

  const DesktopNavbar = () => {
    return (
      <>
        <Code sx={{ display: { xs: "none", md: "flex" }, mr: 1 }} />
        <Typography
          variant="h6"
          noWrap
          component="a"
          href="/"
          sx={{
            mr: 2,
            display: { xs: "none", md: "flex" },
            fontFamily: "monospace",
            fontWeight: 700,
            letterSpacing: ".3rem",
            color: "inherit",
            textDecoration: "none",
          }}
        >
          Avogador
        </Typography>
        <Box sx={{ flexGrow: 1, display: { xs: "flex", md: "none" } }}>
          <IconButton
            size="large"
            aria-label="account of current user"
            aria-controls="menu-appbar"
            aria-haspopup="true"
            onClick={handleOpenNavMenu}
            color="inherit"
          >
            <MenuIcon />
          </IconButton>
          <Menu
            id="menu-appbar"
            anchorEl={anchorElNav}
            anchorOrigin={{
              vertical: "bottom",
              horizontal: "left",
            }}
            keepMounted
            transformOrigin={{
              vertical: "top",
              horizontal: "left",
            }}
            open={Boolean(anchorElNav)}
            onClose={handleCloseNavMenu}
            sx={{
              display: { xs: "block", md: "none" },
            }}
          >
            {pages.map((page) => (
              <MenuItem
                key={page.name}
                onClick={() => {
                  handleCloseNavMenu();
                  page.callback();
                }}
              >
                <Typography textAlign="center">{page.name}</Typography>
              </MenuItem>
            ))}
          </Menu>
        </Box>
      </>
    );
  };

  const MobileNavbar = () => {
    return (
      <>
        <Code sx={{ display: { xs: "flex", md: "none" }, mr: 1 }} />
        <Typography
          variant="h5"
          noWrap
          component="a"
          href="/"
          sx={{
            mr: 2,
            display: { xs: "flex", md: "none" },
            flexGrow: 1,
            fontFamily: "monospace",
            fontWeight: 700,
            letterSpacing: ".3rem",
            color: "inherit",
            textDecoration: "none",
          }}
        >
          Avogador
        </Typography>
        <Box sx={{ flexGrow: 1, display: { xs: "none", md: "flex" } }}>
          {pages.map((page) => (
            <Button
              key={page.name}
              onClick={() => {
                handleCloseNavMenu();
                page.callback();
              }}
              sx={{ my: 2, color: "white", display: "block" }}
            >
              {page.name}
            </Button>
          ))}
        </Box>
      </>
    );
  };

  return (
    <AppBar position="static" style={{ marginBottom: 8 }}>
      <Container maxWidth={false}>
        <Toolbar disableGutters>
          <DesktopNavbar />
          <MobileNavbar />

          <Box sx={{ flexGrow: 0 }}>
            <Tooltip title="Open settings">
              <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                <Avatar
                  src={user && profilePicture !== null ? profilePicture : ""}
                />
              </IconButton>
            </Tooltip>
            <Menu
              sx={{ mt: "45px" }}
              id="menu-appbar"
              anchorEl={anchorElUser}
              anchorOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              keepMounted
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              open={Boolean(anchorElUser)}
              onClose={handleCloseUserMenu}
            >
              {settings.map((setting) => (
                <MenuItem
                  key={setting.name}
                  onClick={() => {
                    handleCloseUserMenu();
                    setting.callback();
                  }}
                >
                  <Typography textAlign="center">{setting.name}</Typography>
                </MenuItem>
              ))}
              <Divider />
              <ButtonGroup sx={{ mx: 2 }}>
                <Button
                  variant={colorMode === "light" ? "contained" : "outlined"}
                  onClick={() => {
                    handleChangeColorMode("light");
                  }}
                >
                  <LightMode />
                </Button>
                <Button
                  variant={colorMode === "dark" ? "contained" : "outlined"}
                  onClick={() => {
                    handleChangeColorMode("dark");
                  }}
                >
                  <DarkMode />
                </Button>
              </ButtonGroup>
            </Menu>
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
}
