export const getCellBorderColor = (
  cellType: "EDITABLE" | "VISIBLE" | "HIDDEN",
) => {
  switch (cellType) {
    case "EDITABLE":
      return "primary.main";
    case "VISIBLE":
      return "secondary.main";
    case "HIDDEN":
      return "secondary.main";
  }
};

export const getCellBorderStyle = (
  cellType: "EDITABLE" | "VISIBLE" | "HIDDEN",
) => {
  return cellType === "HIDDEN" ? "dashed" : "solid";
};
