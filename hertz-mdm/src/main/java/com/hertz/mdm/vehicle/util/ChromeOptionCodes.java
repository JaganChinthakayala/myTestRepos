package com.hertz.mdm.vehicle.util;

import java.util.*;
import java.util.regex.*;

/**
 * Represents a set of valid Chrome Option Codes. The result is
 * immutable. No new codes can be added, removed or existing codes modified.
 *
 * @author pbranch
 */
public class ChromeOptionCodes {

	/** Valid characters, order is not important */
	public static final Pattern OPTION_CODE_PATTERN = Pattern.compile("([A-Z0-9-]+)");

	/** Empty set of codes */
	public static final ChromeOptionCodes NONE = new ChromeOptionCodes();

	private final LinkedHashSet<String> normalizedCodes;
	private final Set<String> immutableSetOfCodes;
	private String asString;

	/**
	 * Create an immutable set of valid Chrome Option Codes from list of strings.
	 * <br>
	 * A valid Chrome Option Code is a string containing one or more of the
	 * following characters: 'A' to 'Z', '0' to '9', '-'
	 * <br>
	 * Empty, {@code null}, or otherwise invalid codes are discarded without notice.
	 * Duplicate codes are also discarded.
	 * <br>
	 * Source strings are normalized using the following rules:
	 *  - trim whitespace
	 *  - convert text to upper case
	 *
	 * @param rawCodes the candidate option codes; will be normalized
	 */
	public ChromeOptionCodes(String ...rawCodes) {
		// maintain insert order
		normalizedCodes = new LinkedHashSet<>();
		for (String rawCode : rawCodes) {
			String code = normalizeCode(rawCode);
			if (isValidCode(code)) {
				normalizedCodes.add(code);
			}
		}
		// "unmodifiableSet" will throw exceptions if changes are attempted
		immutableSetOfCodes = Collections.unmodifiableSet(normalizedCodes);
	}

	/**
	 * {@inheritDoc #ChromeOptionCodes(String...)}
	 *
	 * @param rawCodes {@code null} list is considered empty
	 */
	public ChromeOptionCodes(List<String> rawCodes) {
		// ternary operator is required as the constructor call MUST be the first statement
		this(rawCodes == null ? new String[0] : rawCodes.toArray(new String[rawCodes.size()]));
	}

	private String normalizeCode(String code) {
		if (code == null) {
			return "";
		}
		return code.trim().toUpperCase();
	}

	private boolean isValidCode(String code) {
		return OPTION_CODE_PATTERN.matcher(code).matches();
	}

	/**
	 * Searches for specified element in this set.
	 *
	 * @param optionCode element to find. It will be "normalized" before comparison.
	 *
	 * @return {@code true} if specified element is matched
	 */
	public boolean contains(String optionCode) {
		return normalizedCodes.contains(normalizeCode(optionCode));
	}

	/**
	 * @return {@code true} if set contains no elements
	 */
	public boolean isEmpty() {
		return normalizedCodes.isEmpty();
	}

	/**
	 * Provides a {@link java.util.Set Set} view of the option codes. This
	 * allows the group of codes to be treated as a
	 * {@link java.util.Collection Collection} without implementing the entire
	 * interface.
	 *
	 * @return unmodifiable set of normalized, verified codes
	 *
	 * @see java.util.Collections#unmodifiableSet(Set)
	 */
	public Set<String> asSet() {
		return immutableSetOfCodes;
	}

	/**
	 * @return comma separated list of option codes, each wrapped in single quotes
	 */
	@Override
	public String toString() {
		if (normalizedCodes.size() == 0 && asString == null) {
			asString = "";
		}

		// if still null then must have at least one code
		if (asString == null) {
			StringBuilder joinedValues = new StringBuilder();
			String prefix = "'";
			for (String code : normalizedCodes) {
				joinedValues.append(prefix).append(code);
				prefix = "', '";
			}
			joinedValues.append("'");
			asString = joinedValues.toString();
		}

		return asString;
	}
}
