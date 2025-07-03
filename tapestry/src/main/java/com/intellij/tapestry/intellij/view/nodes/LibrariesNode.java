package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.module.Module;
import com.intellij.tapestry.core.TapestryProject;
import com.intellij.tapestry.core.model.TapestryLibrary;
import com.intellij.tapestry.intellij.TapestryModuleSupportLoader;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LibrariesNode extends TapestryNode<String> {
    public LibrariesNode(Module module, @NotNull TapestryProjectViewPane pane) {
        super(module, "Libraries", pane);
    }

    @Override
    @NotNull
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<TapestryNode<?>> children = new ArrayList<>();
        for (TapestryLibrary library : TapestryModuleSupportLoader.getTapestryProject(myModule).getLibraries()) {
            if (!library.getId().equals(TapestryProject.APPLICATION_LIBRARY_ID)) {
                children.add(new ExternalLibraryNode(library, myModule, myPane));
            }
        }
        return children;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText("Libraries");
        presentation.setIcon(AllIcons.Nodes.PpLib);
    }
}
