package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.PresentableNodeDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * Modern base class for all Tapestry related nodes, extending AbstractTreeNode.
 */
public abstract class TapestryNode<T> extends AbstractTreeNode<T> {

    protected final TapestryProjectViewPane myPane;
    protected Module myModule;

    protected TapestryNode(Module module, T value, @NotNull TapestryProjectViewPane pane) {
        super(module.getProject(), value);
        this.myModule = module;
        this.myPane = pane;
    }

    // Constructor for nodes that don't need a specific value (like container nodes).
    protected TapestryNode(@NotNull Project project, T value, @NotNull TapestryProjectViewPane pane) {
        super(project, value);
        this.myPane = pane;
    }

    @Override
    @NotNull
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        return Collections.emptyList();
    }

    @Override
    protected void update(@NotNull com.intellij.ide.projectView.PresentationData presentation) {
        // Subclasses will implement this to set their icon and text.
    }

    public Module getModule() {
        return myModule;
    }

    // This method is useful for sorting and comparison.
    public String getPresentableText() {
        return getName();
    }
}
