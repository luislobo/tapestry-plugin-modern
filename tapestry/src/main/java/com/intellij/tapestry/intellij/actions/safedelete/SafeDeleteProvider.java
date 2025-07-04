package com.intellij.tapestry.intellij.actions.safedelete;

import com.intellij.ide.DeleteProvider;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.safeDelete.SafeDeleteHandler;
import com.intellij.tapestry.core.model.presentation.PresentationLibraryElement;
import com.intellij.tapestry.core.resource.IResource;
import com.intellij.tapestry.intellij.core.resource.IntellijResource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SafeDeleteProvider implements DeleteProvider {

    @Override
    public void deleteElement(@NotNull DataContext dataContext) {
        Project project = PlatformCoreDataKeys.PROJECT.getData(dataContext);
        if (project == null) return;

        final Collection<PsiElement> elements = getElementsToDelete(dataContext);
        if (!elements.isEmpty()) {
            SafeDeleteHandler.invoke(project, elements.toArray(PsiElement.EMPTY_ARRAY), true);
        }
    }

    @Override
    public boolean canDeleteElement(@NotNull DataContext dataContext) {
        return !getElementsToDelete(dataContext).isEmpty();
    }

    @NotNull
    private static Collection<PsiElement> getElementsToDelete(DataContext dataContext) {
        final Object[] items = PlatformCoreDataKeys.SELECTED_ITEMS.getData(dataContext);
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }

        List<PsiElement> result = new ArrayList<>();
        for (Object item : items) {
            // The item from the DataContext is our AbstractTreeNode
            if (item instanceof AbstractTreeNode) {
                Object value = ((AbstractTreeNode<?>) item).getValue();

                if (value instanceof PresentationLibraryElement) {
                    addPresentationLibraryElements((PresentationLibraryElement) value, result);
                } else if (value instanceof PsiElement) {
                    result.add((PsiElement) value);
                }
            }
        }
        return result;
    }

    private static void addPresentationLibraryElements(PresentationLibraryElement ple, List<PsiElement> list) {
        // Add the main class file
        PsiFile elementClassFile = ((IntellijResource) ple.getElementClass().getFile()).getPsiFile();
        if (elementClassFile != null) {
            list.add(elementClassFile);
        }
        // Add all associated template files
        for (IResource template : ple.getTemplate()) {
            PsiFile templateFile = ((IntellijResource) template).getPsiFile();
            if (templateFile != null) {
                list.add(templateFile);
            }
        }
        // Add all associated message catalog files
        for (IResource catalog : ple.getMessageCatalog()) {
            PsiFile catalogFile = ((IntellijResource) catalog).getPsiFile();
            if (catalogFile != null) {
                list.add(catalogFile);
            }
        }
    }
}
