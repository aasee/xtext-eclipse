/**
 * generated by Xtext 2.9.1-SNAPSHOT
 */
package org.eclipse.xtext.example.homeautomation.validation;

import com.google.common.base.Objects;
import java.util.HashSet;
import javax.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.example.homeautomation.jvmmodel.RuleEngineJvmModelInferrer;
import org.eclipse.xtext.example.homeautomation.ruleEngine.Declaration;
import org.eclipse.xtext.example.homeautomation.ruleEngine.Device;
import org.eclipse.xtext.example.homeautomation.ruleEngine.Model;
import org.eclipse.xtext.example.homeautomation.ruleEngine.Rule;
import org.eclipse.xtext.example.homeautomation.ruleEngine.RuleEnginePackage;
import org.eclipse.xtext.example.homeautomation.validation.AbstractRuleEngineValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.XbasePackage;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

/**
 * This class contains custom validation rules.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
@SuppressWarnings("all")
public class RuleEngineValidator extends AbstractRuleEngineValidator {
  @Inject
  @Extension
  private IJvmModelAssociations _iJvmModelAssociations;
  
  @Check
  public void checkUniqueDeclarations(final Model model) {
    final HashSet<String> deviceNames = CollectionLiterals.<String>newHashSet();
    final HashSet<String> ruleDescriptions = CollectionLiterals.<String>newHashSet();
    EList<Declaration> _declarations = model.getDeclarations();
    for (final Declaration decl : _declarations) {
      if ((decl instanceof Device)) {
        String _name = ((Device)decl).getName();
        boolean _add = deviceNames.add(_name);
        boolean _not = (!_add);
        if (_not) {
          this.error("Device names must be unique.", decl, RuleEnginePackage.Literals.DEVICE__NAME);
        }
      } else {
        if ((decl instanceof Rule)) {
          final String methodName = RuleEngineJvmModelInferrer.getRuleMethodName(((Rule)decl));
          boolean _add_1 = ruleDescriptions.add(methodName);
          boolean _not_1 = (!_add_1);
          if (_not_1) {
            this.error("Rule descriptions must be unique.", decl, RuleEnginePackage.Literals.RULE__DESCRIPTION);
          }
        }
      }
    }
  }
  
  @Check
  public void checkStatesNotEmpty(final Device device) {
    EList<org.eclipse.xtext.example.homeautomation.ruleEngine.State> _states = device.getStates();
    boolean _isEmpty = _states.isEmpty();
    if (_isEmpty) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("The device \"");
      String _name = device.getName();
      _builder.append(_name, "");
      _builder.append("\" must have at least one state.");
      this.error(_builder.toString(), device, RuleEnginePackage.Literals.DEVICE__NAME);
    }
  }
  
  @Check
  public void checkUniqueStates(final Device device) {
    final HashSet<String> stateNames = CollectionLiterals.<String>newHashSet();
    EList<org.eclipse.xtext.example.homeautomation.ruleEngine.State> _states = device.getStates();
    for (final org.eclipse.xtext.example.homeautomation.ruleEngine.State state : _states) {
      String _name = state.getName();
      boolean _add = stateNames.add(_name);
      boolean _not = (!_add);
      if (_not) {
        this.error("State names must be unique.", state, RuleEnginePackage.Literals.STATE__NAME);
      }
    }
  }
  
  @Check
  public void checkRuleDescriptionNotEmpty(final Rule rule) {
    String _description = rule.getDescription();
    boolean _isNullOrEmpty = StringExtensions.isNullOrEmpty(_description);
    if (_isNullOrEmpty) {
      this.error("A rule description must not be empty.", rule, RuleEnginePackage.Literals.RULE__DESCRIPTION);
    }
  }
  
  @Check
  public void checkRuleRecursion(final XFeatureCall featureCall) {
    final Rule containingRule = EcoreUtil2.<Rule>getContainerOfType(featureCall, Rule.class);
    if (((((!Objects.equal(containingRule, null)) && (featureCall.getFeature() instanceof JvmOperation)) && Objects.equal(featureCall.getConcreteSyntaxFeatureName(), "fire")) && (featureCall.getFeatureCallArguments().size() == 1))) {
      EList<XExpression> _featureCallArguments = featureCall.getFeatureCallArguments();
      final XExpression argument = IterableExtensions.<XExpression>head(_featureCallArguments);
      if ((argument instanceof XAbstractFeatureCall)) {
        JvmIdentifiableElement _feature = ((XAbstractFeatureCall)argument).getFeature();
        final EObject sourceElem = this._iJvmModelAssociations.getPrimarySourceElement(_feature);
        org.eclipse.xtext.example.homeautomation.ruleEngine.State _deviceState = containingRule.getDeviceState();
        boolean _equals = Objects.equal(sourceElem, _deviceState);
        if (_equals) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("Firing the same device state that triggers the rule \"");
          String _description = containingRule.getDescription();
          _builder.append(_description, "");
          _builder.append("\" may lead to endless recursion.");
          this.warning(_builder.toString(), featureCall, XbasePackage.Literals.XFEATURE_CALL__FEATURE_CALL_ARGUMENTS, 0);
        }
      }
    }
  }
}