package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps;

import java.util.Locale;

/**
 * This class allows to define the options of a Google Maps.
 *
 * @author ATI
 * @since 1.0.0
 */
public final class GoogleMapsOptions
{
	protected static class JsObjectName
	{
		protected static class Center
		{
			protected static final String JS_NAME = "center";
		}
		protected static class DisableDefaultUI
		{
			protected static final String JS_NAME = "disableDefaultUI";
		}
		protected static class Draggable
		{
			protected static final String JS_NAME = "draggable";
		}
		protected static class InfoWindow
		{
			protected static class MaxWidth
			{
				protected static final String JS_NAME = "maxWidth";
			}
		}
		protected static class MapType
		{
			protected static final String JS_NAME = "mapTypeId";
		}
		protected static class MapTypeControl
		{
			protected static final String JS_NAME = "mapTypeControl";
		}
		protected static class MapTypeControlOptions
		{
			protected static class Position
			{
				@SuppressWarnings("hiding")
				protected static final String JS_NAME = "position";
			}

			protected static final String JS_NAME = "mapTypeControlOptions";
		}
		protected static class MaxZoom
		{
			protected static final String JS_NAME = "maxZoom";
		}
		protected static class MinZoom
		{
			protected static final String JS_NAME = "minZoom";
		}
		protected static class OverviewMapControl
		{
			protected static final String JS_NAME = "overviewMapControl";
		}
		protected static class PanControl
		{
			protected static final String JS_NAME = "panControl";
		}
		protected static class PanControlOptions
		{
			protected static class Position
			{
				@SuppressWarnings("hiding")
				protected static final String JS_NAME = "position";
			}

			protected static final String JS_NAME = "panControlOptions";
		}
		protected static class RotateControl
		{
			protected static final String JS_NAME = "rotateControl";
		}
		protected static class RotateControlOptions
		{
			protected static class Position
			{
				@SuppressWarnings("hiding")
				protected static final String JS_NAME = "position";
			}

			protected static final String JS_NAME = "rotateControlOptions";
		}
		protected static class ScaleControl
		{
			protected static final String JS_NAME = "scaleControl";
		}
		protected static class Scrollwheel
		{
			protected static final String JS_NAME = "scrollwheel";
		}
		protected static class StreetViewControl
		{
			protected static final String JS_NAME = "streetViewControl";
		}
		protected static class StreetViewControlOptions
		{
			protected static class Position
			{
				@SuppressWarnings("hiding")
				protected static final String JS_NAME = "position";
			}

			protected static final String JS_NAME = "streetViewControlOptions";
		}
		protected static class Zoom
		{
			protected static final String JS_NAME = "zoom";
		}
		protected static class ZoomControl
		{
			protected static final String JS_NAME = "zoomControl";
		}
		protected static class ZoomControlOptions
		{
			protected static class Position
			{
				@SuppressWarnings("hiding")
				protected static final String JS_NAME = "position";
			}
			protected static class Style
			{
				@SuppressWarnings("hiding")
				protected static final String JS_NAME = "style";
			}

			protected static final String JS_NAME = "zoomControlOptions";
		}
	}

	private final Double defaultCenterLat = new Double(23.3100192);
	private final Double defaultCenterLong = new Double(-1.8458414);
	private Double initialCenterLat = this.defaultCenterLat;
	private Double initialCenterLong = this.defaultCenterLong;
	private boolean disableControls = false;
	private boolean draggable = true;
	private boolean draggableMarker = false;
	private String mapType = GoogleMaps.MapType.ROADMAP;
	private boolean mapTypeControl = false;
	private String mapTypeControlPosition = "null";
	private String zoomSingleMarker = GoogleMaps.Zoom.DEFAULT_SINGLE_MARKER;
	private String defaultZoom = GoogleMaps.Zoom.DEFAULT;
	private String minZoom = GoogleMaps.Zoom.MIN;
	private String maxZoom = GoogleMaps.Zoom.MAX;
	private boolean zoomControl = true;
	private String zoomControlPosition = "null";
	private String zoomControlStyle = "null";
	private boolean overviewMapControl = false;
	private boolean panControl = false;
	private String panControlPosition = "null";
	private boolean rotateControl = false;
	private String rotateControlPosition = "null";
	private boolean scaleControl = false;
	private boolean scrollwheel = true;
	private boolean streetViewControl = false;
	private String streetViewControlPosition = "null";
	private String defaultIcon;
	private Integer infoWindowMaxWidth;
	private String numberAddressComponentType;
	private String streetAddressComponentType;
	private String zipCodeAddressComponentType;
	private String cityAddressComponentType;
	private String regionAddressComponentType;
	private String countryAddressComponentType;
	private Locale locale = null;

	/**
	 * Instantiates a new google maps options.
	 *
	 * @since 1.0.0
	 */
	public GoogleMapsOptions()
	{
	}

	/**
	 * Gets the city address component type.
	 *
	 * @return the city address component type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	public String getCityAddressComponentType()
	{
		if (this.cityAddressComponentType == null)
		{
			return GoogleMaps.AddressComponents.Type.LONG_NAME;
		}
		return this.cityAddressComponentType;
	}

	/**
	 * Gets the country address component type.
	 *
	 * @return the country address component type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	public String getCountryAddressComponentType()
	{
		if (this.countryAddressComponentType == null)
		{
			return GoogleMaps.AddressComponents.Type.SHORT_NAME;
		}
		return this.countryAddressComponentType;
	}

	/**
	 * Gets the default icon.
	 *
	 * @return the default icon
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Icons
	 * @since 1.0.0
	 */
	public String getDefaultIcon()
	{
		if (this.defaultIcon == null)
		{
			return GoogleMaps.Icons.RED;
		}
		return this.defaultIcon;
	}

	/**
	 * Gets the default zoom.
	 *
	 * @return the default zoom
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Zoom
	 * @since 1.0.0
	 */
	public String getDefaultZoom()
	{
		return this.defaultZoom;
	}

	/**
	 * Gets the info window max width.
	 *
	 * @return the info window max width
	 * @since 1.0.0
	 */
	public Integer getInfoWindowMaxWidth()
	{
		if (this.infoWindowMaxWidth == null)
		{
			return new Integer(1000);
		}
		return this.infoWindowMaxWidth;
	}

	/**
	 * Gets the initial center lat.
	 *
	 * @return the initial center lat
	 * @since 1.0.0
	 */
	public Double getInitialCenterLat()
	{
		if (this.initialCenterLat == null)
		{
			return this.defaultCenterLat;
		}
		return this.initialCenterLat;
	}

	/**
	 * Gets the initial center long.
	 *
	 * @return the initial center long
	 * @since 1.0.0
	 */
	public Double getInitialCenterLong()
	{
		if (this.initialCenterLong == null)
		{
			return this.defaultCenterLong;
		}
		return this.initialCenterLong;
	}

	/**
	 * Gets the locale used for the Google Maps results.
	 *
	 * @return the locale
	 * @since TODO since
	 */
	public Locale getLocale()
	{
		return this.locale;
	}

	/**
	 * Gets the map type.
	 *
	 * @return the map type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.MapType
	 * @since 1.0.0
	 */
	public String getMapType()
	{
		return this.mapType;
	}

	/**
	 * Gets the position of the map type control.
	 *
	 * @return the control position
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.ControlPosition
	 * @since 1.0.0
	 */
	public String getMapTypeControlPosition()
	{
		return this.mapTypeControlPosition;
	}

	/**
	 * Gets the max zoom.
	 *
	 * @return the max zoom.
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Zoom
	 * @since 1.0.0
	 */
	public String getMaxZoom()
	{
		return this.maxZoom;
	}

	/**
	 * Gets the min zoom.
	 *
	 * @return the min zoom
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Zoom
	 * @since 1.0.0
	 */
	public String getMinZoom()
	{
		return this.minZoom;
	}

	/**
	 * Gets the number address component type.
	 *
	 * @return the number address component type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	public String getNumberAddressComponentType()
	{
		if (this.numberAddressComponentType == null)
		{
			return GoogleMaps.AddressComponents.Type.LONG_NAME;
		}
		return this.numberAddressComponentType;
	}

	/**
	 * Gets the position of the pan control.
	 *
	 * @return the control position.
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.ControlPosition
	 * @since 1.0.0
	 */
	public String getPanControlPosition()
	{
		return this.panControlPosition;
	}

	/**
	 * Gets the region address component type.
	 *
	 * @return the region address component type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	public String getRegionAddressComponentType()
	{
		if (this.regionAddressComponentType == null)
		{
			return GoogleMaps.AddressComponents.Type.LONG_NAME;
		}
		return this.regionAddressComponentType;
	}

	/**
	 * Gets the street address component type.
	 *
	 * @return the street address component type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	public String getStreetAddressComponentType()
	{
		if (this.streetAddressComponentType == null)
		{
			return GoogleMaps.AddressComponents.Type.LONG_NAME;
		}
		return this.streetAddressComponentType;
	}

	/**
	 * Gets the position of the street view control.
	 *
	 * @return the control position
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.ControlPosition
	 * @since 1.0.0
	 */
	public String getStreetViewControlPosition()
	{
		return this.streetViewControlPosition;
	}

	/**
	 * Gets the zip code address component type.
	 *
	 * @return the zip code address component type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	public String getZipCodeAddressComponentType()
	{
		if (this.zipCodeAddressComponentType == null)
		{
			return GoogleMaps.AddressComponents.Type.LONG_NAME;
		}
		return this.zipCodeAddressComponentType;
	}

	/**
	 * Gets the position of the zoom control.
	 *
	 * @return the control position.
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.ControlPosition
	 * @since 1.0.0
	 */
	public String getZoomControlPosition()
	{
		return this.zoomControlPosition;
	}

	/**
	 * Gets the style of the zoom control.
	 *
	 * @return the control style
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.ZoomControlStyle
	 * @since 1.0.0
	 */
	public String getZoomControlStyle()
	{
		return this.zoomControlStyle;
	}

	/**
	 * Gets the zoom with single marker.
	 *
	 * @return the default zoom with single marker
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Zoom
	 * @since 1.0.0
	 */
	public String getZoomSingleMarker()
	{
		if (this.zoomSingleMarker == null)
		{
			return GoogleMaps.Zoom.DEFAULT_SINGLE_MARKER;
		}
		return this.zoomSingleMarker;
	}

	/**
	 * Checks whether the map type control is available.
	 *
	 * @return true, if available.
	 * @since 1.0.0
	 */
	public boolean hasMapTypeControl()
	{
		return this.mapTypeControl;
	}

	/**
	 * Checks whether the pan control is available.
	 *
	 * @return true, if available.
	 * @since 1.0.0
	 */
	public boolean hasPanControl()
	{
		return this.panControl;
	}

	/**
	 * Checks whether the street view control is available.
	 *
	 * @return true, if available
	 * @since 1.0.0
	 */
	public boolean hasStreetViewControl()
	{
		return this.streetViewControl;
	}

	/**
	 * Checks whether the zoom control is available.
	 *
	 * @return true, if available
	 * @since 1.0.0
	 */
	public boolean hasZoomControl()
	{
		return this.zoomControl;
	}

	/**
	 * Checks whether all controls are disabled.
	 *
	 * @return true, if controls are disabled.
	 * @since 1.0.0
	 */
	public boolean isDisableControls()
	{
		return this.disableControls;
	}

	/**
	 * Checks whether the map is draggable.
	 *
	 * @return true, if is draggable
	 * @since 1.0.0
	 */
	public boolean isDraggable()
	{
		return this.draggable;
	}

	/**
	 * Checks if markers are draggable.
	 *
	 * @return true, if markers are draggable.
	 * @since 1.0.0
	 */
	public boolean isDraggableMarker()
	{
		return this.draggableMarker;
	}

	/**
	 * Checks whether the scroll wheel is enabled.
	 *
	 * @return true, if the scroll wheel is enabled.
	 * @since 1.0.0
	 */
	public boolean isScrollwheel()
	{
		return this.scrollwheel;
	}

	/**
	 * Sets the city address component type. Default is LONG_NAME.
	 *
	 * @param cityAddressComponentType the new city address component type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	public void setCityAddressComponentType(final String cityAddressComponentType)
	{
		this.cityAddressComponentType = cityAddressComponentType;
	}

	/**
	 * Sets the country address component type. Default is SHORT_NAME.
	 *
	 * @param countryAddressComponentType the new country address component type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	public void setCountryAddressComponentType(final String countryAddressComponentType)
	{
		this.countryAddressComponentType = countryAddressComponentType;
	}

	/**
	 * Sets the default icon used on map. <br>
	 * The argument is the url to the image. Custom image can be used, default are proposed in the
	 * class GoogleMaps.Icons. Default is RED.
	 *
	 * @param defaultIcon the url to the image
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Icons
	 * @since 1.0.0
	 */
	public void setDefaultIcon(final String defaultIcon)
	{
		this.defaultIcon = defaultIcon;
	}

	/**
	 * Sets the default zoom. Default is LEVEL_2.
	 *
	 * @param defaultZoom the new default zoom
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Zoom
	 * @since 1.0.0
	 */
	public void setDefaultZoom(final String defaultZoom)
	{
		this.defaultZoom = defaultZoom;
	}

	/**
	 * Sets whether all controls are disabled. Default is false.
	 *
	 * @param disableControls true to disable all controls.
	 * @since 1.0.0
	 */
	public void setDisableControls(final boolean disableControls)
	{
		this.disableControls = disableControls;
	}

	/**
	 * Sets whether the map is dragabble. Default is true.
	 *
	 * @param draggable true to enable the drag of the map.
	 * @since 1.0.0
	 */
	public void setDraggable(final boolean draggable)
	{
		this.draggable = draggable;
	}

	/**
	 * Sets whether markers are draggable.
	 *
	 * @param draggableMarker true to enable the drag of the marker.
	 * @since 1.0.0
	 */
	public void setDraggableMarker(final boolean draggableMarker)
	{
		this.draggableMarker = draggableMarker;
	}

	/**
	 * Sets the max width of the info window. Default is 1000.
	 *
	 * @param infoWindowMaxWidth the new info window max width
	 * @since 1.0.0
	 */
	public void setInfoWindowMaxWidth(final Integer infoWindowMaxWidth)
	{
		this.infoWindowMaxWidth = infoWindowMaxWidth;
	}

	/**
	 * Sets the initial center lat.
	 *
	 * @param initialCenterLat the new default center lat
	 * @since 1.0.0
	 */
	public void setInitialCenterLat(final Double initialCenterLat)
	{
		this.initialCenterLat = initialCenterLat;
	}

	/**
	 * Sets the initial center long.
	 *
	 * @param initialCenterLong the new default center long
	 * @since 1.0.0
	 */
	public void setInitialCenterLong(final Double initialCenterLong)
	{
		this.initialCenterLong = initialCenterLong;
	}

	/**
	 * Sets the locale used for the Google Maps results.
	 *
	 * @param locale the locale.
	 * @since TODO since
	 */
	public void setLocale(final Locale locale)
	{
		this.locale = locale;
	}

	/**
	 * Sets the map type. Default is HYBRID.
	 *
	 * @param mapType the new map type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.MapType
	 * @since 1.0.0
	 */
	public void setMapType(final String mapType)
	{
		this.mapType = mapType;
	}

	/**
	 * Sets whether the map type control is available. Default is false.
	 *
	 * @param mapTypeControl true to enable the map type control
	 * @since 1.0.0
	 */
	public void setMapTypeControl(final boolean mapTypeControl)
	{
		this.mapTypeControl = mapTypeControl;
	}

	/**
	 * Sets the position of the map type control.
	 *
	 * @param mapTypeControlPosition the new position of the map type control.
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.ControlPosition
	 * @since 1.0.0
	 */
	public void setMapTypeControlPosition(final String mapTypeControlPosition)
	{
		this.mapTypeControlPosition = mapTypeControlPosition;
	}

	/**
	 * Sets the max zoom. Default is LEVEL_21.
	 *
	 * @param maxZoom the new max zoom
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Zoom
	 * @since 1.0.0
	 */
	public void setMaxZoom(final String maxZoom)
	{
		this.maxZoom = maxZoom;
	}

	/**
	 * Sets the min zoom. Default is LEVEL_1.
	 *
	 * @param minZoom the new min zoom
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Zoom
	 * @since 1.0.0
	 */
	public void setMinZoom(final String minZoom)
	{
		this.minZoom = minZoom;
	}

	/**
	 * Sets the number address component type. Default is LONG_NAME.
	 *
	 * @param numberAddressComponentType the new number address component type.
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	public void setNumberAddressComponentType(final String numberAddressComponentType)
	{
		this.numberAddressComponentType = numberAddressComponentType;
	}

	/**
	 * Sets whether the pan control is available. Default is false.
	 *
	 * @param panControl true to enable the new pan control
	 * @since 1.0.0
	 */
	public void setPanControl(final boolean panControl)
	{
		this.panControl = panControl;
	}

	/**
	 * Sets the position of the pan control.
	 *
	 * @param panControlPosition the new position of the pan control.
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.ControlPosition
	 * @since 1.0.0
	 */
	public void setPanControlPosition(final String panControlPosition)
	{
		this.panControlPosition = panControlPosition;
	}

	/**
	 * Sets the region address component type. Default is LONG_NAME.
	 *
	 * @param regionAddressComponentType the new region address component type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	public void setRegionAddressComponentType(final String regionAddressComponentType)
	{
		this.regionAddressComponentType = regionAddressComponentType;
	}

	/**
	 * Sets whether the scrollwheel is enable. Default is true.
	 *
	 * @param scrollwheel true to enable the scroll wheel.
	 * @since 1.0.0
	 */
	public void setScrollwheel(final boolean scrollwheel)
	{
		this.scrollwheel = scrollwheel;
	}

	/**
	 * Sets the street address component type. Default is LONG_NAME.
	 *
	 * @param streetAddressComponentType the new street address component type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	public void setStreetAddressComponentType(final String streetAddressComponentType)
	{
		this.streetAddressComponentType = streetAddressComponentType;
	}

	/**
	 * Sets whether the street view control is available. Default is false.
	 *
	 * @param streetViewControl true to enable the street view control
	 * @since 1.0.0
	 */
	public void setStreetViewControl(final boolean streetViewControl)
	{
		this.streetViewControl = streetViewControl;
	}

	/**
	 * Sets the position of the street view control.
	 *
	 * @param streetViewControlPosition the new street view control position
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.ControlPosition
	 * @since 1.0.0
	 */
	public void setStreetViewControlPosition(final String streetViewControlPosition)
	{
		this.streetViewControlPosition = streetViewControlPosition;
	}

	/**
	 * Sets the zip code address component type. Default is LONG_NAME.
	 *
	 * @param zipCodeAddressComponentType the new zip code address component type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	public void setZipCodeAddressComponentType(final String zipCodeAddressComponentType)
	{
		this.zipCodeAddressComponentType = zipCodeAddressComponentType;
	}

	/**
	 * Sets whether the zoom control is available. Default is true.
	 *
	 * @param zoomControl the new zoom control
	 * @since 1.0.0
	 */
	public void setZoomControl(final boolean zoomControl)
	{
		this.zoomControl = zoomControl;
	}

	/**
	 * Sets position of the zoom control.
	 *
	 * @param zoomControlPosition the new zoom control position.
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.ControlPosition
	 * @since 1.0.0
	 */
	public void setZoomControlPosition(final String zoomControlPosition)
	{
		this.zoomControlPosition = zoomControlPosition;
	}

	/**
	 * Sets the style of the zoom control. Default is DEFAULT.
	 *
	 * @param zoomControlStyle the new zoom control style
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.ZoomControlStyle
	 * @since 1.0.0
	 */
	public void setZoomControlStyle(final String zoomControlStyle)
	{
		this.zoomControlStyle = zoomControlStyle;
	}

	/**
	 * Sets the zoom with single marker. Default is Level_16.
	 *
	 * @param zoomSingleMarker the new default zoom with single marker
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Zoom
	 * @since 1.0.0
	 */
	public void setZoomSingleMarker(final String zoomSingleMarker)
	{
		this.zoomSingleMarker = zoomSingleMarker;
	}

	/**
	 * Gets the center.
	 *
	 * @return the center
	 * @since 1.0.0
	 */
	private String getCenter()
	{
		Double centerLat = this.getInitialCenterLat();
		Double centerLong = this.getInitialCenterLong();
		String center = "new google.maps.LatLng(" + String.valueOf(centerLat) + ", "
			+ String.valueOf(centerLong) + ")";

		return center;
	}

	/**
	 * Builds the content of the infoWindow JavaScript object.
	 *
	 * @return the object content.
	 * @since 1.0.0
	 */
	protected String buildInfoWindowJsObjectContent()
	{
		String jsObjectContent = "";
		jsObjectContent += JsObjectName.InfoWindow.MaxWidth.JS_NAME + ": "
			+ String.valueOf(this.getInfoWindowMaxWidth().intValue());
		return jsObjectContent;
	}

	/**
	 * Builds the content of the Maps JavaScript object.
	 *
	 * @return the object content.
	 * @since 1.0.0
	 */
	protected String buildMapsJsObjectContent()
	{
		String jsObjectContent = "";

		jsObjectContent += JsObjectName.Center.JS_NAME + ": " + this.getCenter() + ", ";
		jsObjectContent += JsObjectName.DisableDefaultUI.JS_NAME + ": " + this.isDisableControls()
			+ ", ";
		jsObjectContent += JsObjectName.Draggable.JS_NAME + ": " + this.isDraggable() + ", ";
		jsObjectContent += JsObjectName.MapType.JS_NAME + ": " + this.getMapType() + ", ";
		jsObjectContent += JsObjectName.MapTypeControl.JS_NAME + ": " + this.hasMapTypeControl()
			+ ", ";
		jsObjectContent += JsObjectName.MapTypeControlOptions.JS_NAME + ":{";
		jsObjectContent += JsObjectName.MapTypeControlOptions.Position.JS_NAME + ": "
			+ this.getMapTypeControlPosition();
		jsObjectContent += "}, ";
		jsObjectContent += JsObjectName.Zoom.JS_NAME + ": " + this.getDefaultZoom() + ", ";
		jsObjectContent += JsObjectName.MaxZoom.JS_NAME + ": " + this.getMaxZoom() + ", ";
		jsObjectContent += JsObjectName.MinZoom.JS_NAME + ": " + this.getMinZoom() + ", ";
		jsObjectContent += JsObjectName.ZoomControl.JS_NAME + ": " + this.hasZoomControl() + ", ";
		jsObjectContent += JsObjectName.ZoomControlOptions.JS_NAME + ": {";
		jsObjectContent += JsObjectName.ZoomControlOptions.Position.JS_NAME + ": "
			+ this.getZoomControlPosition() + ", ";
		jsObjectContent += JsObjectName.ZoomControlOptions.Style.JS_NAME + ": "
			+ this.getZoomControlStyle();
		jsObjectContent += "}, ";
		jsObjectContent += JsObjectName.OverviewMapControl.JS_NAME + ": "
			+ this.hasOverviewMapControl() + ", ";
		jsObjectContent += JsObjectName.PanControl.JS_NAME + ": " + this.hasPanControl() + ", ";
		jsObjectContent += JsObjectName.PanControlOptions.JS_NAME + ": {";
		jsObjectContent += JsObjectName.PanControlOptions.Position.JS_NAME + ": "
			+ this.getPanControlPosition();
		jsObjectContent += "}, ";
		jsObjectContent += JsObjectName.RotateControl.JS_NAME + ": " + this.hasRotateControl()
			+ ", ";
		jsObjectContent += JsObjectName.RotateControlOptions.JS_NAME + ": {";
		jsObjectContent += JsObjectName.RotateControlOptions.Position.JS_NAME + ": "
			+ this.getRotateControlPosition();
		jsObjectContent += "}, ";
		jsObjectContent += JsObjectName.ScaleControl.JS_NAME + ": " + this.hasScaleControl() + ", ";
		jsObjectContent += JsObjectName.Scrollwheel.JS_NAME + ": " + this.isScrollwheel() + ", ";
		jsObjectContent += JsObjectName.StreetViewControl.JS_NAME + ": "
			+ this.hasStreetViewControl() + ", ";
		jsObjectContent += JsObjectName.StreetViewControlOptions.JS_NAME + ": {";
		jsObjectContent += JsObjectName.StreetViewControlOptions.Position.JS_NAME + ": "
			+ this.getStreetViewControlPosition();
		jsObjectContent += "}";

		return jsObjectContent;
	}

	/**
	 * Gets the address component type.
	 *
	 * @param addressComponent the address component
	 * @return the type
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.AddressComponents.Type
	 * @since 1.0.0
	 */
	protected String getAddressComponentType(final String addressComponent)
	{
		if (addressComponent == null)
		{
			return "";
		}
		else if (addressComponent.equals(GoogleMaps.AddressComponents.STREET_NUMBER))
		{
			return this.getNumberAddressComponentType();
		}
		else if (addressComponent.equals(GoogleMaps.AddressComponents.ROUTE))
		{
			return this.getStreetAddressComponentType();
		}
		else if (addressComponent.equals(GoogleMaps.AddressComponents.ADMINISTRATIVE_AREA_1))
		{
			return this.getRegionAddressComponentType();
		}
		else if (addressComponent.equals(GoogleMaps.AddressComponents.POSTAL_CODE))
		{
			return this.getZipCodeAddressComponentType();
		}
		else if (addressComponent.equals(GoogleMaps.AddressComponents.LOCALITY))
		{
			return this.getCityAddressComponentType();
		}
		else if (addressComponent.equals(GoogleMaps.AddressComponents.COUNTRY))
		{
			return this.getCountryAddressComponentType();
		}
		else
		{
			return "";
		}
	}

	/**
	 * Gets the JavaScript object of the infoWindow options.
	 *
	 * @return the JavaScript object.
	 * @since 1.0.0
	 */
	protected String getInfoWindowJsObject()
	{
		String jsObject = "{";
		jsObject += this.buildInfoWindowJsObjectContent();
		jsObject += "}";
		return jsObject;
	}

	/**
	 * Gets the JavaScript object of the Maps options.
	 *
	 * @return the JavaScript object.
	 * @since 1.0.0
	 */
	protected String getMapsJsObject()
	{
		String jsObject = "{";
		jsObject += this.buildMapsJsObjectContent();
		jsObject += "}";
		return jsObject;
	}

	/**
	 * Gets the position of the rotate control.
	 *
	 * @return the control position
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.ControlPosition
	 * @since 1.0.0
	 */
	protected String getRotateControlPosition()
	{
		return this.rotateControlPosition;
	}

	/**
	 * Checks whether the overview map control is available.
	 *
	 * @return true, if available.
	 * @since 1.0.0
	 */
	protected boolean hasOverviewMapControl()
	{
		return this.overviewMapControl;
	}

	/**
	 * Checks whether the rotate control is available.
	 *
	 * @return true, if available
	 * @since 1.0.0
	 */
	protected boolean hasRotateControl()
	{
		return this.rotateControl;
	}

	/**
	 * Checks whether the scale control is available.
	 *
	 * @return true, if available
	 * @since 1.0.0
	 */
	protected boolean hasScaleControl()
	{
		return this.scaleControl;
	}

	/**
	 * Sets whether the overview map control is available. Defautl is false.
	 *
	 * @param overviewMapControl true to enable the overview map control
	 * @since 1.0.0
	 */
	protected void setOverviewMapControl(final boolean overviewMapControl)
	{
		this.overviewMapControl = overviewMapControl;
	}

	/**
	 * Sets whether the rotate control is available. Default is false.
	 *
	 * @param rotateControl true to enable the rotate control
	 * @since 1.0.0
	 */
	protected void setRotateControl(final boolean rotateControl)
	{
		this.rotateControl = rotateControl;
	}

	/**
	 * Sets the position of the rotate control.
	 *
	 * @param rotateControlPosition the new rotate control position
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.ControlPosition
	 * @since 1.0.0
	 */
	protected void setRotateControlPosition(final String rotateControlPosition)
	{
		this.rotateControlPosition = rotateControlPosition;
	}

	/**
	 * Sets whether the scale control is available. Default is false.
	 *
	 * @param scaleControl true to enable the scale control
	 * @since 1.0.0
	 */
	protected void setScaleControl(final boolean scaleControl)
	{
		this.scaleControl = scaleControl;
	}

}
