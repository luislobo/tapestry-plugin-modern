package com.intellij.tapestry.intellij.view;

import com.intellij.ide.util.treeView.AbstractTreeStructure;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.tapestry.intellij.view.nodes.RootNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines the basic tree structure. This is the modern replacement for SimpleTreeStructure.
 * Its job is to define the shape and rules of the tree.
 */
public class TapestryProjectTreeStructure extends AbstractTreeStructure {

    private final Project myProject;
    private final TapestryProjectViewPane myPane;
    private RootNode myRootNode;

    public TapestryProjectTreeStructure(TapestryProjectViewPane pane, Project project) {
        this.myPane = pane;
        this.myProject = project;
    }

    @NotNull
    @Override
    public Object getRootElement() {
        if (myRootNode == null) {
            myRootNode = new RootNode(myProject, myPane);
        }
        return myRootNode;
    }

    @NotNull
    @Override
    public Object[] getChildElements(@NotNull Object element) {
        if (element instanceof AbstractTreeNode) {
            return ((AbstractTreeNode<?>) element).getChildren().toArray();
        }
        // Use the standard way to return an empty object array.
        return new Object[0];
    }

    @Nullable
    @Override
    public Object getParentElement(@NotNull Object element) {
        if (element instanceof AbstractTreeNode) {
            return ((AbstractTreeNode<?>) element).getParent();
        }
        return null;
    }

    @NotNull
    @Override
    public NodeDescriptor createDescriptor(@NotNull Object element, @Nullable NodeDescriptor parentDescriptor) {
        if (element instanceof NodeDescriptor) {
            return (NodeDescriptor) element;
        }
        throw new IllegalArgumentException("Element is not a NodeDescriptor: " + element);
    }

    @Override
    public void commit() {
        // Not needed for a read-only view.
    }

    @Override
    public boolean hasSomethingToCommit() {
        return false;
    }
}
