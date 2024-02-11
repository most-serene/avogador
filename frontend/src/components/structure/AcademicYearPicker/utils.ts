const getCourseYear = (delta = 0) => {
  const now = new Date();
  if (now.getMonth() < 6) {
    return `${getYearFromNow(delta - 1)}/${getYearFromNow(delta)}`;
  }
  return `${getYearFromNow(delta)}/${getYearFromNow(delta + 1)}`;
};

const getYearFromNow = (delta: number) => {
  return new Date(
    new Date().setFullYear(new Date().getFullYear() + delta),
  ).getFullYear();
};

export { getCourseYear };
