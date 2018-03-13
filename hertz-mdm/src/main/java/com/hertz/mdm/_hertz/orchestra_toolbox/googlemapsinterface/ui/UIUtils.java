package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.message.Messages;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.text.Severity;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.info.SchemaNodeInformation;
import com.orchestranetworks.ui.ResourceType;
import com.orchestranetworks.ui.UIButtonIcon;
import com.orchestranetworks.ui.UIButtonLayout;
import com.orchestranetworks.ui.UIButtonRelief;
import com.orchestranetworks.ui.UIButtonSpecJSAction;
import com.orchestranetworks.ui.UIComponentWriter;
import com.orchestranetworks.ui.UIFormLabelSpec;
import com.orchestranetworks.ui.UIHttpManagerComponent;
import com.orchestranetworks.ui.UIJavaScriptWriter;
import com.orchestranetworks.ui.form.UIFormPaneWriter;

/**
 * UI Utilities
 *
 * @author ATI
 * @since 1.0.0
 */
public final class UIUtils
{
	private static final String JS_FUNCNAME_GET_DATE = "getEBXPrivateDate";

	/**
	 * Adds a columns layout for the provided list of nodes.<br>
	 * Technically insert in the html stream a div tag containing as many table form row as expected
	 * column. Nodes are equally dispatched in the columns.
	 *
	 * @param pWriter the writer
	 * @param pNodes the nodes
	 * @param pNbColumns the nb columns
	 * @param pCustomStyle the custom style
	 * @param pNodeInformationToExclude the node information to exclude
	 * @throws IllegalArgumentException if the arguments pWriter and pNode are null
	 * @since 1.2.0
	 */
	public static void addColumnsLayout(
		final UIFormPaneWriter pWriter,
		final List<SchemaNode> pNodes,
		int pNbColumns,
		String pCustomStyle,
		final String pNodeInformationToExclude) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}
		if (pNodes == null)
		{
			throw new IllegalArgumentException("pNodes argument shall not be null");
		}
		if (pNbColumns == 0)
		{
			pNbColumns = 1;
		}
		if (pCustomStyle == null)
		{
			pCustomStyle = "";
		}

		// Identify node children to be included in the layout
		List<SchemaNode> keptNodes = UIUtils
			.addColumnsLayout_introspectSchema(pNodes, pNodeInformationToExclude);

		int nbFields = keptNodes.size();
		int nbFieldsPerColumn = (int) Math.ceil((double) nbFields / (double) pNbColumns);
		int width = Math.round(100 / pNbColumns);

		pWriter.add("<div>");
		{
			String layoutDivStyle = pCustomStyle + "box-sizing:border-box;width:" + width
				+ "%;float:left;";

			int globalCounter = 0;
			int counter = 0;
			for (SchemaNode node : keptNodes)
			{
				if (counter == 0)
				{
					pWriter.add("<div style=\"" + layoutDivStyle + "\">");
					pWriter.startTableFormRow();
				}

				pWriter.addFormRow(Path.SELF.add(node.getPathInAdaptation()));
				counter++;
				globalCounter++;

				if (counter == nbFieldsPerColumn || globalCounter == nbFields)
				{
					pWriter.endTableFormRow();
					pWriter.add("</div>");
					counter = 0;
				}
			}
			pWriter.add("<div style=\"clear:both\"></div>");
		}
		pWriter.add("</div>");
	}

	/**
	 * Adds a columns layout for the children nodes of the provided group node.<br>
	 * Technically insert in the html stream a div tag containing as many table form row as expected
	 * column. Nodes are equally dispatched in the columns.
	 *
	 * @param pWriter the writer
	 * @param pGroupNode the node of a group
	 * @param pNbColumns the nb columns
	 * @param pCustomStyle the custom style
	 * @param pNodeInformationToExclude the node information to exclude
	 * @throws IllegalArgumentException if the arguments pWriter and pNode are null
	 * @since 1.0.0
	 */
	public static void addColumnsLayout(
		final UIFormPaneWriter pWriter,
		final SchemaNode pGroupNode,
		final int pNbColumns,
		final String pCustomStyle,
		final String pNodeInformationToExclude) throws IllegalArgumentException
	{
		if (pGroupNode == null)
		{
			throw new IllegalArgumentException("pGroupNode argument shall not be null");
		}

		// Identify node children to be included in the layout
		SchemaNode[] children = pGroupNode.getNodeChildren();
		List<SchemaNode> nodes = Arrays.asList(children);
		UIUtils
			.addColumnsLayout(pWriter, nodes, pNbColumns, pCustomStyle, pNodeInformationToExclude);
	}

	/**
	 * Add a link tag to import a stylesheet.
	 *
	 * @param pWriter the writer
	 * @param pLocation The location of the stylesheet from /www/{@literal <locale>}/stylesheets
	 * @throws IllegalArgumentException if the argument pWriter is null.
	 * @since 1.0.0
	 */
	public static void addCssLink(final UIComponentWriter pWriter, final String pLocation)
		throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}

		String location = pWriter.getURLForResource(ResourceType.STYLESHEET, pLocation);
		pWriter.add("<link rel=\"stylesheet\" href=\"" + location + "\" > ");
	}

	/**
	 * Add a link tag to import a stylesheet.
	 *
	 * @param pWriter the writer
	 * @param pLocation The location of the stylesheet from /www/{@literal <locale>}/stylesheets
	 * @param pModuleName The module name
	 * @throws IllegalArgumentException if the argument pWriter is null.
	 * @since 1.0.0
	 */
	public static void addCssLink(
		final UIComponentWriter pWriter,
		final String pLocation,
		final String pModuleName) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}

		String location = pWriter.getURLForResource(
			pModuleName,
			ResourceType.STYLESHEET,
			pLocation,
			pWriter.getLocale());
		pWriter.add("<link rel=\"stylesheet\" href=\"" + location + "\" > ");
	}

	/**
	 * <strong>Using non public API</strong><br>
	 * <br>
	 * Add the standard EBX date Widget in a UIFormRow (must be). This method is deprecated because
	 * using EBX private API. It is not guarantee to work in the future. It must be replaced by an
	 * other implementation being close to the standard EBX one.
	 *
	 * @param pId used to build the identifier of the the different element that compose the widget.
	 * @param pWriter the writer
	 * @param pCalendar the custom date initially set on the widget
	 * @throws IllegalArgumentException if the argument pWriter is null.
	 * @since 1.4.0
	 */
	public static void addEBXDateComponent(
		final String pId,
		final UIComponentWriter pWriter,
		final Calendar pCalendar) throws IllegalArgumentException
	{
		// USING_NON_PUBLIC_API

		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}

		String dayId = pId + "_day";
		String monthId = pId + "_month";
		String yearId = pId + "_year";

		String day = "";
		String month = "";
		String year = "";
		if (pCalendar != null)
		{
			day = pCalendar.get(Calendar.DAY_OF_MONTH) + "";
			month = pCalendar.get(Calendar.MONTH) + 1 + "";
			year = pCalendar.get(Calendar.YEAR) + "";
		}

		UIButtonSpecJSAction dateButton = new UIButtonSpecJSAction(
			Messages.get(UIUtils.class, Severity.INFO, "OpenCalendar"),
			"EBX_Form.openCalendar('" + pId + "_year','" + pId + "_month','" + pId + "_day','" + pId
				+ "');");
		dateButton.setButtonIcon(UIButtonIcon.CALENDAR);
		dateButton.setButtonLayout(UIButtonLayout.ICON_ONLY);
		dateButton.setRelief(UIButtonRelief.FLAT);

		pWriter.add("<span id=\"" + pId + "_dateInputs\" class=\"ebx_InputsDateContainer\">");
		pWriter.add(
			"<input type=\"text\" tabindex=\"1\" maxlength=\"2\" size=\"2\" style=\"width: 4ex;text-align: right;\""
				+ " value=\"" + day + "\" name=\"" + dayId + "\" id=\"" + dayId + "\"/>");
		pWriter.add(
			"/<input type=\"text\" tabindex=\"2\" maxlength=\"2\" size=\"2\" style=\"width: 4ex;text-align: right;\" value=\""
				+ month + "\" name=\"" + monthId + "\" id=\"" + monthId + "\" />");
		pWriter.add(
			"/<input type=\"text\" tabindex=\"3\" maxlength=\"4\" size=\"4\" style=\"width: 6ex;text-align: right;\" value=\""
				+ year + "\" name=\"" + yearId + "\" id=\"" + yearId + "\"/ >");
		pWriter.add("</span>");
		pWriter.addButtonJavaScript(dateButton);
	}

	/**
	 * <strong>Using non public API</strong><br>
	 * Add the standard EBX date Widget in a UIFormRow (must be). This method is deprecated because
	 * using EBX private API. It is not guarantee to work in the future. It must be replaced by an
	 * other implementation being close to the standard EBX one.
	 *
	 * @param pId used to build the identifier of the the different element that compose the widget.
	 * @param pWriter the writer
	 * @param pCalendar the custom date initially set on the widget
	 * @throws IllegalArgumentException if the argument pWriter is null.
	 * @deprecated Use {@link UIUtils#addEBXDateComponent(String, UIComponentWriter, Calendar)}
	 *             instead.
	 * @since 1.0.0
	 */
	@Deprecated
	public static void addEBXPrivateDateWidget(
		final String pId,
		final UIComponentWriter pWriter,
		final Calendar pCalendar) throws IllegalArgumentException
	{
		UIUtils.addEBXDateComponent(pId, pWriter, pCalendar);
	}

	/**
	 * Adds the getDate function to return the formated date of the ebx private date widget.
	 *
	 * @param pWriter the writer
	 * @return the name of the function
	 * @throws IllegalArgumentException if the argument pWriter is null.
	 * @since 1.0.0
	 */
	public static String addEBXPrivateDateWidget_GetDateJsFunction(final UIComponentWriter pWriter)
		throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}

		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + UIUtils.JS_FUNCNAME_GET_DATE + "(widgetId){");
		pWriter.addJS_cr("    var dayInput = document.getElementById(widgetId + '_day');");
		pWriter.addJS_cr("    var monthInput = document.getElementById(widgetId + '_month');");
		pWriter.addJS_cr("    var yearInput = document.getElementById(widgetId + '_year');");
		pWriter.addJS_cr("    ");
		pWriter.addJS_cr("    if (dayInput && monthInput && yearInput) {");
		pWriter.addJS_cr(
			"        return yearInput.value + '-' + monthInput.value + '-' + dayInput.value;");
		pWriter.addJS_cr("    } else {");
		pWriter.addJS_cr("        return '';");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		return UIUtils.JS_FUNCNAME_GET_DATE;
	}

	/**
	 * Add a JavaScript function to size an html element according to the workspace size.
	 *
	 * @param pWriter the writer
	 * @param pElementId id of the HTML tag to resize according to the workspace.
	 * @throws IllegalArgumentException if the argument pWriter or pElementId are null
	 * @since 1.0.0
	 */
	public static void addElementResizeToWorkspaceJS(
		final UIComponentWriter pWriter,
		final String pElementId) throws IllegalArgumentException
	{
		UIUtils.addElementResizeToWorkspaceJS(
			pWriter,
			pElementId,
			new Integer(0),
			new Integer(0),
			new Double(1),
			new Double(1));
	}

	/**
	 * Add a JavaScript function to size an html element according to the workspace size.
	 *
	 * @param pWriter the writer
	 * @param pElementId id of the HTML tag to resize according to the workspace.
	 * @param pHeightRatio the height ratio
	 * @param pWidthRatio the width ratio
	 * @throws IllegalArgumentException if the argument pWriter or pElementId are null
	 * @since 1.0.0
	 */
	public static void addElementResizeToWorkspaceJS(
		final UIComponentWriter pWriter,
		final String pElementId,
		final Double pHeightRatio,
		final Double pWidthRatio) throws IllegalArgumentException
	{
		UIUtils.addElementResizeToWorkspaceJS(
			pWriter,
			pElementId,
			new Integer(0),
			new Integer(0),
			pHeightRatio,
			pWidthRatio);
	}

	/**
	 * Add a JavaScript function to size an html element according to the workspace size.
	 *
	 * @param pWriter the writer
	 * @param pElementId id of the HTML tag to resize according to the workspace.
	 * @param pHeightOffset Add an offset to the height.
	 * @param pWidthOffset Add an offset to the width.
	 * @throws IllegalArgumentException if the argument pWriter or pElementId are null
	 * @since 1.0.0
	 */
	public static void addElementResizeToWorkspaceJS(
		final UIComponentWriter pWriter,
		final String pElementId,
		final Integer pHeightOffset,
		final Integer pWidthOffset) throws IllegalArgumentException
	{
		UIUtils.addElementResizeToWorkspaceJS(
			pWriter,
			pElementId,
			pHeightOffset,
			pWidthOffset,
			new Double(1),
			new Double(1));
	}

	/**
	 * Add a JavaScript function to size an html element according to the workspace size.
	 *
	 * @param pWriter the writer
	 * @param pElementId id of the HTML tag to resize according to the workspace.
	 * @param pHeightOffset Add an offset to the height.
	 * @param pWidthOffset Add an offset to the width.
	 * @param pHeightRatio The ratio of the workspace height (0.5 for half the height).
	 * @param pWidthRatio The ratio of the workspace width (0.5 for half the width).
	 * @throws IllegalArgumentException if the argument pWriter or pElementId are null
	 * @since 1.0.0
	 */
	public static void addElementResizeToWorkspaceJS(
		final UIComponentWriter pWriter,
		final String pElementId,
		final Integer pHeightOffset,
		final Integer pWidthOffset,
		final Double pHeightRatio,
		final Double pWidthRatio) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}
		if (pElementId == null)
		{
			throw new IllegalArgumentException("pElementId argument shall not be null");
		}

		String jsFunctionName = "sync_" + pElementId + "_ToWorkspace";
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + jsFunctionName + "(size) {");
		pWriter.addJS_cr("    var heightOffset = " + String.valueOf(pHeightOffset) + " || 0;");
		pWriter.addJS_cr("    var widthOffset = " + String.valueOf(pWidthOffset) + " || 0;");
		pWriter.addJS_cr("    var heightRatio = " + String.valueOf(pHeightRatio) + " || 1;");
		pWriter.addJS_cr("    var widthRatio = " + String.valueOf(pWidthRatio) + " || 1;");
		pWriter.addJS_cr("    var ele = document.getElementById('" + pElementId + "');");
		pWriter.addJS_cr("    if(ele) {");
		pWriter
			.addJS_cr("        ele.style.height = ((size.h * heightRatio) + heightOffset) + 'px';");
		pWriter.addJS_cr("        ele.style.width = ((size.w * widthRatio) + widthOffset) + 'px';");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
		pWriter.addJS_addResizeWorkspaceListener(jsFunctionName);
		pWriter.addJS_cr();
	}

	/**
	 * Add form rows for the sub-nodes of a parent with the parent label as first row.
	 *
	 * @param pWriter the writer
	 * @param pGroupNode the node to introspect to add its children as form rows
	 * @param pNodeInformationToExclude the node information to check to exclude a node.
	 * @throws IllegalArgumentException if the argument pWriter or pNode are null.
	 * @since 1.0.0
	 * @see #addTitleFormRow
	 * @see #addSubNodesFormRow
	 */
	public static void addGroupFormRows(
		final UIFormPaneWriter pWriter,
		final SchemaNode pGroupNode,
		final String pNodeInformationToExclude) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}
		if (pGroupNode == null)
		{
			throw new IllegalArgumentException("pGroupNode argument shall not be null");
		}

		Locale locale = pWriter.getLocale();
		String title = pGroupNode.getLabel(locale);
		UIUtils.addTitleFormRow(pWriter, title);
		UIUtils.addSubNodesFormRow(pWriter, pGroupNode, pNodeInformationToExclude);
	}

	/**
	 * Add a script tag to import a javascript file.
	 *
	 * @param pWriter the writer
	 * @param pLocation The location of the javascript library from /www/{@literal <locale>}
	 *            /jscripts
	 * @throws IllegalArgumentException if the argument pWriter is null
	 * @since 1.0.0
	 */
	public static void addJsLibrary(final UIComponentWriter pWriter, final String pLocation)
		throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}

		String location = pWriter.getURLForResource(ResourceType.JSCRIPT, pLocation);
		pWriter.add("<script src=\"" + location + "\" type=\"text/javascript\"></script>");
	}

	/**
	 * Add a script tag to import a javascript file.
	 *
	 * @param pWriter the writer
	 * @param pLocation The location of the javascript library from /www/{@literal <locale>}
	 *            /jscripts
	 * @param pModuleName The module name
	 * @throws IllegalArgumentException if the argument pWriter is null
	 * @since 1.0.0
	 */
	public static void addJsLibrary(
		final UIComponentWriter pWriter,
		final String pLocation,
		final String pModuleName) throws IllegalArgumentException
	{
		// TODO to be deprecated to use the native pWriter#addJavaScriptDependency
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}

		String location = pWriter
			.getURLForResource(pModuleName, ResourceType.JSCRIPT, pLocation, pWriter.getLocale());
		pWriter.add("<script src=\"" + location + "\" type=\"text/javascript\"></script>");
	}

	/**
	 * <strong>Using non public API</strong><br>
	 * Add the label or name of the record with a pop-up icon to open a record.
	 *
	 * @param pWriter the writer
	 * @param pRecord The record to open in pop-up.
	 * @throws IllegalArgumentException if the argument pWriter or pRecord are null
	 * @since 1.0.0
	 */
	public static void addPopUpLink(final UIComponentWriter pWriter, final Adaptation pRecord)
		throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}
		if (pRecord == null)
		{
			throw new IllegalArgumentException("pRecord argument shall not be null");
		}

		String label = pRecord.getLabel(pWriter.getLocale());
		UIUtils.addPopUpLink(pWriter, pRecord, label);
	}

	/**
	 * <strong>Using non public API</strong><br>
	 * <br>
	 * Add a label with a pop-up icon to open a record.
	 *
	 * @param pWriter the writer
	 * @param pRecord The record to open in pop-up.
	 * @param pLabel The label to include before the pop-up icon.
	 * @throws IllegalArgumentException if the argument pWriter or pRecord are null
	 * @since 1.0.0
	 */
	public static void addPopUpLink(
		final UIComponentWriter pWriter,
		final Adaptation pRecord,
		final String pLabel) throws IllegalArgumentException
	{
		// USING_NON_PUBLIC_API

		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}
		if (pRecord == null)
		{
			throw new IllegalArgumentException("pRecord argument shall not be null");
		}

		// Insert the label
		if (pLabel != null)
		{
			pWriter.add("<span>" + pLabel + " </span>");
		}
		if (pRecord != null)
		{
			String jsCommand = UIUtils.getPopUpLinkCommand(pWriter, pRecord);
			// Insert the link
			pWriter.add("<a href=\"#\" onclick=\"" + jsCommand + "\">");
			// Insert the preview icon
			pWriter.add("<span class=\"ebx_Input\">");
			pWriter.add(
				"<span class=\"ebx_FlatButton ebx_IconButton ebx_Open\" style=\"text-align:center;vertical-align:middle;\">");
			pWriter.add("<span class=\"ebx_Icon\"></span>");
			pWriter.add("</span>");
			pWriter.add("</span>");
			pWriter.add("</a>");
		}
	}

	/**
	 * Add form rows for the sub-nodes of a parent.
	 *
	 * @param pWriter the writer
	 * @param pGroupNode the node to introspect to add its children as form rows.
	 * @param pNodeInformationToExclude the node information to check to exclude a node.
	 * @throws IllegalArgumentException if the arguments pWriter or pNode are null.
	 * @since 1.0.0
	 */
	public static void addSubNodesFormRow(
		final UIFormPaneWriter pWriter,
		final SchemaNode pGroupNode,
		final String pNodeInformationToExclude) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}
		if (pGroupNode == null)
		{
			throw new IllegalArgumentException("pGroupNode argument shall not be null");
		}

		SchemaNode[] children = pGroupNode.getNodeChildren();
		for (SchemaNode child : children)
		{
			SchemaNodeInformation nodeInformation = child.getInformation();
			if (nodeInformation != null && pNodeInformationToExclude != null
				&& !pNodeInformationToExclude.equals(""))
			{
				String information = nodeInformation.getInformation();
				if (information.contains(pNodeInformationToExclude))
				{
					continue;
				}
			}

			if (child.isTerminalValue())
			{
				pWriter.addFormRow(Path.SELF.add(child.getPathInAdaptation()));
			}
			else
			{
				SchemaNode[] childChildren = child.getNodeChildren();
				if (childChildren == null || childChildren.length == 0)
				{
					continue;
				}

				pWriter.startFormGroup(Path.SELF.add(child.getPathInAdaptation()));
				UIUtils.addSubNodesFormRow(pWriter, child, pNodeInformationToExclude);
				pWriter.endFormGroup();
			}
		}
	}

	/**
	 * Add a tile in the UI.
	 *
	 * @param pWriter the writer
	 * @param pLabel The label. Displayed at the top of the tile.
	 * @param pValue The value. Displayed at the center of the tile.
	 * @param pInfo The additional information. Displayed at the bottom of the tile.
	 * @throws IllegalArgumentException if the argument pWriter is null.
	 * @since 1.2.0
	 */
	public static void addTile(
		final UIComponentWriter pWriter,
		final String pLabel,
		final String pValue,
		final String pInfo) throws IllegalArgumentException
	{
		UIUtils.addTile(pWriter, pLabel, pValue, pInfo, 0, 0, null);
	}

	/**
	 * Add a tile in the UI.
	 *
	 * @param pWriter the writer
	 * @param pLabel The label. Displayed at the top of the tile.
	 * @param pValue The value. Displayed at the center of the tile.
	 * @param pInfo The additional information. Displayed at the bottom of the tile.
	 * @param pWidth The width in px of the tile (Default is 90).
	 * @param pHeight The height in px of the tile (Default is 90).
	 * @throws IllegalArgumentException if the argument pWriter is null.
	 * @since 1.0.0
	 */
	public static void addTile(
		final UIComponentWriter pWriter,
		final String pLabel,
		final String pValue,
		final String pInfo,
		final int pWidth,
		final int pHeight) throws IllegalArgumentException
	{
		UIUtils.addTile(pWriter, pLabel, pValue, pInfo, pWidth, pHeight, null);
	}

	/**
	 * Add a tile in the UI.
	 *
	 * @param pWriter the writer
	 * @param pLabel The label. Displayed at the top of the tile.
	 * @param pValue The value. Displayed at the center of the tile.
	 * @param pInfo The additional information. Displayed at the bottom of the tile.
	 * @param pWidth The width in px of the tile (Default is 90).
	 * @param pHeight The height in px of the tile (Default is 90).
	 * @param pBackgroundColor the background color in hexa (Default is #FAFAFA).
	 * @throws IllegalArgumentException if the argument pWriter is null.
	 * @since 1.2.0
	 */
	public static void addTile(
		final UIComponentWriter pWriter,
		String pLabel,
		String pValue,
		String pInfo,
		int pWidth,
		int pHeight,
		String pBackgroundColor) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}

		if (pLabel == null)
		{
			pLabel = "";
		}
		if (pValue == null)
		{
			pValue = "";
		}
		if (pInfo == null)
		{
			pInfo = "";
		}
		if (pBackgroundColor == null)
		{
			pBackgroundColor = "#FAFAFA";
		}
		if (pWidth == 0)
		{
			pWidth = 90;
		}
		if (pHeight == 0)
		{
			pHeight = 90;
		}

		String containerStyle = "background-color:" + pBackgroundColor
			+ ";box-sizing: border-box;border: 1px solid #EEEEEE;text-align:center;font-size:1em;position:relative;border-radius:4px;";
		containerStyle += "width:" + String.valueOf(pWidth) + "px;";
		containerStyle += "height:" + String.valueOf(pHeight) + "px;";

		pWriter.add("<div style=\"" + containerStyle + "\">");

		// Label ----
		String labelStyle = "margin:0;padding:5px;font-size:1em;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;";
		pWriter.add("<p style=\"" + labelStyle + "\" title=\"" + pLabel + "\">");
		// TODO Label should be escaped to avoid forbidden html string
		pWriter.add(pLabel);
		pWriter.add("</p>");

		// Value ----
		String valueStyle = "margin:0;padding:5px;font-size:1.4em;font-weight:bold;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;";
		Integer valueHeight = new Integer(pHeight - 25 - 22 - 10 - 2);
		valueStyle += "line-height:" + String.valueOf(valueHeight) + "px;";
		pWriter.add("<p style=\"" + valueStyle + "\" title=\"" + pValue + "\">");
		// TODO Value should be escaped to avoid forbidden html string
		pWriter.add(pValue);
		pWriter.add("</p>");

		// Info -----
		String infoStyle = "margin:0;padding:5px;text-align:right;font-size:0.8em;font-style:italic;position:absolute;bottom:0;right:0;white-space:nowrap;";
		pWriter.add("<p style=\"" + infoStyle + "\">");
		// TODO Info should be escaped to avoid forbidden html string
		pWriter.add(pInfo);
		pWriter.add("</p>");

		pWriter.add("</div>");
	}

	/**
	 * Add a tile in the UI.
	 *
	 * @param pWriter the writer
	 * @param pLabel The label. Displayed at the top of the tile.
	 * @param pValue The value. Displayed at the center of the tile.
	 * @param pInfo The additional information. Displayed at the bottom of the tile.
	 * @param pBackgroundColor the background color in hexa (Default is #FAFAFA).
	 * @throws IllegalArgumentException if the argument pWriter is null.
	 * @since 1.2.0
	 */
	public static void addTile(
		final UIComponentWriter pWriter,
		final String pLabel,
		final String pValue,
		final String pInfo,
		final String pBackgroundColor) throws IllegalArgumentException
	{
		UIUtils.addTile(pWriter, pLabel, pValue, pInfo, 0, 0, pBackgroundColor);
	}

	/**
	 * Insert a new form row containing a title.
	 *
	 * @param pWriter the writer
	 * @param pTitle The title to insert.
	 * @throws IllegalArgumentException if the argument pWriter is null
	 * @since 1.0.0
	 */
	public static void addTitleFormRow(final UIFormPaneWriter pWriter, final String pTitle)
		throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}

		pWriter.startFormRow(new UIFormLabelSpec(""));
		if (pTitle != null)
		{
			pWriter.add("<h3>" + pTitle + "</h3>");
		}
		pWriter.endFormRow();
	}

	/**
	 * Gets the JavaScript instruction calling the getDate function of the EBX private date widget.
	 *
	 * @param pId the id of the targeted date widget
	 * @return the JavaScript instruction
	 * @throws IllegalArgumentException if the argument is null
	 * @since 1.0.0
	 */
	public static String getEBXPrivateDateWidget_GetDateInstruction(final String pId)
		throws IllegalArgumentException
	{
		if (pId == null)
		{
			throw new IllegalArgumentException("pId argument shall not be null");
		}
		return UIUtils.JS_FUNCNAME_GET_DATE + "('" + pId + "')";
	}

	/**
	 * Gets the JavaScript command to open a record in a pop-up.
	 *
	 * @param pWriter the writer
	 * @param pRecord the record
	 * @return the JavaScript command to open the pop up
	 * @throws IllegalArgumentException if one of the argument is null
	 * @since 1.0.0
	 */
	public static String getPopUpLinkCommand(
		final UIComponentWriter pWriter,
		final Adaptation pRecord) throws IllegalArgumentException
	{
		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}
		if (pRecord == null)
		{
			throw new IllegalArgumentException("pRecord argument shall not be null");
		}

		UIHttpManagerComponent managerComponent = pWriter.createWebComponentForSubSession();
		managerComponent.selectInstanceOrOccurrence(pRecord);
		UIButtonSpecJSAction buttonSpec = pWriter
			.buildButtonPreview(managerComponent.getURIWithParameters());
		return buttonSpec.getJavaScriptCommand().replace("\"", "'");
	}

	/**
	 * Modifies style properties of an html element. The method has to be called after the
	 * definition of the html element in the page.
	 *
	 * @param writer the writer of the html page
	 * @param idHtmlElement id of the html element
	 * @param properties map of properties (name and value) to change on the html element; the
	 *            property name must be a html recognized property of attribute style
	 * @throws IllegalArgumentException when writer or idHtmlElement or properties is null
	 * @author LLU
	 * @since 1.4.0
	 */
	public static void modifyHtmlElement(
		final UIJavaScriptWriter writer,
		final String idHtmlElement,
		final Map<String, String> properties) throws IllegalArgumentException
	{
		if (writer == null)
		{
			throw new IllegalArgumentException("writer shall not be null");
		}

		if (idHtmlElement == null || idHtmlElement.length() == 0)
		{
			throw new IllegalArgumentException("idHtmlElement shall not be null");
		}

		if (properties == null || properties.isEmpty())
		{
			throw new IllegalArgumentException("styleProperties shall not be null");
		}

		writer.addJS_cr();
		writer.addJS_cr("(function(){");
		writer.addJS_cr("   var element = document.getElementById('" + idHtmlElement + "');");
		writer.addJS_cr("   if (element == null) {} else {");
		for (Map.Entry<String, String> entry : properties.entrySet())
		{
			String propertyName = entry.getKey();
			String value = entry.getValue();

			writer.addJS_cr("      element.style." + propertyName + " = \"" + value + "\";");
		}
		writer.addJS_cr("   }");
		writer.addJS_cr("})();");
	}

	/**
	 * <strong>Using non public API</strong><br>
	 * <br>
	 * Modifies style properties of the Insight graph tooltip. <br>
	 * Default values applied :
	 * <ul>
	 * <li>backgroundColor : #F0F0D0</li>
	 * <li>borderColor : #C0C0C0</li>
	 * <li>padding : #F0F0D0</li>
	 * </ul>
	 * <b>WARNING</b><br>
	 * There is only one tooltip element on a html page for insight, even if there are several
	 * graphs. Only one call per page to this method is necessary. <br>
	 * <br>
	 *
	 * @param writer the writer of the html page
	 * @throws IllegalArgumentException when writer is null
	 * @author LLU
	 * @see UIUtils#modifyInsightChartTooltip(UIJavaScriptWriter, Map)
	 * @since 1.4.0
	 */
	public static void modifyInsightChartTooltip(final UIJavaScriptWriter writer)
		throws IllegalArgumentException
	{
		// USING_NON_PUBLIC_API

		Map<String, String> styleProperties = new HashMap<String, String>();
		styleProperties.put("backgroundColor", "#F0F0D0");
		styleProperties.put("borderColor", "#C0C0C0");
		styleProperties.put("padding", "5px 10px");
		UIUtils.modifyInsightChartTooltip(writer, styleProperties);
	}

	/**
	 * <strong>Using non public API</strong><br>
	 * <br>
	 * Modifies style properties of the Insight graph tooltip. <br>
	 * <br>
	 * <b>WARNING</b><br>
	 * There is only one tooltip element on a html page for insight, even if there are several
	 * graphs. Only one call per page to this method is necessary.
	 *
	 * @param writer the writer of the html page
	 * @param properties map of properties (name and value) to change on the tooltip; the property
	 *            name must be a html recognized property of attribute style
	 * @throws IllegalArgumentException when writer or properties is null
	 * @author LLU
	 * @see UIUtils#modifyHtmlElement(UIJavaScriptWriter, String, Map)
	 * @since 1.4.0
	 */
	public static void modifyInsightChartTooltip(
		final UIJavaScriptWriter writer,
		final Map<String, String> properties) throws IllegalArgumentException
	{
		// USING_NON_PUBLIC_API

		UIUtils.modifyHtmlElement(writer, "tooltip", properties);
	}

	/**
	 * <strong>Using non public API</strong><br>
	 * <br>
	 * Modifies style properties of the Insight graph tooltip. <br>
	 * <br>
	 * <b>WARNING</b><br>
	 * There is only one tooltip element on a html page for insight, even if there are several
	 * graphs. Only one call per page to this method is necessary.
	 *
	 * @param writer the writer of the html page
	 * @param backgroundColor the backgroundColor as the value accepted by html (example : green or
	 *            #123456)
	 * @param borderColor the borderColor as the value accepted by html (example : green or #123456)
	 * @throws IllegalArgumentException when writer or both backgroundColor and borderColor are null
	 * @see UIUtils#modifyInsightChartTooltip(UIJavaScriptWriter, Map)
	 * @author LLU
	 * @since 1.4.0
	 */
	public static void modifyInsightChartTooltip(
		final UIJavaScriptWriter writer,
		final String backgroundColor,
		final String borderColor) throws IllegalArgumentException
	{
		// USING_NON_PUBLIC_API

		boolean argumentFilled = false;
		Map<String, String> styleProperties = new HashMap<String, String>();
		if (backgroundColor != null && backgroundColor.trim().length() > 0)
		{
			styleProperties.put("backgroundColor", backgroundColor);
			argumentFilled = true;
		}
		if (borderColor != null && borderColor.trim().length() > 0)
		{
			styleProperties.put("borderColor", borderColor);
			argumentFilled = true;
		}
		if (!argumentFilled)
		{
			throw new IllegalArgumentException(
				"At least one of backgroundColor or borderColor shall not be null");
		}
		UIUtils.modifyInsightChartTooltip(writer, styleProperties);
	}

	/**
	 * <strong>Using non public API</strong><br>
	 * Standardize the width of field labels in different table form row. In a single page (or a
	 * single tab), all field label will be aligned whatever they are in different tableFormRow.
	 * Tabs are considered independant by default, to override this behavior, check the method
	 * {@link #standardizeFieldLabelWidth(UIComponentWriter, boolean)
	 * standardizeFieldLabelWidth(UIFormPaneWriter, boolean)}.<br>
	 * <br>
	 * This should be used in a writePane of a custom layout. For a layout with multiple tabs, only
	 * a single call in one of the writePane is necessary.<br>
	 * <br>
	 * This function is using ebx internal terms not provided by the public API. Further EBX version
	 * may break it.
	 *
	 * @param pWriter the writer
	 * @throws IllegalArgumentException if the argument pWriter is null.
	 * @since 1.2.0
	 */
	public static void standardizeFieldLabelWidth(final UIComponentWriter pWriter)
		throws IllegalArgumentException
	{
		UIUtils.standardizeFieldLabelWidth(pWriter, true);
	}

	/**
	 * <strong>Using non public API</strong><br>
	 * <br>
	 * Standardize the width of field labels in different table form row. In a single page (or a
	 * single tab), all field label will be aligned whatever they are in different tableFormRow.<br>
	 * <br>
	 * This should be used in a writePane of a custom layout. For a layout with multiple tabs, only
	 * a single call in one of the writePane is necessary.<br>
	 * <br>
	 * This function is using ebx internal terms not provided by the public API. Further EBX version
	 * may break it.
	 *
	 * @param pWriter the writer
	 * @param pIndependantTab if true, field label width will be standardize independantly in each
	 *            tab, if false the same width will be applied in all tabs.
	 * @throws IllegalArgumentException if the argument pWriter is null.
	 * @since 1.2.0
	 */
	public static void standardizeFieldLabelWidth(
		final UIComponentWriter pWriter,
		final boolean pIndependantTab) throws IllegalArgumentException
	{
		// USING_NON_PUBLIC_API

		if (pWriter == null)
		{
			throw new IllegalArgumentException("pWriter argument shall not be null");
		}

		pWriter.addJS_cr();
		pWriter.addJS_cr("function setLabelWidth(id) {");
		pWriter.addJS_cr("    var query = 'td.ebx_Label';");
		pWriter.addJS_cr("    if (id) {");
		pWriter.addJS_cr("        query = '#' + id + ' ' + query;");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("    var tdLabelList = document.querySelectorAll(query);");
		pWriter.addJS_cr("    var maxWidth = 0;");
		pWriter.addJS_cr("    for (var i = 0; i < tdLabelList.length; i++) {");
		pWriter.addJS_cr("        var tdLabel = tdLabelList[i];");
		pWriter.addJS_cr("        var width = tdLabel.clientWidth;");
		pWriter.addJS_cr("        if (width > maxWidth) {");
		pWriter.addJS_cr("            maxWidth = width;");
		pWriter.addJS_cr("        }");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("    var defaultPadding = 17;");
		pWriter.addJS_cr("    for (var i = 0; i < tdLabelList.length; i++) {");
		pWriter.addJS_cr("        var tdLabel = tdLabelList[i];");
		pWriter.addJS_cr("        tdLabel.style.minWidth = (maxWidth - defaultPadding) + 'px';");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
		pWriter.addJS_cr("(function () {");
		if (pIndependantTab)
		{
			pWriter.addJS_cr(
				"    var tabs = document.querySelectorAll('.ebx_WorkspaceFormTabContent');");
			pWriter.addJS_cr("    if (tabs.length > 0) {");
			pWriter.addJS_cr("        for (var i = 0; i < tabs.length; i++) {");
			pWriter.addJS_cr("            var tab = tabs[i];");
			pWriter.addJS_cr("            setLabelWidth(tab.id);");
			pWriter.addJS_cr("        }");
			pWriter.addJS_cr("    } else {");
			pWriter.addJS_cr("        setLabelWidth();");
			pWriter.addJS_cr("    }");
		}
		else
		{
			pWriter.addJS_cr("    setLabelWidth();");
		}
		pWriter.addJS_cr("})();");
		pWriter.addJS_cr();
	}

	/**
	 * Introspect the schema for the addColumnsLayout methods.
	 *
	 * @param pNodes the nodes
	 * @param pNodeInformationToExclude the node information to exclude
	 * @return the list
	 * @since 1.2.0
	 */
	private static List<SchemaNode> addColumnsLayout_introspectSchema(
		final List<SchemaNode> pNodes,
		final String pNodeInformationToExclude)
	{
		List<SchemaNode> keptNodes = new ArrayList<SchemaNode>();
		if (pNodes == null || pNodes.isEmpty())
		{
			return keptNodes;
		}
		for (SchemaNode node : pNodes)
		{
			SchemaNodeInformation nodeInformation = node.getInformation();
			if (nodeInformation != null && pNodeInformationToExclude != null
				&& !pNodeInformationToExclude.equals(""))
			{
				String information = nodeInformation.getInformation();
				if (information.contains(pNodeInformationToExclude))
				{
					continue;
				}
			}

			if (node.isTerminalValue())
			{
				keptNodes.add(node);
			}
			else
			{
				List<SchemaNode> children = Arrays.asList(node.getNodeChildren());
				keptNodes.addAll(
					UIUtils.addColumnsLayout_introspectSchema(children, pNodeInformationToExclude));
			}
		}
		return keptNodes;
	}

	private UIUtils()
	{
	}
}
