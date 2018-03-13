package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;

import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.message.Messages;
import com.orchestranetworks.ui.UIComponentWriter;

/**
 * UIComponentUtils provides methods to add UI components.
 *
 * @deprecated temporary class waiting for the EBX core 5.8 Custom widget API.
 * @author ATI
 * @since 1.4.0
 */
@Deprecated
public final class UIComponentUtils
{
	// TODO Check EBX core 5.8 Custom widget API.

	/**
	 * Adds a boolean UI component.
	 *
	 * @deprecated temporary class waiting for the EBX core 5.8 Custom Widget API.
	 * @param pWriter the writer
	 * @param pValue the value
	 * @param pInputId the id of the input
	 * @param pInputName the name of the input
	 * @throws IllegalArgumentException if pWriter is null or if pInputId and pInputName are null
	 * @since 1.4.0
	 */
	@Deprecated
	public static void addInputBoolean(
		final UIComponentWriter pWriter,
		final String pValue,
		final String pInputId,
		final String pInputName) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter shall not be null");
		}

		Locale locale = pWriter.getLocale();
		LinkedHashMap<String, String> pValueList = new LinkedHashMap<String, String>();
		pValueList.put("true", Messages.get(UIComponentUtils.class, locale, "boolean.value.true"));
		pValueList
			.put("false", Messages.get(UIComponentUtils.class, locale, "boolean.value.false"));
		UIComponentUtils.addInputRadioButton(pWriter, pValue, pValueList, pInputId, pInputName);
	}

	/**
	 * Adds a check box UI component. Default display is with new lines.
	 *
	 * @deprecated temporary class waiting for the EBX core 5.8 Custom Widget API.
	 * @param pWriter the writer
	 * @param pValues the values
	 * @param pValueList the value list
	 * @param pInputId the id of the input
	 * @param pInputName the name of the input
	 * @throws IllegalArgumentException if pWriter is null or if pInputId and pInputName are null
	 * @since 1.4.0
	 */
	@Deprecated
	public static void addInputCheckBox(
		final UIComponentWriter pWriter,
		final ArrayList<String> pValues,
		final LinkedHashMap<String, String> pValueList,
		final String pInputId,
		final String pInputName) throws IllegalArgumentException
	{
		UIComponentUtils
			.addInputCheckBox(pWriter, pValues, pValueList, pInputId, pInputName, false);
	}

	/**
	 * Adds a check box UI component.
	 *
	 * @deprecated temporary class waiting for the EBX core 5.8 Custom Widget API.
	 * @param pWriter the writer
	 * @param pValues the values
	 * @param pValueList the value list
	 * @param pInputId the id of the input
	 * @param pInputName the name of the input
	 * @param pInline whether it is displayed on the same line (true) or with new lines (false)
	 * @throws IllegalArgumentException if pWriter is null or if pInputId and pInputName are null
	 * @since 1.4.0
	 */
	@Deprecated
	public static void addInputCheckBox(
		final UIComponentWriter pWriter,
		final ArrayList<String> pValues,
		final LinkedHashMap<String, String> pValueList,
		String pInputId,
		String pInputName,
		final boolean pInline) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter shall not be null");
		}
		if (pInputId == null && pInputName != null && !pInputName.equals(""))
		{
			pInputId = pInputName;
		}
		else if (pInputName == null && pInputId != null && !pInputId.equals(""))
		{
			pInputName = pInputId;
		}
		else if (pInputId == null && pInputName == null)
		{
			throw new IllegalArgumentException("pInputId and pInputName shall not be null");
		}

		if (pValueList == null || pValueList.isEmpty())
		{
			return;
		}

		pWriter.add("<span>");

		Iterator<Entry<String, String>> iterator = pValueList.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry<String, String> valueItem = iterator.next();
			String valueKey = valueItem.getKey();
			String valueLabel = valueItem.getValue();

			if (valueLabel == null || valueLabel.equals(""))
			{
				valueLabel = valueKey;
			}

			String inputId = pInputId + "_" + valueKey;

			String style = "display:inline;margin-right:15px;";
			if (!pInline)
			{
				style = "display:block;margin-bottom:5px;";
			}

			pWriter.add("<span style=\"" + style + "\">");

			pWriter.add(
				"<input id=\"" + inputId + "\" type=\"checkbox\" name=\"" + pInputName
					+ "\" value=\"" + valueKey + "\"");

			if (pValues != null && pValues.contains(valueKey))
			{
				pWriter.add(" checked");
			}
			pWriter.add(">");
			pWriter.add("<label for=\"" + inputId + "\">" + valueLabel + "</label>");
			pWriter.add("</span>");
		}
		pWriter.add("</span>");
	}

	/**
	 * <strong>Using non public API</strong><br>
	 * <br>
	 * Adds a date UI component.
	 *
	 * @deprecated temporary class waiting for the EBX core 5.8 Custom Widget API.
	 * @param pWriter the writer
	 * @param pValue the value
	 * @param pInputId the id of the input
	 * @param pInputName the name of the input
	 * @throws IllegalArgumentException if pWriter is null or if pInputId and pInputName are null
	 * @since 1.4.0
	 */
	@Deprecated
	public static void addInputDate(
		final UIComponentWriter pWriter,
		final Date pValue,
		String pInputId,
		String pInputName) throws IllegalArgumentException
	{
		// USING_NON_PUBLIC_API

		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter shall not be null");
		}
		if (pInputId == null && pInputName != null && !pInputName.equals(""))
		{
			pInputId = pInputName;
		}
		else if (pInputName == null && pInputId != null && !pInputId.equals(""))
		{
			pInputName = pInputId;
		}
		else if (pInputId == null && pInputName == null)
		{
			throw new IllegalArgumentException("pInputId and pInputName shall not be null");
		}

		Calendar calendar = null;
		if (pValue != null)
		{
			calendar = Calendar.getInstance();
			calendar.setTime(pValue);
		}

		pWriter.add("<span>");
		UIUtils.addEBXDateComponent(pInputId, pWriter, calendar);
		pWriter.add("</span>");
	}

	/**
	 * Adds an integer UI component.
	 *
	 * @deprecated temporary class waiting for the EBX core 5.8 Custom Widget API.
	 * @param pWriter the writer
	 * @param pValue the value
	 * @param pInputId the id of the input
	 * @param pInputName the name of the input
	 * @throws IllegalArgumentException if pWriter is null or if pInputId and pInputName are null
	 * @since 1.4.0
	 */
	@Deprecated
	public static void addInputInteger(
		final UIComponentWriter pWriter,
		final String pValue,
		String pInputId,
		String pInputName) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter shall not be null");
		}
		if (pInputId == null && pInputName != null && !pInputName.equals(""))
		{
			pInputId = pInputName;
		}
		else if (pInputName == null && pInputId != null && !pInputId.equals(""))
		{
			pInputName = pInputId;
		}
		else if (pInputId == null && pInputName == null)
		{
			throw new IllegalArgumentException("pInputId and pInputName shall not be null");
		}

		String value = pValue;
		if (value == null)
		{
			value = "";
		}
		pWriter.add(
			"<input type=\"text\" id=\"" + pInputId + "\" name=\"" + pInputName + "\" value=\""
				+ value + "\" pattern=\"[0-9]*\" >");
	}

	/**
	 * Adds a plain text UI component.
	 *
	 * @deprecated temporary class waiting for the EBX core 5.8 Custom Widget API.
	 * @param pWriter the writer
	 * @param pValue the value
	 * @param pInputId the id of the input
	 * @param pInputName the name of the input
	 * @throws IllegalArgumentException if pWriter is null or if pInputId and pInputName are null
	 * @since 1.4.0
	 */
	@Deprecated
	public static void addInputPlainText(
		final UIComponentWriter pWriter,
		final String pValue,
		String pInputId,
		String pInputName) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter shall not be null");
		}
		if (pInputId == null && pInputName != null && !pInputName.equals(""))
		{
			pInputId = pInputName;
		}
		else if (pInputName == null && pInputId != null && !pInputId.equals(""))
		{
			pInputName = pInputId;
		}
		else if (pInputId == null && pInputName == null)
		{
			throw new IllegalArgumentException("pInputId and pInputName shall not be null");
		}

		String value = pValue;
		if (value == null)
		{
			value = "";
		}
		pWriter.add(
			"<input type=\"text\" id=\"" + pInputId + "\" name=\"" + pInputName + "\" value=\""
				+ value + "\" >");
	}

	/**
	 * Adds a radio button UI component. Default display is on the same line.
	 *
	 * @deprecated temporary class waiting for the EBX core 5.8 Custom Widget API.
	 * @param pWriter the writer
	 * @param pValue the existing value
	 * @param pValueList the value list
	 * @param pInputId the id of the input
	 * @param pInputName the name of the input
	 * @throws IllegalArgumentException if pWriter is null or if pInputId and pInputName are null
	 * @since 1.4.0
	 */
	@Deprecated
	public static void addInputRadioButton(
		final UIComponentWriter pWriter,
		final String pValue,
		final LinkedHashMap<String, String> pValueList,
		final String pInputId,
		final String pInputName) throws IllegalArgumentException
	{
		UIComponentUtils
			.addInputRadioButton(pWriter, pValue, pValueList, pInputId, pInputName, true);
	}

	/**
	 * Adds a radio button UI component.
	 *
	 * @deprecated temporary class waiting for the EBX core 5.8 Custom Widget API.
	 * @param pWriter the writer
	 * @param pValue the existing value
	 * @param pValueList the value list
	 * @param pInputId the id of the input
	 * @param pInputName the name of the input
	 * @param pInline whether it is displayed on the same line (true) or with new lines (false)
	 * @throws IllegalArgumentException if pWriter is null or if pInputId and pInputName are null
	 * @since 1.4.0
	 */
	@Deprecated
	public static void addInputRadioButton(
		final UIComponentWriter pWriter,
		final String pValue,
		final LinkedHashMap<String, String> pValueList,
		String pInputId,
		String pInputName,
		final boolean pInline) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}
		if (pInputId == null && pInputName != null && !pInputName.equals(""))
		{
			pInputId = pInputName;
		}
		else if (pInputName == null && pInputId != null && !pInputId.equals(""))
		{
			pInputName = pInputId;
		}
		else if (pInputId == null && pInputName == null)
		{
			throw new IllegalArgumentException("pInputId and pInputName shall not be null");
		}

		if (pValueList == null || pValueList.isEmpty())
		{
			return;
		}

		pWriter.add("<span>");

		Iterator<Entry<String, String>> iterator = pValueList.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry<String, String> valueItem = iterator.next();
			String valueKey = valueItem.getKey();
			String valueLabel = valueItem.getValue();

			if (valueLabel == null || valueLabel.equals(""))
			{
				valueLabel = valueKey;
			}

			String inputId = pInputId + "_" + valueKey;

			String style = "display:inline;margin-right:15px;";
			if (!pInline)
			{
				style = "display:block;margin-bottom:5px;";
			}

			pWriter.add("<span style=\"" + style + "\">");

			pWriter.add(
				"<input id=\"" + inputId + "\" type=\"radio\" name=\"" + pInputName + "\" value=\""
					+ valueKey + "\"");
			if (pValue != null && pValue.equals(valueKey))
			{
				pWriter.add(" checked");
			}
			pWriter.add(">");
			pWriter.add("<label for=\"" + inputId + "\">" + valueLabel + "</label>");

			pWriter.add("</span>");
		}
		pWriter.add("</span>");
	}

	/**
	 * Adds a selection box UI component.
	 *
	 * @deprecated temporary class waiting for the EBX core 5.8 Custom Widget API.
	 * @param pWriter the writer
	 * @param pValue the already selected value
	 * @param pValueList the value list
	 * @param pInputId the id of the input
	 * @param pInputName the name of the input
	 * @throws IllegalArgumentException if pWriter is null or if pInputId and pInputName are null
	 * @since 1.4.0
	 */
	@Deprecated
	public static void addInputSelectionBox(
		final UIComponentWriter pWriter,
		final String pValue,
		final LinkedHashMap<String, String> pValueList,
		String pInputId,
		String pInputName) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter shall not be null");
		}
		if (pInputId == null && pInputName != null && !pInputName.equals(""))
		{
			pInputId = pInputName;
		}
		else if (pInputName == null && pInputId != null && !pInputId.equals(""))
		{
			pInputName = pInputId;
		}
		else if (pInputId == null && pInputName == null)
		{
			throw new IllegalArgumentException("pInputId and pInputName shall not be null");
		}

		pWriter.add("<span>");
		pWriter.add("<select id=\"" + pInputId + "\" name=\"" + pInputName + "\">");

		if (pValue == null || pValue.equals(""))
		{
			pWriter.add("<option value=\"\" selected></option>");
		}
		else
		{
			pWriter.add("<option value=\"\" ></option>");
		}

		if (pValueList != null && !pValueList.isEmpty())
		{
			Iterator<Entry<String, String>> iterator = pValueList.entrySet().iterator();
			while (iterator.hasNext())
			{
				Entry<String, String> valueItem = iterator.next();
				String valueKey = valueItem.getKey();
				String valueLabel = valueItem.getValue();

				if (valueLabel == null || valueLabel.equals(""))
				{
					valueLabel = valueKey;
				}

				if (pValue != null && pValue.equals(valueKey))
				{
					pWriter.add(
						"<option value=\"" + valueKey + "\" selected>" + valueLabel + "</option>");
				}
				else
				{
					pWriter.add("<option value=\"" + valueKey + "\" >" + valueLabel + "</option>");
				}
			}
		}

		pWriter.add("</select>");
		pWriter.add("</span>");
	}

	private UIComponentUtils()
	{
	}
}
