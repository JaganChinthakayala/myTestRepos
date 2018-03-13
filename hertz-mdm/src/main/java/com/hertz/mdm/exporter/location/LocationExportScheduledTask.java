package com.hertz.mdm.exporter.location;

import com.hertz.mdm.exporter.location.LocationTableExporterFactory;
import com.hertz.mdm.exporter.TableExporterFactory;
import com.hertz.mdm.exporter.location.DataSetExporterScheduledTask;

public class LocationExportScheduledTask extends DataSetExporterScheduledTask {

	@Override
	protected TableExporterFactory createTableExporterFactory() {
		return new LocationTableExporterFactory();
	}
}
