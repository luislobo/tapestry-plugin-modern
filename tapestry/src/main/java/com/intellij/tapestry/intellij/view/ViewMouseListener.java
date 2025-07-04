package com.intellij.tapestry.intellij.view;

import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project; // Make sure this import exists
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.tapestry.core.exceptions.NotTapestryElementException;
import com.intellij.tapestry.core.model.presentation.PresentationLibraryElement;
import com.intellij.tapestry.intellij.TapestryModuleSupportLoader;
import com.intellij.tapestry.intellij.core.java.IntellijJavaClassType;
import com.intellij.tapestry.intellij.util.IdeaUtils;
import com.intellij.tapestry.intellij.util.TapestryUtils;
import com.intellij.tapestry.intellij.view.nodes.ComponentNode;
import com.intellij.tapestry.intellij.view.nodes.MixinNode;
import com.intellij.tapestry.intellij.view.nodes.PageNode;
import com.intellij.tapestry.lang.TmlFileType;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseEvent;

class ViewMouseListener extends MouseInputAdapter {

    private MouseEvent _firstMouseEvent = null;
    private final TapestryProjectViewPane _tapestryProjectViewPane;

    ViewMouseListener(TapestryProjectViewPane tapestryProjectViewPane) {
        _tapestryProjectViewPane = tapestryProjectViewPane;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (_tapestryProjectViewPane.getTree() != null && _tapestryProjectViewPane.getTree().getSelectionPath() != null && _tapestryProjectViewPane.getTree().getSelectionPaths().length < 2) {
            Object selectedNode = ((DefaultMutableTreeNode) _tapestryProjectViewPane.getTree().getSelectionPath().getLastPathComponent()).getUserObject();
            Project project = _tapestryProjectViewPane.getProject(); // Use the public getter
            Module module = (Module) _tapestryProjectViewPane.getData(PlatformCoreDataKeys.MODULE.getName());

            if (!(selectedNode instanceof PageNode) && !(selectedNode instanceof ComponentNode) && !(selectedNode instanceof MixinNode)) {
                return;
            }

            if (FileEditorManager.getInstance(project).getSelectedFiles().length == 0) {
                return;
            }

            PsiFile fileInEditor = PsiManager.getInstance(project).findFile(
                    FileDocumentManager.getInstance().getFile(FileEditorManager.getInstance(project).getSelectedTextEditor().getDocument())
            );
            if (fileInEditor == null) return;

            FileType typeFileInEditor = fileInEditor.getFileType();

            if (!(fileInEditor instanceof PsiClassOwner) && !typeFileInEditor.equals(TmlFileType.INSTANCE)) {
                return;
            }

            Module moduleForFile = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(fileInEditor.getVirtualFile());
            if (!fileInEditor.isWritable() || moduleForFile == null || !moduleForFile.equals(module)) {
                return;
            }

            if (typeFileInEditor.equals(TmlFileType.INSTANCE)) {
                if (TapestryUtils.getTapestryNamespacePrefix((XmlFile) fileInEditor) == null) {
                    return;
                }
                if (selectedNode instanceof MixinNode) {
                    return;
                }
            }

            if (fileInEditor instanceof PsiClassOwner) {
                PsiClass psiClass = IdeaUtils.findPublicClass(fileInEditor);
                if (psiClass == null) return;

                IntellijJavaClassType elementClass = new IntellijJavaClassType(module, psiClass.getContainingFile());
                PresentationLibraryElement presentationLibraryElement;
                try {
                    presentationLibraryElement = PresentationLibraryElement.createProjectElementInstance(elementClass, TapestryModuleSupportLoader.getTapestryProject(module));
                } catch (NotTapestryElementException e) {
                    return;
                }

                if (presentationLibraryElement.getElementType().equals(PresentationLibraryElement.ElementType.PAGE)) {
                    if (!(selectedNode instanceof PageNode) && !(selectedNode instanceof ComponentNode)) {
                        return;
                    }
                }

                if (presentationLibraryElement.getElementType().equals(PresentationLibraryElement.ElementType.COMPONENT)) {
                    if (!(selectedNode instanceof PageNode) && !(selectedNode instanceof ComponentNode) && !(selectedNode instanceof MixinNode)) {
                        return;
                    }
                }

                if (presentationLibraryElement.getElementType().equals(PresentationLibraryElement.ElementType.MIXIN)) {
                    if (!(selectedNode instanceof PageNode)) {
                        return;
                    }
                }
            }

            _firstMouseEvent = event;
            event.consume();
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (_firstMouseEvent != null) {
            event.consume();
            int dx = Math.abs(event.getX() - _firstMouseEvent.getX());
            int dy = Math.abs(event.getY() - _firstMouseEvent.getY());
            if (dx > 5 || dy > 5) {
                JComponent component = (JComponent) event.getSource();
                TransferHandler handler = component.getTransferHandler();
                handler.exportAsDrag(component, _firstMouseEvent, TransferHandler.COPY);
                _firstMouseEvent = null;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        _firstMouseEvent = null;
    }
}
