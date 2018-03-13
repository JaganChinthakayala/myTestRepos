package com.hertz.mdm.vehicle.ui.form.style;

import com.orchestranetworks.ui.form.*;
import com.hertz.mdm.vehicle.path.VehicleReferencePaths;
import com.onwbp.adaptation.*;

public class StyleMainPane implements UIFormPane
{

	@Override
	public void writePane(UIFormPaneWriter pWriter, UIFormContext pContext)
	{
		pWriter.add("<table border=0>");
		pWriter.add("<tr>");
		{
			pWriter.add("<td style=\"padding:12px; vertical-align:top;\">");
			pWriter.startTableFormRow();
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_StyleID);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_HistStyleID);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_ModelID);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_ModelYear);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_Sequence);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_StyleCode);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_FullStyleCode);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_StyleName);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_TrueBasePrice);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_Invoice);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_Msrp);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_Destination);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_StyleCVCList);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_MktClassID);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_StyleNameWOTrim);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_Trim);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_PassengerCapacity);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_PassangerDoors);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_ManualTrans);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_AutoTrans);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_FrontWD);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_RearWD);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_AllWD);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_FourWD);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_StepSide);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_Caption);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_Availability);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_PriceState);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_AutoBuilderStyleID);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_CfModelName);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_CfStyleName);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_CfDriveTrain);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_CfBodyType);
			pWriter.addFormRow(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles__makeModel);
			pWriter.endTableFormRow();

			pWriter.add("</td>");

			final String FRONT_VIEW = "Front";

			Adaptation styleRecord = pContext.getCurrentRecord();

			AdaptationTable styleImageLinksTable = styleRecord.getContainer()
				.getTable(VehicleReferencePaths._Root_ChromeNewVehicleData_StyleImageLinks.getPathInSchema());
	
			String styleId = styleRecord.get(VehicleReferencePaths._Root_ChromeNewVehicleData_Styles._Root_ChromeNewVehicleData_Styles_StyleID).toString();
			
			// Query the StyleImageLink table to get front image url
			RequestResult requestResult = styleImageLinksTable.createRequestResult(
				VehicleReferencePaths._Root_ChromeNewVehicleData_StyleImageLinks._Root_ChromeNewVehicleData_StyleImageLinks_StyleID.format()
			  + "='" + styleId + "' and "
			  + VehicleReferencePaths._Root_ChromeNewVehicleData_StyleImageLinks._Root_ChromeNewVehicleData_StyleImageLinks_ViewType.format()
			  + "='" + FRONT_VIEW + "'");
			
			if (requestResult.isEmpty())
			{
				requestResult.close();
				return;
			}
			
			Adaptation styleImageRecord = requestResult.nextAdaptation();
			
			requestResult.close();
			String imageUrl = styleImageRecord
		  	  					.get(VehicleReferencePaths._Root_ChromeNewVehicleData_StyleImageLinks._Root_ChromeNewVehicleData_StyleImageLinks_ImageLink)
		  	  					.toString();

			pWriter.add("<td colspan=2 style=\"padding:12px; vertical-align:top;\">");
			pWriter.add("<div style=\"text-align: center;\"><img width=\"320px;\" src=\"")
							.add(imageUrl)
							.add("\"></div>");
			pWriter.add("<br>");
			pWriter.add("</td>");
		}

		pWriter.add("</tr>");
		pWriter.add("</table>");
	}

}
