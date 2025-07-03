package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiDirectory;
import com.intellij.tapestry.core.model.TapestryLibrary;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import icons.TapestryIcons;
import org.jetbrains.annotations.NotNull;

public class FolderNode extends TapestryNode<PsiDirectory> {

    private final Class<?> myClassToCreate;
    private final TapestryLibrary myLibrary;

    public FolderNode(PsiDirectory directory, TapestryLibrary library, Class<?> classToCreate, Module module, @NotNull TapestryProjectViewPane pane) {
        super(module, directory, pane);
        this.myClassToCreate = classToCreate;
        this.myLibrary = library;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getValue().getName());
        presentation.setIcon(TapestryIcons.Folder);
    }

    public TapestryLibrary getLibrary() {
        return myLibrary;
    }

    public Class<?> getClassToCreate() {
        return myClassToCreate;
    }
}
