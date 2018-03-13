package com.hertz.mdm.vehicle.matching.service;

import javax.servlet.http.*;

import com.hertz.mdm.vehicle.matching.constant.*;
import com.hertz.mdm.vehicle.matching.procedure.*;
import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.ui.*;

public class ManualOverrideService
{

	private static final LoggingCategory LOG = LoggingCategory.getKernel();

	public void execute(final HttpServletRequest request) throws Exception
	{

		ServiceContext sContext = ServiceContext.getServiceContext(request);
		Adaptation record = sContext.getCurrentAdaptation();
		AdaptationTable matchTable = record.getContainerTable();
		Adaptation duplicateRecord = null;
		UIComponentWriter writer = sContext.getUIComponentWriter();

		{
			String recordState = record.get(Path.parse("./DaqaMetaData/State")).toString();
			if (recordState.equals(DaqaConstant.STATUS_GOLDEN))
			{

				duplicateRecord = findManualRecord(record, sContext);
				if (duplicateRecord == null)
					duplicateRecord = doManualOverride(record, sContext);

				writer.addJS_cr(
					"window.location.href='" + sContext.getURLForSelection(duplicateRecord) + "';");
			}
			else
			{
				String messageContent = "Operation available only for Golden Records";
				String message = "alert('" + messageContent + "');";
				writer.addJS_cr(message);

				writer
					.addJS_cr("window.location.href='" + sContext.getURLForEndingService() + "';");

			}
		}

	}

	private Adaptation findManualRecord(Adaptation record, ServiceContext sContext)
	{
		Session aSession = sContext.getSession();
		AdaptationHome aDataSpace = record.getHome();
		AdaptationTable matchTable = record.getContainerTable();
		Adaptation dataSet = matchTable.getContainerAdaptation();
		Adaptation overRiddenRecord = null;

		final Procedure findproc = new FindManualRecordProcedure(record, matchTable, dataSet);
		final ProgrammaticService srv = ProgrammaticService.createForSession(aSession, aDataSpace);

		try
		{
			srv.execute(findproc);
			overRiddenRecord = ((FindManualRecordProcedure) findproc).getOverRiddenRecord();
		}
		catch (Exception e)
		{
			String msg = "Error overriding record";
			LOG.error(msg, e);
		}
		return overRiddenRecord;
	}

	private Adaptation doManualOverride(Adaptation record, ServiceContext sContext)
	{
		ValueContext vc = record.createValueContext();
		Session aSession = sContext.getSession();
		AdaptationHome aDataSpace = record.getHome();
		AdaptationTable table = record.getContainerTable();
		Adaptation dataSet = table.getContainerAdaptation();
		Adaptation overRiddenRecord = null;

		final ManualOverrideProcedure proc = new ManualOverrideProcedure(record, table, dataSet);

		final ProgrammaticService srv = ProgrammaticService.createForSession(aSession, aDataSpace);

		try
		{
			srv.execute(proc);
			overRiddenRecord = proc.getOverRiddenRecord();
		}
		catch (Exception e)
		{
			String msg = "Error overriding record";
			LOG.error(msg, e);
		}
		return overRiddenRecord;
	}
}
