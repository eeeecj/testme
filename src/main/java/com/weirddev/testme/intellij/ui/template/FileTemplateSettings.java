package com.weirddev.testme.intellij.ui.template;

import com.intellij.ide.fileTemplates.impl.FileTemplateBase;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Exportable part of file template settings. User-specific (local) settings are handled by FileTemplateManagerImpl.
 *
 * @see com.intellij.ide.fileTemplates.impl.FileTemplateSettings
 */
@State(
  name = FileTemplateSettings.FILE_TEMPLATE_SETTINGS,
  storages = @Storage(FileTemplateSettings.EXPORTABLE_SETTINGS_FILE)
)
class FileTemplateSettings extends FileTemplatesLoader implements PersistentStateComponent<Element> {
  public static final String FILE_TEMPLATE_SETTINGS = "TestMeFileTemplateSettings";
  static final String EXPORTABLE_SETTINGS_FILE = "testme.file.template.settings.xml";

  private static final String ELEMENT_TEMPLATE = "template";
  private static final String ATTRIBUTE_NAME = "name";
  private static final String ATTRIBUTE_REFORMAT = "reformat";
  private static final String ATTRIBUTE_LIVE_TEMPLATE = "live-template-enabled";
  private static final String ATTRIBUTE_ENABLED = "enabled";

  static final boolean DEFAULT_REFORMAT_CODE_VALUE = true;
  static final boolean DEFAULT_ENABLED_VALUE = true; // todo make use of

  FileTemplateSettings(@Nullable Project project) {
    super(project);
  }

  @Nullable
  @Override
  public Element getState() {
    Element element = new Element("fileTemplateSettings");

    for (FTManager manager : getAllManagers()) {
      Element templatesGroup = null;
      for (FileTemplateBase template : manager.getAllTemplates(true)) {
        // save only those settings that differ from defaults
        boolean shouldSave = template.isReformatCode() != FileTemplateSettings.DEFAULT_REFORMAT_CODE_VALUE ||
                             // check isLiveTemplateEnabledChanged() first to avoid expensive loading all templates on exit
                             template.isLiveTemplateEnabledChanged() && template.isLiveTemplateEnabled() != template.isLiveTemplateEnabledByDefault();
//        if (template instanceof BundledFileTemplate) {
//          shouldSave |= ((BundledFileTemplate)template).isEnabled() != FileTemplateSettings.DEFAULT_ENABLED_VALUE;
//        }
        if (!shouldSave) continue;

        final Element templateElement = new Element(ELEMENT_TEMPLATE);
        templateElement.setAttribute(ATTRIBUTE_NAME, template.getQualifiedName());
        templateElement.setAttribute(ATTRIBUTE_REFORMAT, Boolean.toString(template.isReformatCode()));
        templateElement.setAttribute(ATTRIBUTE_LIVE_TEMPLATE, Boolean.toString(template.isLiveTemplateEnabled()));

//        if (template instanceof BundledFileTemplate) {
//          templateElement.setAttribute(ATTRIBUTE_ENABLED, Boolean.toString(((BundledFileTemplate)template).isEnabled()));
//        }

        if (templatesGroup == null) {
          templatesGroup = new Element(getXmlElementGroupName(manager));
          element.addContent(templatesGroup);
        }
        templatesGroup.addContent(templateElement);
      }
    }

    return element;
  }

  @Override
  public void loadState(@NotNull Element state) {
    for (final FTManager manager : getAllManagers()) {
      final Element templatesGroup = state.getChild(getXmlElementGroupName(manager));
      if (templatesGroup == null) continue;

      for (Element child : templatesGroup.getChildren(ELEMENT_TEMPLATE)) {
        final String qName = child.getAttributeValue(ATTRIBUTE_NAME);
        final FileTemplateBase template = manager.getTemplate(qName);
        if (template == null) continue;

        template.setReformatCode(Boolean.parseBoolean(child.getAttributeValue(ATTRIBUTE_REFORMAT)));
        template.setLiveTemplateEnabled(Boolean.parseBoolean(child.getAttributeValue(ATTRIBUTE_LIVE_TEMPLATE)));

//        if (template instanceof BundledFileTemplate) {
//          ((BundledFileTemplate)template).setEnabled(Boolean.parseBoolean(child.getAttributeValue(ATTRIBUTE_ENABLED, "true")));
//        }
      }
    }
  }

  private static String getXmlElementGroupName(@NotNull FTManager manager) {
    return manager.getName().toLowerCase(Locale.US) + "_templates";
  }
}