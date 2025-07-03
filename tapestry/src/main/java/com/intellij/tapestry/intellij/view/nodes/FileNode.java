package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiFile;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import org.jetbrains.annotations.NotNull;

public class FileNode extends TapestryNode<PsiFile> {
    public FileNode(PsiFile file, Module module, @NotNull TapestryProjectViewPane pane) {
        super(module, file, pane);
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getValue().getName());
        presentation.setIcon(getValue().getFileType().getIcon());
    }
}
