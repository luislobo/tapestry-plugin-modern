package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClassOwner;
import com.intellij.tapestry.core.model.presentation.Page;
import com.intellij.tapestry.core.model.presentation.PresentationLibraryElement;
import com.intellij.tapestry.core.resource.IResource;
import com.intellij.tapestry.intellij.core.java.IntellijJavaClassType;
import com.intellij.tapestry.intellij.core.resource.IntellijResource;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import icons.TapestryIcons;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PageNode extends TapestryNode<PresentationLibraryElement> {
    public PageNode(PresentationLibraryElement page, Module module, @NotNull TapestryProjectViewPane pane) {
        super(module, page, pane);
    }

    @Override
    @NotNull
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        if (!myPane.isGroupElementFiles()) return super.getChildren();
        Page page = (Page) getValue();
        List<TapestryNode<?>> children = new ArrayList<>();
        children.add(new ClassNode((PsiClassOwner) ((IntellijJavaClassType) page.getElementClass()).getPsiClass().getContainingFile(), getModule(), myPane));
        for (IResource template : page.getTemplate())
            children.add(new FileNode(((IntellijResource) template).getPsiFile(), getModule(), myPane));
        for (IResource catalog : page.getMessageCatalog())
            children.add(new FileNode(((IntellijResource) catalog).getPsiFile(), getModule(), myPane));
        return children;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getValue().getElementClass().getName());
        presentation.setIcon(TapestryIcons.Page);
    }
}
