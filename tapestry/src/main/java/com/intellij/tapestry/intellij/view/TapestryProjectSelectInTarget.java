package com.intellij.tapestry.intellij.view;

import com.intellij.ide.SelectInContext;
import com.intellij.ide.impl.ProjectViewSelectInTarget;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileSystemItem;
import org.jetbrains.annotations.NotNull;

public class TapestryProjectSelectInTarget extends ProjectViewSelectInTarget {

    public TapestryProjectSelectInTarget(final Project project) {
        super(project);
    }

    @Override
    public String toString() {
        return "Tapestry Project View";
    }

    @Override
    protected boolean canSelect(PsiFileSystemItem psiFileSystemItem) {
        if (!super.canSelect(psiFileSystemItem)) return false;
        // This call will now succeed because we added canSelect() back to the pane.
        return TapestryProjectViewPane.getInstance(myProject).canSelect();
    }

    @Override
    public String getMinorViewId() {
        return TapestryProjectViewPane.ID;
    }

    @Override
    public float getWeight() {
        return 5;
    }

    @Override
    public boolean isSubIdSelectable(String subId, SelectInContext context) {
        return true;
    }
}
