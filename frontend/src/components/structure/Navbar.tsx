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
import { DarkMode, LightMode } from "@mui/icons-material";
import { useAuthService } from "@authentication/hooks/useAuthService";
import { Button, ButtonGroup, Divider } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useAtom } from "jotai";
import userAtom from "@authentication/userAtom";
import colorModeAtom from "@theme/colorModeAtom.ts";
import { forwardRef, useEffect } from "react";

import Logo from "@assets/logo.svg";

interface PageItem {
  name: string;
  callback: () => void;
}

interface SettingsItem {
  name: string;
  callback: () => void;
}

const Navbar = forwardRef<HTMLElement>(function Navbar(_, ref) {
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
    {
      name: "Logout",
      callback: () => {
        logout();
        navigate("/");
      },
    },
  ];

  const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(
    null,
  );

  const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElUser(event.currentTarget);
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
        <Button sx={{ display: { xs: "none", md: "flex" } }}>
          <Box
            component="img"
            onClick={() => {
              navigate("/");
            }}
            sx={{
              height: 30,
              display: { xs: "none", md: "flex" },
              mr: 1,
            }}
            src={Logo}
          />
        </Button>

        <Box sx={{ flexGrow: 1, display: { xs: "none", md: "flex" } }}>
          {pages.map((page) => (
            <Button
              key={page.name}
              onClick={() => {
                page.callback();
              }}
              sx={{ my: 2, color: "black", display: "block" }}
            >
              {page.name}
            </Button>
          ))}
        </Box>
      </>
    );
  };

  const MobileNavbar = () => {
    const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(
      null,
    );
    const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
      setAnchorElNav(event.currentTarget);
    };
    const handleCloseNavMenu = () => {
      setAnchorElNav(null);
    };

    return (
      <>
        <Box sx={{ flexGrow: 0, display: { xs: "flex", md: "none" } }}>
          <IconButton
            size="large"
            aria-label="account of current user"
            aria-controls="menu-appbar"
            aria-haspopup="true"
            onClick={handleOpenNavMenu}
            color="inherit"
            style={{ width: 40, height: 40 }}
          >
            <MenuIcon style={{ color: "black" }} />
          </IconButton>
          <Menu
            id="menu-appbar"
            keepMounted
            anchorEl={anchorElNav}
            anchorOrigin={{
              vertical: "bottom",
              horizontal: "left",
            }}
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

        <Box sx={{ flexGrow: 1 }} justifyContent={"center"} display={"flex"}>
          <Button>
            <Box
              component="img"
              onClick={() => {
                navigate("/");
              }}
              sx={{
                height: 30,
                display: { xs: "flex", md: "none" },
              }}
              src={Logo}
            />
          </Button>
        </Box>
      </>
    );
  };

  return (
    <AppBar
      ref={ref}
      id="navbar"
      position="static"
      style={{ marginBottom: 8 }}
      enableColorOnDark
    >
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
              {user &&
                settings.map((setting) => (
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
              {user && <Divider />}
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
});

export default Navbar;
