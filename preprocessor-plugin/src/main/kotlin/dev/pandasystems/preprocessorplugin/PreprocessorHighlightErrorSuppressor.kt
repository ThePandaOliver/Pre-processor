package dev.pandasystems.preprocessorplugin

import com.intellij.codeInsight.highlighting.HighlightErrorFilter
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiErrorElement

class PreprocessorHighlightErrorFilter : HighlightErrorFilter() {
    override fun shouldHighlightErrorElement(element: PsiErrorElement): Boolean {
        val file = element.containingFile ?: return true
        val document = PsiDocumentManager.getInstance(file.project).getDocument(file) ?: return true

        val lineNumber = document.getLineNumber(element.textOffset)
        val lineStartOffset = document.getLineStartOffset(lineNumber)
        val lineEndOffset = document.getLineEndOffset(lineNumber)

        val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))

        // If the line containing the error starts with '#', don't highlight it.
        if (lineText.trim().startsWith("#")) {
            return false
        }

        // Otherwise, let the default highlighting occur.
        return true
    }
}
