package com.github.gaoxiang15125.myideaplugin.listeners;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class PsiNavigationAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if(editor == null || psiFile == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();

        final StringBuilder infoBuilder = new StringBuilder();
        // 这玩意可以 debug 吗
        PsiElement element = psiFile.findElementAt(offset);
        infoBuilder.append("Element at caret: ").append(element).append("\n");
        if(element != null) {
            PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            infoBuilder
                    .append("Containing method: ")
                    .append(containingMethod != null ? containingMethod.getName() : "none")
                    .append("\n");
            if (containingMethod != null) {
                PsiClass containingClass = containingMethod.getContainingClass();
                infoBuilder
                        .append("Containing class: ")
                        .append(containingClass != null ? containingClass.getName() : "none")
                        .append("\n");

                infoBuilder.append("Local variables:\n");
                containingMethod.accept(new JavaRecursiveElementVisitor() {
                    @Override
                    public void visitLocalVariable(PsiLocalVariable variable) {
                        super.visitLocalVariable(variable);
                        infoBuilder.append(variable.getName()).append("\n");
                    }
                });
            }
        }
        Messages.showMessageDialog(anActionEvent.getProject(), infoBuilder.toString(), "PSI Info", null);
    }
}
