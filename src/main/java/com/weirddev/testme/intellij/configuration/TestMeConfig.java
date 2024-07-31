package com.weirddev.testme.intellij.configuration;

/**
 * Date: 29/07/2018
 *
 * @author Yaron Yamin
 */
public class TestMeConfig {
    /**
     * Test generator behavior option. Generate tests for inherited methods
     * Valid values:true,false
     * Default:true
     */
    private boolean generateTestsForInheritedMethods = true;
    /**
     * Test generator styling feature. reformat generated test code according to relevant language styling settings in IDEA
     * Valid values:true,false
     * Default:true
     */
    private boolean reformatCode= true;
    /**
     * Test generator styling feature. replace fully qualified class names with import statements where possible in generated test
     * Valid values:true,false
     * Default:true
     */
    private boolean replaceFullyQualifiedNames= true;
    /**
     * Test generator styling feature. optimize imports in generated test
     * Valid values:true,false
     * Default:true
     */
    private boolean optimizeImports= true;

    /**
     * Test generator behavior option. Generate stubs for internal method calls in powermock
     * Valid values:true,false
     * Default:false
     */
    private boolean renderInternalMethodCallStubs = false;


    /**
     * Test generator behavior option. Declare specific test method thrown exception types
     * Valid values:true,false
     * Default:false
     */
    private boolean throwSpecificExceptionTypes = false;
    /**
     * Test generator behavior option. open user check dialog
     * Valid values:true,false
     * Default:false
     */
    private boolean openCustomizeTestDialog = false;

    public boolean getGenerateTestsForInheritedMethods() {
        return generateTestsForInheritedMethods;
    }

    public void setGenerateTestsForInheritedMethods(boolean generateTestsForInheritedMethods) {
        this.generateTestsForInheritedMethods = generateTestsForInheritedMethods;
    }

    public boolean getReformatCode() {
        return reformatCode;
    }

    public void setReformatCode(boolean reformatCode) {
        this.reformatCode = reformatCode;
    }

    public boolean getReplaceFullyQualifiedNames() {
        return replaceFullyQualifiedNames;
    }

    public void setReplaceFullyQualifiedNames(boolean replaceFullyQualifiedNames) {
        this.replaceFullyQualifiedNames = replaceFullyQualifiedNames;
    }

    public boolean getOptimizeImports() {
        return optimizeImports;
    }

    public void setOptimizeImports(boolean optimizeImports) {
        this.optimizeImports = optimizeImports;
    }

    public boolean isRenderInternalMethodCallStubs() {
        return renderInternalMethodCallStubs;
    }

    public void setRenderInternalMethodCallStubs(boolean renderInternalMethodCallStubs) {
        this.renderInternalMethodCallStubs = renderInternalMethodCallStubs;
    }

    public boolean isOpenCustomizeTestDialog() {
        return openCustomizeTestDialog;
    }

    public void setOpenCustomizeTestDialog(boolean openCustomizeTestDialog) {
        this.openCustomizeTestDialog = openCustomizeTestDialog;
    }
    public boolean isThrowSpecificExceptionTypes() {
        return throwSpecificExceptionTypes;
    }

    public void setThrowSpecificExceptionTypes(boolean throwSpecificExceptionTypes) {
        this.throwSpecificExceptionTypes = throwSpecificExceptionTypes;
    }
}
