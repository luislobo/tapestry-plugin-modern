package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.tapestry.intellij.util.TapestryUtils;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RootNode extends TapestryNode<Project> {

    public RootNode(@NotNull Project project, @NotNull TapestryProjectViewPane pane) {
        super(project, project, pane);
    }

    @Override
    @NotNull
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        final List<ModuleNode> newNodes = new ArrayList<>();
        final Module[] allTapestryModules = TapestryUtils.getAllTapestryModules(myProject);

        for (final Module module : allTapestryModules) {
            newNodes.add(new ModuleNode(module, myPane));
        }
        return newNodes;
    }

    @Override
    protected void update(@NotNull com.intellij.ide.projectView.PresentationData presentation) {
        // Root node is not visible, so no presentation is needed.
    }
}
