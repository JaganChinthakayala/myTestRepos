package com.hertz.mdm.vehicle.ui.form.style;

import com.orchestranetworks.ui.form.*;
import com.hertz.mdm.vehicle.path.VehicleReferencePaths;

public class BodyStylesPane implements UIFormPane
{

	@Override
	public void writePane(UIFormPaneWriter pWriter, UIFormContext pContext)
	{
		pWriter.add("<table border=0>");
		pWriter.add("<tr>");
		pWriter.add("<td style=\"padding:12px; vertical-align:top;\">");
		pWriter.startTableFormRow();
		{
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_BodyStyles);
			/*if (pContext.isCreatingRecord())
			{
				pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_BodyStyles._Root_ChromeNewVehicleData_BodyStyles_StyleID);
			}
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_BodyStyles._Root_ChromeNewVehicleData_BodyStyles_BodyStyle);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_BodyStyles._Root_ChromeNewVehicleData_BodyStyles_StyleName);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_BodyStyles._Root_ChromeNewVehicleData_BodyStyles_IsPrimary);
			*/
		}
		pWriter.endTableFormRow();
		pWriter.add("</td>");
		pWriter.add("</tr>");
		pWriter.add("</table>");
	}
}
