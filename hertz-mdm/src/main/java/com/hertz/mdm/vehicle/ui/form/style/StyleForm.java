package com.hertz.mdm.vehicle.ui.form.style;

import com.orchestranetworks.ui.form.*;
//import com.onwbp.base.text.UserMessage;

public class StyleForm extends UIForm
{

	@Override
	public void defineBody(final UIFormBody body, final UIFormContext context)
	{
		if (context.isCreatingRecord())
		{
			super.defineBody(body, context);
		}
		else
		{
			final UIFormPaneWithTabs tabs = new UIFormPaneWithTabs();

			tabs.setHomeIconDisplayed(true);
			tabs.addHomeTab(new StyleMainPane());
			tabs.addTab("Body Styles", new BodyStylesPane());
			tabs.addTab("Options", new OptionsPane());
			tabs.addTab("Colors", new ColorsPane());
			tabs.addTab("Prices", new PricesPane());
			tabs.addTab("Standards", new StandardsPane());
			tabs.addTab("Style Categories", new StyleCategoriesPane());
			tabs.addTab("Tech Specifications", new TechSpecPane());
			tabs.addTab("Vehicle Images", new VehicleImagesPane());
			
			body.setContent(tabs);
		}
	}
	

}
