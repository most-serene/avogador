package eu.mostserene.avogador.exerciseservice.storage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class TreeNode<T> implements Iterable<TreeNode<T>> {

    private final List<TreeNode<T>> elementsIndex;
    public T data;
    public TreeNode<T> parent;
    public List<TreeNode<T>> children;

    public TreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<>();
        this.elementsIndex = new LinkedList<>();
        this.elementsIndex.add(this);
    }

    public static TreeNode<File> createDirTree(File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        TreeNode<File> DirRoot = new TreeNode<>(folder);

        Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .forEach(file -> {
                    if (file.isDirectory()) {
                        appendDirTree(file, DirRoot);
                    } else {
                        appendFile(file, DirRoot);
                    }
                });

        return DirRoot;
    }

    public static void appendDirTree(File folder, TreeNode<File> DirRoot) {
        DirRoot.addChild(folder);
        Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .forEach(file -> {
                    if (file.isDirectory()) {
                        appendDirTree(file,
                                DirRoot.children.get(DirRoot.children.size() - 1));
                    } else {
                        appendFile(file,
                                DirRoot.children.get(DirRoot.children.size() - 1));
                    }
                });
    }

    public static void appendFile(File file, TreeNode<File> filenode) {
        filenode.addChild(file);
    }

    public static String renderDirectoryTree(TreeNode<File> tree) {
        List<StringBuilder> lines = renderDirectoryTreeLines(tree);
        String newline = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder(lines.size() * 20);
        lines.forEach(line -> {
            sb.append(line);
            sb.append(newline);
        });

        return sb.toString();
    }

    public static List<StringBuilder> renderDirectoryTreeLines(TreeNode<File> tree) {
        List<StringBuilder> result = new LinkedList<>();
        result.add(new StringBuilder().append(tree.data.getName()));
        Iterator<TreeNode<File>> iterator = tree.children.iterator();
        while (iterator.hasNext()) {
            List<StringBuilder> subtree =
                    renderDirectoryTreeLines(iterator.next());
            if (iterator.hasNext()) {
                addSubtree(result, subtree);
            } else {
                addLastSubtree(result, subtree);
            }
        }
        return result;
    }

    private static void addSubtree(List<StringBuilder> result,
                                   List<StringBuilder> subtree) {
        Iterator<StringBuilder> iterator = subtree.iterator();
        result.add(iterator.next().insert(0, "├── "));
        while (iterator.hasNext()) {
            result.add(iterator.next().insert(0, "│   "));
        }
    }

    private static void addLastSubtree(List<StringBuilder> result,
                                       List<StringBuilder> subtree) {
        Iterator<StringBuilder> iterator = subtree.iterator();
        result.add(iterator.next().insert(0, "└── "));
        while (iterator.hasNext()) {
            result.add(iterator.next().insert(0, "    "));
        }
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public TreeNode<T> addChild(T child) {
        TreeNode<T> childNode = new TreeNode<>(child);
        childNode.parent = this;
        this.children.add(childNode);
        this.registerChildForSearch(childNode);
        return childNode;
    }

    public int getLevel() {
        if (this.isRoot())
            return 0;
        else
            return parent.getLevel() + 1;
    }

    private void registerChildForSearch(TreeNode<T> node) {
        elementsIndex.add(node);
        if (parent != null)
            parent.registerChildForSearch(node);
    }

    public TreeNode<T> findTreeNode(Comparable<T> cmp) {
        for (TreeNode<T> element : this.elementsIndex) {
            T elData = element.data;
            if (cmp.compareTo(elData) == 0)
                return element;
        }

        return null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "[data null]";
    }

    @NotNull
    @Override
    public Iterator<TreeNode<T>> iterator() {
        return new TreeNodeIter<>(this);
    }
}
