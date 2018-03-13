package com.hertz.mdm.vehicle.ui.form.style;

import com.hertz.mdm.vehicle.path.*;
import com.orchestranetworks.ui.form.*;

public class StyleCategoriesPane implements UIFormPane
{

	@Override
	public void writePane(UIFormPaneWriter pWriter, UIFormContext pContext)
	{
		pWriter.add("<table border=0>");
		pWriter.add("<tr>");
		pWriter.add("<td style=\"padding:12px; vertical-align:top;\">");
		pWriter.startTableFormRow();
		{
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_StyleCategories);
			/*if (pContext.isCreatingRecord())
			{
				pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_StyleCats._Root_ChromeNewVehicleData_StyleCats_StyleID);
			}
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_StyleCats._Root_ChromeNewVehicleData_StyleCats_CategoryID);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_StyleCats._Root_ChromeNewVehicleData_StyleCats_StyleName);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_StyleCats._Root_ChromeNewVehicleData_StyleCats_FeatureType);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_StyleCats._Root_ChromeNewVehicleData_StyleCats_Sequence);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_StyleCats._Root_ChromeNewVehicleData_StyleCats_State);
			*/
		}
		pWriter.endTableFormRow();
		pWriter.add("</td>");
		pWriter.add("</tr>");
		pWriter.add("</table>");
	}

}
