/*******************************************************************************
 * Copyright (c) 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.refactoring.participant

import java.util.List
import org.apache.log4j.Logger
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.Platform
import org.eclipse.xtext.ide.refactoring.XtextMoveResourceStrategy

/**
 * @author koehnlein - Initial contribution and API
 * @since 2.13
 */
class XtextMoveResourceStrategyRegistry {
	
	static Logger LOG = Logger.getLogger(XtextMoveResourceStrategyRegistry)
	
	private List<XtextMoveResourceStrategy> strategies
	
	def List<? extends XtextMoveResourceStrategy> getStrategies() {
		return strategies ?: {
			val configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionPointID);
			val strategies= <XtextMoveResourceStrategy>newArrayList
			for (configurationElement : configurationElements) {
				try {
					strategies += configurationElement.createExecutableExtension('class') as XtextMoveResourceStrategy
				} catch (CoreException e) {
					LOG.error("Error instantiating participant strategy", e);
				}
			}
			strategies
		}
	}
	
	def  protected getExtensionPointID() {
		'org.eclipse.xtext.ui.resourceMoveStrategy'
	}
}

