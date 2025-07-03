package com.intellij.tapestry.intellij.view.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleFileIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.tapestry.intellij.TapestryModuleSupportLoader;
import com.intellij.tapestry.intellij.util.IdeaUtils;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.TreeSet;

public class ModuleNode extends TapestryNode<Module> {

    public ModuleNode(@NotNull Module module, @NotNull TapestryProjectViewPane pane) {
        super(module, module, pane);
    }

    @Override
    @NotNull
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        final TreeSet<TapestryNode<?>> children = new TreeSet<>(PackageNodesComparator.getInstance());
        final ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(getValue());
        final ModuleFileIndex moduleFileIndex = moduleRootManager.getFileIndex();

        moduleFileIndex.iterateContent(virtualFile -> {
            if (virtualFile.isDirectory() && moduleFileIndex.isInSourceContent(virtualFile)) {
                PsiDirectory psiDirectory = PsiManager.getInstance(myProject).findDirectory(virtualFile);
                if (psiDirectory == null) return true;

                PsiPackage aPackage = IdeaUtils.getPackage(psiDirectory);
                if (aPackage == null) return true;

                if (myPane.isFromBasePackage()) {
                    if (aPackage.getQualifiedName().equals(TapestryModuleSupportLoader.getTapestryProject(getValue()).getApplicationLibrary().getBasePackage())) {
                        children.add(new LibraryNode(TapestryModuleSupportLoader.getTapestryProject(getValue()).getApplicationLibrary(), psiDirectory, getValue(), myPane));
                    }
                } else if (aPackage.getParentPackage() == null || aPackage.getParentPackage().getName() == null) {
                    children.add(new PackageNode(psiDirectory, getValue(), myPane));
                }
            }
            return true;
        });

        if (myPane.isShowLibraries()) {
            children.add(new LibrariesNode(getValue(), myPane));
        }

        return children;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getValue().getName());
        presentation.setIcon(PlatformIcons.WEB_ICON);
    }
}
