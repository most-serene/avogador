import { MDXEditor } from "@mdxeditor/editor/MDXEditor";
import {
  BoldItalicUnderlineToggles,
  CodeToggle,
  headingsPlugin,
  InsertTable,
  InsertThematicBreak,
  listsPlugin,
  ListsToggle,
  markdownShortcutPlugin,
  quotePlugin,
  Separator,
  tablePlugin,
  thematicBreakPlugin,
  UndoRedo,
} from "@mdxeditor/editor";
import "@mdxeditor/editor/style.css";
import { toolbarPlugin } from "@mdxeditor/editor/plugins/toolbar";
import Box from "@mui/material/Box";
import { useTheme } from "@mui/material";

interface MarkdownEditorProps {
  value: string;
  onChange: (newVal: string) => void;
}

const MarkdownEditor = ({
  value,
  onChange: handleChange,
}: MarkdownEditorProps) => {
  const theme = useTheme();

  return (
    <Box
      sx={{
        border: 1,
        borderColor: "rgba(122, 122, 122, 0.5)",
        borderRadius: 0.5,
      }}
    >
      <MDXEditor
        className={
          theme.palette.mode === "light" ? "light-theme" : "dark-theme"
        }
        markdown={value}
        onChange={handleChange}
        plugins={[
          headingsPlugin(),
          listsPlugin(),
          quotePlugin(),
          thematicBreakPlugin(),
          markdownShortcutPlugin(),
          tablePlugin(),
          toolbarPlugin({
            toolbarContents: () => (
              <Box
                sx={{
                  display: "flex",
                  width: "100%",
                  borderBottom: 1,
                  borderColor: "rgba(122, 122, 122, 0.5)",
                  borderRadius: 0.5,
                }}
              >
                <UndoRedo />
                <Separator />
                <BoldItalicUnderlineToggles />
                <CodeToggle />
                <Separator />
                <ListsToggle />
                <Separator />
                <InsertTable />
                <InsertThematicBreak />
              </Box>
            ),
          }),
        ]}
      />
    </Box>
  );
};

export default MarkdownEditor;
