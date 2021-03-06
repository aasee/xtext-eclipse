/*******************************************************************************
 * Copyright (c) 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.refactoring.participant

import com.google.common.base.Predicate
import com.google.inject.Inject
import java.io.ByteArrayOutputStream
import org.eclipse.ltk.core.refactoring.Change
import org.eclipse.ltk.core.refactoring.CompositeChange
import org.eclipse.ltk.core.refactoring.TextFileChange
import org.eclipse.ltk.core.refactoring.resource.MoveResourceChange
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange
import org.eclipse.text.edits.MultiTextEdit
import org.eclipse.text.edits.ReplaceEdit
import org.eclipse.xtext.ide.refactoring.RefactoringIssueAcceptor
import org.eclipse.xtext.ide.serializer.IEmfResourceChange
import org.eclipse.xtext.ide.serializer.ITextDocumentChange
import org.eclipse.xtext.util.IAcceptor

import static org.eclipse.xtext.ui.refactoring.participant.TryWithResource.*
import org.eclipse.xtext.ide.refactoring.RefactoringIssueAcceptor.Severity
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IWorkspace

/**
 * Converts {@link IEmfResourceChange}s to LTK {@link Change}s.
 * 
 * @author koehnlein - Initial contribution and API
 * @since 2.13
 */
class ChangeConverter implements IAcceptor<IEmfResourceChange> {
	
	CompositeChange currentChange 
	RefactoringIssueAcceptor issues
	Predicate<Change> changeFilter
	
	@Inject extension ResourceURIConverter

	def initialize(String name, Predicate<Change> changeFilter, RefactoringIssueAcceptor issues) {
		currentChange = new CompositeChange(name)
		this.issues = issues
		this.changeFilter = changeFilter
	}
	
	override accept(IEmfResourceChange emfResourceChange) {
		doConvert(emfResourceChange)
	}
	
	def Change getChange() {
		if (currentChange.children.length === 0)
			return null
		else
			return currentChange
	}

	protected def void doConvert(IEmfResourceChange change) {
		handleUriChange(change)
		handleReplacements(change)
	}

	protected def dispatch void handleReplacements(IEmfResourceChange change) {
		val outputStream = new ByteArrayOutputStream
		tryWith(outputStream) [
			val file = change.resource.URI.toFile
			if (!file.canWrite) 
				issues.add(Severity.ERROR, '''Affected file '�file.fullPath�' is read-only''')
			file.checkDerived
			change.resource.save(outputStream, null)
			val newContent = outputStream.toByteArray
			val ltkChange = new ReplaceFileContentChange(file, newContent) 
			addChange(ltkChange)
		]
	}
	
	protected def dispatch void handleReplacements(ITextDocumentChange change) {
		if(change.replacements.size > 0) {
			val file = change.newURI.toFile
			if (!file.canWrite) 
				issues.add(Severity.FATAL, '''Affected file '�file.fullPath�' is read-only''')
			file.checkDerived
			val textEdits = change.replacements.map[ replacement |
				new ReplaceEdit(replacement.offset, replacement.length, replacement.replacementText)
			]
			val textEdit = new MultiTextEdit()
			textEdit.addChildren(textEdits)
			val textFileChange = new TextFileChange(change.oldURI.lastSegment, file)
			textFileChange.setSaveMode(TextFileChange.FORCE_SAVE)
			textFileChange.setEdit(textEdit)
			textFileChange.setTextType(change.oldURI.fileExtension)
			addChange(textFileChange)
		}
	}
	
	protected def void handleUriChange(IEmfResourceChange change) {
		if(change.newURI != change.oldURI) {
			if(change.newURI.lastSegment == change.oldURI.lastSegment) { 
				val oldFile = change.oldURI.toFile
				if (!oldFile.canWrite) 
					issues.add(Severity.FATAL, '''Cannot move read-only file '�oldFile.fullPath�'��''')
				oldFile.checkDerived
				val newFile = change.newURI.toFile
				val newContainer = newFile.parent
				val ltkChange = new MoveResourceChange(oldFile, newContainer)
				addChange(ltkChange)
			} else if(change.newURI.trimSegments(1) == change.oldURI.trimSegments(1)) {
				val ltkChange = new RenameResourceChange(change.oldURI.toFile.fullPath, change.newURI.lastSegment)
				addChange(ltkChange)
			} 
		}
	}
	
	protected def void addChange(Change change) {
		if(changeFilter.apply(change))
			currentChange.add(change)
	}
	
	protected def boolean canWrite(IFile file) {
		return file.workspace.validateEdit(#[file], IWorkspace.VALIDATE_PROMPT).isOK
	}
	
	protected def checkDerived(IFile file) {
		if (file.derived) {
			issues.add(Severity.WARNING, '''Affected file '�file.fullPath�' is derived''')
		}
	}
}