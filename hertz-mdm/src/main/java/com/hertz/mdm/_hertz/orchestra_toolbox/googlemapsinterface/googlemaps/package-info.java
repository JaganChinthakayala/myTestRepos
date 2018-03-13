/**
 * Provides classes and methods to integrate Google Maps in EBX UI Form and UI Service.<br>
 * <br>
 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps GoogleMaps} is the main class
 * to be instanciated in your UI Form or UI Service. A map in the UI will rely on an instance of
 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressValue
 * GoogleMapsAddressValue} which defines the value of the address components expected by Google Maps
 * (cf.
 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps#insertMap(com.orchestranetworks.ui.UIComponentWriter, GoogleMapsAddressValue, String)
 * insertMap(...)}).<br>
 * <br>
 * Additional features can be inserted like a search field with autocompletion (cf.
 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps#insertAutocompleteAddressField(com.orchestranetworks.ui.UIComponentWriter, String, String, String)
 * insertAutocompleteAddressField(...)}) and a standardization button (cf.
 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps#insertStandardizeAddressButton(com.orchestranetworks.ui.UIComponentWriter, com.onwbp.base.text.UserMessage, String)
 * insertStandardizeAddressButton(...)}).<br>
 * Those features requires an instance of
 * {@link com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping
 * GoogleMapsAddressFieldMapping} which defines the mapping between the information expected by
 * Google Maps (street, city, country, ...) and the MDM information.<br>
 * <br>
 * Two User Services are provided by the PresalesToolbox:<br>
 * <ul>
 * <li>
 * {@link com.orchestranetworks.presales.toolbox.googlemaps.locationmap.LocationMapServiceBuilder
 * Location map}</li>
 * <li>
 * {@link com.orchestranetworks.presales.toolbox.googlemaps.addressnormalization.NormalizeAddressesService
 * Normalize addresses}</li>
 * </ul>
 *
 * @since 1.0.0
 * @see com.orchestranetworks.presales.toolbox.ui.form.UIFormAddressPane
 */
package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps;