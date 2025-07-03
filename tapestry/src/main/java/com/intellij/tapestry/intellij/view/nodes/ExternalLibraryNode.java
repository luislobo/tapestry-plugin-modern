package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.module.Module;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.tapestry.core.TapestryConstants;
import com.intellij.tapestry.core.model.TapestryLibrary;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExternalLibraryNode extends TapestryNode<TapestryLibrary> {
    public ExternalLibraryNode(TapestryLibrary library, Module module, @NotNull TapestryProjectViewPane pane) {
        super(module, library, pane);
    }

    @Override
    @NotNull
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        List<TapestryNode<?>> children = new ArrayList<>();
        TapestryLibrary library = getValue();

        if (!library.getPages().isEmpty()) {
            children.add(new PagesNode(library, JavaPsiFacade.getInstance(myProject).findPackage(library.getBasePackage() + "." + TapestryConstants.PAGES_PACKAGE).getDirectories(GlobalSearchScope.moduleWithLibrariesScope(myModule))[0], myModule, myPane));
        }
        if (!library.getComponents().isEmpty()) {
            children.add(new ComponentsNode(library, JavaPsiFacade.getInstance(myProject).findPackage(library.getBasePackage() + "." + TapestryConstants.COMPONENTS_PACKAGE).getDirectories(GlobalSearchScope.moduleWithLibrariesScope(myModule))[0], myModule, myPane));
        }
        if (!library.getMixins().isEmpty()) {
            children.add(new MixinsNode(library, JavaPsiFacade.getInstance(myProject).findPackage(library.getBasePackage() + "." + TapestryConstants.MIXINS_PACKAGE).getDirectories(GlobalSearchScope.moduleWithLibrariesScope(myModule))[0], myModule, myPane));
        }
        return children;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getValue().getId());
        presentation.setIcon(AllIcons.Nodes.PpLib);
    }
}
