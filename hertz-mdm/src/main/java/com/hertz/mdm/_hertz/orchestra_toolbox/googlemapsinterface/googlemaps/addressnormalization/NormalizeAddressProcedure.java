package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.addressnormalization;

import java.util.ArrayList;
import java.util.List;

import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressFieldMapping;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressValue;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsItem;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.Procedure;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 * The Class NormalizeAddressProcedure.
 *
 * @author ATI
 * @since 1.0.0
 */
final class NormalizeAddressProcedure implements Procedure
{
	// TODO Possibility to use the generic procedures ?

	private final AdaptationTable table;
	private final GoogleMapsAddressFieldMapping mapping;
	private final List<GoogleMapsItem> itemsToUpdate;

	private final List<Adaptation> updatedRecords = new ArrayList<Adaptation>();

	/**
	 * Instantiates a new normalize address procedure.
	 *
	 * @param table the table
	 * @param itemsToUpdate the items to update
	 * @param mapping the mapping
	 * @since 1.0.0
	 */
	public NormalizeAddressProcedure(
		final AdaptationTable table,
		final List<GoogleMapsItem> itemsToUpdate,
		final GoogleMapsAddressFieldMapping mapping)
	{
		this.table = table;
		this.itemsToUpdate = itemsToUpdate;
		this.mapping = mapping;
	}

	/**
	 * Execute.
	 *
	 * @param pContext the context
	 * @throws Exception the exception
	 * @since 1.0.0
	 */
	@Override
	public void execute(final ProcedureContext pContext) throws Exception
	{
		if (this.table == null || this.itemsToUpdate == null || this.mapping == null)
		{
			return;
		}

		for (GoogleMapsItem itemToUpdate : this.itemsToUpdate)
		{

			String pk = itemToUpdate.getPk();

			Adaptation record = this.table.lookupAdaptationByPrimaryKey(PrimaryKey.parseString(pk));

			if (record == null)
			{
				continue;
			}

			GoogleMapsAddressValue addressValue = itemToUpdate.getAddressValue();

			ValueContextForUpdate update = pContext.getContext(record.getAdaptationName());
			this.setValues(update, addressValue);

			this.updatedRecords.add(pContext.doModifyContent(record, update));
		}
	}

	/**
	 * Gets the updated records.
	 *
	 * @return the updated records
	 * @since 1.0.0
	 */
	public List<Adaptation> getUpdatedRecords()
	{
		return this.updatedRecords;
	}

	/**
	 * Gets the value.
	 *
	 * @param value the value
	 * @param fkMapping the fk mapping
	 * @return the value
	 * @since 1.0.0
	 */
	private String getValue(final String value, final ForeignKeyMapping fkMapping)
	{
		if (value == null || value.equals(""))
		{
			return null;
		}
		if (fkMapping == null)
		{
			return value;
		}
		else
		{
			AdaptationTable fkTable = fkMapping.getTable();
			if (fkTable == null)
			{
				return value;
			}

			Path refPath = fkMapping.getGoogleMapsRef();
			if (refPath == null)
			{
				return value;
			}

			String predicate = refPath.format() + "='" + value + "'";
			RequestResult result = fkTable.createRequestResult(predicate);

			Adaptation fkRecord = result.nextAdaptation();
			if (fkRecord == null)
			{
				return value;
			}

			String fk = fkRecord.getOccurrencePrimaryKey().format();

			return fk;
		}
	}

	/**
	 * Sets the values.
	 *
	 * @param pUpdate the update
	 * @param pAddressValue the address value
	 * @since 1.0.0
	 */
	private void setValues(
		final ValueContextForUpdate pUpdate,
		final GoogleMapsAddressValue pAddressValue)
	{
		Path numberPath = this.mapping.getNumberPath();
		if (numberPath != null)
		{
			pUpdate.setValue(pAddressValue.getNumber(), numberPath);
		}

		Path streetPath = this.mapping.getStreetPath();
		if (streetPath != null)
		{
			String value = this
				.getValue(pAddressValue.getStreet(), this.mapping.getStreetFKMapping());
			pUpdate.setValue(value, streetPath);
		}

		Path zipcodePath = this.mapping.getZipcodePath();
		if (zipcodePath != null)
		{
			String value = this
				.getValue(pAddressValue.getZipcode(), this.mapping.getZipcodeFKMapping());
			pUpdate.setValue(value, zipcodePath);
		}

		Path cityPath = this.mapping.getCityPath();
		if (cityPath != null)
		{
			String value = this.getValue(pAddressValue.getCity(), this.mapping.getCityFKMapping());
			pUpdate.setValue(value, cityPath);
		}

		Path regionPath = this.mapping.getRegionPath();
		if (regionPath != null)
		{
			String value = this
				.getValue(pAddressValue.getRegion(), this.mapping.getRegionFKMapping());
			pUpdate.setValue(value, regionPath);
		}

		Path countryPath = this.mapping.getCountryPath();
		if (countryPath != null)
		{
			String value = this
				.getValue(pAddressValue.getCountry(), this.mapping.getCountryFKMapping());
			pUpdate.setValue(value, countryPath);
		}

		Path latitudePath = this.mapping.getLatitudePath();
		if (latitudePath != null)
		{
			pUpdate.setValue(pAddressValue.getLatitude(), latitudePath);
		}

		Path longitudePath = this.mapping.getLongitudePath();
		if (longitudePath != null)
		{
			pUpdate.setValue(pAddressValue.getLongitude(), longitudePath);
		}

	}

}
