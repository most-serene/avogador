// eslint-disable-next-line import/named
import { AxiosResponse } from "axios";

export const saveResponseToFile = (
  response: AxiosResponse,
  filename: string,
) => {
  // create file link in browser's memory
  // eslint-disable-next-line @typescript-eslint/no-unsafe-argument
  const href = URL.createObjectURL(response.data);

  // create "a" HTML element with href to file & click
  const link = document.createElement("a");
  link.href = href;
  link.setAttribute("download", filename);
  document.body.appendChild(link);
  link.click();

  // clean up "a" element & remove ObjectURL
  document.body.removeChild(link);
  URL.revokeObjectURL(href);
};
