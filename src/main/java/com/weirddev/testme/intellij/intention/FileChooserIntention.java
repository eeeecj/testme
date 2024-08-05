package com.weirddev.testme.intellij.intention;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileChooserIntention {
    private Project project;

    private FileEditorManager fileEditorManager;

    public FileChooserIntention(Project project) {
        this.project = project;
        this.fileEditorManager = FileEditorManager.getInstance(project);
    }

    public static FileChooserIntention getInstance(@NotNull Project project) {
        return new FileChooserIntention(project);
    }

    public VirtualFile showFolderSelectionDialog(@NotNull String title, @Nullable VirtualFile toSelect, @Nullable VirtualFile... roots) {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(title);
        if (null != roots) {
            descriptor.setRoots(roots);
        }
        return FileChooser.chooseFile(descriptor, project, toSelect);
    }

}
