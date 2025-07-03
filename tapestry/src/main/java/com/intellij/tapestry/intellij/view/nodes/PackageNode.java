package com.intellij.tapestry.intellij.view.nodes;

// ... other necessary imports
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.lang.properties.PropertiesFileType;
import com.intellij.openapi.module.Module;
import com.intellij.psi.*;
import com.intellij.tapestry.core.TapestryProject;
import com.intellij.tapestry.core.exceptions.NotTapestryElementException;
import com.intellij.tapestry.core.java.IJavaClassType;
import com.intellij.tapestry.core.model.TapestryLibrary;
import com.intellij.tapestry.core.model.presentation.PresentationLibraryElement;
import com.intellij.tapestry.intellij.TapestryModuleSupportLoader;
import com.intellij.tapestry.intellij.core.java.IntellijJavaClassType;
import com.intellij.tapestry.intellij.util.IdeaUtils;
import com.intellij.tapestry.intellij.view.TapestryProjectViewPane;
import com.intellij.tapestry.lang.TmlFileType;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.TreeSet;

public class PackageNode extends TapestryNode<PsiDirectory> {

    protected final TapestryLibrary myLibrary;

    public PackageNode(PsiDirectory psiDirectory, Module module, @NotNull TapestryProjectViewPane pane) {
        this(null, psiDirectory, module, pane);
    }

    public PackageNode(@Nullable TapestryLibrary library, PsiDirectory psiDirectory, Module module, @NotNull TapestryProjectViewPane pane) {
        super(module, psiDirectory, pane);
        this.myLibrary = library;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getValue().getName());
        presentation.setIcon(PlatformIcons.PACKAGE_ICON);
    }

    @Override
    @NotNull
    public Collection<? extends AbstractTreeNode<?>> getChildren() {
        final TreeSet<TapestryNode<?>> children = new TreeSet<>(PackageNodesComparator.getInstance());
        PsiDirectory directory = getValue();

        for (PsiDirectory psiDirectory : directory.getSubdirectories()) {
            PackageNode node = createNewNode(psiDirectory);
            if (node != null) {
                children.add(node);
            }
        }

        for (PsiFile psiFile : directory.getFiles()) {
            if (psiFile instanceof PsiClassOwner) {
                try {
                    PsiClass psiClass = IdeaUtils.findPublicClass(psiFile);
                    if (psiClass == null || !myPane.isGroupElementFiles()) {
                        throw new NotTapestryElementException("");
                    }
                    IJavaClassType javaClassType = new IntellijJavaClassType(myModule, psiClass.getContainingFile());

                    PresentationLibraryElement element = (myLibrary == null) ?
                            PresentationLibraryElement.createProjectElementInstance(javaClassType, TapestryModuleSupportLoader.getTapestryProject(myModule)) :
                            PresentationLibraryElement.createElementInstance(myLibrary, javaClassType, TapestryModuleSupportLoader.getTapestryProject(myModule));

                    switch (element.getElementType()) {
                        case PAGE:      children.add(new PageNode(element, myModule, myPane)); break;
                        case COMPONENT: children.add(new ComponentNode(element, myModule, myPane)); break;
                        case MIXIN:     children.add(new MixinNode(element, myModule, myPane)); break;
                    }
                } catch (NotTapestryElementException e) {
                    children.add(new ClassNode((PsiClassOwner) psiFile, myModule, myPane));
                }
            }

            if ((psiFile.getFileType().equals(TmlFileType.INSTANCE) || psiFile.getFileType().equals(PropertiesFileType.INSTANCE)) && !myPane.isGroupElementFiles()) {
                children.add(new FileNode(psiFile, myModule, myPane));
            }
        }
        return children;
    }

    @Nullable
    private PackageNode createNewNode(PsiDirectory psiDirectory) {
        final PsiPackage aPackage = IdeaUtils.getPackage(psiDirectory);
        if (aPackage == null) return null;
        TapestryProject tapestryProject = TapestryModuleSupportLoader.getTapestryProject(myModule);
        if (tapestryProject == null) return null;
        String applicationRootPackage = tapestryProject.getApplicationRootPackage();
        String packageName = aPackage.getQualifiedName();
        if (packageName.equals(applicationRootPackage)) {
            return new LibraryNode(tapestryProject.getApplicationLibrary(), psiDirectory, myModule, myPane);
        }
        if (packageName.equals(tapestryProject.getPagesRootPackage())) {
            return new PagesNode(psiDirectory, myModule, myPane);
        }
        if (packageName.equals(tapestryProject.getComponentsRootPackage())) {
            return new ComponentsNode(psiDirectory, myModule, myPane);
        }
        if (packageName.equals(tapestryProject.getMixinsRootPackage())) {
            return new MixinsNode(psiDirectory, myModule, myPane);
        }
        return new PackageNode(psiDirectory, myModule, myPane);
    }
}
