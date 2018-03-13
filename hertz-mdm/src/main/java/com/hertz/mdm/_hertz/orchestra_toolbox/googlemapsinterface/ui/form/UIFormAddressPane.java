package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.ui.form;

import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.PresalesToolbox;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMaps;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressFieldMapping;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressValue;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsOptions;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.message.Messages;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.UIFormLabelSpec;
import com.orchestranetworks.ui.form.UIFormContext;
import com.orchestranetworks.ui.form.UIFormPane;
import com.orchestranetworks.ui.form.UIFormPaneWriter;

/**
 * <strong>Using non public API</strong><br>
 * <br>
 * Provides a generic UIFormPane to integrate the address form row of a record with a Google Maps
 * and related features.<br>
 * <br>
 * <strong>Warning</strong>: The width of the panel and the Google Maps autocomplete field try to be
 * set automatically based on other EBX fields. <strong>It does not work properly for inherited data
 * set.</strong> You can set the width manually with
 * {@link UIFormAddressPane#setFieldPanelWidth(Integer)} and
 * {@link UIFormAddressPane#setAutocompleteFieldWidth(Integer)}.
 *
 * @author ATI
 * @since 1.4.0
 */
public class UIFormAddressPane implements UIFormPane
{
	private final GoogleMapsAddressFieldMapping googleMapsMapping;
	private GoogleMapsOptions googleMapsOptions;
	private String hideMapTrackingInfo;
	private GoogleMaps googleMaps;
	private final String mapId = PresalesToolbox.PREFIX + "LocationMap";
	private Integer fieldPanelWidth;
	private Integer autocompleteFieldWidth;

	/**
	 * Instanciate a new UIFormAddressPane.
	 *
	 * @param pGoogleMapsMapping the Google Maps mapping
	 * @throws IllegalArgumentException if the mapping is null
	 * @author ATI
	 * @since 1.4.0
	 */
	public UIFormAddressPane(GoogleMapsAddressFieldMapping pGoogleMapsMapping)
		throws IllegalArgumentException
	{
		if (pGoogleMapsMapping == null)
		{
			throw new IllegalArgumentException("The Google Maps mapping shall not be null");
		}
		this.googleMapsMapping = pGoogleMapsMapping;
		this.googleMapsOptions = new GoogleMapsOptions();
		this.hideMapTrackingInfo = null;
	}

	/**
	 * Instanciate a new UIFormAddressPane.
	 *
	 * @param pGoogleMapsMapping the Google Maps mapping
	 * @param pGoogleMapsOptions the Google Maps options
	 * @throws IllegalArgumentException if the mapping is null
	 * @author ATI
	 * @since 1.4.0
	 */
	public UIFormAddressPane(
		final GoogleMapsAddressFieldMapping pGoogleMapsMapping,
		final GoogleMapsOptions pGoogleMapsOptions) throws IllegalArgumentException
	{
		if (pGoogleMapsMapping == null)
		{
			throw new IllegalArgumentException("The Google Maps mapping shall not be null");
		}
		this.googleMapsMapping = pGoogleMapsMapping;
		this.googleMapsOptions = pGoogleMapsOptions;
		this.hideMapTrackingInfo = null;
	}

	/**
	 * Instanciate a new UIFormAddressPane.
	 *
	 * @param pGoogleMapsMapping the Google Maps mapping
	 * @param pGoogleMapsOptions the Google Maps options
	 * @param pHideMapTrackingInfo the tracking info to check to hide the map and map-related
	 *            features
	 * @throws IllegalArgumentException if the mapping is null
	 * @author ATI
	 * @since 1.4.0
	 */
	public UIFormAddressPane(
		final GoogleMapsAddressFieldMapping pGoogleMapsMapping,
		final GoogleMapsOptions pGoogleMapsOptions,
		final String pHideMapTrackingInfo) throws IllegalArgumentException
	{
		if (pGoogleMapsMapping == null)
		{
			throw new IllegalArgumentException("The Google Maps mapping shall not be null");
		}
		this.googleMapsMapping = pGoogleMapsMapping;
		this.googleMapsOptions = pGoogleMapsOptions;
		this.hideMapTrackingInfo = pHideMapTrackingInfo;
	}

	/**
	 * Instanciate a new UIFormAddressPane.
	 *
	 * @param pGoogleMapsMapping the Google Maps mapping
	 * @param pHideMapTrackingInfo the tracking info to check to hide the map and map-related
	 *            features
	 * @throws IllegalArgumentException if the mapping is null
	 * @author ATI
	 * @since 1.4.0
	 */
	public UIFormAddressPane(
		final GoogleMapsAddressFieldMapping pGoogleMapsMapping,
		final String pHideMapTrackingInfo) throws IllegalArgumentException
	{
		if (pGoogleMapsMapping == null)
		{
			throw new IllegalArgumentException("The Google Maps mapping shall not be null");
		}
		this.googleMapsMapping = pGoogleMapsMapping;
		this.googleMapsOptions = new GoogleMapsOptions();
		this.hideMapTrackingInfo = pHideMapTrackingInfo;
	}

	/**
	 * Get the width of the Google Maps autocomplete field.
	 *
	 * @return the width.
	 * @author ATI
	 * @since 1.4.0
	 */
	public Integer getAutocompleteFieldWidth()
	{
		return this.autocompleteFieldWidth;
	}

	/**
	 * Get the width of the fields panel.
	 *
	 * @return the width.
	 * @author ATI
	 * @since 1.4.0
	 */
	public Integer getFieldPanelWidth()
	{
		return this.fieldPanelWidth;
	}

	/**
	 * Get the Google Maps mapping.
	 *
	 * @return the mapping of the map.
	 * @author ATI
	 * @since 1.4.0
	 */
	public GoogleMapsAddressFieldMapping getGoogleMapsMapping()
	{
		return this.googleMapsMapping;
	}

	/**
	 * Get the Google Maps options.
	 *
	 * @return the options of the map.
	 * @author ATI
	 * @since 1.4.0
	 */
	public GoogleMapsOptions getGoogleMapsOptions()
	{
		return this.googleMapsOptions;
	}

	/**
	 * Get the tracking to be checked to hide the map and other map-related features.
	 *
	 * @return the tracking info.
	 * @author ATI
	 * @since 1.4.0
	 */
	public String getHideMapTrackingInfo()
	{
		return this.hideMapTrackingInfo;
	}

	/**
	 * Set the width of the Google Maps autocomplete field.
	 *
	 * @param autocompleteFieldWidth the width to set.
	 * @author ATI
	 * @since 1.4.0
	 */
	public void setAutocompleteFieldWidth(final Integer autocompleteFieldWidth)
	{
		this.autocompleteFieldWidth = autocompleteFieldWidth;
	}

	/**
	 * Set the width of the fields panel.
	 *
	 * @param fieldPanelWidth the width to set.
	 * @author ATI
	 * @since 1.4.0
	 */
	public void setFieldPanelWidth(final Integer fieldPanelWidth)
	{
		this.fieldPanelWidth = fieldPanelWidth;
	}

	/**
	 * Set the Google Maps options.
	 *
	 * @param pGoogleMapsOptions the Google Maps options
	 * @author ATI
	 * @since 1.4.0
	 */
	public void setGoogleMapsOptions(final GoogleMapsOptions pGoogleMapsOptions)
	{
		this.googleMapsOptions = pGoogleMapsOptions;
	}

	/**
	 * Set the tracking to check to hide the map and other map-related features.
	 *
	 * @param pHideMapTrackingInfo the tracking info to check
	 * @author ATI
	 * @since 1.4.0
	 */
	public void setHideMapTrackingInfo(final String pHideMapTrackingInfo)
	{
		this.hideMapTrackingInfo = pHideMapTrackingInfo;
	}

	@Override
	public void writePane(final UIFormPaneWriter pWriter, final UIFormContext pContext)
	{
		boolean withinMaps = false;
		if (this.hideMapTrackingInfo != null)
		{
			String trackingInfo = pContext.getSession().getTrackingInfo();
			if (trackingInfo != null && trackingInfo.equals(this.hideMapTrackingInfo))
			{
				withinMaps = true;
			}
		}

		Adaptation record = pContext.getCurrentRecord();
		boolean readWrite = false;
		if (record != null)
		{
			readWrite = pContext.getSession()
				.getPermissions()
				.getAdaptationAccessPermission(record)
				.isReadWrite();
		}
		else
		{
			AdaptationTable table = pContext.getCurrentTable();
			readWrite = pContext.getSession()
				.getPermissions()
				.getTableActionPermissionToCreateRootOccurrence(table)
				.isEnabled();
		}

		// Instantiate a new GoogleMaps object
		this.googleMaps = new GoogleMaps(this.googleMapsMapping, this.googleMapsOptions);

		pWriter.add("<div style=\"position:relative; width:100%; height:100%;\">");

		// Map
		if (!withinMaps)
		{
			this.insertMap(pWriter, record);
		}

		// Fields
		this.insertFieldInputsPanel(pWriter, readWrite, withinMaps);

		pWriter.add("</div>");
	}

	/**
	 * Insert the form rows from the Google Maps mapping.
	 *
	 * @param pWriter the writer
	 * @author ATI
	 * @since 1.4.0
	 */
	private void insertAddressNodeFormRow(final UIFormPaneWriter pWriter)
	{
		if (this.googleMapsMapping == null)
		{
			return;
		}
		Path numberPath = this.googleMapsMapping.getNumberPath();
		if (numberPath != null)
		{
			pWriter.addFormRow(numberPath);
		}
		Path streetPath = this.googleMapsMapping.getStreetPath();
		if (streetPath != null)
		{
			pWriter.addFormRow(streetPath);
		}
		Path zipCodePath = this.googleMapsMapping.getZipcodePath();
		if (zipCodePath != null)
		{
			pWriter.addFormRow(zipCodePath);
		}
		Path cityPath = this.googleMapsMapping.getCityPath();
		if (cityPath != null)
		{
			pWriter.addFormRow(cityPath);
		}
		Path regionPath = this.googleMapsMapping.getRegionPath();
		if (regionPath != null)
		{
			pWriter.addFormRow(regionPath);
		}
		Path countryPath = this.googleMapsMapping.getCountryPath();
		if (countryPath != null)
		{
			pWriter.addFormRow(countryPath);
		}
		Path latitudePath = this.googleMapsMapping.getLatitudePath();
		if (latitudePath != null)
		{
			pWriter.addFormRow(latitudePath);
		}
		Path longitudePath = this.googleMapsMapping.getLongitudePath();
		if (longitudePath != null)
		{
			pWriter.addFormRow(longitudePath);
		}
	}

	/**
	 * Insert the panel with the Google Maps autocomplete field, the address form rows and the
	 * standardize button.
	 *
	 * @param pWriter the writer
	 * @param pReadWrite true if in read write permission
	 * @param pWithinMaps true if displayed within a map (ie. do not display some features)
	 * @author ATI
	 * @since 1.4.0
	 */
	private void insertFieldInputsPanel(
		final UIFormPaneWriter pWriter,
		final boolean pReadWrite,
		final boolean pWithinMaps)
	{
		String fieldInputsPanelId = PresalesToolbox.PREFIX + "FieldInputsPanel";
		String autocompleteFieldId = PresalesToolbox.PREFIX + "LocationAutocompleteField";

		int panelWidth = 575;
		if (this.fieldPanelWidth != null)
		{
			panelWidth = this.fieldPanelWidth.intValue();
		}
		String containerStyle = "position:absolute;top:15px;left:15px;background:white;padding:10px;width:"
			+ panelWidth + "px";

		if (!pWithinMaps)
		{
			pWriter.add("<div id=\"" + fieldInputsPanelId + "\" style=\"" + containerStyle + "\">");
		}

		pWriter.startTableFormRow();

		if (pReadWrite && !pWithinMaps)
		{
			pWriter.startFormRow(new UIFormLabelSpec(""));
			String placeholder = Messages
				.get(this.getClass(), pWriter.getLocale(), "form.address.autocomplete.placeholder");

			int fieldWidth = 250;
			if (this.autocompleteFieldWidth != null)
			{
				fieldWidth = this.autocompleteFieldWidth.intValue();
			}
			// Insert the Google Maps Autocomplete field
			this.googleMaps.insertAutocompleteAddressField(
				pWriter,
				placeholder,
				"width:" + fieldWidth + "px;",
				this.mapId,
				autocompleteFieldId);

			pWriter.endFormRow();
		}

		this.insertAddressNodeFormRow(pWriter);

		if (pReadWrite && !pWithinMaps)
		{
			pWriter.startFormRow(new UIFormLabelSpec(""));
			UserMessage standardizeButtonLabel = Messages
				.getInfo(this.getClass(), "form.address.standardize.label");

			// Insert the button to standardize addresses with Google Maps
			this.googleMaps.insertStandardizeAddressButton(
				pWriter,
				standardizeButtonLabel,
				autocompleteFieldId);

			pWriter.endFormRow();
		}

		pWriter.endTableFormRow();

		if (!pWithinMaps)
		{
			pWriter.add("</div>");
		}

		this.insertJSFunction(pWriter, autocompleteFieldId, fieldInputsPanelId);
	}

	/**
	 * <strong>Using non public API</strong><br>
	 * <br>
	 * Insert the javascript functions, such as setting the autocomplete field the same size as
	 * other EBX input.
	 *
	 * @param pWriter the writer.
	 * @param pAutocompleteFieldId the id of the autocomplete field.
	 * @param pFieldInputsPanelId the id of the field input panel.
	 * @author ATI
	 * @since 1.4.0
	 */
	private void insertJSFunction(
		final UIFormPaneWriter pWriter,
		final String pAutocompleteFieldId,
		final String pFieldInputsPanelId)
	{
		// USING_NON_PUBLIC_API

		pWriter.addJS_cr();
		pWriter.addJS_cr("(function() {");
		pWriter.addJS_cr(
			"    var inputReference = document.querySelector('#" + pFieldInputsPanelId
				+ " > table > tbody > tr.ebx_ComplexField > td.ebx_Input > div.ebx_OverwrittenValue > div.ebx_InputFieldWrapper');");
		pWriter.addJS_cr(
			"    var inputReferenceButton = document.querySelector('#" + pFieldInputsPanelId
				+ " > table > tbody > tr.ebx_ComplexField > td.ebx_Input > button');");
		pWriter.addJS_cr(
			"    var formRowLabel = document.querySelector('#" + pFieldInputsPanelId
				+ " > table > tbody > tr.ebx_ComplexField > td.ebx_Label');");
		pWriter.addJS_cr(
			"    var formRowFieldInfo = document.querySelector('#" + pFieldInputsPanelId
				+ " > table > tbody > tr.ebx_ComplexField > td.ebx_FieldInfos');");

		if (this.autocompleteFieldWidth == null)
		{
			pWriter.addJS_cr(
				"    var autocompleteField = document.getElementById('" + pAutocompleteFieldId
					+ "');");
			pWriter.addJS_cr("    if (inputReference && autocompleteField) {");
			pWriter.addJS_cr("        var autocompleteFieldWidth = inputReference.offsetWidth;");
			pWriter.addJS_cr("        if (inputReferenceButton) {");
			pWriter.addJS_cr("            panelWidth += inputReferenceButton.offsetWidth;");
			pWriter.addJS_cr("        }");
			pWriter.addJS_cr(
				"        autocompleteField.style.width = (autocompleteFieldWidth) + 'px';");
			pWriter.addJS_cr("    }");
		}

		if (this.fieldPanelWidth == null)
		{
			pWriter.addJS_cr(
				"    var panelDiv = document.getElementById('" + pFieldInputsPanelId + "');");
			pWriter.addJS_cr(
				"    if (panelDiv && inputReference && formRowLabel && formRowFieldInfo) {");
			pWriter.addJS_cr(
				"        var panelWidth = formRowLabel.offsetWidth + formRowFieldInfo.offsetWidth + inputReference.offsetWidth;");
			pWriter.addJS_cr("        if (inputReferenceButton) {");
			pWriter.addJS_cr("            panelWidth += inputReferenceButton.offsetWidth;");
			pWriter.addJS_cr("        }");
			pWriter.addJS_cr("        panelDiv.style.width = (panelWidth + 32 + 20) + 'px';");
			pWriter.addJS_cr("    }");
		}

		pWriter.addJS_cr("})();");
		pWriter.addJS_cr();
	}

	/**
	 * Insert the map.
	 *
	 * @param pWriter the writer
	 * @param pRecord the current record to get the address value
	 * @author ATI
	 * @since 1.4.0
	 */
	private void insertMap(final UIFormPaneWriter pWriter, final Adaptation pRecord)
	{
		// Set the map style to take the whole space in the container
		String containerStyle = "position:absolute;top:-5px;bottom:0px;left:0px;right:0px;";

		pWriter.add("<div style=\"" + containerStyle + "\">");

		// Get the address from the record
		GoogleMapsAddressValue addressValue = GoogleMapsAddressValue
			.createAddressValue(pRecord, this.googleMapsMapping, pWriter.getLocale());

		// Insert the Map
		this.googleMaps.insertMap(pWriter, addressValue, "width:100%; height:100%;", this.mapId);

		// TODO find something for setting an offset to the center of the map

		pWriter.add("</div>");
	}
}
