package dev.pandasystems.preprocessorplugin

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiComment

class PreprocessorAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		if (element is PsiComment) {
			val text = element.text
			if (text.trim().startsWith("#")) {
				// Suppress errors by creating a suppression annotation
				holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
					.range(element.textRange)
					.create()
			}
		}
	}
}