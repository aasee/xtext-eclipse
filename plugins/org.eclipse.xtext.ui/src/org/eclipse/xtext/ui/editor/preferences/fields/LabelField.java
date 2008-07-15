/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.ui.editor.preferences.fields;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.widgets.Composite;

public final class LabelField extends FieldEditor {
	public LabelField(String labelText, Composite parent) {
		super("label", labelText, parent);
	}

	@Override
	public int getNumberOfControls() {
		return 1;
	}

	@Override
	protected void doStore() {
	}

	@Override
	protected void doLoadDefault() {
	}

	@Override
	protected void doLoad() {
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		getLabelControl(parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
	}
}