package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClassOwner;
import com.intellij.tapestry.core.model.presentation.PresentationLibraryElement;
import com.intellij.tapestry.core.model.presentation.TapestryComponent;
import com.intellij.tapestry.core.resource.IResource;
import com.intellij.tapestry.intellij.core.java.IntellijJavaClassType;
import com.intellij.tapestry.intellij.core.resource.IntellijResource;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import icons.TapestryIcons;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComponentNode extends TapestryNode<PresentationLibraryElement> {
    public ComponentNode(PresentationLibraryElement component, Module module, @NotNull TapestryProjectViewPane pane) {
        super(module, component, pane);
    }

    @Override
    @NotNull
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        if (!myPane.isGroupElementFiles()) return super.getChildren();
        TapestryComponent component = (TapestryComponent) getValue();
        List<TapestryNode<?>> children = new ArrayList<>();
        children.add(new ClassNode((PsiClassOwner) ((IntellijJavaClassType) component.getElementClass()).getPsiClass().getContainingFile(), getModule(), myPane));
        for (IResource template : component.getTemplate())
            children.add(new FileNode(((IntellijResource) template).getPsiFile(), getModule(), myPane));
        for (IResource catalog : component.getMessageCatalog())
            children.add(new FileNode(((IntellijResource) catalog).getPsiFile(), getModule(), myPane));
        return children;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getValue().getElementClass().getName());
        presentation.setIcon(TapestryIcons.Component);
    }
}
