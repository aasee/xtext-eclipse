/*
 * generated by Xtext
 */
package org.eclipse.xtext.ui.tests.editor.encoding.ide.contentassist.antlr;

import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.ide.editor.contentassist.antlr.AbstractContentAssistParser;
import org.eclipse.xtext.ui.tests.editor.encoding.ide.contentassist.antlr.internal.InternalEncodingUiTestLanguageParser;
import org.eclipse.xtext.ui.tests.editor.encoding.services.EncodingUiTestLanguageGrammarAccess;

public class EncodingUiTestLanguageParser extends AbstractContentAssistParser {

	@Inject
	private EncodingUiTestLanguageGrammarAccess grammarAccess;

	private Map<AbstractElement, String> nameMappings;

	@Override
	protected InternalEncodingUiTestLanguageParser createParser() {
		InternalEncodingUiTestLanguageParser result = new InternalEncodingUiTestLanguageParser(null);
		result.setGrammarAccess(grammarAccess);
		return result;
	}

	@Override
	protected String getRuleName(AbstractElement element) {
		if (nameMappings == null) {
			nameMappings = new HashMap<AbstractElement, String>() {
				private static final long serialVersionUID = 1L;
				{
					put(grammarAccess.getModelAccess().getWordsAssignment(), "rule__Model__WordsAssignment");
					put(grammarAccess.getWordAccess().getValueAssignment(), "rule__Word__ValueAssignment");
				}
			};
		}
		return nameMappings.get(element);
	}
			
	@Override
	protected String[] getInitialHiddenTokens() {
		return new String[] { "RULE_WS" };
	}

	public EncodingUiTestLanguageGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(EncodingUiTestLanguageGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
