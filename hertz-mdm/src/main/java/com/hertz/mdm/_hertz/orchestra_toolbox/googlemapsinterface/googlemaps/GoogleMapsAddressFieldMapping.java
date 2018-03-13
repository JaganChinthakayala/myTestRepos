package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps;

import java.util.List;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.UIComponentWriter;

/**
 * Bean to set the mapping to the address component. <br>
 * In the context of a UIFormPane, only Paths are used. <br>
 * In the context of a Service, input tag Ids are used.
 *
 * @author ATI
 * @since 1.0.0
 */
public final class GoogleMapsAddressFieldMapping
{
	/**
	 * The Class ForeignKeyMapping defines the mapping of a foreign key for an address component.
	 *
	 * @since 1.0.0
	 */
	public class ForeignKeyMapping
	{
		/**
		 * The Constant JS_VARNAME_FK_GOOGLEMAPS_REF with value ref, property of the FK data
		 * javascript object.
		 */
		public static final String JS_VARNAME_FK_GOOGLEMAPS_REF = "ref";
		/**
		 * The Constant JS_VARNAME_FK_EBX_VALUE with value ebxValue, property of the FK data
		 * javascript object.
		 */
		public static final String JS_VARNAME_FK_EBX_VALUE = "ebxValue";
		/**
		 * The Constant JS_VARNAME_FK_EBX_VALUE_KEY with value key, property of the FK data
		 * javascript object.
		 */
		public static final String JS_VARNAME_FK_EBX_VALUE_KEY = "key";
		/**
		 * The Constant JS_VARNAME_FK_EBX_VALUE_LABEL with value label, property of the FK data
		 * javascript object.
		 */
		public static final String JS_VARNAME_FK_EBX_VALUE_LABEL = "label";
		/**
		 * The Constant JS_FUNCNAME_GET_VALUE_FROM_FK with value getValueFromFK representing the
		 * name of the javascript function to get the value from a FK data.
		 */
		public static final String JS_FUNCNAME_GET_VALUE_FROM_FK = "getValueFromFK";
		/**
		 * The Constant JS_FUNCNAME_GET_FK_FROM_VALUE with value getFKFromValue representing the
		 * name of the javascript function to get the FK data from a value.
		 */
		public static final String JS_FUNCNAME_GET_FK_FROM_VALUE = "getFKFromValue";

		protected final static String FK_MAPPING_SUFFIX = "_fkMapping";

		private AdaptationTable table;
		private Path pkPath;
		private Path labelPath;
		private Path googleMapsRef;

		/**
		 * Instantiates a new foreign key mapping.
		 */
		public ForeignKeyMapping()
		{
		}

		/**
		 * Instantiates a new foreign key mapping.
		 *
		 * @param table the table
		 */
		public ForeignKeyMapping(final AdaptationTable table)
		{
			this.table = table;
		}

		/**
		 * Instantiates a new foreign key mapping.
		 *
		 * @param table the table
		 * @param pkPath the pk path
		 * @param labelPath the label path
		 * @param googleMapsRef the google maps ref
		 */
		public ForeignKeyMapping(
			final AdaptationTable table,
			final Path pkPath,
			final Path labelPath,
			final Path googleMapsRef)
		{
			this.table = table;
			this.pkPath = pkPath;
			this.labelPath = labelPath;
			this.googleMapsRef = googleMapsRef;
		}

		/**
		 * Gets the path of the value corresponding to the returned google maps.
		 *
		 * @return the google maps ref
		 */
		public Path getGoogleMapsRef()
		{
			return this.googleMapsRef;
		}

		/**
		 * Gets the path of the label.
		 *
		 * @return the label path
		 */
		public Path getLabelPath()
		{
			return this.labelPath;
		}

		/**
		 * Gets the pk path.
		 *
		 * @return the pk path
		 */
		public Path getPkPath()
		{
			return this.pkPath;
		}

		/**
		 * Gets the table.
		 *
		 * @return the table
		 */
		public AdaptationTable getTable()
		{
			return this.table;
		}

		/**
		 * Inserts the JavaScript list of foreign key.
		 *
		 * @param pWriter the writer
		 * @param pType the type of foreign key (ie. country or city).
		 */
		public void insertFkVarJs(final UIComponentWriter pWriter, final String pType)
		{
			if (this.table == null || pType == null || pType.equals(""))
			{
				return;
			}

			pWriter.addJS_cr();
			pWriter.addJS_cr("var " + pType + ForeignKeyMapping.FK_MAPPING_SUFFIX + " = [");
			pWriter.addJS_cr();

			@SuppressWarnings("unchecked")
			List<Adaptation> records = this.getTable().selectOccurrences(null);
			boolean firstItem = true;
			for (Adaptation record : records)
			{
				if (firstItem)
				{
					firstItem = false;
				}
				else
				{
					pWriter.addJS_cr(",");
				}

				pWriter.addJS(this.getJsObject(record));
			}

			pWriter.addJS_cr();
			pWriter.addJS_cr();
			pWriter.addJS_cr("];");
			pWriter.addJS_cr();
		}

		/**
		 * Inserts the JavaScript function to get the FK from a google maps value.
		 *
		 * @param pWriter the writer
		 */
		public void insertJsFunctionGetFKFromValue(final UIComponentWriter pWriter)
		{
			pWriter.addJS_cr();
			pWriter.addJS_cr(
				"    var " + ForeignKeyMapping.JS_FUNCNAME_GET_FK_FROM_VALUE
					+ " = function(fkArray, gMapValue) {");
			pWriter.addJS_cr("        if (fkArray) {");
			pWriter.addJS_cr("            for (var i = 0; i < fkArray.length; i++) {");
			pWriter.addJS_cr("                var tempFkValue = fkArray[i];");
			pWriter.addJS_cr(
				"                if (tempFkValue." + ForeignKeyMapping.JS_VARNAME_FK_GOOGLEMAPS_REF
					+ " === gMapValue) {");
			pWriter.addJS_cr(
				"                    return tempFkValue."
					+ ForeignKeyMapping.JS_VARNAME_FK_EBX_VALUE + ";");
			pWriter.addJS_cr("                }");
			pWriter.addJS_cr("            }");
			pWriter.addJS_cr("        }");
			pWriter.addJS_cr("        return null;");
			pWriter.addJS_cr("    }");
			pWriter.addJS_cr();
		}

		/**
		 * Inserts the JavaScript function to get the google maps value from an FK.
		 *
		 * @param pWriter the writer
		 */
		public void insertJsFunctionGetValueFromFK(final UIComponentWriter pWriter)
		{
			pWriter.addJS_cr();
			pWriter.addJS_cr(
				"    var " + ForeignKeyMapping.JS_FUNCNAME_GET_VALUE_FROM_FK
					+ " = function(fkArray, ebxFkValue) {");
			pWriter.addJS_cr("        if (fkArray && ebxFkValue) {");
			pWriter.addJS_cr("            for (var i = 0; i < fkArray.length; i++) {");
			pWriter.addJS_cr("                var tempFkValue = fkArray[i];");
			pWriter.addJS_cr(
				"                if (tempFkValue." + ForeignKeyMapping.JS_VARNAME_FK_EBX_VALUE + "."
					+ ForeignKeyMapping.JS_VARNAME_FK_EBX_VALUE_KEY + " === ebxFkValue."
					+ ForeignKeyMapping.JS_VARNAME_FK_EBX_VALUE_KEY + ") {");
			pWriter.addJS_cr(
				"                    return tempFkValue."
					+ ForeignKeyMapping.JS_VARNAME_FK_GOOGLEMAPS_REF + ";");
			pWriter.addJS_cr("                }");
			pWriter.addJS_cr("            }");
			pWriter.addJS_cr("        }");
			pWriter.addJS_cr("        return '';");
			pWriter.addJS_cr("    }");
			pWriter.addJS_cr();
		}

		/**
		 * Sets the path of the value corresponding to the returned google maps.
		 *
		 * @param googleMapsRef the new google maps ref
		 */
		public void setGoogleMapsRef(final Path googleMapsRef)
		{
			this.googleMapsRef = googleMapsRef;
		}

		/**
		 * Sets the label path.
		 *
		 * @param labelPath the new label path
		 */
		public void setLabelPath(final Path labelPath)
		{
			this.labelPath = labelPath;
		}

		/**
		 * Sets the pk path.
		 *
		 * @param pkPath the new pk path
		 */
		public void setPkPath(final Path pkPath)
		{
			this.pkPath = pkPath;
		}

		/**
		 * Sets the table.
		 *
		 * @param table the new table
		 */
		public void setTable(final AdaptationTable table)
		{
			this.table = table;
		}

		/**
		 * Gets the JavaScript object of the mapping for the FK record.
		 *
		 * @param pRecord the FK record
		 * @return the JavaScript object
		 */
		private String getJsObject(final Adaptation pRecord)
		{
			if (pRecord == null || this.pkPath == null || this.googleMapsRef == null)
			{
				return "";
			}

			String pk = null;
			Object pkObject = pRecord.get(this.getPkPath());
			if (pkObject != null)
			{
				pk = pkObject.toString();
			}

			String label = pk;
			if (this.labelPath != null)
			{
				Object labelObject = pRecord.get(this.getLabelPath());
				if (labelObject != null)
				{
					label = labelObject.toString();
				}
			}
			if (label != null)
			{
				label = label.replace("'", "\\'");
			}

			String ref = null;
			Object refObject = pRecord.get(this.getGoogleMapsRef());
			if (refObject != null)
			{
				ref = refObject.toString();
			}

			if (pk == null || ref == null)
			{
				return "";
			}

			String jsObject = "";
			jsObject += "{ ";
			jsObject += ForeignKeyMapping.JS_VARNAME_FK_GOOGLEMAPS_REF + ": '" + ref + "', ";
			jsObject += ForeignKeyMapping.JS_VARNAME_FK_EBX_VALUE + ": ";
			jsObject += "{ ";
			jsObject += ForeignKeyMapping.JS_VARNAME_FK_EBX_VALUE_KEY + ": '" + pk + "', ";
			jsObject += ForeignKeyMapping.JS_VARNAME_FK_EBX_VALUE_LABEL + ": '" + label + "'";
			jsObject += "}";
			jsObject += "}";

			return jsObject;
		}
	}

	private boolean isUIFormPane = false;
	private String numberId;
	private String streetId;
	private String zipcodeId;
	private String cityId;
	private String regionId;
	private String countryId;
	private String latitudeId;

	private String longitudeId;
	private Path numberPath;
	private Path streetPath;
	private Path zipcodePath;
	private Path cityPath;
	private Path regionPath;
	private Path countryPath;
	private Path latitudePath;
	private Path longitudePath;

	private Path addressStatusPath;
	private ForeignKeyMapping streetFKMapping;
	private ForeignKeyMapping zipcodeFKMapping;
	private ForeignKeyMapping cityFKMapping;
	private ForeignKeyMapping regionFKMapping;
	private ForeignKeyMapping countryFKMapping;

	/**
	 * Instantiates a new google maps address field mapping.
	 *
	 * @since 1.0.0
	 */
	public GoogleMapsAddressFieldMapping()
	{
	}

	/**
	 * Instantiates a new google maps address field mapping.
	 *
	 * @param numberPath the number path
	 * @param streetPath the street path
	 * @param zipcodePath the zipcode path
	 * @param cityPath the city path
	 * @param regionPath the region path
	 * @param countryPath the country path
	 * @param latitudePath the latitude path
	 * @param longitudePath the longitude path
	 * @param addressStatusPath the address status path
	 * @since 1.0.0
	 */
	public GoogleMapsAddressFieldMapping(
		final Path numberPath,
		final Path streetPath,
		final Path zipcodePath,
		final Path cityPath,
		final Path regionPath,
		final Path countryPath,
		final Path latitudePath,
		final Path longitudePath,
		final Path addressStatusPath)
	{
		this.numberPath = numberPath;
		this.streetPath = streetPath;
		this.zipcodePath = zipcodePath;
		this.cityPath = cityPath;
		this.regionPath = regionPath;
		this.countryPath = countryPath;
		this.latitudePath = latitudePath;
		this.longitudePath = longitudePath;
		this.addressStatusPath = addressStatusPath;
		this.isUIFormPane = true;
	}

	/**
	 * Gets the address status path.
	 *
	 * @return the address status path
	 * @since 1.0.0
	 */
	public Path getAddressStatusPath()
	{
		return this.addressStatusPath;
	}

	/**
	 * Gets the city fk mapping.
	 *
	 * @return the city fk mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping
	 * @since 1.0.0
	 */
	public ForeignKeyMapping getCityFKMapping()
	{
		return this.cityFKMapping;
	}

	/**
	 * Gets the city id.
	 *
	 * @return the city id
	 * @since 1.0.0
	 */
	public String getCityId()
	{
		return this.cityId;
	}

	/**
	 * Gets the city path.
	 *
	 * @return the city path
	 * @since 1.0.0
	 */
	public Path getCityPath()
	{
		return this.cityPath;
	}

	/**
	 * Gets the country fk mapping.
	 *
	 * @return the country fk mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping
	 * @since 1.0.0
	 */
	public ForeignKeyMapping getCountryFKMapping()
	{
		return this.countryFKMapping;
	}

	/**
	 * Gets the country id.
	 *
	 * @return the country id
	 * @since 1.0.0
	 */
	public String getCountryId()
	{
		return this.countryId;
	}

	/**
	 * Gets the country path.
	 *
	 * @return the country path
	 * @since 1.0.0
	 */
	public Path getCountryPath()
	{
		return this.countryPath;
	}

	/**
	 * Gets the latitude id.
	 *
	 * @return the latitude id
	 * @since 1.0.0
	 */
	public String getLatitudeId()
	{
		return this.latitudeId;
	}

	/**
	 * Gets the latitude path.
	 *
	 * @return the latitude path
	 * @since 1.0.0
	 */
	public Path getLatitudePath()
	{
		return this.latitudePath;
	}

	/**
	 * Gets the longitude id.
	 *
	 * @return the longitude id
	 * @since 1.0.0
	 */
	public String getLongitudeId()
	{
		return this.longitudeId;
	}

	/**
	 * Gets the longitude path.
	 *
	 * @return the longitude path
	 * @since 1.0.0
	 */
	public Path getLongitudePath()
	{
		return this.longitudePath;
	}

	/**
	 * Gets the number id.
	 *
	 * @return the number id
	 * @since 1.0.0
	 */
	public String getNumberId()
	{
		return this.numberId;
	}

	/**
	 * Gets the number path.
	 *
	 * @return the number path
	 * @since 1.0.0
	 */
	public Path getNumberPath()
	{
		return this.numberPath;
	}

	/**
	 * Gets the region fk mapping.
	 *
	 * @return the region fk mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping
	 * @since 1.0.0
	 */
	public ForeignKeyMapping getRegionFKMapping()
	{
		return this.regionFKMapping;
	}

	/**
	 * Gets the region id.
	 *
	 * @return the region id
	 * @since 1.0.0
	 */
	public String getRegionId()
	{
		return this.regionId;
	}

	/**
	 * Gets the region path.
	 *
	 * @return the region path
	 * @since 1.0.0
	 */
	public Path getRegionPath()
	{
		return this.regionPath;
	}

	/**
	 * Gets the street fk mapping.
	 *
	 * @return the street fk mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping
	 * @since 1.0.0
	 */
	public ForeignKeyMapping getStreetFKMapping()
	{
		return this.streetFKMapping;
	}

	/**
	 * Gets the street id.
	 *
	 * @return the street id
	 * @since 1.0.0
	 */
	public String getStreetId()
	{
		return this.streetId;
	}

	/**
	 * Gets the street path.
	 *
	 * @return the street path
	 * @since 1.0.0
	 */
	public Path getStreetPath()
	{
		return this.streetPath;
	}

	/**
	 * Gets the zipcode fk mapping.
	 *
	 * @return the zipcode fk mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping
	 * @since 1.0.0
	 */
	public ForeignKeyMapping getZipcodeFKMapping()
	{
		return this.zipcodeFKMapping;
	}

	/**
	 * Gets the zipcode id.
	 *
	 * @return the zipcode id
	 * @since 1.0.0
	 */
	public String getZipcodeId()
	{
		return this.zipcodeId;
	}

	/**
	 * Gets the zipcode path.
	 *
	 * @return the zipcode path
	 * @since 1.0.0
	 */
	public Path getZipcodePath()
	{
		return this.zipcodePath;
	}

	/**
	 * Checks if is UI form pane.
	 *
	 * @return true, if is UI form pane
	 * @since 1.0.0
	 */
	public boolean isUIFormPane()
	{
		return this.isUIFormPane;
	}

	/**
	 * Sets the address status path.
	 *
	 * @param addressStatusPath the new address status path
	 * @since 1.0.0
	 */
	public void setAddressStatusPath(final Path addressStatusPath)
	{
		this.addressStatusPath = addressStatusPath;
	}

	/**
	 * Sets the city fk mapping.
	 *
	 * @param cityFKMapping the new city fk mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping
	 * @since 1.0.0
	 */
	public void setCityFKMapping(final ForeignKeyMapping cityFKMapping)
	{
		this.cityFKMapping = cityFKMapping;
	}

	/**
	 * Sets the city id.
	 *
	 * @param cityId the new city id
	 * @since 1.0.0
	 */
	public void setCityId(final String cityId)
	{
		this.cityId = cityId;
	}

	/**
	 * Sets the city path.
	 *
	 * @param cityPath the new city path
	 * @since 1.0.0
	 */
	public void setCityPath(final Path cityPath)
	{
		this.cityPath = cityPath;
	}

	/**
	 * Sets the country fk mapping.
	 *
	 * @param countryFKMapping the new country fk mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping
	 * @since 1.0.0
	 */
	public void setCountryFKMapping(final ForeignKeyMapping countryFKMapping)
	{
		this.countryFKMapping = countryFKMapping;
	}

	/**
	 * Sets the country id.
	 *
	 * @param countryId the new country id
	 * @since 1.0.0
	 */
	public void setCountryId(final String countryId)
	{
		this.countryId = countryId;
	}

	/**
	 * Sets the country path.
	 *
	 * @param countryPath the new country path
	 * @since 1.0.0
	 */
	public void setCountryPath(final Path countryPath)
	{
		this.countryPath = countryPath;
	}

	/**
	 * Sets the latitude id.
	 *
	 * @param latitudeId the new latitude id
	 * @since 1.0.0
	 */
	public void setLatitudeId(final String latitudeId)
	{
		this.latitudeId = latitudeId;
	}

	/**
	 * Sets the latitude path.
	 *
	 * @param latitudePath the new latitude path
	 * @since 1.0.0
	 */
	public void setLatitudePath(final Path latitudePath)
	{
		this.latitudePath = latitudePath;
	}

	/**
	 * Sets the longitude id.
	 *
	 * @param longitudeId the new longitude id
	 * @since 1.0.0
	 */
	public void setLongitudeId(final String longitudeId)
	{
		this.longitudeId = longitudeId;
	}

	/**
	 * Sets the longitude path.
	 *
	 * @param longitudePath the new longitude path
	 * @since 1.0.0
	 */
	public void setLongitudePath(final Path longitudePath)
	{
		this.longitudePath = longitudePath;
	}

	/**
	 * Sets the number id.
	 *
	 * @param numberId the new number id
	 * @since 1.0.0
	 */
	public void setNumberId(final String numberId)
	{
		this.numberId = numberId;
	}

	/**
	 * Sets the number path.
	 *
	 * @param numberPath the new number path
	 * @since 1.0.0
	 */
	public void setNumberPath(final Path numberPath)
	{
		this.numberPath = numberPath;
	}

	/**
	 * Sets the region fk mapping.
	 *
	 * @param regionFKMapping the new region fk mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping
	 * @since 1.0.0
	 */
	public void setRegionFKMapping(final ForeignKeyMapping regionFKMapping)
	{
		this.regionFKMapping = regionFKMapping;
	}

	/**
	 * Sets the region id.
	 *
	 * @param regionId the new region id
	 * @since 1.0.0
	 */
	public void setRegionId(final String regionId)
	{
		this.regionId = regionId;
	}

	/**
	 * Sets the region path.
	 *
	 * @param regionPath the new region path
	 * @since 1.0.0
	 */
	public void setRegionPath(final Path regionPath)
	{
		this.regionPath = regionPath;
	}

	/**
	 * Sets the street fk mapping.
	 *
	 * @param streetFKMapping the new street fk mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping
	 * @since 1.0.0
	 */
	public void setStreetFKMapping(final ForeignKeyMapping streetFKMapping)
	{
		this.streetFKMapping = streetFKMapping;
	}

	/**
	 * Sets the street id.
	 *
	 * @param streetId the new street id
	 * @since 1.0.0
	 */
	public void setStreetId(final String streetId)
	{
		this.streetId = streetId;
	}

	/**
	 * Sets the street path.
	 *
	 * @param streetPath the new street path
	 * @since 1.0.0
	 */
	public void setStreetPath(final Path streetPath)
	{
		this.streetPath = streetPath;
	}

	/**
	 * Sets the UI form pane.
	 *
	 * @param isUIFormPane the new UI form pane
	 * @since 1.0.0
	 */
	public void setUIFormPane(final boolean isUIFormPane)
	{
		this.isUIFormPane = isUIFormPane;
	}

	/**
	 * Sets the zipcode fk mapping.
	 *
	 * @param zipcodeFKMapping the new zipcode fk mapping
	 * @see com.orchestranetworks.presales.toolbox.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping
	 * @since 1.0.0
	 */
	public void setZipcodeFKMapping(final ForeignKeyMapping zipcodeFKMapping)
	{
		this.zipcodeFKMapping = zipcodeFKMapping;
	}

	/**
	 * Sets the zipcode id.
	 *
	 * @param zipcodeId the new zipcode id
	 * @since 1.0.0
	 */
	public void setZipcodeId(final String zipcodeId)
	{
		this.zipcodeId = zipcodeId;
	}

	/**
	 * Sets the zipcode path.
	 *
	 * @param zipcodePath the new zipcode path
	 * @since 1.0.0
	 */
	public void setZipcodePath(final Path zipcodePath)
	{
		this.zipcodePath = zipcodePath;
	}
}
