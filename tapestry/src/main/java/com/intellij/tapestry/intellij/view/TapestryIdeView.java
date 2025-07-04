package com.intellij.tapestry.intellij.view;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleFileIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TapestryIdeView implements IdeView {
    private final TapestryProjectViewPane _viewPane;

    protected TapestryIdeView(TapestryProjectViewPane viewPane) {
        _viewPane = viewPane;
    }

    @Override
    public PsiDirectory @NotNull [] getDirectories() {
        // Use the new public getter on the view pane
        final Module module = (Module) _viewPane.getData(PlatformCoreDataKeys.MODULE.getName());
        if (module == null) return PsiDirectory.EMPTY_ARRAY;

        final List<PsiDirectory> directories = new ArrayList<>();
        final ModuleFileIndex moduleFileIndex = ModuleRootManager.getInstance(module).getFileIndex();

        moduleFileIndex.iterateContent(
                virtualfile -> {
                    if (virtualfile.isDirectory() && moduleFileIndex.isInSourceContent(virtualfile)) {
                        PsiDirectory dir = PsiManager.getInstance(_viewPane.getProject()).findDirectory(virtualfile);
                        if (dir != null) {
                            directories.add(dir);
                        }
                    }
                    return true;
                }
        );
        return directories.toArray(PsiDirectory.EMPTY_ARRAY);
    }

    @Override
    @Nullable
    public PsiDirectory getOrChooseDirectory() {
        // Use the new helper method to get the underlying value of the selected node
        Object element = _viewPane.getSelectedValue();

        if (element instanceof PsiDirectory) {
            return (PsiDirectory) element;
        }

        if (element instanceof PsiFile) {
            return ((PsiFile) element).getContainingDirectory();
        }

        return null;
    }
}
