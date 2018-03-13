package com.hertz.mdm.location.ui.form.address;

import com.hertz.mdm.location.LocationAddressForm;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ui.form.UIForm;
import com.orchestranetworks.ui.form.UIFormBody;
import com.orchestranetworks.ui.form.UIFormContext;
import com.orchestranetworks.ui.form.UIFormPaneWithTabs;

public class ProjectAddressForm extends UIForm
{
	@Override
	public void defineBody(final UIFormBody pBody, final UIFormContext pContext)
	{
		String trackingInfo = pContext.getSession().getTrackingInfo();
		if (trackingInfo != null
			&& trackingInfo.equals(LocationAddressForm.TrackingInfo.WITHIN_GOOGLE_MAPS))
		{
		}

		if (pContext.isCreatingRecord())
		{
			pBody.setContent(new ProjectMainPane());
		}
		else
		{
			UIFormPaneWithTabs tabs = new UIFormPaneWithTabs();

			tabs.setHomeIconDisplayed(true);
			tabs.addTab(UserMessage.createInfo(""), new ProjectMainPane());

			pBody.setContent(tabs);
		}
	}
}
