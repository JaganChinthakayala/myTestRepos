package com.hertz.mdm.vehicle.util;

import java.util.*;
import java.util.regex.*;

import javax.script.*;

/**
 * Helper class to determine if a set of Chrome Option Codes satisfies a
 * Chrome "Boolean Logic" condition. Appendix A of the "Chrome NVD Schema"
 * document describes the syntax.
 *
 * An instance of this class caches each condition tested to reduce parsing
 * overhead.
 *
 * @author pbranch
 *
 */
public class ChromeOptionEvaluator {

	private ScriptEngine javascriptEngine;

	// used to ensure each unique condition is only registered once
	private final Set<String> registeredConditions = new HashSet<>();

	/**
	 * Evaluates the specified condition using the option codes provided. An
	 * empty or {@code null} condition will always pass. An invalid condition
	 * always fails to match.
	 *
	 * @param conditionText Chrome NVD "Boolean Logic" condition; can be {@code null}
	 * @param optionCodes Chrome Vehicle Option Codes for evaluation; can NOT be {@code null}
	 *
	 * @return the result of applying the condition to the specified option codes
	 */
	public boolean isMatch(String conditionText, ChromeOptionCodes optionCodes) {

		JavaScriptCondition condition = JavaScriptCondition.fromText(conditionText);

		// An empty condition always matches per Chrome NVD matching rules
		if (condition.isEmpty()) {
			return true;
		}

		Boolean matchResult = Boolean.FALSE;
		try {
			if (javascriptEngine == null) {
				// lazy instantiation
				javascriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");

				// create container for condition matching functions
				String script = "var chromeConditionFunctions = new java.util.HashMap();";

				// create top level method to run a matching function identified
				// by key for a set of options. The name "options" is critical
				// and should only be changed carefully.
				script += "function matchCondition(jsKey, options) {return (chromeConditionFunctions.get(jsKey))(options);};";

				javascriptEngine.eval(script);
			}

			if (!registeredConditions.contains(condition.key)) {
				// this condition function not yet defined in JavaScript so register it by key
				String script =
                    "chromeConditionFunctions.put('"+ condition.key + "', function(options) {return " + condition.statement + ";});";
				javascriptEngine.eval(script);

				registeredConditions.add(condition.key);
			}

			Object result = ((Invocable)javascriptEngine).invokeFunction("matchCondition", condition.key, optionCodes);
			if (result != null) {
				matchResult = (Boolean)result;
			}
		} catch (ScriptException e) {
			// invalid syntax in the condition simply cause a "false" result
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			// TODO: NoSuchMethodException is a System Error and should be handled differently
			e.printStackTrace();
			return false;
		}

		return matchResult.booleanValue();
	}

	public static class JavaScriptCondition {

		/** Empty condition CONSTANT */
		public static final JavaScriptCondition NONE = new JavaScriptCondition(null);

		/**
		 * Creates a normalized, verified Chrome condition. Never returns {@code null}.
		 *
		 * @param conditionText Chrome "Boolean Logic" condition to parse
		 *
		 * @return a valid condition or {@link JavaScriptConditionText#NONE} if input is invalid.
		 */
		public static JavaScriptCondition fromText(String conditionText) {

			if (conditionText == null) {
				return NONE;
			}

			String normalizedCondition = conditionText.trim()
					.toUpperCase()
					// remove anything that is NOT a valid character
					.replaceAll("[^A-Z0-9-,&!()]", "");

			if (normalizedCondition.isEmpty()) {
				return NONE;
			}

			return new JavaScriptCondition(normalizedCondition);
		}

		/** Normalized version of the original condition */
		public final String key;

		/** The condition translated to JavaScript syntax */
		public final String statement;

		// consumers should only use the static factory method
		private JavaScriptCondition(String normalizedCondition) {
			if (normalizedCondition == null || normalizedCondition.isEmpty()) {
				key = "";
				statement = "";
				return;
			}

			key = normalizedCondition;

			/* Change "Chrome Boolean Logic" operators to JavaScript version
			 * - comma is OR: ||
			 * - ampersand is AND: &&
			 * the NOT "!" and Grouping "(" ")" operators are the same for both
			 */
			String javascriptStatement = ""; 
			String keyString = key.replaceAll(",", "||").replaceAll("&", "&&");
			Matcher m = ChromeOptionCodes.OPTION_CODE_PATTERN.matcher(keyString);
			int stringStartPos = 0;

			// find each Chrome Option Code in the condition
			while (m.find()) {
				// match is a Chrome Option Code
				String optionCode = m.group();
				int patternStartPos = m.start();
				int patternEndPos = m.end();

				// for each Option Code in the condition, replace with check to
				// see if it is contained in the specified option list
				javascriptStatement = javascriptStatement + keyString.substring(stringStartPos, patternStartPos)
						            + "options.contains('" + optionCode + "')";
				stringStartPos = patternEndPos;
			}
			
			javascriptStatement = javascriptStatement + keyString.substring(stringStartPos);
			statement = javascriptStatement;
		}

		/** An instance is considered empty if either the key or statement are empty */
		public boolean isEmpty() {
			return key.isEmpty() || statement.isEmpty();
		}

		@Override
		/** The normalized version of the original Chrome "Boolean Logic" condition i.e. @{link #key} */
		public String toString() {
			return key;
		}
	}
}
