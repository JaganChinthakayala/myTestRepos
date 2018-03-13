package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.UUID;

import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.UIButtonSpecJSAction;
import com.orchestranetworks.ui.UIComponentWriter;
import com.orchestranetworks.ui.form.UIFormPaneWriter;

/**
 * This class eases the integration of Google Maps features in EBX whether it's a UIFormPane or a
 * Service.<br>
 * <br>
 * Usage:<br>
 *
 * <pre>
 * GoogleMapsAddressFieldMapping mapping = new GoogleMapsAddressFieldMapping();
 *
 * mapping.setNumberPath(Paths._MyEntity._Address_Number);
 * mapping.setStreetPath(Paths._MyEntity._Address_Street);
 * mapping.setCityPath(Paths._MyEntity._Address_City);
 * mapping.setCountryPath(Paths._MyEntity._Address_Country);
 *
 * GoogleMapsAddressValue addressValue = GoogleMapsAddressValue
 * 	.createAddressValue(record, mapping, locale);
 *
 * GoogleMaps googleMaps = new GoogleMaps();
 * googleMaps.insertMap(writer, addressValue, "width:100%; height:400px;");
 *
 * </pre>
 *
 * @author ATI
 * @since 1.0.0
 */
public final class GoogleMaps
{
	/**
	 * The Class AddressComponents defines the values and names used by the Google Maps services.
	 *
	 * @since 1.0.0
	 */
	public final class AddressComponents
	{
		/**
		 * The Class Type desfines the values used in the Google Maps options.
		 *
		 * @since 1.0.0
		 */
		public final class Type
		{
			/** The Constant LONG_NAME with value long_name. */
			public final static String LONG_NAME = "long_name";
			/** The Constant SHORT_NAME with value short_name. */
			public final static String SHORT_NAME = "short_name";

			private Type()
			{
			}
		}

		/** The Constant STREET_NUMBER with value street_number. */
		public final static String STREET_NUMBER = "street_number";
		/** The Constant ROUTE with value route. */
		public final static String ROUTE = "route";
		/** The Constant LOCALITY with value locality. */
		public final static String LOCALITY = "locality";
		/** The Constant ADMINISTRATIVE_AREA_1 with value administrative_area_level_1. */
		public final static String ADMINISTRATIVE_AREA_1 = "administrative_area_level_1";
		/** The Constant COUNTRY with value country. */
		public final static String COUNTRY = "country";
		/** The Constant POSTAL_CODE with value postal_code. */
		public final static String POSTAL_CODE = "postal_code";
		/** The Constant LAT with value lat. */
		public final static String LAT = "lat";
		/** The Constant LONG with value long. */
		public final static String LONG = "long";

		private AddressComponents()
		{
		}
	}

	/**
	 * The Class ControlPosition defines the values of the control position used in the Google Maps
	 * options.
	 *
	 * @since 1.0.0
	 */
	public final class ControlPosition
	{
		/** The Constant BOTTOM_CENTER with value google.maps.ControlPosition.BOTTOM_CENTER */
		public final static String BOTTOM_CENTER = "google.maps.ControlPosition.BOTTOM_CENTER";
		/** The Constant BOTTOM_LEFT with value google.maps.ControlPosition.BOTTOM_LEFT */
		public final static String BOTTOM_LEFT = "google.maps.ControlPosition.BOTTOM_LEFT";
		/** The Constant BOTTOM_RIGHT with value google.maps.ControlPosition.BOTTOM_RIGHT */
		public final static String BOTTOM_RIGHT = "google.maps.ControlPosition.BOTTOM_RIGHT";
		/** The Constant LEFT_BOTTOM with value google.maps.ControlPosition.LEFT_BOTTOM */
		public final static String LEFT_BOTTOM = "google.maps.ControlPosition.LEFT_BOTTOM";
		/** The Constant LEFT_CENTER with value google.maps.ControlPosition.LEFT_CENTER */
		public final static String LEFT_CENTER = "google.maps.ControlPosition.LEFT_CENTER";
		/** The Constant LEFT_TOP with value google.maps.ControlPosition.LEFT_TOP */
		public final static String LEFT_TOP = "google.maps.ControlPosition.LEFT_TOP";
		/** The Constant RIGHT_BOTTOM with value google.maps.ControlPosition.RIGHT_BOTTOM */
		public final static String RIGHT_BOTTOM = "google.maps.ControlPosition.RIGHT_BOTTOM";
		/** The Constant RIGHT_CENTER with value google.maps.ControlPosition.RIGHT_CENTER */
		public final static String RIGHT_CENTER = "google.maps.ControlPosition.RIGHT_CENTER";
		/** The Constant RIGHT_TOP with value google.maps.ControlPosition.RIGHT_TOP */
		public final static String RIGHT_TOP = "google.maps.ControlPosition.RIGHT_TOP";
		/** The Constant TOP_CENTER with value google.maps.ControlPosition.TOP_CENTER */
		public final static String TOP_CENTER = "google.maps.ControlPosition.TOP_CENTER";
		/** The Constant TOP_LEFT with value google.maps.ControlPosition.TOP_LEFT */
		public final static String TOP_LEFT = "google.maps.ControlPosition.TOP_LEFT";
		/** The Constant TOP_RIGHT with value google.maps.ControlPosition.TOP_RIGHT */
		public final static String TOP_RIGHT = "google.maps.ControlPosition.TOP_RIGHT";

		private ControlPosition()
		{
		}
	}

	/**
	 * The Class Events defines the values of JavaScript events used by the Google Maps services.
	 *
	 * @since 1.0.0
	 */
	public final class Events
	{
		/** The Constant PLACE_CHANGED with value place_changed. */
		public final static String PLACE_CHANGED = "place_changed";
		/** The Constant CLICK with value click. */
		public final static String CLICK = "click";
		/** The Constant DRAG_END with value dragend. */
		public final static String DRAG_END = "dragend";

		private Events()
		{
		}
	}

	/**
	 * The Class Icon defines the values of default Google Maps icons to be used. Icons are url to
	 * the images.
	 *
	 * @since 1.0.0
	 */
	public final class Icons
	{
		/** The Constant RED with value http://maps.google.com/mapfiles/ms/icons/red-dot.png. */
		public final static String RED = "http://maps.google.com/mapfiles/ms/icons/red-dot.png";
		/** The Constant BLUE with value http://maps.google.com/mapfiles/ms/icons/blue-dot.png. */
		public final static String BLUE = "http://maps.google.com/mapfiles/ms/icons/blue-dot.png";
		/** The Constant GREEN with value http://maps.google.com/mapfiles/ms/icons/green-dot.png. */
		public final static String GREEN = "http://maps.google.com/mapfiles/ms/icons/green-dot.png";
		/**
		 * The Constant ORANGE with value http://maps.google.com/mapfiles/ms/icons/orange-dot.png.
		 */
		public final static String ORANGE = "http://maps.google.com/mapfiles/ms/icons/orange-dot.png";
		/**
		 * The Constant PURPLE with value http://maps.google.com/mapfiles/ms/icons/purple-dot.png.
		 */
		public final static String PURPLE = "http://maps.google.com/mapfiles/ms/icons/purple-dot.png";
		/** The Constant PINK with value http://maps.google.com/mapfiles/ms/icons/pink-dot.png. */
		public final static String PINK = "http://maps.google.com/mapfiles/ms/icons/pink-dot.png";
		/**
		 * The Constant YELLOW with value http://maps.google.com/mapfiles/ms/icons/yellow-dot.png.
		 */
		public final static String YELLOW = "http://maps.google.com/mapfiles/ms/icons/yellow-dot.png";

		private Icons()
		{
		}
	}

	/**
	 * The Class MapType defines the values of the map types used in the Google Maps options.
	 *
	 * @since 1.0.0
	 */
	public final class MapType
	{
		/** The Constant HYBRID with value google.maps.MapTypeId.HYBRID. */
		public final static String HYBRID = "google.maps.MapTypeId.HYBRID";
		/** The Constant ROADMAP with value google.maps.MapTypeId.ROADMAP. */
		public final static String ROADMAP = "google.maps.MapTypeId.ROADMAP";
		/** The Constant SATELLITE with value google.maps.MapTypeId.SATELLITE. */
		public final static String SATELLITE = "google.maps.MapTypeId.SATELLITE";
		/** The Constant TERRAIN with value google.maps.MapTypeId.TERRAIN. */
		public final static String TERRAIN = "google.maps.MapTypeId.TERRAIN";

		private MapType()
		{
		}
	}

	/**
	 * The Class Zoom defines the values of the zoom used in the Google Maps options.
	 *
	 * @since 1.0.0
	 */
	public final class Zoom
	{
		/** The Constant LEVEL_1 with value 1. */
		public final static String LEVEL_1 = "1";
		/** The Constant LEVEL_2 with value 2. */
		public final static String LEVEL_2 = "2";
		/** The Constant LEVEL_3 with value 3. */
		public final static String LEVEL_3 = "3";
		/** The Constant LEVEL_4 with value 4. */
		public final static String LEVEL_4 = "4";
		/** The Constant LEVEL_5 with value 5. */
		public final static String LEVEL_5 = "5";
		/** The Constant LEVEL_6 with value 6. */
		public final static String LEVEL_6 = "6";
		/** The Constant LEVEL_7 with value 7. */
		public final static String LEVEL_7 = "7";
		/** The Constant LEVEL_8 with value 8. */
		public final static String LEVEL_8 = "8";
		/** The Constant LEVEL_9 with value 9. */
		public final static String LEVEL_9 = "9";
		/** The Constant LEVEL_10 with value 10. */
		public final static String LEVEL_10 = "10";
		/** The Constant LEVEL_11 with value 11. */
		public final static String LEVEL_11 = "11";
		/** The Constant LEVEL_12 with value 12. */
		public final static String LEVEL_12 = "12";
		/** The Constant LEVEL_13 with value 13. */
		public final static String LEVEL_13 = "13";
		/** The Constant LEVEL_14 with value 14. */
		public final static String LEVEL_14 = "14";
		/** The Constant LEVEL_15 with value 15. */
		public final static String LEVEL_15 = "15";
		/** The Constant LEVEL_16 with value 16. */
		public final static String LEVEL_16 = "16";
		/** The Constant LEVEL_17 with value 17. */
		public final static String LEVEL_17 = "17";
		/** The Constant LEVEL_18 with value 18. */
		public final static String LEVEL_18 = "18";
		/** The Constant LEVEL_19 with value 19. */
		public final static String LEVEL_19 = "19";
		/** The Constant LEVEL_20 with value 20. */
		public final static String LEVEL_20 = "20";
		/** The Constant LEVEL_21 with value 21. */
		public final static String LEVEL_21 = "21";
		/** The Constant MIN with value 1. */
		public final static String MIN = Zoom.LEVEL_1;
		/** The Constant MAX with value 21. */
		public final static String MAX = Zoom.LEVEL_21;
		/** The Constant DEFAULT with value 2. */
		public final static String DEFAULT = Zoom.LEVEL_2;
		/** The Constant DEFAULT_SINGLE_MARKER with value 16. */
		public final static String DEFAULT_SINGLE_MARKER = Zoom.LEVEL_16;

		private Zoom()
		{
		}
	}

	/**
	 * The Class ZoomControlStyle defines the values of the style of the zoom control used in Google
	 * Maps options.
	 *
	 * @since 1.0.0
	 */
	public final class ZoomControlStyle
	{
		/** The Constant DEFAULT with value google.maps.ZoomControlStyle.DEFAULT. */
		public final static String DEFAULT = "google.maps.ZoomControlStyle.DEFAULT";
		/** The Constant LARGE with value google.maps.ZoomControlStyle.LARGE. */
		public final static String LARGE = "google.maps.ZoomControlStyle.LARGE";
		/** The Constant SMALL with value google.maps.ZoomControlStyle.SMALL. */
		public final static String SMALL = "google.maps.ZoomControlStyle.SMALL";

		private ZoomControlStyle()
		{
		}
	}

	/** The name of the JavaScript function to standardize. Could be used in your custom JS. */
	public final static String JS_FUNCNAME_STANDARDIZE = "standardize";
	/** The name of the JavaScript function fill address fields. Could be used in your custom JS. */
	public final static String JS_FUNCNAME_FILL_IN_ADDRESS = "fillInAddress";
	/** The Constant JS_FUNCNAME_SET_EBX_VALUE with value setEBXValue. */
	public final static String JS_FUNCNAME_SET_EBX_VALUE = "setEBXValue";
	/** The Constant JS_FUNCNAME_GET_EBX_VALUE with value getEBXValue. */
	public final static String JS_FUNCNAME_GET_EBX_VALUE = "getEBXValue";
	/** The name of the JavaScript function to geolocate. Could be used in your custom JS. */
	public final static String JS_FUNCNAME_GEOLOCATE = "geolocate";
	/** The name of the JavaScript function to update the map. Could be used in your custom JS. */
	public final static String JS_FUNCNAME_UPDATE_MAP = "updateMap";
	/** The name of the JavaScript function to replace a marker. Could be used in your custom JS. */
	public final static String JS_FUNCNAME_REPLACE_MARKER = "replaceMarker";
	/** The name of the JavaScript function to create a marker. Could be used in your custom JS. */
	public final static String JS_FUNCNAME_CREATE_MARKER = "createMarker";
	/**
	 * The name of the JavaScript function to create a Google Maps item. Could be used in your
	 * custom JS.
	 */
	public final static String JS_FUNCNAME_CREATE_GOOGLE_MAPS_ITEM = "createGoogleMapsItem";
	/** The Constant JS_FUNCNAME_GET_GEOCODE with value getGeocode. */
	public final static String JS_FUNCNAME_GET_GEOCODE = "getGeoCode";
	/** The Constant JS_FUNCNAME_INITIALIZE_MAP_WITH_MAKERS with value initialize. */
	public final static String JS_FUNCNAME_INITIALIZE_MAP_WITH_MAKERS = "initialize";
	/** The Constant JS_FUNCNAME_ADD_MARKERS_FROM_ITEMS with value addMarkersFromItems. */
	public final static String JS_FUNCNAME_ADD_MARKERS_FROM_ITEMS = "addMarkersFromItems";

	private final static String JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION = "addressComponentsDefinition";
	private final static String JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION_INTERNAL_NAME = "internalName";
	private final static String JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION_TYPE = "type";
	private final static String JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION_INPUT_ID = "inputId";
	private final static String JS_VARNAME_NUMBER = "number";
	private final static String JS_VARNAME_STREET = "street";
	private final static String JS_VARNAME_ZIPCODE = "zipcode";
	private final static String JS_VARNAME_CITY = "city";
	private final static String JS_VARNAME_REGION = "region";
	private final static String JS_VARNAME_COUNTRY = "country";
	private final static String JS_VARNAME_LAT = "lat";
	private final static String JS_VARNAME_LONG = "long";
	private final static String JS_VARNAME_SET_EBX_VALUE_TYPE = "type";
	private final static String JS_VARNAME_GET_EBX_VALUE_TYPE = "type";
	private final static String JS_VARNAME_SET_EBX_VALUE_VALUE = "value";
	private final static String JS_VARNAME_EVENT_ARGUMENT_IN_LISTENER = "event";

	private final static String JS_VARNAME_PREFIX_AUTOCOMPLETE = "autocomplete";
	private final static String JS_VARNAME_PREFIX_MAP = "map";
	private final static String JS_VARNAME_PREFIX_ITEMS = "items";
	private final static String JS_VARNAME_PREFIX_MAPDIV = "mapDiv";
	private final static String JS_VARNAME_PREFIX_INFOWINDOW = "infowindow";
	private final static String JS_VARNAME_PREFIX_BOUNDS = "bounds";
	private final static String JS_VARNAME_PREFIX_MARKERS = "markers";

	private final static String AUTOCOMPLETE_FIELD_DEFAULT_STYLE = "width:300px;height:30px;background-color:#F0F0F0;";

	private final static String GOOGLE_MAPS_SCRIPT_URL = "https://maps.googleapis.com/maps/api/js?v=3.26&libraries=places";
	private final static String GOOGLE_MAPS_API_KEY = "AIzaSyAng856k1YnuGu1ftwxofKQYZ_UVroZfTE";

	/**
	 * Gets the function impl_ create marker.
	 *
	 * @param pMap the map
	 * @param pLatLng the lat lng
	 * @param pIcon the icon
	 * @param pInfoWindow the info window
	 * @param pContent the content
	 * @param pMarkersArray the markers array
	 * @return the function impl_ create marker
	 * @since 1.0.0
	 */
	public static String getFunctionImpl_CreateMarker(
		final String pMap,
		final String pLatLng,
		final String pIcon,
		final String pInfoWindow,
		final String pContent,
		final String pMarkersArray)
	{
		return GoogleMaps.JS_FUNCNAME_CREATE_MARKER + "(" + pMap + ", " + pLatLng + ", " + pIcon
			+ ", " + pInfoWindow + ", " + pContent + ", " + pMarkersArray + ");";
	}

	/**
	 * Gets the JavaScript variable name of a dedicated autocomplete.
	 *
	 * @param pAutocompleteId the id of the autocomplete input
	 * @return the autocomplete var name
	 * @since 1.0.0
	 */
	public static String getVarNameAutocomplete(final String pAutocompleteId)
	{
		return GoogleMaps.JS_VARNAME_PREFIX_AUTOCOMPLETE + "_" + pAutocompleteId;
	}

	/**
	 * Gets the JavaScript variable name of the bounds for a dedicated map.
	 *
	 * @param pMapId the id of the map container
	 * @return the JavaScript variable name of the bounds
	 * @since 1.0.0
	 */
	public static String getVarNameBounds(final String pMapId)
	{
		return GoogleMaps.JS_VARNAME_PREFIX_BOUNDS + "_" + pMapId;
	}

	/**
	 * Gets the JavaScript variable name of the info window for a dedicated map.
	 *
	 * @param pMapId the id of the map container
	 * @return the JavaScript variable name of the info window
	 * @since 1.0.0
	 */
	public static String getVarNameInfoWindow(final String pMapId)
	{
		return GoogleMaps.JS_VARNAME_PREFIX_INFOWINDOW + "_" + pMapId;
	}

	/**
	 * Gets the JavaScript variable name of the items list for a dedicated map.
	 *
	 * @param pMapId the id of the map container
	 * @return the JavaScript variable name of the items list
	 * @since 1.0.0
	 */
	public static String getVarNameItems(final String pMapId)
	{
		return GoogleMaps.JS_VARNAME_PREFIX_ITEMS + "_" + pMapId;
	}

	/**
	 * Gets the JavaScript variable name of a dedicated map.
	 *
	 * @param pMapId the id of the map container
	 * @return the JavaScript variable name of the map
	 * @since 1.0.0
	 */
	public static String getVarNameMap(final String pMapId)
	{
		return GoogleMaps.JS_VARNAME_PREFIX_MAP + "_" + pMapId;
	}

	/**
	 * Gets the JavaScript variable name of the div container of a dedicated map.
	 *
	 * @param pMapId the id of the map container
	 * @return the JavaScript variable name of the map div element
	 * @since 1.0.0
	 */
	public static String getVarNameMapDiv(final String pMapId)
	{
		return GoogleMaps.JS_VARNAME_PREFIX_MAPDIV + "_" + pMapId;
	}

	/**
	 * Gets the JavaScript variable name of the markers list for a dedicated map.
	 *
	 * @param pMapId the id of the map container
	 * @return the JavaScript variable name of the markers list
	 * @since 1.0.0
	 */
	public static String getVarNameMarkers(final String pMapId)
	{
		return GoogleMaps.JS_VARNAME_PREFIX_MARKERS + "_" + pMapId;
	}

	/**
	 * Gets the random id.
	 *
	 * @return the random id
	 * @since 1.0.0
	 */
	private static String getRandomID()
	{
		String id = UUID.randomUUID().toString();
		return id.substring(0, 5);
	}

	private GoogleMapsAddressFieldMapping mapping;
	private GoogleMapsOptions options = new GoogleMapsOptions();
	private boolean googleMapsScriptAlreadyAdded = false;
	private boolean functionGeolocateAlreadyAdded = false;
	private boolean functionFillInAddressAlreadyAdded = false;
	private boolean functionSetValueAlreadyAdded = false;
	private boolean functionGetValueAlreadyAdded = false;
	private boolean functionStandardizeAlreadyAdded = false;
	private boolean functionUpdateMapAlreadyAdded = false;
	private boolean functionReplaceMarkerAlreadyAdded = false;
	private boolean functionCreateGoogleMapsItemAlreadyAdded = false;
	private boolean functionCreateMarkerAlreadyAdded = false;
	private boolean functionGetGeoCodeAlreadyAdded = false;
	private boolean functionAddMarkersFromItemsAlreadyAdded = false;

	/**
	 * Instantiates a new google maps.
	 *
	 * @since 1.0.0
	 */
	public GoogleMaps()
	{
	}

	/**
	 * Instantiates a new google maps.
	 *
	 * @param mapping the google maps mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping
	 * @since 1.0.0
	 */
	public GoogleMaps(final GoogleMapsAddressFieldMapping mapping)
	{
		this.mapping = mapping;
	}

	/**
	 * Instantiates a new google maps.
	 *
	 * @param mapping the google maps mapping
	 * @param options the google maps options
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsOptions
	 * @since 1.0.0
	 */
	public GoogleMaps(final GoogleMapsAddressFieldMapping mapping, final GoogleMapsOptions options)
	{
		this.mapping = mapping;
		this.options = options;
	}

	/**
	 * Instantiates a new google maps.
	 *
	 * @param options the google maps options
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsOptions
	 * @since 1.0.0
	 */
	public GoogleMaps(final GoogleMapsOptions options)
	{
		this.options = options;
	}

	/**
	 * Insert the Google Place Autocomplete field in the stream of the UIComponentWriter. The
	 * AddressMapping should be filled.
	 *
	 * @param pWriter the writer
	 * @param pPlaceholder The placeholder of the input.
	 * @param pStyle The custom style of the field (ex: "width:400px").
	 * @param pMapId the id of the related map container
	 * @return the (randomly generated) id of the autocomplete input field
	 * @since 1.0.0
	 */
	public String insertAutocompleteAddressField(
		final UIComponentWriter pWriter,
		final String pPlaceholder,
		final String pStyle,
		final String pMapId)
	{
		String id = GoogleMaps.getRandomID();
		this.insertAutocompleteAddressField(pWriter, pPlaceholder, pStyle, pMapId, id, null);
		return id;
	}

	/**
	 * Insert the Google Place Autocomplete field in the stream of the UIComponentWriter. The
	 * AddressMapping should be filled.
	 *
	 * @param pWriter the writer
	 * @param pPlaceholder The placeholder of the input.
	 * @param pStyle The custom style of the field (ex: "width:400px").
	 * @param pMapId the id of the related map container
	 * @param pCustomOptions the custom options
	 * @return the (randomly generated) id of the autocomplete input field
	 * @since 1.0.0
	 */
	public String insertAutocompleteAddressField(
		final UIComponentWriter pWriter,
		final String pPlaceholder,
		final String pStyle,
		final String pMapId,
		final GoogleMapsOptions pCustomOptions)
	{
		String id = GoogleMaps.getRandomID();
		this.insertAutocompleteAddressField(
			pWriter,
			pPlaceholder,
			pStyle,
			pMapId,
			id,
			pCustomOptions);
		return id;
	}

	/**
	 * Insert the Google Place Autocomplete field in the stream of the UIComponentWriter. The
	 * AddressMapping should be filled.
	 *
	 * @param pWriter the writer
	 * @param pPlaceholder The placeholder of the input.
	 * @param pStyle The custom style of the field (ex: "width:400px").
	 * @param pMapId the id of the related map container
	 * @param pFieldId the id of the autocomplete input field
	 * @since 1.0.0
	 */
	public void insertAutocompleteAddressField(
		final UIComponentWriter pWriter,
		final String pPlaceholder,
		final String pStyle,
		final String pMapId,
		final String pFieldId)
	{
		this.insertAutocompleteAddressField(pWriter, pPlaceholder, pStyle, pMapId, pFieldId, null);
	}

	/**
	 * Insert the Google Place Autocomplete field in the stream of the UIComponentWriter. The
	 * AddressMapping should be filled.
	 *
	 * @param pWriter the writer
	 * @param pPlaceholder The placeholder of the input.
	 * @param pStyle The custom style of the field (ex: "width:400px").
	 * @param pMapId the id of the related map container
	 * @param pFieldId the id of the autocomplete input field
	 * @param pCustomOptions the custom options
	 * @since 1.0.0
	 */
	public void insertAutocompleteAddressField(
		final UIComponentWriter pWriter,
		final String pPlaceholder,
		final String pStyle,
		final String pMapId,
		final String pFieldId,
		GoogleMapsOptions pCustomOptions)
	{
		if (pCustomOptions == null)
		{
			pCustomOptions = this.getOptions();
		}
		Locale locale = pCustomOptions.getLocale();
		this.insertAutocompleteInputField(pWriter, pPlaceholder, pStyle, pFieldId);
		this.insertAutocompleteHiddenInputs(pWriter, pFieldId);
		this.insertGoogleMapsScript(pWriter, locale);
		this.addJsFunctionsForAutocomplete(pWriter, pMapId, pFieldId, pCustomOptions);
	}

	/**
	 * Insert the google maps component in the stream of the UIComponentWriter. The address
	 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressValue} passed as
	 * arguments is added as marker on the map.
	 *
	 * @param pWriter the writer
	 * @param pAddressValue the address value to insert on the map
	 * @param pCustomStyle the custom style (ex: "width:200px;height:200px")
	 * @return the (randomly generated) id of the inserted div of the map
	 * @since 1.0.0
	 */
	public String insertMap(
		final UIComponentWriter pWriter,
		final GoogleMapsAddressValue pAddressValue,
		final String pCustomStyle)
	{
		String iframeMapId = GoogleMaps.getRandomID();
		this.insertMap(pWriter, pAddressValue, pCustomStyle, null, iframeMapId);
		return iframeMapId;
	}

	/**
	 * Insert the google maps component in the stream of the UIComponentWriter. The address
	 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressValue} passed as
	 * arguments is added as marker on the map.
	 *
	 * @param pWriter the writer
	 * @param pAddressValue the address value to insert on the map
	 * @param pCustomStyle the custom style (ex: "width:200px;height:200px")
	 * @param pCustomOptions the custom options
	 * @return the (randomly generated) id of the inserted div of the map
	 * @since 1.0.0
	 */
	public String insertMap(
		final UIComponentWriter pWriter,
		final GoogleMapsAddressValue pAddressValue,
		final String pCustomStyle,
		final GoogleMapsOptions pCustomOptions)
	{
		String iframeMapId = GoogleMaps.getRandomID();
		this.insertMap(pWriter, pAddressValue, pCustomStyle, pCustomOptions, iframeMapId);
		return iframeMapId;
	}

	/**
	 * Insert the google maps component in the stream of the UIComponentWriter. The address
	 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressValue} passed as
	 * arguments is added as marker on the map.
	 *
	 * @param pWriter the writer
	 * @param pAddressValue the address value to insert on the map
	 * @param pCustomStyle the custom style (ex: "width:200px;height:200px")
	 * @param pCustomOptions the custom options
	 * @param pMapId the id of the inserted div of the map
	 * @since 1.0.0
	 */
	public void insertMap(
		final UIComponentWriter pWriter,
		final GoogleMapsAddressValue pAddressValue,
		final String pCustomStyle,
		final GoogleMapsOptions pCustomOptions,
		final String pMapId)
	{
		List<GoogleMapsItem> items = new ArrayList<GoogleMapsItem>();
		if (pAddressValue != null)
		{
			items.add(this.createGoogleMapsItemFromAddressValue(pAddressValue));
		}
		this.insertMapWithMarkers(pWriter, items, pCustomStyle, pCustomOptions, pMapId);
	}

	/**
	 * Insert the google maps component in the stream of the UIComponentWriter. The address
	 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressValue} passed as
	 * arguments is added as marker on the map.
	 *
	 * @param pWriter the writer
	 * @param pAddressValue the address value to insert on the map
	 * @param pCustomStyle the custom style (ex: "width:200px;height:200px")
	 * @param pMapId the id of the inserted div of the map
	 * @since 1.0.0
	 */
	public void insertMap(
		final UIComponentWriter pWriter,
		final GoogleMapsAddressValue pAddressValue,
		final String pCustomStyle,
		final String pMapId)
	{
		this.insertMap(pWriter, pAddressValue, pCustomStyle, null, pMapId);
	}

	/**
	 * Insert the google maps component in the stream of the UIComponentWriter. The items
	 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsItem} passed as arguments
	 * are added as markers on the map.
	 *
	 * @param pWriter the writer
	 * @param pItems the items to insert on the map
	 * @param pCustomStyle the custom style (ex: "width:200px;height:200px")
	 * @return the (randomly generated) id of the inserted div of the map
	 * @since 1.0.0
	 */
	public String insertMapWithMarkers(
		final UIComponentWriter pWriter,
		final List<GoogleMapsItem> pItems,
		final String pCustomStyle)
	{
		String id = GoogleMaps.getRandomID();
		this.insertMapWithMarkers(pWriter, pItems, pCustomStyle, null, id);
		return id;
	}

	/**
	 * Insert the google maps component in the stream of the UIComponentWriter. The items
	 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsItem} passed as arguments
	 * are added as markers on the map.
	 *
	 * @param pWriter the writer
	 * @param pItems the items to insert on the map
	 * @param pCustomStyle the custom style (ex: "width:200px;height:200px")
	 * @param pCustomOptions the custom options
	 * @return the (randomly generated) id of the inserted div of the map
	 * @since 1.0.0
	 */
	public String insertMapWithMarkers(
		final UIComponentWriter pWriter,
		final List<GoogleMapsItem> pItems,
		final String pCustomStyle,
		final GoogleMapsOptions pCustomOptions)
	{
		String id = GoogleMaps.getRandomID();
		this.insertMapWithMarkers(pWriter, pItems, pCustomStyle, pCustomOptions, id);
		return id;
	}

	/**
	 * Insert the google maps component in the stream of the UIComponentWriter. The items
	 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsItem} passed as arguments
	 * are added as markers on the map.
	 *
	 * @param pWriter the writer
	 * @param pItems the items to insert on the map
	 * @param pCustomStyle the custom style (ex: "width:200px;height:200px")
	 * @param pCustomOptions the custom options
	 * @param pMapId the id of the inserted div of the map
	 * @since 1.0.0
	 */
	public void insertMapWithMarkers(
		final UIComponentWriter pWriter,
		final List<GoogleMapsItem> pItems,
		final String pCustomStyle,
		GoogleMapsOptions pCustomOptions,
		final String pMapId)
	{
		if (pCustomOptions == null)
		{
			pCustomOptions = this.getOptions();
		}
		Locale locale = pCustomOptions.getLocale();
		this.insertMapDiv(pWriter, pCustomStyle, pMapId);
		this.insertGoogleMapsScript(pWriter, locale);
		this.addVariableMapItems(pWriter, pItems, pMapId);
		this.addJsFunctionForMapsWithMakers(pWriter, pMapId, pCustomOptions);
	}

	/**
	 * Insert the google maps component in the stream of the UIComponentWriter. The items
	 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsItem} passed as arguments
	 * are added as markers on the map.
	 *
	 * @param pWriter the writer
	 * @param pItems the items to insert on the map
	 * @param pCustomStyle the custom style (ex: "width:200px;height:200px")
	 * @param pMapId the id of the inserted div of the map
	 * @since 1.0.0
	 */
	public void insertMapWithMarkers(
		final UIComponentWriter pWriter,
		final List<GoogleMapsItem> pItems,
		final String pCustomStyle,
		final String pMapId)
	{
		this.insertMapWithMarkers(pWriter, pItems, pCustomStyle, null, pMapId);
	}

	/**
	 * Insert an EBX JavaScript button to launch the standardization of the address. A Google Place
	 * Autocomplete component is required.
	 *
	 * @param pWriter the writer
	 * @param pLabel the label fo the button
	 * @param pAutocompleteFieldId the id of the related autocomplete field
	 * @return the (randomly generated) id of the button.
	 * @since 1.0.0
	 */
	public String insertStandardizeAddressButton(
		final UIComponentWriter pWriter,
		final UserMessage pLabel,
		final String pAutocompleteFieldId)
	{
		String id = GoogleMaps.getRandomID();
		this.insertStandardizeAddressButton(pWriter, pLabel, pAutocompleteFieldId, id);
		return id;
	}

	/**
	 * Insert an EBX JavaScript button to launch the standardization of the address. A Google Place
	 * Autocomplete component is required.
	 *
	 * @param pWriter the writer
	 * @param pLabel the label fo the button
	 * @param pAutocompleteFieldId the id of the related autocomplete field
	 * @param pButtonId the id of the button
	 * @since 1.0.0
	 */
	public void insertStandardizeAddressButton(
		final UIComponentWriter pWriter,
		final UserMessage pLabel,
		final String pAutocompleteFieldId,
		final String pButtonId)
	{
		String standardizeFunctionName = this.addFunctionStandardize(pWriter);
		String buttonCommand = standardizeFunctionName + "('" + pAutocompleteFieldId + "')";

		UIButtonSpecJSAction buttonSpec = new UIButtonSpecJSAction(pLabel, buttonCommand);
		buttonSpec.setId(pButtonId);

		pWriter.addButtonJavaScript(buttonSpec);
	}

	/**
	 * Set the mapping to the address components expected by Google Maps.<br>
	 * The mapping can be defined from the paths of the data model (in the case Google Maps is used
	 * in a UIFormPane) or by using input html tag (in case of a UI Service).
	 *
	 * @param mapping the new address mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping
	 * @since 1.0.0
	 */
	public void setAddressMapping(final GoogleMapsAddressFieldMapping mapping)
	{
		this.mapping = mapping;
	}

	/**
	 * Sets the options.
	 *
	 * @param options the new options
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsOptions
	 * @since 1.0.0
	 */
	public void setOptions(final GoogleMapsOptions options)
	{
		this.options = options;
	}

	/**
	 * Adds the function add multiple maps items.
	 *
	 * @param pWriter the writer
	 * @return the string
	 * @since 1.0.0
	 */
	private String addFunctionAddMarkersFromItems(final UIComponentWriter pWriter)
	{
		String functionName = GoogleMaps.JS_FUNCNAME_ADD_MARKERS_FROM_ITEMS;
		if (this.functionAddMarkersFromItemsAlreadyAdded)
		{
			return functionName;
		}

		pWriter.addJS_cr();
		pWriter.addJS_cr(
			"function " + functionName + "(map, items, markersArray, infowindow, done) {");

		pWriter.addJS_cr("    var bounds = new google.maps.LatLngBounds();");
		pWriter.addJS_cr("    var itemCounter = 0;");

		pWriter.addJS_cr("    var nbItems = items.length;");
		pWriter.addJS_cr("    for (var i = 0; i < nbItems; i++) {");
		pWriter.addJS_cr("        var location = items[i];");
		pWriter.addJS_cr("        (function(item) {");
		pWriter.addJS_cr("            if (item." + GoogleMapsItem.JsObject.ADDRESSVALUE + ") {");
		pWriter.addJS_cr(
			"                if (item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.LAT + " && item."
				+ GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.LONG + ") {");
		pWriter.addJS_cr(
			"                    var latLng = new google.maps.LatLng(item."
				+ GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.LAT + ", item."
				+ GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.LONG + ", false);");
		pWriter.addJS_cr(
			"                    " + GoogleMaps.JS_FUNCNAME_CREATE_MARKER + "(map, latLng, item."
				+ GoogleMapsItem.JsObject.ICON + ", infowindow, item."
				+ GoogleMapsItem.JsObject.CONTENT + ", markersArray);");
		pWriter.addJS_cr("                    bounds.extend(latLng);");
		pWriter.addJS_cr("                    itemCounter++;");
		pWriter.addJS_cr("                    if (itemCounter === nbItems) {");
		pWriter.addJS_cr("                        done(bounds);");
		pWriter.addJS_cr("                    }");
		pWriter.addJS_cr(
			"                } else if (item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.CONCATENATED_ADDRESS + ") {");
		pWriter.addJS_cr(
			"                    " + GoogleMaps.JS_FUNCNAME_GET_GEOCODE + "(item."
				+ GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.CONCATENATED_ADDRESS
				+ ", function(results, status) {");
		pWriter.addJS_cr("                        if (status === google.maps.GeocoderStatus.OK) {");
		pWriter.addJS_cr("                            var latLng = results[0].geometry.location;");
		pWriter.addJS_cr(
			"                            " + GoogleMaps.JS_FUNCNAME_CREATE_MARKER
				+ "(map, latLng, item." + GoogleMapsItem.JsObject.ICON + ", infowindow, item."
				+ GoogleMapsItem.JsObject.CONTENT + ", markersArray);");
		pWriter.addJS_cr("                            bounds.extend(latLng);");
		pWriter.addJS_cr("                        }");
		pWriter.addJS_cr("                        itemCounter++;");
		pWriter.addJS_cr("                        if (itemCounter === nbItems) {");
		pWriter.addJS_cr("                            done(bounds);");
		pWriter.addJS_cr("                        }");
		pWriter.addJS_cr("                    });");
		pWriter.addJS_cr("                }");
		pWriter.addJS_cr("            }");
		pWriter.addJS_cr("        })(location);");
		pWriter.addJS_cr("    }");

		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		this.functionAddMarkersFromItemsAlreadyAdded = true;
		return functionName;
	}

	/**
	 * Adds the function create google maps item.
	 *
	 * @param pWriter the writer
	 * @param pCustomOptions the options
	 * @return the string
	 * @since 1.0.0
	 */
	private String addFunctionCreateGoogleMapsItem(
		final UIComponentWriter pWriter,
		final GoogleMapsOptions pCustomOptions)
	{
		String functionName = GoogleMaps.JS_FUNCNAME_CREATE_GOOGLE_MAPS_ITEM;
		if (this.functionCreateGoogleMapsItemAlreadyAdded)
		{
			return functionName;
		}

		pWriter.addJS_cr();
		pWriter
			.addJS_cr("function " + functionName + "(pk, label, latLng, address, content, icon) {");
		pWriter.addJS_cr("    var item = {};");
		pWriter.addJS_cr("    item." + GoogleMapsItem.JsObject.PK + " = pk;");
		pWriter.addJS_cr("    item." + GoogleMapsItem.JsObject.LABEL + " = label;");
		pWriter.addJS_cr("    item." + GoogleMapsItem.JsObject.CONTENT + " = content;");
		pWriter.addJS_cr("    item." + GoogleMapsItem.JsObject.ICON + " = icon;");
		pWriter.addJS_cr("    item." + GoogleMapsItem.JsObject.ADDRESSVALUE + " = {};");
		pWriter.addJS_cr("    if (address) {");
		pWriter.addJS_cr("        address.forEach(function(component) {");

		pWriter.addJS_cr(
			"            if(component.types.indexOf('" + GoogleMaps.AddressComponents.STREET_NUMBER
				+ "') >= 0) {");
		pWriter.addJS_cr(
			"                item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.NUMBER + " = component."
				+ pCustomOptions.getAddressComponentType(GoogleMaps.AddressComponents.STREET_NUMBER)
				+ ";");

		pWriter.addJS_cr(
			"            } else if(component.types.indexOf('" + GoogleMaps.AddressComponents.ROUTE
				+ "') >= 0) {");
		pWriter.addJS_cr(
			"                item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.STREET + " = component."
				+ pCustomOptions.getAddressComponentType(GoogleMaps.AddressComponents.ROUTE) + ";");

		pWriter.addJS_cr(
			"            } else if(component.types.indexOf('"
				+ GoogleMaps.AddressComponents.ADMINISTRATIVE_AREA_1 + "') >= 0) {");
		pWriter.addJS_cr(
			"                item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.REGION + " = component." + pCustomOptions
					.getAddressComponentType(GoogleMaps.AddressComponents.ADMINISTRATIVE_AREA_1)
				+ ";");

		pWriter.addJS_cr(
			"            } else if(component.types.indexOf('"
				+ GoogleMaps.AddressComponents.POSTAL_CODE + "') >= 0) {");
		pWriter.addJS_cr(
			"                item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.ZIPCODE + " = component."
				+ pCustomOptions.getAddressComponentType(GoogleMaps.AddressComponents.POSTAL_CODE)
				+ ";");

		pWriter.addJS_cr(
			"            } else if(component.types.indexOf('"
				+ GoogleMaps.AddressComponents.LOCALITY + "') >= 0) {");
		pWriter.addJS_cr(
			"                item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.CITY + " = component."
				+ pCustomOptions.getAddressComponentType(GoogleMaps.AddressComponents.LOCALITY)
				+ ";");

		pWriter.addJS_cr(
			"            } else if(component.types.indexOf('" + GoogleMaps.AddressComponents.COUNTRY
				+ "') >= 0) {");
		pWriter.addJS_cr(
			"                item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.COUNTRY + " = component."
				+ pCustomOptions.getAddressComponentType(GoogleMaps.AddressComponents.COUNTRY)
				+ ";");
		pWriter.addJS_cr("            }");

		pWriter.addJS_cr("        });");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr(
			"    item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.CONCATENATED_ADDRESS + " = null;");
		pWriter.addJS_cr(
			"    item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.LAT + " = latLng.lat();");
		pWriter.addJS_cr(
			"    item." + GoogleMapsItem.JsObject.ADDRESSVALUE + "."
				+ GoogleMapsItem.JsObject.AddressValue.LONG + " = latLng.lng();");
		pWriter.addJS_cr("    return item;");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		this.functionCreateGoogleMapsItemAlreadyAdded = true;
		return functionName;
	}

	/**
	 * Adds the function create marker.
	 *
	 * @param pWriter the writer
	 * @param pCustomOptions the options
	 * @return the string
	 * @since 1.0.0
	 */
	private String addFunctionCreateMarker(
		final UIComponentWriter pWriter,
		final GoogleMapsOptions pCustomOptions)
	{
		String functionName = GoogleMaps.JS_FUNCNAME_CREATE_MARKER;
		if (this.functionCreateMarkerAlreadyAdded)
		{
			return functionName;
		}

		String markerVarName = "marker";

		pWriter.addJS_cr();
		pWriter.addJS_cr(
			"function " + functionName
				+ "(map, latLng, icon, infowindow, content, markersArray) {");
		pWriter.addJS_cr("    var " + markerVarName + " = new google.maps.Marker({");
		pWriter.addJS_cr("        map: map,");
		pWriter.addJS_cr("        position: latLng,");
		//pWriter.addJS_cr("        draggable: true,"); // TODO Handle draggable marker
		pWriter.addJS_cr("        icon: icon || '" + pCustomOptions.getDefaultIcon() + "'");
		pWriter.addJS_cr("    });");
		pWriter.addJS_cr("    if (content && infowindow) {");

		String eventFunctionContent = "";
		eventFunctionContent += "infowindow.setContent(content);";
		eventFunctionContent += "infowindow.open(map, " + markerVarName + ");";
		this.addGoogleMapsEventListener(pWriter, markerVarName, Events.CLICK, eventFunctionContent);

		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("    if (markersArray) {");
		pWriter.addJS_cr("        markersArray.push(" + markerVarName + ");");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("    return " + markerVarName + ";");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		this.functionCreateMarkerAlreadyAdded = true;
		return functionName;
	}

	/**
	 * Adds the function fill in address.
	 *
	 * @param pWriter the writer
	 * @return the function name
	 * @since 1.0.0
	 */
	private String addFunctionFillInAddress(final UIComponentWriter pWriter)
	{
		String functionName = GoogleMaps.JS_FUNCNAME_FILL_IN_ADDRESS;
		if (this.functionFillInAddressAlreadyAdded)
		{
			return functionName;
		}

		this.addFunctionSetValue(pWriter);

		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + functionName + "(placeResult) {");
		pWriter.addJS_cr(
			"    for (var component in " + GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION
				+ ") {");
		pWriter.addJS_cr(
			"        var inputId = " + GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION
				+ "[component].inputId;");
		pWriter.addJS_cr("        var componentElement = document.getElementById(inputId);");
		pWriter.addJS_cr("        if (componentElement) {");
		pWriter.addJS_cr("            componentElement.value = '';");
		pWriter.addJS_cr("        }");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr();
		pWriter.addJS_cr("    var nbAddressComponents = placeResult.address_components.length;");
		pWriter.addJS_cr("    for (var i = 0; i < nbAddressComponents; i++) {");
		pWriter.addJS_cr("        var addressType = placeResult.address_components[i].types[0];");
		pWriter.addJS_cr(
			"        var componentDef = " + GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION
				+ "[addressType];");
		pWriter.addJS_cr("        if (componentDef) {");
		pWriter.addJS_cr(
			"            var valueType = componentDef."
				+ GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION_TYPE + ";");
		pWriter.addJS_cr(
			"            var componentValue = placeResult.address_components[i][valueType];");
		pWriter.addJS_cr(
			"            document.getElementById(componentDef."
				+ GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION_INPUT_ID
				+ ").value = componentValue;");
		pWriter.addJS_cr("            var internalName = componentDef.internalName;");
		pWriter.addJS_cr(
			"            " + GoogleMaps.JS_FUNCNAME_SET_EBX_VALUE
				+ "(internalName, componentValue);");
		pWriter.addJS_cr("        }");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr();
		pWriter.addJS_cr(
			"    var latDef = " + GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION + "['"
				+ GoogleMaps.AddressComponents.LAT + "'];");
		pWriter.addJS_cr("    if (latDef) {");
		pWriter.addJS_cr(
			"        " + GoogleMaps.JS_VARNAME_LAT + " = placeResult.geometry.location.lat();");
		pWriter.addJS_cr(
			"        document.getElementById(latDef."
				+ GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION_INPUT_ID + ").value = "
				+ GoogleMaps.JS_VARNAME_LAT + ";");
		pWriter.addJS_cr(
			"        " + GoogleMaps.JS_FUNCNAME_SET_EBX_VALUE + "('" + GoogleMaps.JS_VARNAME_LAT
				+ "', " + GoogleMaps.JS_VARNAME_LAT + ");");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr();
		pWriter.addJS_cr(
			"    var longDef = " + GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION + "['"
				+ GoogleMaps.AddressComponents.LAT + "'];");
		pWriter.addJS_cr("    if (longDef) {");
		pWriter.addJS_cr(
			"        " + GoogleMaps.JS_VARNAME_LONG + " = placeResult.geometry.location.lng();");
		pWriter.addJS_cr(
			"        document.getElementById(longDef."
				+ GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION_INPUT_ID + ").value = "
				+ GoogleMaps.JS_VARNAME_LONG + ";");
		pWriter.addJS_cr(
			"        " + GoogleMaps.JS_FUNCNAME_SET_EBX_VALUE + "('" + GoogleMaps.JS_VARNAME_LONG
				+ "', " + GoogleMaps.JS_VARNAME_LONG + ");");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr();
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		this.functionFillInAddressAlreadyAdded = true;
		return functionName;
	}

	/**
	 * Adds the function geolocate.
	 *
	 * @param pWriter the writer
	 * @return the function name
	 * @since 1.0.0
	 */
	@SuppressWarnings("unused")
	private String addFunctionGeolocate(final UIComponentWriter pWriter)
	{
		String functionName = GoogleMaps.JS_FUNCNAME_GEOLOCATE;
		if (this.functionGeolocateAlreadyAdded)
		{
			return functionName;
		}
		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + functionName + "() {");
		pWriter.addJS_cr("    if (navigator.geolocation) {");
		pWriter.addJS_cr("        navigator.geolocation.getCurrentPosition(function(position) {");
		pWriter.addJS_cr("			var lat = position.coords.latitude;");
		pWriter.addJS_cr("			var long = position.coords.longitude;");
		pWriter.addJS_cr("			return new google.maps.LatLng(lat, long);");
		pWriter.addJS_cr("        });");
		pWriter.addJS_cr("    } else {");
		pWriter.addJS_cr("        return null;");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		this.functionGeolocateAlreadyAdded = true;
		return functionName;
	}

	/**
	 * Adds the function get fk from value.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addFunctionGetFKFromValue(final UIFormPaneWriter pWriter)
	{
		if (this.mapping.getStreetFKMapping() != null)
		{
			this.mapping.getStreetFKMapping().insertJsFunctionGetFKFromValue(pWriter);
			return;
		}

		if (this.mapping.getZipcodeFKMapping() != null)
		{
			this.mapping.getZipcodeFKMapping().insertJsFunctionGetFKFromValue(pWriter);
			return;
		}

		if (this.mapping.getCityFKMapping() != null)
		{
			this.mapping.getCityFKMapping().insertJsFunctionGetFKFromValue(pWriter);
			return;
		}

		if (this.mapping.getCountryFKMapping() != null)
		{
			this.mapping.getCountryFKMapping().insertJsFunctionGetFKFromValue(pWriter);
			return;
		}
	}

	/**
	 * Adds the function get geo code.
	 *
	 * @param pWriter the writer
	 * @return the string
	 * @since 1.0.0
	 */
	private String addFunctionGetGeoCode(final UIComponentWriter pWriter)
	{
		String functionName = GoogleMaps.JS_FUNCNAME_GET_GEOCODE;
		if (this.functionGetGeoCodeAlreadyAdded)
		{
			return functionName;
		}

		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + functionName + "(address, callback) {");
		pWriter.addJS_cr("    var geocoder = new google.maps.Geocoder();");
		pWriter.addJS_cr("    geocoder.geocode({address: address}, function(results, status) {");
		pWriter.addJS_cr("        callback(results, status);");
		pWriter.addJS_cr("    });");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		this.functionGetGeoCodeAlreadyAdded = true;
		return functionName;
	}

	/**
	 * Adds the function get value.
	 *
	 * @param pWriter the writer
	 * @return the string
	 * @since 1.0.0
	 */
	private String addFunctionGetValue(final UIComponentWriter pWriter)
	{
		String functionName = GoogleMaps.JS_FUNCNAME_GET_EBX_VALUE;
		if (this.functionGetValueAlreadyAdded)
		{
			return functionName;
		}

		pWriter.addJS_cr();
		pWriter.addJS_cr(
			"function " + functionName + "(" + GoogleMaps.JS_VARNAME_GET_EBX_VALUE_TYPE + ") {");

		if (this.mapping != null)
		{
			if (this.mapping.isUIFormPane())
			{
				this.addGetNodeValues((UIFormPaneWriter) pWriter);
			}
			else
			{
				this.addGetInputValues(pWriter);
			}
		}
		else
		{
			pWriter.addJS_cr("    return null;");
		}

		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		this.functionGetValueAlreadyAdded = true;
		return functionName;
	}

	/**
	 * Adds the function get value from fk.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addFunctionGetValueFromFK(final UIFormPaneWriter pWriter)
	{
		if (this.mapping.getStreetFKMapping() != null)
		{
			this.mapping.getStreetFKMapping().insertJsFunctionGetValueFromFK(pWriter);
			;
			return;
		}

		if (this.mapping.getZipcodeFKMapping() != null)
		{
			this.mapping.getZipcodeFKMapping().insertJsFunctionGetValueFromFK(pWriter);
			return;
		}

		if (this.mapping.getCityFKMapping() != null)
		{
			this.mapping.getCityFKMapping().insertJsFunctionGetValueFromFK(pWriter);
			return;
		}

		if (this.mapping.getRegionFKMapping() != null)
		{
			this.mapping.getRegionFKMapping().insertJsFunctionGetValueFromFK(pWriter);
			return;
		}

		if (this.mapping.getCountryFKMapping() != null)
		{
			this.mapping.getCountryFKMapping().insertJsFunctionGetValueFromFK(pWriter);
			return;
		}
	}

	/**
	 * Adds the function initialize.
	 *
	 * @param pWriter the writer
	 * @param pAutocompleteId the autocomplete id
	 * @param pMapId the map id
	 * @param pCustomOptions the options
	 * @since 1.0.0
	 */
	private void addFunctionInitializeAutocomplete(
		final UIComponentWriter pWriter,
		final String pAutocompleteId,
		final String pMapId,
		final GoogleMapsOptions pCustomOptions)
	{
		pWriter.addJS_cr();
		pWriter.addJS_cr("(function () {");

		String mapVarname = GoogleMaps.getVarNameMap(pMapId);
		String markersArrayVarName = GoogleMaps.getVarNameMarkers(pMapId);

		String placeChangedEventFunction = "var newPlace = "
			+ GoogleMaps.getVarNameAutocomplete(pAutocompleteId) + ".getPlace();";
		placeChangedEventFunction += GoogleMaps.JS_FUNCNAME_FILL_IN_ADDRESS + "(newPlace);";
		placeChangedEventFunction += GoogleMaps.JS_FUNCNAME_UPDATE_MAP + "(" + mapVarname
			+ ", newPlace, " + pCustomOptions.getZoomSingleMarker() + ");";
		placeChangedEventFunction += GoogleMaps.JS_FUNCNAME_REPLACE_MARKER + "(" + mapVarname + ", "
			+ markersArrayVarName + ", newPlace);";

		this.addGoogleMapsEventListener(
			pWriter,
			GoogleMaps.getVarNameAutocomplete(pAutocompleteId),
			Events.PLACE_CHANGED,
			placeChangedEventFunction);

		pWriter.addJS_cr("})();");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the function initialize map with marker.
	 *
	 * @param pWriter the writer
	 * @param pMapId the map id
	 * @param pCustomOptions the options
	 * @since 1.0.0
	 */
	private void addFunctionInitializeMapWithMarkers(
		final UIComponentWriter pWriter,
		final String pMapId,
		final GoogleMapsOptions pCustomOptions)
	{
		String optionsObject = this.getMapOptionsJsObject(pCustomOptions);
		String mapVarName = GoogleMaps.getVarNameMap(pMapId);
		String itemsVarName = GoogleMaps.getVarNameItems(pMapId);
		String markersArrayVarName = GoogleMaps.getVarNameMarkers(pMapId);
		String infoWindowVarName = GoogleMaps.getVarNameInfoWindow(pMapId);

		pWriter.addJS_cr();
		pWriter.addJS_cr("(function () {");
		pWriter.addJS_cr("    var options = " + optionsObject + ";");
		pWriter.addJS_cr("    " + mapVarName + ".setOptions(options);");
		pWriter.addJS_cr(
			"    " + GoogleMaps.JS_FUNCNAME_ADD_MARKERS_FROM_ITEMS + "(" + mapVarName + ", "
				+ itemsVarName + ", " + markersArrayVarName + ", " + infoWindowVarName
				+ ", function(bounds){");
		pWriter.addJS_cr("        " + mapVarName + ".fitBounds(bounds);");
		if (pCustomOptions.getZoomSingleMarker() != null)
		{
			pWriter.addJS_cr("        if (" + itemsVarName + ".length === 1 ) {");
			pWriter.addJS_cr(
				"            " + mapVarName + ".setZoom(" + pCustomOptions.getZoomSingleMarker()
					+ ");");
			pWriter.addJS_cr("        }");
		}
		pWriter.addJS_cr("    });");
		pWriter.addJS_cr("})();");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the function replace marker.
	 *
	 * @param pWriter the writer
	 * @return the string
	 * @since 1.0.0
	 */
	private String addFunctionReplaceMarker(final UIComponentWriter pWriter)
	{
		String functionName = GoogleMaps.JS_FUNCNAME_REPLACE_MARKER;
		if (this.functionReplaceMarkerAlreadyAdded)
		{
			return functionName;
		}

		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + functionName + "(map, markersArray, placeResult) {");
		pWriter.addJS_cr("    if (markersArray) {");
		pWriter.addJS_cr("        for (var i = 0; i < markersArray.length; i++) {");
		pWriter.addJS_cr("            markersArray[i].setMap(null);");
		pWriter.addJS_cr("        }");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("    var latLng = placeResult.geometry.location;");
		pWriter.addJS_cr(
			"    " + GoogleMaps.JS_FUNCNAME_CREATE_MARKER
				+ "(map, latLng, null, null, null, markersArray);");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		this.functionReplaceMarkerAlreadyAdded = true;
		return functionName;
	}

	/**
	 * Adds the function set value.
	 *
	 * @param pWriter the writer
	 * @return the string
	 * @since 1.0.0
	 */
	private String addFunctionSetValue(final UIComponentWriter pWriter)
	{
		String functionName = GoogleMaps.JS_FUNCNAME_SET_EBX_VALUE;
		if (this.functionSetValueAlreadyAdded)
		{
			return functionName;
		}

		pWriter.addJS_cr();
		pWriter.addJS_cr(
			"function " + functionName + "(" + GoogleMaps.JS_VARNAME_SET_EBX_VALUE_TYPE + ", "
				+ GoogleMaps.JS_VARNAME_SET_EBX_VALUE_VALUE + ") {");

		if (this.mapping != null)
		{
			if (this.mapping.isUIFormPane())
			{
				this.addSetNodeValues((UIFormPaneWriter) pWriter);
			}
			else
			{
				this.addSetInputValues(pWriter);
			}
		}

		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		this.functionSetValueAlreadyAdded = true;
		return functionName;
	}

	/**
	 * Adds the function standardize.
	 *
	 * @param pWriter the writer
	 * @return the function name
	 * @since 1.0.0
	 */
	private String addFunctionStandardize(final UIComponentWriter pWriter)
	{
		String functionName = GoogleMaps.JS_FUNCNAME_STANDARDIZE;
		if (this.functionStandardizeAlreadyAdded)
		{
			return functionName;
		}

		this.addFunctionGetValue(pWriter);

		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + functionName + "(autocompleteId) {");
		pWriter.addJS_cr("	var myAddress = '';");

		pWriter.addJS_cr();
		pWriter.addJS_cr(
			"	myAddress += " + GoogleMaps.JS_FUNCNAME_GET_EBX_VALUE + "('"
				+ GoogleMaps.JS_VARNAME_NUMBER + "') + ' ';");
		pWriter.addJS_cr(
			"	myAddress += " + GoogleMaps.JS_FUNCNAME_GET_EBX_VALUE + "('"
				+ GoogleMaps.JS_VARNAME_STREET + "') + ' ';");
		pWriter.addJS_cr(
			"	myAddress += " + GoogleMaps.JS_FUNCNAME_GET_EBX_VALUE + "('"
				+ GoogleMaps.JS_VARNAME_ZIPCODE + "') + ' ';");
		pWriter.addJS_cr(
			"	myAddress += " + GoogleMaps.JS_FUNCNAME_GET_EBX_VALUE + "('"
				+ GoogleMaps.JS_VARNAME_CITY + "') + ' ';");
		pWriter.addJS_cr(
			"	myAddress += " + GoogleMaps.JS_FUNCNAME_GET_EBX_VALUE + "('"
				+ GoogleMaps.JS_VARNAME_COUNTRY + "') + ' ';");
		pWriter.addJS_cr();
		pWriter.addJS_cr("    var autocompleteField = document.getElementById(autocompleteId);");
		pWriter.addJS_cr("    if (autocompleteField) {");
		pWriter.addJS_cr("        autocompleteField.value =  myAddress;");
		pWriter.addJS_cr("        autocompleteField.focus();");
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		this.functionStandardizeAlreadyAdded = true;
		return functionName;
	}

	/**
	 * Adds the function update map.
	 *
	 * @param pWriter the writer
	 * @return the function name
	 * @since 1.0.0
	 */
	private String addFunctionUpdateMap(final UIComponentWriter pWriter)
	{
		String functionName = GoogleMaps.JS_FUNCNAME_UPDATE_MAP;
		if (this.functionUpdateMapAlreadyAdded)
		{
			return functionName;
		}

		pWriter.addJS_cr();
		pWriter.addJS_cr("function " + functionName + "(map, placeResult, zoom) {");
		pWriter.addJS_cr("    var latLng = placeResult.geometry.location;");
		pWriter.addJS_cr("    var bounds = new google.maps.LatLngBounds().extend(latLng);");
		pWriter.addJS_cr("    map.fitBounds(bounds);");
		pWriter.addJS_cr("    map.setZoom(zoom);");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();

		this.functionUpdateMapAlreadyAdded = true;
		return functionName;
	}

	/**
	 * In the context of a service, the values are gotten thanks to the id of the input used for the
	 * fields. The address mapping should be filled with ids to the input tag of the address.
	 *
	 * @param pWriter the writer
	 * @see #setAddressMapping(GoogleMapsAddressFieldMapping)
	 * @since 1.0.0
	 */
	private void addGetInputValues(final UIComponentWriter pWriter)
	{
		pWriter.addJS_cr();
		pWriter.addJS_cr("    switch (" + GoogleMaps.JS_VARNAME_GET_EBX_VALUE_TYPE + ") {");
		pWriter.addJS_cr();

		if (this.mapping.getNumberId() != null)
		{
			this.addGetInputValuesSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_NUMBER,
				this.mapping.getNumberId());
		}

		if (this.mapping.getStreetId() != null)
		{
			this.addGetInputValuesSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_STREET,
				this.mapping.getStreetId());
		}

		if (this.mapping.getZipcodeId() != null)
		{
			this.addGetInputValuesSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_ZIPCODE,
				this.mapping.getZipcodeId());
		}

		if (this.mapping.getCityId() != null)
		{
			this.addGetInputValuesSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_CITY,
				this.mapping.getCityId());
		}

		if (this.mapping.getRegionId() != null)
		{
			this.addGetInputValuesSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_REGION,
				this.mapping.getRegionId());
		}

		if (this.mapping.getCountryId() != null)
		{
			this.addGetInputValuesSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_COUNTRY,
				this.mapping.getCountryId());
		}

		if (this.mapping.getLatitudeId() != null)
		{
			this.addGetInputValuesSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_LAT,
				this.mapping.getLatitudeId());
		}

		if (this.mapping.getLongitudeId() != null)
		{
			this.addGetInputValuesSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_LONG,
				this.mapping.getLongitudeId());
		}

		pWriter.addJS_cr("    }");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the get input values switch case.
	 *
	 * @param pWriter the writer
	 * @param pTypeName the type name
	 * @param pElementId the element id
	 * @since 1.0.0
	 */
	private void addGetInputValuesSwitchCase(
		final UIComponentWriter pWriter,
		final String pTypeName,
		final String pElementId)
	{
		pWriter.addJS_cr("        case '" + pTypeName + "':");
		pWriter.addJS_cr("            return document.getElementById('" + pElementId + "').value;");
		pWriter.addJS_cr("            break;");
	}

	/**
	 * In the context of an UIForm, the values are gotten thanks to the exposed API getNodeValue
	 * method. The address mapping should be filled with Paths to the model.
	 *
	 * @param pWriter the writer
	 * @see #setAddressMapping(GoogleMapsAddressFieldMapping)
	 * @since 1.0.0
	 */
	private void addGetNodeValues(final UIFormPaneWriter pWriter)
	{
		this.addFunctionGetValueFromFK(pWriter);

		pWriter.addJS_cr();
		pWriter.addJS_cr("    switch (" + GoogleMaps.JS_VARNAME_GET_EBX_VALUE_TYPE + ") {");
		pWriter.addJS_cr();

		if (this.mapping.getNumberPath() != null)
		{
			this.addGetNodeValuesSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_NUMBER,
				this.mapping.getNumberPath());
		}

		if (this.mapping.getStreetPath() != null)
		{
			if (this.mapping.getStreetFKMapping() == null)
			{
				this.addGetNodeValuesSwitchCase(
					pWriter,
					GoogleMaps.JS_VARNAME_STREET,
					this.mapping.getStreetPath());
			}
			else
			{
				this.addGetNodeValuesSwitchCaseWithFK(
					pWriter,
					GoogleMaps.JS_VARNAME_STREET,
					this.mapping.getStreetPath());
			}
		}

		if (this.mapping.getZipcodePath() != null)
		{
			if (this.mapping.getZipcodeFKMapping() == null)
			{
				this.addGetNodeValuesSwitchCase(
					pWriter,
					GoogleMaps.JS_VARNAME_ZIPCODE,
					this.mapping.getZipcodePath());
			}
			else
			{
				this.addGetNodeValuesSwitchCaseWithFK(
					pWriter,
					GoogleMaps.JS_VARNAME_ZIPCODE,
					this.mapping.getZipcodePath());
			}
		}

		if (this.mapping.getCityPath() != null)
		{
			if (this.mapping.getCityFKMapping() == null)
			{
				this.addGetNodeValuesSwitchCase(
					pWriter,
					GoogleMaps.JS_VARNAME_CITY,
					this.mapping.getCityPath());
			}
			else
			{
				this.addGetNodeValuesSwitchCaseWithFK(
					pWriter,
					GoogleMaps.JS_VARNAME_CITY,
					this.mapping.getCityPath());
			}
		}

		if (this.mapping.getRegionPath() != null)
		{
			if (this.mapping.getRegionFKMapping() == null)
			{
				this.addGetNodeValuesSwitchCase(
					pWriter,
					GoogleMaps.JS_VARNAME_REGION,
					this.mapping.getRegionPath());
			}
			else
			{
				this.addGetNodeValuesSwitchCaseWithFK(
					pWriter,
					GoogleMaps.JS_VARNAME_REGION,
					this.mapping.getRegionPath());
			}
		}

		if (this.mapping.getCountryPath() != null)
		{
			if (this.mapping.getCountryFKMapping() == null)
			{
				this.addGetNodeValuesSwitchCase(
					pWriter,
					GoogleMaps.JS_VARNAME_COUNTRY,
					this.mapping.getCountryPath());
			}
			else
			{
				this.addGetNodeValuesSwitchCaseWithFK(
					pWriter,
					GoogleMaps.JS_VARNAME_COUNTRY,
					this.mapping.getCountryPath());
			}
		}

		if (this.mapping.getLatitudePath() != null)
		{
			this.addGetNodeValuesSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_LAT,
				this.mapping.getLatitudePath());
		}

		if (this.mapping.getLongitudePath() != null)
		{
			this.addGetNodeValuesSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_LONG,
				this.mapping.getLongitudePath());
		}

		pWriter.addJS_cr("    }");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the get node values switch case.
	 *
	 * @param pWriter the writer
	 * @param pTypeName the type name
	 * @param pNodePath the node path
	 * @since 1.0.0
	 */
	private void addGetNodeValuesSwitchCase(
		final UIFormPaneWriter pWriter,
		final String pTypeName,
		final Path pNodePath)
	{
		pWriter.addJS_cr("        case '" + pTypeName + "':");
		pWriter.addJS("            return ");
		pWriter.addJS_getNodeValue(pNodePath).addJS_cr(";");
		pWriter.addJS_cr("            break;");

	}

	/**
	 * Adds the get node values switch case with fk.
	 *
	 * @param pWriter the writer
	 * @param pTypeName the type name
	 * @param pNodePath the node path
	 * @since 1.0.0
	 */
	private void addGetNodeValuesSwitchCaseWithFK(
		final UIFormPaneWriter pWriter,
		final String pTypeName,
		final Path pNodePath)
	{
		String ebxValue = "ebxValue";
		String array = pTypeName + ForeignKeyMapping.FK_MAPPING_SUFFIX;

		pWriter.addJS_cr("        case '" + pTypeName + "':");
		pWriter.addJS("            var " + ebxValue + " = ");
		pWriter.addJS_getNodeValue(pNodePath);
		pWriter.addJS_cr(";");
		pWriter.addJS_cr(
			"            return " + ForeignKeyMapping.JS_FUNCNAME_GET_VALUE_FROM_FK + "(" + array
				+ ", " + ebxValue + ");");
		pWriter.addJS_cr("            break;");
	}

	/**
	 * Adds the google maps event listener.
	 *
	 * @param pWriter the writer
	 * @param pHtmlElementVar the html element var
	 * @param pEvent the event
	 * @param pFunctionContent the function content
	 * @since 1.0.0
	 */
	private void addGoogleMapsEventListener(
		final UIComponentWriter pWriter,
		final String pHtmlElementVar,
		final String pEvent,
		final String pFunctionContent)
	{
		pWriter.addJS_cr();
		pWriter.addJS("google.maps.event.addListener(")
			.addJS(pHtmlElementVar + ", ")
			.addJS("'" + pEvent + "'" + ", ")
			.addJS_cr(" function(" + GoogleMaps.JS_VARNAME_EVENT_ARGUMENT_IN_LISTENER + ") {");
		pWriter.addJS_cr(pFunctionContent);
		pWriter.addJS_cr("});");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the maps with makers.
	 *
	 * @param pWriter the writer
	 * @param pMapId the id of the map
	 * @param pCustomOptions the options
	 * @since 1.0.0
	 */
	private void addJsFunctionForMapsWithMakers(
		final UIComponentWriter pWriter,
		final String pMapId,
		final GoogleMapsOptions pCustomOptions)
	{
		this.addVariableMap(pWriter, pMapId);
		this.addVariableMarkers(pWriter, pMapId);
		this.addVariableInfoWindow(pWriter, pMapId, pCustomOptions);
		this.addVariableBounds(pWriter, pMapId);
		this.addFunctionCreateMarker(pWriter, pCustomOptions);
		this.addFunctionCreateGoogleMapsItem(pWriter, pCustomOptions);
		this.addFunctionGetGeoCode(pWriter);
		this.addFunctionAddMarkersFromItems(pWriter);
		this.addFunctionInitializeMapWithMarkers(pWriter, pMapId, pCustomOptions);
	}

	/**
	 * Adds the js functions for autocomplete.
	 *
	 * @param pWriter the writer
	 * @param pMapId the map id
	 * @param pAutocompleteId the autocomplete id
	 * @param pCustomOptions the options
	 * @since 1.0.0
	 */
	private void addJsFunctionsForAutocomplete(
		final UIComponentWriter pWriter,
		final String pMapId,
		final String pAutocompleteId,
		final GoogleMapsOptions pCustomOptions)
	{
		if (this.mapping != null)
		{
			if (UIFormPaneWriter.class.isInstance(pWriter))
			{
				this.mapping.setUIFormPane(true);
			}
		}
		this.addVariableAutocomplete(pWriter, pAutocompleteId);
		this.addVariableAddressComponentsDefinition(pWriter, pAutocompleteId, pCustomOptions);
		this.addVariableFkMappings(pWriter);
		this.addFunctionReplaceMarker(pWriter);
		this.addFunctionUpdateMap(pWriter);
		this.addFunctionFillInAddress(pWriter);
		this.addFunctionInitializeAutocomplete(pWriter, pAutocompleteId, pMapId, pCustomOptions);
	}

	/**
	 * In the context of a service, the values are set thanks to the id of the input used for the
	 * fields. The address mapping should be filled with ids to the input tag of the address.
	 *
	 * @param pWriter the writer
	 * @see #setAddressMapping(GoogleMapsAddressFieldMapping)
	 * @since 1.0.0
	 */
	private void addSetInputValues(final UIComponentWriter pWriter)
	{
		pWriter.addJS_cr();
		pWriter.addJS_cr("    switch (" + GoogleMaps.JS_VARNAME_SET_EBX_VALUE_TYPE + ") {");
		pWriter.addJS_cr();

		if (this.mapping.getNumberId() != null)
		{
			this.addSetInputValueSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_NUMBER,
				this.mapping.getNumberId());
		}
		if (this.mapping.getStreetId() != null)
		{
			this.addSetInputValueSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_STREET,
				this.mapping.getStreetId());
		}
		if (this.mapping.getZipcodeId() != null)
		{
			this.addSetInputValueSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_ZIPCODE,
				this.mapping.getZipcodeId());
		}
		if (this.mapping.getCityId() != null)
		{
			this.addSetInputValueSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_CITY,
				this.mapping.getCityId());
		}
		if (this.mapping.getRegionId() != null)
		{
			this.addSetInputValueSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_REGION,
				this.mapping.getRegionId());
		}
		if (this.mapping.getCountryId() != null)
		{
			this.addSetInputValueSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_COUNTRY,
				this.mapping.getCountryId());
		}
		if (this.mapping.getLatitudeId() != null)
		{
			this.addSetInputValueSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_LAT,
				this.mapping.getLatitudeId());
		}
		if (this.mapping.getLongitudeId() != null)
		{
			this.addSetInputValueSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_LONG,
				this.mapping.getLongitudeId());
		}

		pWriter.addJS_cr();
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr();

	}

	/**
	 * Adds the set input value case iteration.
	 *
	 * @param pWriter the writer
	 * @param pTypeName the type name
	 * @param pElementId the element id
	 * @since 1.0.0
	 */
	private void addSetInputValueSwitchCase(
		final UIComponentWriter pWriter,
		final String pTypeName,
		final String pElementId)
	{
		pWriter.addJS_cr("        case '" + pTypeName + "':");
		pWriter.addJS_cr(
			"            document.getElementById('" + pElementId + "').value =  "
				+ GoogleMaps.JS_VARNAME_SET_EBX_VALUE_VALUE + ";");
		pWriter.addJS_cr("            break;");
	}

	/**
	 * In the context of an UIForm, the values are set thanks to the exposed API setNodeValue
	 * method. The address mapping should be filled with Paths to the model.
	 *
	 * @param pWriter the writer
	 * @see #setAddressMapping(GoogleMapsAddressFieldMapping)
	 * @since 1.0.0
	 */
	private void addSetNodeValues(final UIFormPaneWriter pWriter)
	{
		this.addFunctionGetFKFromValue(pWriter);

		pWriter.addJS_cr();
		pWriter.addJS_cr("    switch (" + GoogleMaps.JS_VARNAME_SET_EBX_VALUE_TYPE + ") {");
		pWriter.addJS_cr();

		if (this.mapping.getNumberPath() != null)
		{
			this.addSetNodeValueSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_NUMBER,
				this.mapping.getNumberPath());
		}

		if (this.mapping.getStreetPath() != null)
		{
			if (this.mapping.getStreetFKMapping() == null)
			{
				this.addSetNodeValueSwitchCase(
					pWriter,
					GoogleMaps.JS_VARNAME_STREET,
					this.mapping.getStreetPath());
			}
			else
			{
				this.addSetNodeValueSwitchCaseWithFK(
					pWriter,
					GoogleMaps.JS_VARNAME_STREET,
					this.mapping.getStreetPath());
			}
		}
		if (this.mapping.getZipcodePath() != null)
		{
			if (this.mapping.getZipcodeFKMapping() == null)
			{
				this.addSetNodeValueSwitchCase(
					pWriter,
					GoogleMaps.JS_VARNAME_ZIPCODE,
					this.mapping.getZipcodePath());
			}
			else
			{
				this.addSetNodeValueSwitchCaseWithFK(
					pWriter,
					GoogleMaps.JS_VARNAME_ZIPCODE,
					this.mapping.getZipcodePath());
			}
		}
		if (this.mapping.getCityPath() != null)
		{
			if (this.mapping.getCityFKMapping() == null)
			{
				this.addSetNodeValueSwitchCase(
					pWriter,
					GoogleMaps.JS_VARNAME_CITY,
					this.mapping.getCityPath());
			}
			else
			{
				this.addSetNodeValueSwitchCaseWithFK(
					pWriter,
					GoogleMaps.JS_VARNAME_CITY,
					this.mapping.getCityPath());
			}
		}
		if (this.mapping.getRegionPath() != null)
		{
			if (this.mapping.getRegionFKMapping() == null)
			{
				this.addSetNodeValueSwitchCase(
					pWriter,
					GoogleMaps.JS_VARNAME_REGION,
					this.mapping.getRegionPath());
			}
			else
			{
				this.addSetNodeValueSwitchCaseWithFK(
					pWriter,
					GoogleMaps.JS_VARNAME_REGION,
					this.mapping.getRegionPath());
			}
		}
		if (this.mapping.getCountryPath() != null)
		{
			if (this.mapping.getCountryFKMapping() == null)
			{
				this.addSetNodeValueSwitchCase(
					pWriter,
					GoogleMaps.JS_VARNAME_COUNTRY,
					this.mapping.getCountryPath());
			}
			else
			{
				this.addSetNodeValueSwitchCaseWithFK(
					pWriter,
					GoogleMaps.JS_VARNAME_COUNTRY,
					this.mapping.getCountryPath());

				//				pWriter.addJS_cr();
				//				String ebxValue = "ebxCountryValue";
				//				String array = GoogleMaps.JS_VARNAME_COUNTRY + ForeignKeyMapping.FK_MAPPING_SUFFIX;
				//				pWriter.addJS_cr("    var " + ebxValue + " = "
				//					+ ForeignKeyMapping.JS_FUNCNAME_GET_FK_FROM_VALUE + "(" + array + ", "
				//					+ GoogleMaps.JS_VARNAME_COUNTRY + ");");
				//				pWriter.addJS_setNodeValue(ebxValue, this.mapping.getCountryPath());
			}
		}
		if (this.mapping.getLatitudePath() != null)
		{
			this.addSetNodeValueSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_LAT,
				this.mapping.getLatitudePath());
		}
		if (this.mapping.getLongitudePath() != null)
		{
			this.addSetNodeValueSwitchCase(
				pWriter,
				GoogleMaps.JS_VARNAME_LONG,
				this.mapping.getLongitudePath());
		}

		pWriter.addJS_cr();
		pWriter.addJS_cr("    }");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the set node value switch case.
	 *
	 * @param pWriter the writer
	 * @param pTypeName the type name
	 * @param pNodePath the node path
	 * @since 1.0.0
	 */
	private void addSetNodeValueSwitchCase(
		final UIFormPaneWriter pWriter,
		final String pTypeName,
		final Path pNodePath)
	{
		pWriter.addJS("        case '" + pTypeName + "':");
		pWriter.addJS_setNodeValue(GoogleMaps.JS_VARNAME_SET_EBX_VALUE_VALUE, pNodePath).addJS_cr();
		pWriter.addJS_cr("            break;");
	}

	/**
	 * Adds the set node value switch case with fk.
	 *
	 * @param pWriter the writer
	 * @param pTypeName the type name
	 * @param pNodePath the node path
	 * @since 1.0.0
	 */
	private void addSetNodeValueSwitchCaseWithFK(
		final UIFormPaneWriter pWriter,
		final String pTypeName,
		final Path pNodePath)
	{
		String ebxValue = "ebxValue";
		String array = pTypeName + ForeignKeyMapping.FK_MAPPING_SUFFIX;

		pWriter.addJS_cr("        case '" + pTypeName + "':");
		pWriter.addJS(
			"            var " + ebxValue + " = " + ForeignKeyMapping.JS_FUNCNAME_GET_FK_FROM_VALUE
				+ "(" + array + ", " + GoogleMaps.JS_VARNAME_SET_EBX_VALUE_VALUE + ");");
		pWriter.addJS_setNodeValue(ebxValue, pNodePath).addJS_cr();
		pWriter.addJS_cr("            break;");
	}

	/**
	 * Adds the address components definition.
	 *
	 * @param pWriter the writer
	 * @param pAutocompleteId the autocomplete id
	 * @param pCustomOptions the custom options
	 * @since 1.0.0
	 */
	private void addVariableAddressComponentsDefinition(
		final UIComponentWriter pWriter,
		final String pAutocompleteId,
		final GoogleMapsOptions pCustomOptions)
	{
		pWriter.addJS_cr();
		pWriter.addJS_cr("var " + GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION + " = {");

		HashMap<String, String> addressComponents = this.getGoogleMapsAddressComponents();
		int nbComponents = addressComponents.size();
		int counter = 0;
		Iterator<Entry<String, String>> iterator = addressComponents.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry<String, String> component = iterator.next();
			String googleMapsComponentName = component.getKey();
			String internalJsName = component.getValue();

			String inputId = this
				.getAddressComponentInputId(googleMapsComponentName, pAutocompleteId);

			pWriter.addJS("    ");
			pWriter.addJS_cr(googleMapsComponentName + ":");
			pWriter.addJS_cr("        {");
			pWriter.addJS_cr(
				"            " + GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION_INTERNAL_NAME
					+ ": '" + internalJsName + "',");
			pWriter.addJS_cr(
				"            " + GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION_TYPE + ": '"
					+ pCustomOptions.getAddressComponentType(googleMapsComponentName) + "',");
			pWriter.addJS_cr(
				"            " + GoogleMaps.JS_VARNAME_ADDRESS_COMPONENTS_DEFINITION_INPUT_ID
					+ ": '" + inputId + "'");
			pWriter.addJS("        }");

			counter++;
			if (counter < nbComponents)
			{
				pWriter.addJS_cr(",");
			}

		}

		pWriter.addJS_cr();
		pWriter.addJS_cr("};");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the autocomplete variable declaration.
	 *
	 * @param pWriter the writer
	 * @param pAutocompleteId the autocomplete id
	 * @since 1.0.0
	 */
	private void addVariableAutocomplete(
		final UIComponentWriter pWriter,
		final String pAutocompleteId)
	{
		pWriter.addJS_cr();
		String autocompleteVar = GoogleMaps.getVarNameAutocomplete(pAutocompleteId);
		pWriter.addJS_cr("var " + autocompleteVar + " = new google.maps.places.Autocomplete(");
		pWriter.addJS_cr(
			"    document.getElementById('" + pAutocompleteId + "'), { types: ['geocode'] }");
		pWriter.addJS_cr(");");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the variable bounds.
	 *
	 * @param pWriter the writer
	 * @param pMapId the map id
	 * @since 1.0.0
	 */
	private void addVariableBounds(final UIComponentWriter pWriter, final String pMapId)
	{
		String boundsVarName = GoogleMaps.getVarNameBounds(pMapId);
		pWriter.addJS_cr();
		pWriter.addJS_cr("var " + boundsVarName + " = new google.maps.LatLngBounds();");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the fk mappings.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addVariableFkMappings(final UIComponentWriter pWriter)
	{
		if (this.mapping == null)
		{
			return;
		}

		ForeignKeyMapping streetFkMapping = this.mapping.getStreetFKMapping();
		if (streetFkMapping != null)
		{
			streetFkMapping.insertFkVarJs(pWriter, GoogleMapsItem.JsObject.AddressValue.STREET);
		}

		ForeignKeyMapping zipCodeFkMapping = this.mapping.getZipcodeFKMapping();
		if (zipCodeFkMapping != null)
		{
			zipCodeFkMapping.insertFkVarJs(pWriter, GoogleMapsItem.JsObject.AddressValue.ZIPCODE);
		}

		ForeignKeyMapping cityFkMapping = this.mapping.getCityFKMapping();
		if (cityFkMapping != null)
		{
			cityFkMapping.insertFkVarJs(pWriter, GoogleMapsItem.JsObject.AddressValue.CITY);
		}

		ForeignKeyMapping regionFkMapping = this.mapping.getRegionFKMapping();
		if (regionFkMapping != null)
		{
			regionFkMapping.insertFkVarJs(pWriter, GoogleMapsItem.JsObject.AddressValue.REGION);
		}

		ForeignKeyMapping countryFkMapping = this.mapping.getCountryFKMapping();
		if (countryFkMapping != null)
		{
			countryFkMapping.insertFkVarJs(pWriter, GoogleMapsItem.JsObject.AddressValue.COUNTRY);
		}
	}

	/**
	 * Adds the variable info window.
	 *
	 * @param pWriter the writer
	 * @param pMapId the map id
	 * @param pCustomOptions the custom options
	 * @since 1.0.0
	 */
	private void addVariableInfoWindow(
		final UIComponentWriter pWriter,
		final String pMapId,
		final GoogleMapsOptions pCustomOptions)
	{
		String infoWindowVarName = GoogleMaps.getVarNameInfoWindow(pMapId);
		pWriter.addJS_cr();
		pWriter.addJS_cr("var " + infoWindowVarName + " = new google.maps.InfoWindow(");
		pWriter.addJS_cr(pCustomOptions.getInfoWindowJsObject());
		pWriter.addJS_cr(");");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the variable map.
	 *
	 * @param pWriter the writer
	 * @param pMapId the map id
	 * @since 1.0.0
	 */
	private void addVariableMap(final UIComponentWriter pWriter, final String pMapId)
	{
		String mapVarName = GoogleMaps.getVarNameMap(pMapId);
		String mapDivVarName = GoogleMaps.getVarNameMapDiv(pMapId);

		pWriter.addJS_cr();
		pWriter.addJS_cr("var " + mapDivVarName + " = document.getElementById('" + pMapId + "');");
		pWriter.addJS_cr("var " + mapVarName + " = new google.maps.Map(" + mapDivVarName + ");");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the map items.
	 *
	 * @param pWriter the writer
	 * @param pItems the items
	 * @param pMapId the map id
	 * @since 1.0.0
	 */
	private void addVariableMapItems(
		final UIComponentWriter pWriter,
		final List<GoogleMapsItem> pItems,
		final String pMapId)
	{
		String varName = GoogleMaps.getVarNameItems(pMapId);

		pWriter.addJS_cr();
		if (pItems == null || pItems.isEmpty())
		{
			pWriter.addJS_cr("var " + varName + " = [];");
		}
		else
		{
			pWriter.addJS_cr("var " + varName + " = [");

			int nbItems = pItems.size();
			for (int i = 0; i < nbItems; i++)
			{
				GoogleMapsItem item = pItems.get(i);

				pWriter.addJS_cr();
				pWriter.addJS_cr();
				pWriter.addJS("    " + item.getJsObject());
				if (i < nbItems - 1)
				{
					pWriter.addJS_cr(",");
				}
				else
				{
					pWriter.addJS_cr();
				}

			}
			pWriter.addJS_cr();
			pWriter.addJS_cr("];");
		}
		pWriter.addJS_cr();
	}

	/**
	 * Adds the variable markers.
	 *
	 * @param pWriter the writer
	 * @param pMapId the map id
	 * @since 1.0.0
	 */
	private void addVariableMarkers(final UIComponentWriter pWriter, final String pMapId)
	{
		String markerVarName = GoogleMaps.getVarNameMarkers(pMapId);

		pWriter.addJS_cr();
		pWriter.addJS_cr("var " + markerVarName + " = [];");
		pWriter.addJS_cr();
	}

	/**
	 * Creates the google maps item from address value.
	 *
	 * @param pAddressValue the address value
	 * @return the google maps item
	 * @since 1.0.0
	 */
	private GoogleMapsItem createGoogleMapsItemFromAddressValue(
		final GoogleMapsAddressValue pAddressValue)
	{
		return new GoogleMapsItem(pAddressValue);
	}

	/**
	 * Gets the address component input id.
	 *
	 * @param pAddressType the address type
	 * @param pAutocompleteId the autocomplete id
	 * @return the address component input id
	 * @since 1.0.0
	 */
	private String getAddressComponentInputId(
		final String pAddressType,
		final String pAutocompleteId)
	{
		return pAddressType + "_" + pAutocompleteId;
	}

	/**
	 * Gets the google maps address components.
	 *
	 * @return a map of string representing the google maps address components
	 * @since 1.0.0
	 */
	private HashMap<String, String> getGoogleMapsAddressComponents()
	{
		HashMap<String, String> components = new HashMap<String, String>();
		components.put(GoogleMaps.AddressComponents.STREET_NUMBER, GoogleMaps.JS_VARNAME_NUMBER);
		components.put(GoogleMaps.AddressComponents.ROUTE, GoogleMaps.JS_VARNAME_STREET);
		components.put(GoogleMaps.AddressComponents.LOCALITY, GoogleMaps.JS_VARNAME_CITY);
		components
			.put(GoogleMaps.AddressComponents.ADMINISTRATIVE_AREA_1, GoogleMaps.JS_VARNAME_REGION);
		components.put(GoogleMaps.AddressComponents.POSTAL_CODE, GoogleMaps.JS_VARNAME_ZIPCODE);
		components.put(GoogleMaps.AddressComponents.COUNTRY, GoogleMaps.JS_VARNAME_COUNTRY);
		components.put(GoogleMaps.AddressComponents.LAT, GoogleMaps.JS_VARNAME_LAT);
		components.put(GoogleMaps.AddressComponents.LONG, GoogleMaps.JS_VARNAME_LONG);
		return components;
	}

	/**
	 * Gets the map options JavaScript object. Deal with global options defined on the class
	 * instance or the custom options passed as argument.
	 *
	 * @param pCustomOptions the options
	 * @return the map options js object
	 * @since 1.0.0
	 */
	private String getMapOptionsJsObject(final GoogleMapsOptions pCustomOptions)
	{
		if (pCustomOptions != null)
		{
			return pCustomOptions.getMapsJsObject();
		}
		else if (this.options != null)
		{
			return this.options.getMapsJsObject();
		}
		else
		{
			return "{}";
		}
	}

	/**
	 * Gets the options.
	 *
	 * @return the options
	 * @since 1.0.0
	 */
	private GoogleMapsOptions getOptions()
	{
		if (this.options == null)
		{
			return new GoogleMapsOptions();
		}
		return this.options;
	}

	/**
	 * Inserts the autocomplete hidden inputs.
	 *
	 * @param pWriter the writer
	 * @param pAutocompleteId the autocomplete id
	 * @since 1.0.0
	 */
	private void insertAutocompleteHiddenInputs(
		final UIComponentWriter pWriter,
		final String pAutocompleteId)
	{
		pWriter.add_cr();
		HashMap<String, String> addressComponents = this.getGoogleMapsAddressComponents();

		Iterator<Entry<String, String>> iterator = addressComponents.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry<String, String> component = iterator.next();
			String googleMapsComponentName = component.getKey();
			String inputId = this
				.getAddressComponentInputId(googleMapsComponentName, pAutocompleteId);
			this.insertHiddenInput(pWriter, inputId);
		}

		pWriter.add_cr();
	}

	/**
	 * Inserts the autocomplete input tag.
	 *
	 * @param pWriter the writer
	 * @param pPlaceholder the placeholder
	 * @param pStyle the style
	 * @param pFieldId the id of the tag
	 * @since 1.0.0
	 */
	private void insertAutocompleteInputField(
		final UIComponentWriter pWriter,
		final String pPlaceholder,
		final String pStyle,
		final String pFieldId)
	{
		String defaultStyle = GoogleMaps.AUTOCOMPLETE_FIELD_DEFAULT_STYLE;
		pWriter.add(
			"<input id=\"" + pFieldId + "\" placeholder=\"" + pPlaceholder
				+ "\" type=\"text\" style=\"" + defaultStyle + pStyle + "\"></input>");
	}

	/**
	 * Inserts the google maps script as a script tag in html.
	 *
	 * @param pWriter the writer
	 * @param pLocale the locale
	 * @since 1.0.0
	 */
	private void insertGoogleMapsScript(final UIComponentWriter pWriter, final Locale pLocale)
	{
		String scriptLanguage = null;
		Locale locale = pWriter.getLocale();
		if (pLocale != null)
		{
			locale = pLocale;
		}

		if (!locale.getLanguage().isEmpty())
		{
			scriptLanguage = "language=" + locale.getLanguage();
		}

		if (!this.googleMapsScriptAlreadyAdded)
		{
			pWriter.add("<script ");
			pWriter.add("src=\"" + GoogleMaps.GOOGLE_MAPS_SCRIPT_URL);
			pWriter.add("&key=" + GoogleMaps.GOOGLE_MAPS_API_KEY);
			if (scriptLanguage != null)
			{
				pWriter.add("&" + scriptLanguage);
			}
			pWriter.add("\" >");
			pWriter.add("</script>");
			this.googleMapsScriptAlreadyAdded = true;
		}
	}

	/**
	 * Inserts a disabled hidden input with the specified id.
	 *
	 * @param pWriter the writer
	 * @param pId the id
	 * @since 1.0.0
	 */
	private void insertHiddenInput(final UIComponentWriter pWriter, final String pId)
	{
		pWriter.add_cr("<input type=\"hidden\" id=\"" + pId + "\" >");
	}

	/**
	 * Insert the div tag, container of the google maps component, in the stream of the
	 * UIComponentWriter.
	 *
	 * @param pWriter the writer
	 * @param pCustomStyle the custom style (ex: "width:200px;height:200px")
	 * @param pMapId the id of the div
	 * @since 1.0.0
	 */
	private void insertMapDiv(
		final UIComponentWriter pWriter,
		String pCustomStyle,
		final String pMapId)
	{
		if (pCustomStyle == null)
		{
			pCustomStyle = "height:100%;width:100%;";
		}
		pWriter.add("<div id=\"" + pMapId + "\" style=\"" + pCustomStyle + "\"></div>");
	}
}
