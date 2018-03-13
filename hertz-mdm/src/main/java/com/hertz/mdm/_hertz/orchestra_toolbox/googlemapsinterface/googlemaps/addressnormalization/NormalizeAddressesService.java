package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.addressnormalization;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMaps;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressFieldMapping;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressValue;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsItem;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsOptions;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.message.Messages;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.onwbp.org.json.JSONArray;
import com.onwbp.org.json.JSONException;
import com.onwbp.org.json.JSONObject;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.ServiceContext;
import com.orchestranetworks.ui.UIButtonSpecJSAction;
import com.orchestranetworks.ui.UIButtonSpecSubmit;
import com.orchestranetworks.ui.UIFormSpec;
import com.orchestranetworks.ui.UIHttpManagerComponent;
import com.orchestranetworks.ui.UIServiceComponentWriter;

/**
 * The Class NormalizeAddressesService provides a generic service for address normalization via
 * Google Maps.
 *
 * @author ATI
 * @since 1.0.0
 */
public final class NormalizeAddressesService
{
	private static final String PARAMETER_NORMALIZED_ADDRESS_ITEMS = "normalizedAddressItems";

	private static final String ID_NORMALIZATION_MAP = "normalizationMap";
	private static final String ID_SAVE_RECORDS_BUTTON = "saveRecordsButton";
	private static final String ID_CLOSE_BUTTON = "closeButton";
	private static final String ID_GET_NORMALIZED_ADDRESSES_BUTTON = "getNormalizedAddressesButton";

	private static final String JS_VARNAME_SELECTED_RECORD_ITEMS = "selectedRecordItems";
	private static final String JS_VARNAME_TODO_ITEMS = "todoItems";
	private static final String JS_VARNAME_OVER_QUOTA_ITEMS = "overQuotaItems";
	private static final String JS_VARNAME_ALREADY_FOUND_ITEMS = "alreadyFoundItems";
	private static final String JS_VARNAME_FOUND_ITEMS = "foundItems";
	private static final String JS_VARNAME_NOT_FOUND_ITEMS = "notFoundItems";
	private static final String JS_VARNAME_ERRORS_ITEMS = "errorsItems";
	private static final String JS_VARNAME_ITEM_COUNTER = "normalizeServiceItemCounter";
	private static final String JS_VARNAME_NORMALIZED_RECORDS = "normalizedRecords";

	private static final String JS_FUNCNAME_RUN_GET_NORMALIZED_ADDRESSES = "runGetNormalizedAddresses";
	private static final String JS_FUNCNAME_UPDATE_SERVICE_COUNTER = "updateServiceCounters";
	private static final String JS_FUNCNAME_GET_NORMALIZED_ADDRESSES = "getNormalizedAddresses";

	private final HttpServletRequest request;
	private final ServiceContext context;
	private final List<Adaptation> selectedRecords;
	private final UIServiceComponentWriter writer;
	private final Locale locale;
	private final AdaptationTable table;

	private GoogleMapsAddressFieldMapping addressPaths;
	private GoogleMapsOptions googleMapsOptions;
	private String mapInfoWindowTrackingInfo;
	private String itemDefaultIcon;

	private GoogleMaps googleMaps;

	/**
	 * Instantiates a new normalize addresses service, relying on record selection.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public NormalizeAddressesService(
		final HttpServletRequest pRequest,
		final HttpServletResponse pResponse)
	{
		this.request = pRequest;
		this.context = ServiceContext.getServiceContext(pRequest);
		this.writer = this.context.getUIComponentWriter();
		this.locale = this.context.getLocale();
		this.selectedRecords = this.context.getSelectedOccurrences();
		if (this.selectedRecords.isEmpty())
		{
			this.table = null;
		}
		else
		{
			this.table = this.selectedRecords.get(0).getContainerTable();
		}
	}

	/**
	 * Instantiates a new normalize addresses service, relying on record selection.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @param addressPaths the mapping of the Google Maps address components to EBX paths
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public NormalizeAddressesService(
		final HttpServletRequest pRequest,
		final HttpServletResponse pResponse,
		final GoogleMapsAddressFieldMapping addressPaths)
	{
		this.request = pRequest;
		this.context = ServiceContext.getServiceContext(pRequest);
		this.writer = this.context.getUIComponentWriter();
		this.locale = this.context.getLocale();
		this.selectedRecords = this.context.getSelectedOccurrences();
		if (this.selectedRecords.isEmpty())
		{
			this.table = null;
		}
		else
		{
			this.table = this.selectedRecords.get(0).getContainerTable();
		}
		this.addressPaths = addressPaths;
	}

	/**
	 * Instantiates a new normalize addresses service, relying on record selection.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @param addressPaths the mapping of the Google Maps address components to EBX paths
	 * @param googleMapsOptions the google maps options
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsOptions
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public NormalizeAddressesService(
		final HttpServletRequest pRequest,
		final HttpServletResponse pResponse,
		final GoogleMapsAddressFieldMapping addressPaths,
		final GoogleMapsOptions googleMapsOptions)
	{
		this.request = pRequest;
		this.context = ServiceContext.getServiceContext(pRequest);
		this.writer = this.context.getUIComponentWriter();
		this.locale = this.context.getLocale();
		this.selectedRecords = this.context.getSelectedOccurrences();
		if (this.selectedRecords.isEmpty())
		{
			this.table = null;
		}
		else
		{
			this.table = this.selectedRecords.get(0).getContainerTable();
		}
		this.addressPaths = addressPaths;
		this.googleMapsOptions = googleMapsOptions;
	}

	/**
	 * Execute the service.
	 *
	 * @since 1.0.0
	 */
	public void execute()
	{
		this.defineMapsOptions();
		this.googleMaps = new GoogleMaps(this.googleMapsOptions);

		String normalizedItemsJsonObject = this.request
			.getParameter(NormalizeAddressesService.PARAMETER_NORMALIZED_ADDRESS_ITEMS);

		boolean normalizationDone = false;
		if (normalizedItemsJsonObject == null)
		{
			this.writePage(normalizationDone, new ArrayList<GoogleMapsItem>(), 0);
			List<GoogleMapsItem> items = this.getAddresses(this.selectedRecords);
			this.insertNormalizeAddressesJavaScript(
				normalizationDone,
				items,
				NormalizeAddressesService.ID_NORMALIZATION_MAP);
		}
		else
		{
			normalizationDone = true;
			List<Adaptation> normalizedRecords = null;
			try
			{
				normalizedRecords = this.updateRecords(normalizedItemsJsonObject);
			}
			catch (JSONException ex)
			{
				ex.printStackTrace();
			}

			List<GoogleMapsItem> items = this.getAddresses(this.selectedRecords);
			int nbNormalizedRecords = 0;
			if (normalizedRecords != null)
			{
				nbNormalizedRecords = normalizedRecords.size();
			}
			this.writePage(normalizationDone, items, nbNormalizedRecords);
		}
	}

	/**
	 * Sets the mapping of the Google Maps address components to EBX paths.
	 *
	 * @param addressPaths the new mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping
	 * @since 1.0.0
	 */
	public void setAddressPaths(final GoogleMapsAddressFieldMapping addressPaths)
	{
		this.addressPaths = addressPaths;
	}

	/**
	 * Sets the google maps options.
	 *
	 * @param googleMapsOptions the new google maps options
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsOptions
	 * @since 1.0.0
	 */
	public void setGoogleMapsOptions(final GoogleMapsOptions googleMapsOptions)
	{
		this.googleMapsOptions = googleMapsOptions;
	}

	/**
	 * Sets the url of the icon image. Proposed icons are proposed in the class GoogleMaps.Icons.
	 *
	 * @param itemDefaultIcon the url to the icon image
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Icons
	 * @since 1.0.0
	 */
	public void setItemDefaultIcon(final String itemDefaultIcon)
	{
		this.itemDefaultIcon = itemDefaultIcon;
	}

	/**
	 * Sets the tracking info passed into the manager component of the info window displaying the
	 * record form on map.
	 *
	 * @param mapInfoWindowTrackingInfo the new tracking info
	 * @since 1.0.0
	 */
	public void setMapInfoWindowTrackingInfo(final String mapInfoWindowTrackingInfo)
	{
		this.mapInfoWindowTrackingInfo = mapInfoWindowTrackingInfo;
	}

	/**
	 * Adds the function get normalized addresses.
	 *
	 * @param pMapId the map id
	 * @since 1.0.0
	 */
	private void addFunctionGetNormalizedAddresses(final String pMapId)
	{
		String mapVarName = GoogleMaps.getVarNameMap(pMapId);
		String infoWindowVarName = GoogleMaps.getVarNameInfoWindow(pMapId);

		this.writer.addJS_cr(); // TODO
		this.writer.addJS_cr(
			"function " + NormalizeAddressesService.JS_FUNCNAME_GET_NORMALIZED_ADDRESSES
				+ "(done) {");
		this.writer.addJS_cr(
			"    if (" + NormalizeAddressesService.JS_VARNAME_TODO_ITEMS + ".length === 0) {");
		this.writer.addJS_cr(
			"        for (var i = 0; i < " + NormalizeAddressesService.JS_VARNAME_OVER_QUOTA_ITEMS
				+ ".length; i++) {");
		this.writer.addJS_cr(
			"            var item = " + NormalizeAddressesService.JS_VARNAME_OVER_QUOTA_ITEMS
				+ "[i];");
		this.writer.addJS_cr(
			"            " + NormalizeAddressesService.JS_VARNAME_TODO_ITEMS + ".push(item);");
		this.writer.addJS_cr("        }");
		this.writer.addJS_cr(
			"        " + NormalizeAddressesService.JS_VARNAME_OVER_QUOTA_ITEMS + " = [];");
		this.writer.addJS_cr(
			"        " + NormalizeAddressesService.JS_FUNCNAME_UPDATE_SERVICE_COUNTER + "('"
				+ NormalizeAddressesService.JS_VARNAME_ERRORS_ITEMS + "');");
		this.writer.addJS_cr("    }");
		this.writer.addJS_cr(
			"    var nbItems = " + NormalizeAddressesService.JS_VARNAME_TODO_ITEMS + ".length;");
		this.writer.addJS_cr("    for (var i = 0; i < nbItems; i++) {");
		this.writer.addJS_cr(
			"        var location = " + NormalizeAddressesService.JS_VARNAME_TODO_ITEMS + "[i];");
		this.writer.addJS_cr("        (function(item) {");
		this.writer
			.addJS_cr("            if (item." + GoogleMapsItem.JsObject.ADDRESSVALUE + ") {");
		this.writer.addJS_cr(
			"                if (item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.LAT + " && item."
				+ GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.LONG + ") {");
		this.writer.addJS_cr(
			"                    var latLng = new google.maps.LatLng(item."
				+ GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.LAT + ", item."
				+ GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.LONG + ", false);");

		String implemCreateMarkerFunction = GoogleMaps.getFunctionImpl_CreateMarker(
			mapVarName,
			"latLng",
			"item." + GoogleMapsItem.JsObject.ICON,
			infoWindowVarName,
			"item." + GoogleMapsItem.JsObject.CONTENT,
			null);
		this.writer.addJS_cr("                    " + implemCreateMarkerFunction);

		this.writer.addJS_cr(
			"                    " + NormalizeAddressesService.JS_VARNAME_ALREADY_FOUND_ITEMS
				+ ".push(item);");
		this.writer.addJS_cr(
			"                    " + NormalizeAddressesService.JS_FUNCNAME_UPDATE_SERVICE_COUNTER
				+ "('" + NormalizeAddressesService.JS_VARNAME_ALREADY_FOUND_ITEMS + "');");
		this.writer.addJS_cr(
			"                    " + NormalizeAddressesService.JS_VARNAME_ITEM_COUNTER + "++;");
		this.writer.addJS_cr(
			"                    if (" + NormalizeAddressesService.JS_VARNAME_ITEM_COUNTER
				+ " === nbItems) {");
		this.writer.addJS_cr("                        done();");
		this.writer.addJS_cr("                    }");
		this.writer.addJS_cr(
			"                } else if (item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.CONCATENATED_ADDRESS + ") {");
		this.writer.addJS_cr(
			"                    " + GoogleMaps.JS_FUNCNAME_GET_GEOCODE + "(item."
				+ GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.CONCATENATED_ADDRESS
				+ ", function(results, status) {");
		this.writer
			.addJS_cr("                        if (status === google.maps.GeocoderStatus.OK) {");
		this.writer
			.addJS_cr("                            var latLng = results[0].geometry.location;");
		this.writer
			.addJS_cr("                            var address = results[0].address_components;");

		this.writer.addJS_cr("                            " + implemCreateMarkerFunction);
		//		this.writer.addJS_cr("                            " + GoogleMaps.JS_FUNCNAME_CREATE_MARKER
		//			+ "(" + mapVarName + ", latLng, item." + GoogleMapsItem.JsObject.ICON + ", "
		//			+ infoWindowVarName + ", item." + GoogleMapsItem.JsObject.CONTENT + ");");

		this.writer.addJS_cr(
			"                            var gmapItem = "
				+ GoogleMaps.JS_FUNCNAME_CREATE_GOOGLE_MAPS_ITEM + "(item."
				+ GoogleMapsItem.JsObject.PK + ", item." + GoogleMapsItem.JsObject.LABEL
				+ ", latLng, address, item." + GoogleMapsItem.JsObject.CONTENT + ", item."
				+ GoogleMapsItem.JsObject.ICON + ");");
		this.writer.addJS_cr(
			"                            " + NormalizeAddressesService.JS_VARNAME_FOUND_ITEMS
				+ ".push(gmapItem);");
		this.writer.addJS_cr(
			"                            "
				+ NormalizeAddressesService.JS_FUNCNAME_UPDATE_SERVICE_COUNTER + "('"
				+ NormalizeAddressesService.JS_VARNAME_FOUND_ITEMS + "');");
		this.writer.addJS_cr(
			"                        } else if (status === google.maps.GeocoderStatus.ZERO_RESULTS) {");
		this.writer.addJS_cr(
			"                            " + NormalizeAddressesService.JS_VARNAME_NOT_FOUND_ITEMS
				+ ".push(item);");
		this.writer.addJS_cr(
			"                            "
				+ NormalizeAddressesService.JS_FUNCNAME_UPDATE_SERVICE_COUNTER + "('"
				+ NormalizeAddressesService.JS_VARNAME_NOT_FOUND_ITEMS + "');");
		this.writer.addJS_cr(
			"                        } else if (status === google.maps.GeocoderStatus.OVER_QUERY_LIMIT) {");
		this.writer.addJS_cr(
			"                            " + NormalizeAddressesService.JS_VARNAME_OVER_QUOTA_ITEMS
				+ ".push(item);");
		this.writer.addJS_cr(
			"                            "
				+ NormalizeAddressesService.JS_FUNCNAME_UPDATE_SERVICE_COUNTER + "('"
				+ NormalizeAddressesService.JS_VARNAME_OVER_QUOTA_ITEMS + "');");
		this.writer.addJS_cr("                        } else {");
		this.writer.addJS_cr(
			"                            " + NormalizeAddressesService.JS_VARNAME_ERRORS_ITEMS
				+ ".push(item);");
		this.writer.addJS_cr(
			"                            "
				+ NormalizeAddressesService.JS_FUNCNAME_UPDATE_SERVICE_COUNTER + "('"
				+ NormalizeAddressesService.JS_VARNAME_ERRORS_ITEMS + "');");
		this.writer.addJS_cr("                        }");
		this.writer.addJS_cr(
			"                        " + NormalizeAddressesService.JS_VARNAME_ITEM_COUNTER + "++;");
		this.writer.addJS_cr(
			"                        if (" + NormalizeAddressesService.JS_VARNAME_ITEM_COUNTER
				+ " === nbItems) {");
		this.writer.addJS_cr("                            done();");
		this.writer.addJS_cr("                        }");
		this.writer.addJS_cr("                    });");
		this.writer.addJS_cr("                } else {");
		this.writer.addJS_cr(
			"                    " + NormalizeAddressesService.JS_VARNAME_ERRORS_ITEMS
				+ ".push(item);");
		this.writer.addJS_cr(
			"                    " + NormalizeAddressesService.JS_FUNCNAME_UPDATE_SERVICE_COUNTER
				+ "('" + NormalizeAddressesService.JS_VARNAME_ERRORS_ITEMS + "');");
		this.writer.addJS_cr(
			"                    " + NormalizeAddressesService.JS_VARNAME_ITEM_COUNTER + "++;");
		this.writer.addJS_cr(
			"                    if (" + NormalizeAddressesService.JS_VARNAME_ITEM_COUNTER
				+ " === nbItems) {");
		this.writer.addJS_cr("                        done();");
		this.writer.addJS_cr("                    }");
		this.writer.addJS_cr("                }");
		this.writer.addJS_cr("            } else {");
		this.writer.addJS_cr(
			"                " + NormalizeAddressesService.JS_VARNAME_ERRORS_ITEMS
				+ ".push(item);");
		this.writer.addJS_cr(
			"                " + NormalizeAddressesService.JS_FUNCNAME_UPDATE_SERVICE_COUNTER + "('"
				+ NormalizeAddressesService.JS_VARNAME_ERRORS_ITEMS + "');");
		this.writer.addJS_cr(
			"                " + NormalizeAddressesService.JS_VARNAME_ITEM_COUNTER + "++;");
		this.writer.addJS_cr(
			"                if (" + NormalizeAddressesService.JS_VARNAME_ITEM_COUNTER
				+ " === nbItems) {");
		this.writer.addJS_cr("                    done();");
		this.writer.addJS_cr("                }");
		this.writer.addJS_cr("            }");
		this.writer.addJS_cr("        })(location);");
		this.writer.addJS_cr("    }");
		this.writer.addJS_cr("    " + NormalizeAddressesService.JS_VARNAME_TODO_ITEMS + " = [];");
		this.writer.addJS_cr("}");
		this.writer.addJS_cr();
	}

	/**
	 * Adds the function run get normalized addresses.
	 *
	 * @since 1.0.0
	 */
	private void addFunctionRunGetNormalizedAddresses()
	{
		this.writer.addJS_cr();
		this.writer.addJS_cr(
			"function " + NormalizeAddressesService.JS_FUNCNAME_RUN_GET_NORMALIZED_ADDRESSES
				+ "(){");
		this.writer.addJS_cr("    var interval = setInterval(function(){");
		this.writer.addJS_cr(
			"        " + NormalizeAddressesService.JS_FUNCNAME_GET_NORMALIZED_ADDRESSES
				+ "(function(){");

		this.writer
			.addJS_cr("            " + NormalizeAddressesService.JS_VARNAME_ITEM_COUNTER + " = 0;");
		this.writer.addJS_cr(
			"            if (" + NormalizeAddressesService.JS_VARNAME_OVER_QUOTA_ITEMS
				+ ".length === 0) {");
		this.writer.addJS_cr(
			"                " + NormalizeAddressesService.JS_FUNCNAME_UPDATE_SERVICE_COUNTER + "('"
				+ NormalizeAddressesService.JS_VARNAME_OVER_QUOTA_ITEMS + "');");
		this.writer.addJS_cr("                clearInterval(interval);");
		this.writer.addJS_cr(
			"                var outputItemsField = document.getElementById('"
				+ NormalizeAddressesService.PARAMETER_NORMALIZED_ADDRESS_ITEMS + "');");
		this.writer.addJS_cr(
			"                outputItemsField.value = JSON.stringify("
				+ NormalizeAddressesService.JS_VARNAME_FOUND_ITEMS + ");");
		this.writer.addJS_setButtonDisabled(
			"document.getElementById('" + NormalizeAddressesService.ID_SAVE_RECORDS_BUTTON + "')",
			"false");
		this.writer.addJS_setButtonDisabled(
			"document.getElementById('"
				+ NormalizeAddressesService.ID_GET_NORMALIZED_ADDRESSES_BUTTON + "')",
			"true");
		this.writer.addJS_cr();
		this.writer.addJS_cr("            }");
		this.writer.addJS_cr("        });");
		this.writer.addJS_cr("    }, 1000);");
		this.writer.addJS_cr("}");
		this.writer.addJS_cr();
	}

	/**
	 * Adds the function update service counter.
	 *
	 * @since 1.0.0
	 */
	private void addFunctionUpdateServiceCounter()
	{
		this.writer.addJS_cr();
		this.writer.addJS_cr(
			"function " + NormalizeAddressesService.JS_FUNCNAME_UPDATE_SERVICE_COUNTER
				+ "(type) {");
		this.writer.addJS_cr("    var span = document.getElementById(type);");
		this.writer.addJS_cr(
			"    if (type ==='" + NormalizeAddressesService.JS_VARNAME_OVER_QUOTA_ITEMS + "') {");
		this.writer.addJS_cr(
			"        span.innerHTML = " + NormalizeAddressesService.JS_VARNAME_OVER_QUOTA_ITEMS
				+ ".length;");
		this.writer.addJS_cr(
			"    } else if (type ==='" + NormalizeAddressesService.JS_VARNAME_ALREADY_FOUND_ITEMS
				+ "') {");
		this.writer.addJS_cr(
			"        span.innerHTML = " + NormalizeAddressesService.JS_VARNAME_ALREADY_FOUND_ITEMS
				+ ".length;");
		this.writer.addJS_cr(
			"    } else if (type ==='" + NormalizeAddressesService.JS_VARNAME_FOUND_ITEMS + "') {");
		this.writer.addJS_cr(
			"        span.innerHTML = " + NormalizeAddressesService.JS_VARNAME_FOUND_ITEMS
				+ ".length;");
		this.writer.addJS_cr(
			"    } else if (type ==='" + NormalizeAddressesService.JS_VARNAME_NOT_FOUND_ITEMS
				+ "') {");
		this.writer.addJS_cr(
			"        span.innerHTML = " + NormalizeAddressesService.JS_VARNAME_NOT_FOUND_ITEMS
				+ ".length;");
		this.writer.addJS_cr(
			"    } else if (type ==='" + NormalizeAddressesService.JS_VARNAME_ERRORS_ITEMS
				+ "') {");
		this.writer.addJS_cr(
			"        span.innerHTML = " + NormalizeAddressesService.JS_VARNAME_ERRORS_ITEMS
				+ ".length;");
		this.writer.addJS_cr("    }");
		this.writer.addJS_cr("}");
		this.writer.addJS_cr();
	}

	/**
	 * Adds the global variables.
	 *
	 * @param pItems the items
	 * @since 1.0.0
	 */
	private void addGlobalVariables(final List<GoogleMapsItem> pItems)
	{
		this.writer.addJS_cr();
		this.writer.addJS_cr("var " + NormalizeAddressesService.JS_VARNAME_ITEM_COUNTER + " = 0;");

		if (pItems == null || pItems.isEmpty())
		{
			this.writer.addJS_cr(
				"var " + NormalizeAddressesService.JS_VARNAME_SELECTED_RECORD_ITEMS + " = [];");
		}
		else
		{
			this.writer.addJS_cr(
				"var " + NormalizeAddressesService.JS_VARNAME_SELECTED_RECORD_ITEMS + " = [");
			int nbItems = pItems.size();
			for (int i = 0; i < nbItems; i++)
			{
				GoogleMapsItem item = pItems.get(i);

				this.writer.addJS_cr();
				this.writer.addJS("    " + item.getJsObject());
				if (i < nbItems - 1)
				{
					this.writer.addJS_cr(",");
				}
				else
				{
					this.writer.addJS_cr();
				}

			}
			this.writer.addJS_cr();
			this.writer.addJS_cr("];");
		}

		this.writer.addJS_cr("var " + NormalizeAddressesService.JS_VARNAME_TODO_ITEMS + " = [];");
		this.writer
			.addJS_cr("var " + NormalizeAddressesService.JS_VARNAME_OVER_QUOTA_ITEMS + " = [];");
		this.writer
			.addJS_cr("var " + NormalizeAddressesService.JS_VARNAME_ALREADY_FOUND_ITEMS + " = [];");
		this.writer.addJS_cr("var " + NormalizeAddressesService.JS_VARNAME_FOUND_ITEMS + " = [];");
		this.writer
			.addJS_cr("var " + NormalizeAddressesService.JS_VARNAME_NOT_FOUND_ITEMS + " = [];");
		this.writer.addJS_cr("var " + NormalizeAddressesService.JS_VARNAME_ERRORS_ITEMS + " = [];");
		this.writer.addJS_cr();
	}

	/**
	 * Adds the map.
	 *
	 * @param pItems the items
	 * @since 1.0.0
	 */
	private void addMap(final List<GoogleMapsItem> pItems)
	{
		// Insert the maps as background
		String containerStyle = "position:absolute;top:0px;bottom:0px;left:0px;right:0px;";
		this.writer.add("<div style=\"" + containerStyle + "\">");

		// Insert the maps with markers representing the items (at startup the item list is empty, so no markers)
		this.googleMaps.insertMapWithMarkers(
			this.writer,
			pItems,
			"width:100%;height:100%;",
			NormalizeAddressesService.ID_NORMALIZATION_MAP);

		this.writer.add("</div>");
	}

	/**
	 * Adds the service panel.
	 *
	 * @param pNormalizationDone the normalization done
	 * @param pNbNormalizedRecords the nb normalized records
	 * @since 1.0.0
	 */
	private void addServicePanel(final boolean pNormalizationDone, final int pNbNormalizedRecords)
	{
		String commandsStyle = "position:absolute;top:15px;left:15px;background:white;padding:10px;";
		String valueStyle = "font-weight:bold;";

		this.writer.add("<div style=\"" + commandsStyle + "\">");

		this.writer.add("<h2>" + this.context.getServiceLabel() + "</h2>");

		this.writer.add("<div style=\"padding:5px;margin-bottom:10px;\">");

		// Selected records
		String selectedRecordsLabel = Messages
			.get(this.getClass(), this.locale, "selectedRecordsLabel");
		String selectedRecordSpanId = NormalizeAddressesService.JS_VARNAME_TODO_ITEMS;
		this.writer.add("<h4>");
		this.writer.add(selectedRecordsLabel + ": ");
		this.writer.add("<span id=\"" + selectedRecordSpanId + "\" style=\"" + valueStyle + "\">");
		if (this.selectedRecords != null)
		{
			this.writer.add(String.valueOf(this.selectedRecords.size()));
		}
		else
		{
			this.writer.add("0");
		}
		this.writer.add("</span>");
		this.writer.add("</h4>");
		this.writer.add("</div>");

		this.writer.add("</br>");

		UIButtonSpecJSAction getNormaliazedAddressButton = new UIButtonSpecJSAction(
			Messages.getInfo(this.getClass(), "getNormalizeAddressesButton"),
			NormalizeAddressesService.JS_FUNCNAME_RUN_GET_NORMALIZED_ADDRESSES + "()");
		getNormaliazedAddressButton
			.setId(NormalizeAddressesService.ID_GET_NORMALIZED_ADDRESSES_BUTTON);
		if (pNormalizationDone)
		{
			getNormaliazedAddressButton.setDisabled(true);
		}
		this.writer.addButtonJavaScript(getNormaliazedAddressButton);

		this.writer.add("<div style=\"padding:5px;margin-bottom:10px;\">");

		// Over quota
		String overQuotaLabel = Messages.get(this.getClass(), this.locale, "overQuotaLabel");
		String overQuotaSpanId = NormalizeAddressesService.JS_VARNAME_OVER_QUOTA_ITEMS;
		this.writer.add("<h4>");
		this.writer.add(overQuotaLabel + ": ");
		this.writer
			.add("<span id=\"" + overQuotaSpanId + "\" style=\"" + valueStyle + "\">0</span>");
		this.writer.add("</h4>");

		// Already found
		String alreadyFoundLabel = Messages.get(this.getClass(), this.locale, "alreadyFoundLabel");
		String alreadyFoundSpanId = NormalizeAddressesService.JS_VARNAME_ALREADY_FOUND_ITEMS;
		this.writer.add("<h4>");
		this.writer.add(alreadyFoundLabel + ": ");
		this.writer
			.add("<span id=\"" + alreadyFoundSpanId + "\" style=\"" + valueStyle + "\">0</span>");
		this.writer.add("</h4>");

		// Found
		String foundLabel = Messages.get(this.getClass(), this.locale, "foundLabel");
		String foundSpanId = NormalizeAddressesService.JS_VARNAME_FOUND_ITEMS;
		this.writer.add("<h4>");
		this.writer.add(foundLabel + ": ");
		this.writer.add("<span id=\"" + foundSpanId + "\" style=\"" + valueStyle + "\">0</span>");
		this.writer.add("</h4>");

		// Not found
		String notFoundLabel = Messages.get(this.getClass(), this.locale, "notFoundLabel");
		String notFoundSpanId = NormalizeAddressesService.JS_VARNAME_NOT_FOUND_ITEMS;
		this.writer.add("<h4>");
		this.writer.add(notFoundLabel + ": ");
		this.writer
			.add("<span id=\"" + notFoundSpanId + "\" style=\"" + valueStyle + "\">0</span>");
		this.writer.add("</h4>");

		// Errors
		String errorsLabel = Messages.get(this.getClass(), this.locale, "errorsLabel");
		String errorsSpanId = NormalizeAddressesService.JS_VARNAME_ERRORS_ITEMS;
		this.writer.add("<h4>");
		this.writer.add(errorsLabel + ": ");
		this.writer.add("<span id=\"" + errorsSpanId + "\" style=\"" + valueStyle + "\">0</span>");
		this.writer.add("</h4>");

		this.writer.add("</div>");

		String hiddenInputId = NormalizeAddressesService.PARAMETER_NORMALIZED_ADDRESS_ITEMS;
		this.writer.add(
			"<input id=\"" + hiddenInputId + "\" name=\"" + hiddenInputId
				+ "\" type=\"hidden\" value=\"\">");

		this.writer.add("</br>");

		// Save button

		UIButtonSpecSubmit saveRecordsButton = new UIButtonSpecSubmit(
			Messages.getInfo(this.getClass(), "saveRecordsButton"),
			NormalizeAddressesService.ID_SAVE_RECORDS_BUTTON,
			"");
		saveRecordsButton.setId(NormalizeAddressesService.ID_SAVE_RECORDS_BUTTON);
		saveRecordsButton.setDisabled(true);
		this.writer.addButtonSubmit(saveRecordsButton);

		this.writer.add("<div style=\"padding:5px;margin-bottom:10px;\">");

		String normalizedRecordsLabel = Messages
			.get(this.getClass(), this.locale, "normalizedRecordsLabel");
		String normalizedRecordsSpanId = NormalizeAddressesService.JS_VARNAME_NORMALIZED_RECORDS;
		this.writer.add("<h4>");
		this.writer.add(normalizedRecordsLabel + ": ");
		this.writer
			.add("<span id=\"" + normalizedRecordsSpanId + "\" style=\"" + valueStyle + "\">");
		this.writer.add(pNbNormalizedRecords);
		this.writer.add("</span>");
		this.writer.add("</h4>");

		this.writer.add("</div>");

		this.writer.add("</br>");

		// Close Button

		String endServiceURL = this.context.getURLForEndingService();
		UIButtonSpecJSAction closeButton = new UIButtonSpecJSAction(
			Messages.getInfo(this.getClass(), "closeButton"),
			"window.location.replace('" + endServiceURL + "')");
		closeButton.setId(NormalizeAddressesService.ID_CLOSE_BUTTON);

		this.writer.addButtonJavaScript(closeButton);

		this.writer.add("</div>");
	}

	/**
	 * Adds the startup function.
	 *
	 * @since 1.0.0
	 */
	private void addStartupFunction()
	{
		this.writer.addJS_cr();
		this.writer.addJS_cr("(function (){");
		this.writer.addJS_cr(
			"    for (var i = 0; i < " + NormalizeAddressesService.JS_VARNAME_SELECTED_RECORD_ITEMS
				+ ".length; i++) {");
		this.writer.addJS_cr(
			"        var item = " + NormalizeAddressesService.JS_VARNAME_SELECTED_RECORD_ITEMS
				+ "[i];");
		this.writer.addJS_cr(
			"        " + NormalizeAddressesService.JS_VARNAME_TODO_ITEMS + ".push(item);");
		this.writer.addJS_cr("    }");
		this.writer.addJS_cr("})();");
		this.writer.addJS_cr();
	}

	/**
	 * Defines the maps options.
	 *
	 * @since 1.0.0
	 */
	private void defineMapsOptions()
	{
		if (this.googleMapsOptions == null)
		{
			this.googleMapsOptions = new GoogleMapsOptions();
			this.googleMapsOptions.setZoomControlPosition(GoogleMaps.ControlPosition.RIGHT_BOTTOM);
		}
	}

	/**
	 * Extract address value from json.
	 *
	 * @param pAddressValueJsonObject the address value json object
	 * @return the google maps address value
	 * @throws JSONException the JSON exception
	 * @since 1.0.0
	 */
	private GoogleMapsAddressValue extractAddressValueFromJSON(
		final JSONObject pAddressValueJsonObject) throws JSONException
	{
		GoogleMapsAddressValue addressValue = new GoogleMapsAddressValue();

		if (pAddressValueJsonObject != null)
		{
			if (pAddressValueJsonObject.has(GoogleMapsItem.JsObject.AddressValue.NUMBER))
			{
				addressValue.setNumber(
					pAddressValueJsonObject.getString(GoogleMapsItem.JsObject.AddressValue.NUMBER));
			}
			if (pAddressValueJsonObject.has(GoogleMapsItem.JsObject.AddressValue.STREET))
			{
				addressValue.setStreet(
					pAddressValueJsonObject.getString(GoogleMapsItem.JsObject.AddressValue.STREET));
			}
			if (pAddressValueJsonObject.has(GoogleMapsItem.JsObject.AddressValue.ZIPCODE))
			{
				addressValue.setZipcode(
					pAddressValueJsonObject
						.getString(GoogleMapsItem.JsObject.AddressValue.ZIPCODE));
			}
			if (pAddressValueJsonObject.has(GoogleMapsItem.JsObject.AddressValue.CITY))
			{
				addressValue.setCity(
					pAddressValueJsonObject.getString(GoogleMapsItem.JsObject.AddressValue.CITY));
			}
			if (pAddressValueJsonObject.has(GoogleMapsItem.JsObject.AddressValue.REGION))
			{
				addressValue.setRegion(
					pAddressValueJsonObject.getString(GoogleMapsItem.JsObject.AddressValue.REGION));
			}
			if (pAddressValueJsonObject.has(GoogleMapsItem.JsObject.AddressValue.COUNTRY))
			{
				addressValue.setCountry(
					pAddressValueJsonObject
						.getString(GoogleMapsItem.JsObject.AddressValue.COUNTRY));
			}
			if (pAddressValueJsonObject.has(GoogleMapsItem.JsObject.AddressValue.LAT))
			{
				addressValue.setLatitude(
					pAddressValueJsonObject.getString(GoogleMapsItem.JsObject.AddressValue.LAT));
			}
			if (pAddressValueJsonObject.has(GoogleMapsItem.JsObject.AddressValue.LONG))
			{
				addressValue.setLongitude(
					pAddressValueJsonObject.getString(GoogleMapsItem.JsObject.AddressValue.LONG));
			}
		}
		return addressValue;
	}

	/**
	 * Gets the addresses.
	 *
	 * @param pRecords the records
	 * @return the addresses
	 * @since 1.0.0
	 */
	private List<GoogleMapsItem> getAddresses(final List<Adaptation> pRecords)
	{
		List<GoogleMapsItem> items = new ArrayList<GoogleMapsItem>();

		if (pRecords == null || this.addressPaths == null)
		{
			return items;
		}

		String iframeDefaultStyle = "border: 0px solid;";
		String iframeCustomStyle = "width:650px;height:400px;";

		for (Adaptation record : pRecords)
		{
			GoogleMapsItem item = new GoogleMapsItem();

			item.setPk(record.getOccurrencePrimaryKey().format());
			item.setLabel(record.getLabelOrName(this.locale));

			GoogleMapsAddressValue address = new GoogleMapsAddressValue();

			if (this.addressPaths.getNumberPath() != null)
			{
				address.setNumber(record.getString(this.addressPaths.getNumberPath()));
			}

			String streetValue = this.getValue(
				record,
				this.addressPaths.getStreetPath(),
				this.addressPaths.getStreetFKMapping());
			address.setStreet(streetValue);

			String zipcodeValue = this.getValue(
				record,
				this.addressPaths.getZipcodePath(),
				this.addressPaths.getZipcodeFKMapping());
			address.setZipcode(zipcodeValue);

			String cityValue = this.getValue(
				record,
				this.addressPaths.getCityPath(),
				this.addressPaths.getCityFKMapping());
			address.setCity(cityValue);

			String regionValue = this.getValue(
				record,
				this.addressPaths.getRegionPath(),
				this.addressPaths.getRegionFKMapping());
			address.setRegion(regionValue);

			String countryValue = this.getValue(
				record,
				this.addressPaths.getCountryPath(),
				this.addressPaths.getCountryFKMapping());
			address.setCountry(countryValue);

			address.setRegion(regionValue);

			if (this.addressPaths.getLatitudePath() != null)
			{
				address.setLatitude(record.getString(this.addressPaths.getLatitudePath()));
			}
			if (this.addressPaths.getLongitudePath() != null)
			{
				address.setLongitude(record.getString(this.addressPaths.getLongitudePath()));
			}

			item.setAddressValue(address);

			String content = "";
			content += "<h4>" + record.getLabelOrName(this.locale) + "</h4>";

			UIHttpManagerComponent managerComponent = this.context
				.createWebComponentForSubSession();
			managerComponent.selectInstanceOrOccurrence(record);
			managerComponent.hideAllDataSetFeatures();
			managerComponent.hideAllRecordFeatures();
			managerComponent.hideAllViewFeatures();
			if (this.mapInfoWindowTrackingInfo != null)
			{
				managerComponent.setTrackingInfo(this.mapInfoWindowTrackingInfo);
			}
			String url = managerComponent.getURIWithParameters();

			content += "<iframe style=\"" + iframeDefaultStyle + iframeCustomStyle + "\" src=\""
				+ url + "\"></iframe>";

			item.setContent(content);
			item.setIcon(this.itemDefaultIcon);

			items.add(item);
		}

		return items;
	}

	/**
	 * Gets the value.
	 *
	 * @param pRecord the record
	 * @param pPath the path
	 * @param pFkMapping the fk mapping
	 * @return the value
	 * @since 1.0.0
	 */
	private String getValue(
		final Adaptation pRecord,
		final Path pPath,
		final ForeignKeyMapping pFkMapping)
	{
		if (pRecord == null || pPath == null)
		{
			return null;
		}

		String fieldValue = pRecord.getString(pPath);
		if (fieldValue == null)
		{
			return null;
		}
		if (pFkMapping == null)
		{
			return fieldValue;
		}
		else
		{
			AdaptationTable fkTable = pFkMapping.getTable();
			if (fkTable == null)
			{
				return fieldValue;
			}
			Adaptation fkRecord = fkTable
				.lookupAdaptationByPrimaryKey(PrimaryKey.parseString(fieldValue));
			if (fkRecord == null)
			{
				return fieldValue;
			}
			Path refPath = pFkMapping.getGoogleMapsRef();
			if (refPath == null)
			{
				return fieldValue;
			}
			String ref = (String) fkRecord.get(refPath);
			return ref;
		}
	}

	/**
	 * Inserts the normalize addresses java script.
	 *
	 * @param pNormalizationDone the normalization done
	 * @param pItems the items
	 * @param pMapId the map id
	 * @since 1.0.0
	 */
	private void insertNormalizeAddressesJavaScript(
		final boolean pNormalizationDone,
		final List<GoogleMapsItem> pItems,
		final String pMapId)
	{
		this.addGlobalVariables(pItems);
		this.addFunctionUpdateServiceCounter();
		this.addFunctionGetNormalizedAddresses(pMapId);
		this.addFunctionRunGetNormalizedAddresses();
		this.addStartupFunction();
	}

	/**
	 * Update records.
	 *
	 * @param pNormalizedItemsJson the normalized items as JSON
	 * @return the list of updated records
	 * @throws JSONException the JSON exception
	 * @since 1.0.0
	 */
	private List<Adaptation> updateRecords(final String pNormalizedItemsJson) throws JSONException
	{
		List<Adaptation> updatedRecords = new ArrayList<Adaptation>();

		if (pNormalizedItemsJson == null || pNormalizedItemsJson.equals(""))
		{
			return updatedRecords;
		}

		List<GoogleMapsItem> itemsToUpdate = new ArrayList<GoogleMapsItem>();

		JSONArray jsonArray = new JSONArray(pNormalizedItemsJson);
		int nbObject = jsonArray.length();

		for (int i = 0; i < nbObject; i++)
		{
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String pk = jsonObject.getString(GoogleMapsItem.JsObject.PK);

			JSONObject addressValueJsonObject = jsonObject
				.getJSONObject(GoogleMapsItem.JsObject.ADDRESSVALUE);

			GoogleMapsAddressValue addressValue = this
				.extractAddressValueFromJSON(addressValueJsonObject);

			GoogleMapsItem item = new GoogleMapsItem(addressValue);
			item.setPk(pk);

			itemsToUpdate.add(item);
		}

		NormalizeAddressProcedure updateProcedure = new NormalizeAddressProcedure(
			this.table,
			itemsToUpdate,
			this.addressPaths);

		this.context.execute(updateProcedure);

		updatedRecords.addAll(updateProcedure.getUpdatedRecords());

		return updatedRecords;
	}

	/**
	 * Write page.
	 *
	 * @param pNormalizationDone the normalization done
	 * @param pMapItems the map items
	 * @param pNbNormalizedRecords the nb normalized records
	 * @since 1.0.0
	 */
	private void writePage(
		final boolean pNormalizationDone,
		final List<GoogleMapsItem> pMapItems,
		final int pNbNormalizedRecords)
	{
		UIFormSpec formSpec = new UIFormSpec();
		formSpec.setDetectionOfLostModificationDisabled(true);

		this.writer.startForm(formSpec);
		this.addMap(pMapItems);
		this.addServicePanel(pNormalizationDone, pNbNormalizedRecords);
		this.writer.endForm();
	}

}
