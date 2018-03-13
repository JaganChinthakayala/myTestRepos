package com.hertz.mdm.vehicle.ui.form.style;

import com.hertz.mdm.vehicle.path.*;
import com.orchestranetworks.ui.form.*;

public class PricesPane implements UIFormPane
{

	@Override
	public void writePane(UIFormPaneWriter pWriter, UIFormContext pContext)
	{
		pWriter.add("<table style=\"width:100%\" border=0>");
		pWriter.add("<tr>");
		pWriter.add("<td style=\"padding:12px; vertical-align:top;\">");
		pWriter.startTableFormRow();
		{
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_Prices);
		}
		pWriter.endTableFormRow();
		pWriter.add("</td>");
		pWriter.add("</tr>");
		pWriter.add("</table>");
	}

}
