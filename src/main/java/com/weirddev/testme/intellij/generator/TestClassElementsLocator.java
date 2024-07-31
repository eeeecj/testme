package com.weirddev.testme.intellij.generator;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiMethod;
import com.weirddev.testme.intellij.template.context.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class TestClassElementsLocator {
    private static final Logger LOG = Logger.getInstance(TestClassElementsLocator.class.getName());
    public TestClassElementsLocator() {
    }

    public PsiElement findOptimalCursorLocation(PsiClass targetClass, PsiMethod selectedMethod) {
        PsiElement defaultLocation = targetClass.getLBrace()==null?targetClass.getFirstChild():targetClass.getLBrace().getNextSibling();
        try {
            PsiMethod testMethod;
            if (null != selectedMethod) {
                testMethod = findTestMethodOfSelected(targetClass, selectedMethod);
            } else {
                testMethod = findTestMethod(targetClass);
            }
            if (testMethod == null) {
                return defaultLocation;
            }
            PsiElement assertExpression = findLastElement(testMethod, PsiExpressionStatement.class);
            if (assertExpression == null) {
                return testMethod;
            } else if (assertExpression.getFirstChild() == null || assertExpression.getFirstChild().getLastChild() == null || assertExpression.getFirstChild().getLastChild().getFirstChild() == null || assertExpression.getFirstChild()
                    .getLastChild().getFirstChild().getNextSibling() == null) {
                return defaultLocation;
            } else {
                return assertExpression.getFirstChild().getLastChild().getFirstChild().getNextSibling();
            }
        } catch (Throwable e) {
            LOG.debug("can't locate optimal cursor location",e);
        }
        return defaultLocation;
    }

    @Nullable
    PsiMethod findTestMethod(PsiClass targetClass) {
        PsiMethod testMethod = null;
        PsiMethod[] methods = targetClass.getMethods();
        for (PsiMethod method : methods) {
            if (!method.getName().startsWith("setUp")) {
                testMethod = method;
                break;
            }
        }
        return testMethod;
    }

    private PsiMethod findTestMethodOfSelected(PsiClass targetClass, PsiMethod selectedMethod) {
        String testMethodName = "test" + StringUtils.capitalizeFirstLetter(selectedMethod.getName());
        List<PsiMethod> list =
            Arrays.stream(targetClass.getMethods()).filter(m -> m.getName().equals(testMethodName))
                .toList();
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    private <T> T  findLastElement(PsiMethod testMethod,Class<T> elementClass) {
        T foundElement = null;
        if(testMethod.getBody()==null) return null;
        for (PsiElement psiElement : testMethod.getBody().getChildren()) {
            if (elementClass.isInstance(psiElement)) {
                foundElement=elementClass.cast(psiElement);
           }
        }
        return foundElement;
    }
}