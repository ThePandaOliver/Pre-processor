package dev.pandasystems.preprocessorplugin

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoFilter
import com.intellij.codeInsight.highlighting.HighlightErrorFilter
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile

class PreprocessorHighlightInfoFilter : HighlightInfoFilter {
	override fun accept(
		highlightInfo: HighlightInfo,
		file: PsiFile?
	): Boolean {
		if (file == null) return true

		val firstElement = file.findElementAt(highlightInfo.startOffset) ?: return true

		println(firstElement.text)

		return true
	}
}