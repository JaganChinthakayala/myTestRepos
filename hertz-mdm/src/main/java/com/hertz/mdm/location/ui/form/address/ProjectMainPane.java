package com.hertz.mdm.location.ui.form.address;

import java.util.Locale;

import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMaps;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressFieldMapping;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressValue;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.message.Messages;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.ui.UIUtils;
import com.hertz.mdm.location.LocationProjectAddressForm;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.ui.UIFormLabelSpec;
import com.orchestranetworks.ui.form.UIFormContext;
import com.orchestranetworks.ui.form.UIFormPane;
import com.orchestranetworks.ui.form.UIFormPaneWriter;

public class ProjectMainPane implements UIFormPane
{
	@Override
	public void writePane(final UIFormPaneWriter pWriter, final UIFormContext pContext)
	{
		Locale locale = pContext.getLocale();

		boolean withinMaps = false;
		String trackingInfo = pContext.getSession().getTrackingInfo();
		if (trackingInfo != null
			&& trackingInfo.equals(LocationProjectAddressForm.TrackingInfo.WITHIN_GOOGLE_MAPS))
		{
			withinMaps = true;
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

		// Instantiate a new GoogleMapsAddressMapping object
		GoogleMapsAddressFieldMapping mapping = LocationProjectAddressForm
			.getLocationGoogleMapsMapping();

		// Instantiate a new GoogleMaps object
		GoogleMaps googleMaps = new GoogleMaps(mapping);

		String mapId = "LocationMap";
		String autocompleteFieldId = "LocationAutocompleteField";

		// Left side
		pWriter.add("<div style=\"width:40%; float:left; min-width:460px;\">");

		String leftDivStyle = "";
		leftDivStyle += "width:100%;";
		if (!withinMaps)
		{
			leftDivStyle += "padding-top:30px;";
		}
		leftDivStyle += "-webkit-box-sizing:border-box; -moz-box-sizing:border-box; box-sizing:border-box;";

		pWriter.add("<div style=\"" + leftDivStyle + "\">");

		pWriter.startTableFormRow();

		if (!pContext.isCreatingRecord())
		{
			pWriter.addFormRow(LocationPaths._Root_ProjectData_Address._LocationProject);
		}

		pWriter.addFormRow(LocationPaths._Root_ProjectData_Address._GeographicPoint_Latitude);
		pWriter.addFormRow(LocationPaths._Root_ProjectData_Address._GeographicPoint_Longitude);

		SchemaNode rootNode = pContext.getCurrentTable().getTableOccurrenceRootNode();
		SchemaNode addressNode = rootNode
			.getNode(LocationPaths._Root_ProjectData_Address._Address);
		UIUtils.addTitleFormRow(pWriter, addressNode.getLabel(locale));

		if (readWrite && !withinMaps)
		{
			pWriter.startFormRow(new UIFormLabelSpec(""));
			String placeholder = Messages.get(this.getClass(), locale, "addressFieldPlaceholder");

			// Insert the Google Maps Autocomplete field
			googleMaps.insertAutocompleteAddressField(
				pWriter,
				placeholder,
				"width:250px;",
				mapId,
				autocompleteFieldId);

			pWriter.endFormRow();
		}

		UIUtils.addSubNodesFormRow(pWriter, addressNode, null);

		if (readWrite && !withinMaps)
		{
			pWriter.startFormRow(new UIFormLabelSpec(""));
			UserMessage standardizeButtonLabel = Messages
				.getInfo(this.getClass(), "addressStandardizeButtonLabel");

			// Insert the button to standardize addresses with Google Maps
			googleMaps.insertStandardizeAddressButton(
				pWriter,
				standardizeButtonLabel,
				autocompleteFieldId);

			pWriter.endFormRow();
		}

		pWriter.endTableFormRow();

		pWriter.add("</div>");
		pWriter.add("</div>");

		// Right side
		if (!withinMaps)
		{
			pWriter.add("<div style=\"width:60%; float:left; min-width:460px;\">");

			String rightDivStyle = "";
			rightDivStyle += "width:100%;";
			rightDivStyle += "padding:30px;";
			rightDivStyle += "-webkit-box-sizing:border-box; -moz-box-sizing:border-box; box-sizing:border-box;";

			pWriter.add("<div style=\"" + rightDivStyle + "\">");

			// Instantiate a new GoogleMapsAddressValue object
			GoogleMapsAddressValue addressValue = GoogleMapsAddressValue
				.createAddressValue(record, mapping, locale);

			// Insert the Map
			googleMaps.insertMap(pWriter, addressValue, "width:100%; height:400px;", mapId);

			pWriter.add("</div>");
			pWriter.add("</div>");
		}
	}
}
