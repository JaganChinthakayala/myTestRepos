package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps;

import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ui.UIComponentWriter;
import com.orchestranetworks.ui.UIHttpManagerComponent;

/**
 * Bean to define a Google Maps item. An item is especially represented by its address and the
 * record PK. On maps it's represented by a marker with an icon and a content of the info window.
 * Extend this class to add additionnal fields and override buildJsObjectContent to add them in the
 * JavaScript Object.
 *
 * @author ATI
 * @since 1.0.0
 */
public final class GoogleMapsItem
{
	/**
	 * The Class JsObject defines the properties to build the javascript object of a GoogleMapsItem.
	 *
	 * @since 1.0.0
	 */
	public class JsObject
	{
		/**
		 * The Class AddressValue defines the properties to build the javascript object representing
		 * an address.
		 *
		 * @since 1.0.0
		 */
		public class AddressValue
		{
			/**
			 * The Constant NUMBER with value number, property of the addressValue javascript
			 * object.
			 */
			public final static String NUMBER = "number";
			/**
			 * The Constant STREET with value street, property of the addressValue javascript
			 * object.
			 */
			public final static String STREET = "street";
			/**
			 * The Constant ZIPCODE with value zipcode, property of the addressValue javascript
			 * object.
			 */
			public final static String ZIPCODE = "zipcode";
			/**
			 * The Constant REGION with value region, property of the addressValue javascript
			 * object.
			 */
			public final static String REGION = "region";
			/**
			 * The Constant CITY with value city, property of the addressValue javascript object.
			 */
			public final static String CITY = "city";
			/**
			 * The Constant COUNTRY with value country, property of the addressValue javascript
			 * object.
			 */
			public final static String COUNTRY = "country";
			/**
			 * The Constant CONCATENATED_ADDRESS with value concatenatedAddress, property of the
			 * addressValue javascript object.
			 */
			public final static String CONCATENATED_ADDRESS = "concatenatedAddress";
			/**
			 * The Constant LAT with value lat, property of the addressValue javascript object.
			 */
			public final static String LAT = "lat";
			/**
			 * The Constant LONG with value lng, property of the addressValue javascript object.
			 */
			public final static String LONG = "lng";
		}

		/**
		 * The Constant PK with value pk, property of the GoogleMaps item javascript object.
		 */
		public final static String PK = "pk";
		/**
		 * The Constant LABEL with value label, property of the GoogleMaps item javascript object.
		 */
		public final static String LABEL = "label";
		/**
		 * The Constant ICON with value icon, property of the GoogleMaps item javascript object.
		 */
		public final static String ICON = "icon";
		/**
		 * The Constant CONTENT with value content, property of the GoogleMaps item javascript
		 * object.
		 */
		public final static String CONTENT = "content";
		/**
		 * The Constant ADDRESSVALUE with value addressValue, property of the GoogleMaps item
		 * javascript object.
		 *
		 * @see AddressValue
		 */
		public final static String ADDRESSVALUE = "addressValue";
	}

	/**
	 * Creates an Google Maps item from the record and mapping passed in parameter.
	 *
	 * @param pRecord the record to get build the item from.
	 * @param pMapping the GoolgeMaps mapping to use to get the address component from the record.
	 * @param pWriter the writer to build a manager component for a preview of the record.
	 * @param pCustomStyle a CSS style to apply on the iframe of the manager component of the record
	 *            preview.
	 * @param pTrackinginfo the tracking info to set to the manager component of the record preview.
	 * @return an instance of GoogleMapsItem representing the address of the record and some
	 *         options.
	 * @since 1.0.0
	 */
	public static GoogleMapsItem createDefaultItem(
		final Adaptation pRecord,
		final GoogleMapsAddressFieldMapping pMapping,
		final UIComponentWriter pWriter,
		final String pCustomStyle,
		final String pTrackinginfo)
	{
		if (pRecord == null)
		{
			return null;
		}

		Locale locale = pWriter.getLocale();

		GoogleMapsItem item = new GoogleMapsItem();

		String iframeDefaultStyle = "border: 0px solid;";

		GoogleMapsAddressValue address = GoogleMapsAddressValue
			.createAddressValue(pRecord, pMapping, locale);
		item.setAddressValue(address);

		String content = "";
		content += "<h4>" + pRecord.getLabelOrName(locale) + "</h4>";

		UIHttpManagerComponent managerComponent = pWriter.createWebComponentForSubSession();
		managerComponent.selectInstanceOrOccurrence(pRecord);
		managerComponent.hideAllDataSetFeatures();
		managerComponent.hideAllRecordFeatures();
		managerComponent.hideAllViewFeatures();
		if (pTrackinginfo != null)
		{
			managerComponent.setTrackingInfo(pTrackinginfo);
		}

		String url = managerComponent.getURIWithParameters();

		content += "<iframe style=\"" + iframeDefaultStyle + pCustomStyle + "\" src=\"" + url
			+ "\"></iframe>";

		item.setContent(content);
		item.setIcon(GoogleMaps.Icons.BLUE);

		return item;
	}

	private String pk;
	private String label;
	private GoogleMapsAddressValue addressValue;
	private String icon;
	private String content;

	/**
	 * Instantiates a new google maps item.
	 *
	 * @since 1.0.0
	 */
	public GoogleMapsItem()
	{
	}

	/**
	 * Instantiates a new google maps item.
	 *
	 * @param addressValue the address value
	 * @since 1.0.0
	 */
	public GoogleMapsItem(final GoogleMapsAddressValue addressValue)
	{
		this.addressValue = addressValue;
	}

	/**
	 * Instantiates a new google maps item.
	 *
	 * @param pk the pk
	 * @param label the label
	 * @param addressValue the address value
	 * @param icon the icon
	 * @param content the content
	 * @since 1.0.0
	 */
	public GoogleMapsItem(
		final String pk,
		final String label,
		final GoogleMapsAddressValue addressValue,
		final String icon,
		final String content)
	{
		this.pk = pk;
		this.label = label;
		this.addressValue = addressValue;
		this.icon = icon;
		this.content = content;
	}

	/**
	 * Gets the address value.
	 *
	 * @return the address value
	 * @since 1.0.0
	 */
	public GoogleMapsAddressValue getAddressValue()
	{
		return this.addressValue;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 * @since 1.0.0
	 */
	public String getContent()
	{
		if (this.content == null)
		{
			return "";
		}
		else
		{
			return this.content;
		}
	}

	/**
	 * Gets the url to the icon.
	 *
	 * @return the icon
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Icons
	 * @since 1.0.0
	 */
	public String getIcon()
	{
		return this.icon;
	}

	/**
	 * Gets the JavaScript object of the Google Maps item.
	 *
	 * @return the JavaScript object of the item.
	 * @since 1.0.0
	 */
	public String getJsObject()
	{
		String jsObject = "{";
		jsObject += this.buildJsObjectContent();
		jsObject += "}";
		return jsObject;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 * @since 1.0.0
	 */
	public String getLabel()
	{
		if (this.label == null)
		{
			return "";
		}
		else
		{
			return this.label;
		}
	}

	/**
	 * Gets the pk.
	 *
	 * @return the pk
	 * @since 1.0.0
	 */
	public String getPk()
	{
		if (this.pk == null)
		{
			return "";
		}
		else
		{
			return this.pk;
		}
	}

	/**
	 * Sets the address value.
	 *
	 * @param addressValue the new address value
	 * @since 1.0.0
	 */
	public void setAddressValue(final GoogleMapsAddressValue addressValue)
	{
		this.addressValue = addressValue;
	}

	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 * @since 1.0.0
	 */
	public void setContent(final String content)
	{
		this.content = content;
	}

	/**
	 * Sets the icon used on map. <br>
	 * The argument is the url to the image. Custom image can be used, default are proposed in the
	 * class GoogleMaps.Icons.
	 *
	 * @param icon the url to the image
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMaps.Icons
	 * @since 1.0.0
	 */
	public void setIcon(final String icon)
	{
		this.icon = icon;
	}

	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 * @since 1.0.0
	 */
	public void setLabel(final String label)
	{
		this.label = label;
	}

	/**
	 * Sets the pk.
	 *
	 * @param pk the new pk
	 * @since 1.0.0
	 */
	public void setPk(final String pk)
	{
		this.pk = pk;
	}

	/**
	 * Builds the content of the JavaScript object.
	 *
	 * @return the string
	 * @since 1.0.0
	 */
	protected String buildJsObjectContent()
	{
		String jsObjectContent = "";

		jsObjectContent += GoogleMapsItem.JsObject.PK + ":'" + this.getPk() + "',";
		jsObjectContent += GoogleMapsItem.JsObject.LABEL + ":'"
			+ this.getLabel().replace("'", "\\'") + "',";
		jsObjectContent += GoogleMapsItem.JsObject.CONTENT + ":'"
			+ this.getContent().replace("'", "\\'") + "',";

		jsObjectContent += GoogleMapsItem.JsObject.ICON + ":";
		if (this.getIcon() == null)
		{
			jsObjectContent += "null,";
		}
		else
		{
			jsObjectContent += "'" + this.getIcon() + "',";
		}

		if (this.getAddressValue() == null)
		{
			jsObjectContent += GoogleMapsItem.JsObject.ADDRESSVALUE + ":null";
		}
		else
		{
			jsObjectContent += GoogleMapsItem.JsObject.ADDRESSVALUE + ":{";
			jsObjectContent += GoogleMapsItem.JsObject.AddressValue.NUMBER + ":'"
				+ this.addressValue.getNumber().replace("'", "\\'") + "',";
			jsObjectContent += GoogleMapsItem.JsObject.AddressValue.STREET + ":'"
				+ this.addressValue.getStreet().replace("'", "\\'") + "',";
			jsObjectContent += GoogleMapsItem.JsObject.AddressValue.ZIPCODE + ":'"
				+ this.addressValue.getZipcode().replace("'", "\\'") + "',";
			jsObjectContent += GoogleMapsItem.JsObject.AddressValue.CITY + ":'"
				+ this.addressValue.getCity().replace("'", "\\'") + "',";
			jsObjectContent += GoogleMapsItem.JsObject.AddressValue.COUNTRY + ":'"
				+ this.addressValue.getCountry().replace("'", "\\'") + "',";
			jsObjectContent += GoogleMapsItem.JsObject.AddressValue.CONCATENATED_ADDRESS + ":'"
				+ this.addressValue.getConcatenatedAddress().replace("'", "\\'") + "',";
			if (this.addressValue.getLatitude() == null
				|| this.addressValue.getLatitude().equals(""))
			{
				jsObjectContent += GoogleMapsItem.JsObject.AddressValue.LAT + ":null,";
			}
			else
			{
				jsObjectContent += GoogleMapsItem.JsObject.AddressValue.LAT + ":'"
					+ this.addressValue.getLatitude() + "',";
			}
			if (this.addressValue.getLongitude() == null
				|| this.addressValue.getLongitude().equals(""))
			{
				jsObjectContent += GoogleMapsItem.JsObject.AddressValue.LONG + ":null";
			}
			else
			{
				jsObjectContent += GoogleMapsItem.JsObject.AddressValue.LONG + ":'"
					+ this.addressValue.getLongitude() + "'";
			}
			jsObjectContent += "}";
		}

		return jsObjectContent;
	}
}