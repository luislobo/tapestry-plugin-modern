package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClassOwner;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;

public class ClassNode extends TapestryNode<PsiClassOwner> {
    public ClassNode(PsiClassOwner psiClassOwner, Module module, @NotNull TapestryProjectViewPane pane) {
        super(module, psiClassOwner, pane);
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getValue().getName());
        presentation.setIcon(PlatformIcons.CLASS_ICON);
    }
}
