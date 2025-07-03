package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiDirectory;
import com.intellij.tapestry.core.model.TapestryLibrary;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import org.jetbrains.annotations.NotNull;

public class LibraryNode extends PackageNode {
    public LibraryNode(TapestryLibrary library, PsiDirectory psiDirectory, Module module, @NotNull TapestryProjectViewPane pane) {
        super(library, psiDirectory, module, pane);
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getValue().getName());
        presentation.setIcon(AllIcons.Nodes.PpLib);
    }
}
