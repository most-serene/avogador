import { atom } from "jotai";
import { StroxCell } from "@exercises/types.ts";

export default atom<StroxCell[]>([{ content: "", type: "EDITABLE" }]);

const getInitializedTemplate = (): StroxCell[] => {
  return [
    {
      content: "",
      type: "VISIBLE",
    },
  ];
};

export { getInitializedTemplate };
