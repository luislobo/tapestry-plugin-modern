package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiDirectory;
import com.intellij.tapestry.core.model.TapestryLibrary;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import icons.TapestryIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MixinsNode extends PackageNode {
    public MixinsNode(@Nullable TapestryLibrary library, PsiDirectory psiDirectory, Module module, @NotNull TapestryProjectViewPane pane) {
        super(library, psiDirectory, module, pane);
    }

    public MixinsNode(PsiDirectory psiDirectory, Module module, @NotNull TapestryProjectViewPane pane) {
        super(null, psiDirectory, module, pane);
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getValue().getName());
        presentation.setIcon(TapestryIcons.Mixins);
    }
}
