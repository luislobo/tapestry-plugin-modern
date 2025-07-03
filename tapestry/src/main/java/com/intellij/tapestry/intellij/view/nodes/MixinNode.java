package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClassOwner;
import com.intellij.tapestry.core.model.presentation.Mixin;
import com.intellij.tapestry.core.model.presentation.PresentationLibraryElement;
import com.intellij.tapestry.intellij.core.java.IntellijJavaClassType;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import icons.TapestryIcons;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Collections;

public class MixinNode extends TapestryNode<PresentationLibraryElement> {
    public MixinNode(PresentationLibraryElement mixin, Module module, @NotNull TapestryProjectViewPane pane) {
        super(module, mixin, pane);
    }

    @Override
    @NotNull
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        if (!myPane.isGroupElementFiles()) return super.getChildren();
        Mixin mixin = (Mixin) getValue();
        return Collections.singletonList(new ClassNode((PsiClassOwner) ((IntellijJavaClassType) mixin.getElementClass()).getPsiClass().getContainingFile(), getModule(), myPane));
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getValue().getName());
        presentation.setIcon(TapestryIcons.Mixin);
    }
}
