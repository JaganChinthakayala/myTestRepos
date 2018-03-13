package com.hertz.mdm.vehicle.ui.form.style;

import com.hertz.mdm.vehicle.path.*;
import com.orchestranetworks.ui.form.*;

public class TechSpecPane implements UIFormPane
{

	@Override
	public void writePane(UIFormPaneWriter pWriter, UIFormContext pContext)
	{
		pWriter.add("<table border=0>");
		pWriter.add("<tr>");
		pWriter.add("<td style=\"padding:12px; vertical-align:top;\">");
		pWriter.startTableFormRow();
		{
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_TechSpecifications);
		}
		pWriter.endTableFormRow();
		pWriter.add("</td>");
		pWriter.add("</tr>");
		pWriter.add("</table>");
	}

}
