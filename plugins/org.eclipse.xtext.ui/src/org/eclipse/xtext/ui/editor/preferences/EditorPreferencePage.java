/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.ui.editor.preferences;

import org.eclipse.xtext.ui.editor.preferences.fields.LabelField;

/**
 * @author Dennis H�bner - Initial contribution and API
 * 
 */
public class EditorPreferencePage extends AbstractPreferencePage {

	@Override
	protected void createFieldEditors() {
		addField(new LabelField(getLanguageName() + " Editor preferences.\nThis is the best " + getLanguageName()
				+ " Editor in the whole world!", getFieldEditorParent()));
	}
}
