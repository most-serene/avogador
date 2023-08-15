import CourseItem from "../../components/courses/CourseItem.tsx";
import { withRouter } from "storybook-addon-react-router-v6";

export default {
  component: CourseItem,
  decorators: [withRouter],
  title: "CourseItem",
  tags: ["autodocs"],
};

export const Course = {
  args: {
    course: { id: 1, name: "Po1", year: "2023/2024", isArchived: false },
  },
};
