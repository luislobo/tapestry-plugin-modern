package com.intellij.tapestry.intellij.view;

import com.intellij.ProjectTopics;
import com.intellij.ide.PsiCopyPasteManager;
import com.intellij.ide.SelectInTarget;
import com.intellij.ide.dnd.aware.DnDAwareTree;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.ide.ui.customization.CustomizationUtil;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.tapestry.core.TapestryProject;
import com.intellij.tapestry.core.events.FileSystemListener;
import com.intellij.tapestry.core.events.TapestryModelChangeListener;
import com.intellij.tapestry.core.exceptions.NotTapestryElementException;
import com.intellij.tapestry.core.java.IJavaClassType;
import com.intellij.tapestry.core.model.presentation.PresentationLibraryElement;
import com.intellij.tapestry.core.resource.IResource;
import com.intellij.tapestry.intellij.TapestryModuleSupportLoader;
import com.intellij.tapestry.intellij.actions.safedelete.SafeDeleteProvider;
import com.intellij.tapestry.intellij.core.java.IntellijJavaClassType;
import com.intellij.tapestry.intellij.core.resource.IntellijResource;
import com.intellij.tapestry.intellij.toolwindow.TapestryToolWindow;
import com.intellij.tapestry.intellij.toolwindow.TapestryToolWindowFactory;
import com.intellij.tapestry.intellij.util.TapestryUtils;
import com.intellij.tapestry.intellij.view.actions.GroupElementFilesToggleAction;
import com.intellij.tapestry.intellij.view.actions.ShowLibrariesTogleAction;
import com.intellij.tapestry.intellij.view.actions.StartInBasePackageAction;
import com.intellij.tapestry.intellij.view.nodes.ClassNode;
import com.intellij.tapestry.intellij.view.nodes.FileNode;
import com.intellij.tapestry.intellij.view.nodes.TapestryNode;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.tree.AsyncTreeModel;
import com.intellij.ui.tree.StructureTreeModel;
import com.intellij.ui.treeStructure.actions.CollapseAllAction;
import com.intellij.util.EditSourceOnDoubleClickHandler;
import com.intellij.util.EditSourceOnEnterKeyHandler;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.tree.TreeUtil;
import icons.TapestryIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.Collections;

public class TapestryProjectViewPane extends AbstractProjectViewPane implements FileSystemListener, TapestryModelChangeListener {

  public static final String ID = "TapestryProjectView";
  private final TapestryIdeView myIdeView;
  private boolean myGroupElementFiles = true;
  private boolean myShowLibraries = true;
  private boolean myFromBasePackage;
  private final MessageBusConnection myMessageBusConnection;
  private AsyncTreeModel myAsyncTreeModel;
  private JScrollPane myComponent;

  public TapestryProjectViewPane(final Project project) {
    super(project);
    myIdeView = new TapestryIdeView(this);

    myMessageBusConnection = project.getMessageBus().connect(this);
    myMessageBusConnection.subscribe(ProjectTopics.MODULES, new ModuleListener() {
      @Override
      public void moduleAdded(@NotNull Project project, @NotNull Module module) { reload(); }
      @Override
      public void moduleRemoved(@NotNull Project project, @NotNull Module module) { reload(); }
    });

    for (Module module : ModuleManager.getInstance(myProject).getModules()) {
      TapestryModuleSupportLoader.getTapestryProject(module).getEventsManager().addFileSystemListener(this);
      TapestryModuleSupportLoader.getTapestryProject(module).getEventsManager().addTapestryModelListener(this);
    }
  }

  @Override
  public void addToolbarActions(@NotNull DefaultActionGroup actionGroup) {
    actionGroup.addAction(new StartInBasePackageAction() {
      @Override public boolean isSelected(@NotNull AnActionEvent e) { return myFromBasePackage; }
      @Override public void setSelected(@NotNull AnActionEvent e, boolean state) {
        myFromBasePackage = state;
        updateFromRoot(false);
      }
    }).setAsSecondary(true);
    actionGroup.addAction(new GroupElementFilesToggleAction() {
      @Override public boolean isSelected(@NotNull AnActionEvent e) { return myGroupElementFiles; }
      @Override public void setSelected(@NotNull AnActionEvent e, boolean state) {
        myGroupElementFiles = state;
        updateFromRoot(false);
      }
    }).setAsSecondary(true);
    actionGroup.addAction(new ShowLibrariesTogleAction() {
      @Override public boolean isSelected(@NotNull AnActionEvent e) { return myShowLibraries; }
      @Override public void setSelected(@NotNull AnActionEvent e, boolean state) {
        myShowLibraries = state;
        updateFromRoot(false);
      }
    }).setAsSecondary(true);
    if (myTree != null) {
      actionGroup.add(new CollapseAllAction(myTree));
    }
  }

  public static TapestryProjectViewPane getInstance(@NotNull final Project project) {
    return (TapestryProjectViewPane) ProjectView.getInstance(project).getProjectViewPaneById(ID);
  }

  public void reload() {
    modulesChanged();
    updateFromRoot(true);
  }

  @NotNull @Override public String getTitle() { return "Tapestry"; }
  @NotNull @Override public Icon getIcon() { return TapestryIcons.Tapestry_logo_small; }
  @NotNull @Override public String getId() { return ID; }

  @NotNull
  @Override
  public JComponent createComponent() {
    if (myComponent == null) {
      myTree = createTree();
      myComponent = ScrollPaneFactory.createScrollPane(myTree);
      myComponent.setBorder(BorderFactory.createEmptyBorder());
    }
    return myComponent;
  }

  private DnDAwareTree createTree() {
    myTreeStructure = new TapestryProjectTreeStructure(this, myProject);
    StructureTreeModel structureTreeModel = new StructureTreeModel(myTreeStructure, this);
    myAsyncTreeModel = new AsyncTreeModel(structureTreeModel, this);
    final DnDAwareTree tree = new DnDAwareTree(myAsyncTreeModel);

    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    TreeUtil.expandRootChildIfOnlyOne(tree);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    EditSourceOnDoubleClickHandler.install(tree);
    EditSourceOnEnterKeyHandler.install(tree);
    TreeUtil.installActions(tree);
    new TreeSpeedSearch(tree);
    tree.setTransferHandler(new ViewTransferHandler(this));
    MouseInputAdapter mouseListener = new ViewMouseListener(this);
    tree.addMouseListener(mouseListener);
    tree.addMouseMotionListener(mouseListener);
    addTreeListeners(tree);
    CustomizationUtil.installPopupHandler(tree, IdeActions.GROUP_PROJECT_VIEW_POPUP, "TapestryProjectViewPopup");

    return tree;
  }

  @NotNull @Override public ActionCallback updateFromRoot(boolean b) {
    if (myAsyncTreeModel != null) {
      myAsyncTreeModel.treeStructureChanged(null);
    }
    return ActionCallback.DONE;
  }

  @Override public void select(Object object, VirtualFile virtualFile, boolean b) { }
  @Override public int getWeight() { return 5; }
  @NotNull @Override public SelectInTarget createSelectInTarget() { return new TapestryProjectSelectInTarget(myProject); }
  @Override public void fileCreated(String path) { updateFromRoot(true); }
  @Override public void fileDeleted(String path) { updateFromRoot(true); }
  @Override public void classCreated(String classFqn) { updateFromRoot(true); }
  @Override public void classDeleted(String classFqn) { updateFromRoot(true); }
  @Override public void fileContentsChanged(IResource changedFile) { }
  @Override public void modelChanged() { reload(); }
  public boolean isGroupElementFiles() { return myGroupElementFiles; }
  public boolean isShowLibraries() { return myShowLibraries; }
  public boolean isFromBasePackage() { return myFromBasePackage; }

  @Override
  public Object getData(@NotNull String dataId) {
    final TreePath path = (myTree != null) ? myTree.getSelectionPath() : null;
    if (path == null) return super.getData(dataId);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
    if (node == null || !(node.getUserObject() instanceof AbstractTreeNode)) return super.getData(dataId);
    Object value = ((AbstractTreeNode<?>)node.getUserObject()).getValue();

    if (CommonDataKeys.NAVIGATABLE.is(dataId) && value instanceof PresentationLibraryElement) {
      return ((IntellijResource)((PresentationLibraryElement)value).getElementClass().getFile()).getPsiFile();
    }
    if (PlatformCoreDataKeys.MODULE.is(dataId)) {
      if (node.getUserObject() instanceof TapestryNode) return ((TapestryNode<?>)node.getUserObject()).getModule();
      if (value instanceof Module) return value;
    }
    if (PlatformDataKeys.DELETE_ELEMENT_PROVIDER.is(dataId)) {
      return new SafeDeleteProvider();
    }
    if (LangDataKeys.IDE_VIEW.is(dataId) && (value instanceof PsiDirectory || value instanceof PsiFile)) {
      return myIdeView;
    }
    return super.getData(dataId);
  }

  private void addTreeListeners(JTree tree) {
    tree.getSelectionModel().addTreeSelectionListener(e -> {
      if (e.getNewLeadSelectionPath() == null) return;

      // Use myProject field instead of getProject()
      TapestryToolWindow toolWindow = TapestryToolWindowFactory.getToolWindow(myProject);
      if (toolWindow == null) return;

      Object userObject = ((DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent()).getUserObject();
      if (!(userObject instanceof TapestryNode)) {
        toolWindow.update(null, null, null);
        return;
      }

      TapestryNode<?> selectedNode = (TapestryNode<?>) userObject;
      Object element = selectedNode.getValue();

      if (element instanceof PresentationLibraryElement) {
        PresentationLibraryElement ple = (PresentationLibraryElement) element;
        toolWindow.update(selectedNode.getModule(), ple, Collections.singletonList(ple.getElementClass()));
      } else if (selectedNode instanceof ClassNode || selectedNode instanceof FileNode) {
        handleClassOrFileNodeSelection(selectedNode);
      } else {
        toolWindow.update(null, null, null);
      }
    });
    tree.addKeyListener(new PsiCopyPasteManager.EscapeHandler());
  }

  private void handleClassOrFileNodeSelection(TapestryNode<?> selectedNode) {
    TapestryToolWindow toolWindow = TapestryToolWindowFactory.getToolWindow(myProject);
    if(toolWindow == null) return;

    AbstractTreeNode<?> parentNode = selectedNode.getParent();
    if (parentNode instanceof TapestryNode && ((TapestryNode<?>)parentNode).getValue() instanceof PresentationLibraryElement) {
      PresentationLibraryElement ple = (PresentationLibraryElement) ((TapestryNode<?>) parentNode).getValue();
      toolWindow.update(selectedNode.getModule(), ple, Collections.singletonList(ple.getElementClass()));
      return;
    }

    PresentationLibraryElement component = null;
    Module module = selectedNode.getModule();
    TapestryProject tapestryProject = TapestryModuleSupportLoader.getTapestryProject(module);

    if (selectedNode.getValue() instanceof PsiFile) {
      PsiFile psiFile = (PsiFile) selectedNode.getValue();
      IJavaClassType elementClass = null;
      if (psiFile instanceof PsiClassOwner) {
        elementClass = new IntellijJavaClassType(module, psiFile);
      } else {
        PresentationLibraryElement foundElement = tapestryProject.findElementByTemplate(psiFile);
        if (foundElement != null) {
          elementClass = foundElement.getElementClass();
        }
      }
      if (elementClass != null) {
        try {
          component = PresentationLibraryElement.createProjectElementInstance(elementClass, tapestryProject);
        } catch (NotTapestryElementException ignored) {}
      }
    }

    if (component != null) {
      toolWindow.update(module, component, Collections.singletonList(component.getElementClass()));
    } else {
      toolWindow.update(null, null, null);
    }
  }

  private void modulesChanged() {
    boolean shouldShow = false;
    for (Module module : ModuleManager.getInstance(myProject).getModules()) {
      if (TapestryUtils.isTapestryModule(module)) {
        TapestryModuleSupportLoader.getTapestryProject(module).getEventsManager().removeFileSystemListener(this);
        TapestryModuleSupportLoader.getTapestryProject(module).getEventsManager().removeTapestryModelListener(this);
        TapestryModuleSupportLoader.getTapestryProject(module).getEventsManager().addFileSystemListener(this);
        TapestryModuleSupportLoader.getTapestryProject(module).getEventsManager().addTapestryModelListener(this);
        shouldShow = true;
      }
    }

    ProjectView projectView = ProjectView.getInstance(myProject);
    // This is the corrected line:
    boolean isCurrentlyShown = projectView.getPaneIds().contains(ID);

    if (shouldShow && !isCurrentlyShown) {
      projectView.addProjectPane(this);
    } else if (!shouldShow && isCurrentlyShown) {
      projectView.removeProjectPane(this);
    }
  }
}
