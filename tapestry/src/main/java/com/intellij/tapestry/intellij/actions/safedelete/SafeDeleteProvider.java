package com.intellij.tapestry.intellij.actions.safedelete;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.safeDelete.SafeDeleteHandler;
import com.intellij.tapestry.core.TapestryProject;
import com.intellij.tapestry.core.model.presentation.PresentationLibraryElement;
import com.intellij.tapestry.core.resource.IResource;
import com.intellij.tapestry.intellij.core.resource.IntellijResource;
import com.intellij.tapestry.intellij.view.nodes.PackageNode;
import com.intellij.tapestry.intellij.view.nodes.TapestryNode;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SafeDeleteProvider implements com.intellij.ide.DeleteProvider {

    @Override
    public void deleteElement(@NotNull DataContext dataContext) {
        final Collection<PsiElement> elements = getElementsToDelete(dataContext);
        SafeDeleteHandler.invoke(getProject(dataContext), elements.toArray(PsiElement.EMPTY_ARRAY), true);
    }

    @Override
    public boolean canDeleteElement(@NotNull DataContext dataContext) {
        return !getElementsToDelete(dataContext).isEmpty();
    }

    private static Collection<PsiElement> getElementsToDelete(DataContext dataContext) {
        final List<TreeNode> nodes = TreeUtil.getSelectedNodes((JTree) dataContext.getData("tree"), TreeNode.class, treeNode -> true);
        if (nodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<PsiElement> result = new ArrayList<>();
        for (TreeNode node : nodes) {
            if (!(node instanceof DefaultMutableTreeNode)) continue;
            Object userObject = ((DefaultMutableTreeNode) node).getUserObject();
            if (!(userObject instanceof TapestryNode)) continue;

            TapestryNode<?> tapestryNode = (TapestryNode<?>) userObject;
            Object element = tapestryNode.getValue();
            if (element instanceof PresentationLibraryElement) {
                PresentationLibraryElement ple = (PresentationLibraryElement) element;
                PsiElement elementClass = ((IntellijResource) ple.getElementClass().getFile()).getPsiFile();
                if (elementClass != null) result.add(elementClass);

                for (IResource template : ple.getTemplate()) {
                    PsiFile templateFile = ((IntellijResource) template).getPsiFile();
                    if (templateFile != null) result.add(templateFile);
                }
                for (IResource catalog : ple.getMessageCatalog()) {
                    PsiFile catalogFile = ((IntellijResource) catalog).getPsiFile();
                    if (catalogFile != null) result.add(catalogFile);
                }
            } else if (element instanceof PsiElement) {
                result.add((PsiElement) element);
            }
        }

        // Additional logic for deleting packages if necessary
        for (TreeNode node : nodes) {
            if (node.getParent() instanceof DefaultMutableTreeNode) {
                Object parentUserObject = ((DefaultMutableTreeNode) node.getParent()).getUserObject();
                if (parentUserObject instanceof PackageNode) {
                    int numberChildren = parentUserObject.getChildren().size(); // Use .size() for Collection
                    if (numberChildren == 1 && result.size() == 1) {
                        PsiElement parentElement = (PsiElement) ((PackageNode) parentUserObject).getValue();
                        if (parentElement != null) result.add(parentElement);
                    }
                }
            }
        }
        return result;
    }
}
