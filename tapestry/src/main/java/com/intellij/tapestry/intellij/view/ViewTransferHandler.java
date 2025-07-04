package com.intellij.tapestry.intellij.view;

import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project; // Make sure this import exists
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.tapestry.core.java.IJavaClassType;
import com.intellij.tapestry.core.model.externalizable.ExternalizableToClass;
import com.intellij.tapestry.core.model.externalizable.ExternalizableToTemplate;
import com.intellij.tapestry.intellij.core.java.IntellijJavaClassType;
import com.intellij.tapestry.intellij.util.IdeaUtils;
import com.intellij.tapestry.intellij.util.TapestryUtils;
import com.intellij.tapestry.intellij.view.nodes.TapestryNode;
import com.intellij.tapestry.lang.TmlFileType;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class ViewTransferHandler extends TransferHandler {

    private static final long serialVersionUID = -6485912040308583746L;
    private static final Logger _logger = Logger.getInstance(ViewTransferHandler.class);

    private final transient TapestryProjectViewPane _tapestryProjectViewPane;

    public ViewTransferHandler(TapestryProjectViewPane tapestryProjectViewPane) {
        _tapestryProjectViewPane = tapestryProjectViewPane;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        Object userObject = ((DefaultMutableTreeNode) _tapestryProjectViewPane.getTree().getSelectionPath().getLastPathComponent()).getUserObject();
        if (userObject instanceof TapestryNode) {
            return new TapestryElementTransferable(((TapestryNode<?>) userObject).getValue());
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    private class TapestryElementTransferable implements Transferable {
        private final Object _data;

        TapestryElementTransferable(Object object) {
            _data = object;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }

            Project project = _tapestryProjectViewPane.getProject(); // Use the public getter
            PsiFile fileInEditor = PsiManager.getInstance(project)
                    .findFile(FileDocumentManager.getInstance().getFile(FileEditorManager.getInstance(project).getSelectedTextEditor().getDocument()));
            if(fileInEditor == null) throw new UnsupportedFlavorException(flavor);

            FileType typeFileInEditor = fileInEditor.getFileType();

            if (fileInEditor instanceof PsiClassOwner && _data instanceof ExternalizableToClass) {
                IJavaClassType dropClass = new IntellijJavaClassType((Module) _tapestryProjectViewPane.getData(PlatformCoreDataKeys.MODULE.getName()),
                        IdeaUtils.findPublicClass(fileInEditor).getContainingFile());
                try {
                    return ((ExternalizableToClass) _data).getClassRepresentation(dropClass);
                } catch (Exception ex) {
                    _logger.error(ex);
                    throw new UnsupportedFlavorException(flavor);
                }
            }

            if (typeFileInEditor.equals(TmlFileType.INSTANCE) && _data instanceof ExternalizableToTemplate) {
                try {
                    return ((ExternalizableToTemplate) _data).getTemplateRepresentation(TapestryUtils.getTapestryNamespacePrefix((XmlFile) fileInEditor));
                } catch (Exception ex) {
                    _logger.error(ex);
                    throw new UnsupportedFlavorException(flavor);
                }
            }
            throw new UnsupportedFlavorException(flavor);
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.stringFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.stringFlavor.equals(flavor);
        }
    }
}
