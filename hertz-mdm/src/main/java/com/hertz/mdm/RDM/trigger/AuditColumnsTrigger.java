package com.hertz.mdm.RDM.trigger;

import java.util.Calendar;
import java.util.Date;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.trigger.BeforeCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.BeforeDeleteOccurrenceContext;
import com.orchestranetworks.schema.trigger.BeforeModifyOccurrenceContext;
import com.orchestranetworks.schema.trigger.TableTrigger;
import com.orchestranetworks.schema.trigger.TriggerSetupContext;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ValueContextForUpdate;
import com.hertz.mdm.RDM.path.CrossReferencePaths;

public class AuditColumnsTrigger extends TableTrigger implements CrossReferencePaths {
	
	protected static final LoggingCategory LOG = LoggingCategory.getKernel();
	
	private Path creationUserFieldPath = CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_STATUS._Root_Cross_Reference_Data_RDM_LOV_STATUS_CREATED_BY;
	private Path creationDateFieldPath = CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_STATUS._Root_Cross_Reference_Data_RDM_LOV_STATUS_CREATED_DT;
	private Path lastUpdateUserFieldPath = CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_STATUS._Root_Cross_Reference_Data_RDM_LOV_STATUS_LAST_UPD_BY;
	private Path lastUpdateDateFieldPath = CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_STATUS._Root_Cross_Reference_Data_RDM_LOV_STATUS_LAST_UPD_DT;
	
	public String getCreationUserFieldPath() {
		return this.creationUserFieldPath.format();
	}

	public void setCreationUserFieldPath(String creationUserFieldPath) {
		this.creationUserFieldPath = Path.parse(creationUserFieldPath);
	}

	public String getCreationDateFieldPath() {
		return creationDateFieldPath.format();
	}

	public void setCreationDateFieldPath(String creationDateFieldPath) {
		this.creationDateFieldPath = Path.parse(creationDateFieldPath);
	}

	public String getLastUpdateUserFieldPath() {
		return lastUpdateUserFieldPath.format();
	}

	public void setLastUpdateUserFieldPath(String lastUpdateUserFieldPath) {
		this.lastUpdateUserFieldPath = Path.parse(lastUpdateUserFieldPath);
	}

	public String getLastUpdateDateFieldPath() {
		return lastUpdateDateFieldPath.format();
	}

	public void setLastUpdateDateFieldPath(String lastUpdateDateFieldPath) {
		this.lastUpdateDateFieldPath = Path.parse(lastUpdateDateFieldPath);
	}


	@Override
	public void setup(TriggerSetupContext context) {
		SchemaNode node = context.getSchemaNode();
		if (!node.isTableNode() && !node.isTableOccurrenceNode())
			context.addError("Table trigger " + this.getClass().getName()
					+ " applied to non table.");
		
		if (node.getNode(creationUserFieldPath) == null)
			context.addError("Creation user field path " + this.creationUserFieldPath + " not found on table.");
		if (node.getNode(lastUpdateUserFieldPath) == null)
			context.addError("Last update field path " + this.lastUpdateUserFieldPath + " not found on table.");
		
		if (node.getNode(creationDateFieldPath) == null)
			context.addError("Creation Date field path " + this.creationDateFieldPath + " not found on table.");
		if (node.getNode(lastUpdateDateFieldPath) == null)
			context.addError("Last update date field path " + this.lastUpdateDateFieldPath + " not found on table.");
		
	}

	public void handleBeforeCreate(BeforeCreateOccurrenceContext context)
			throws OperationException {

		context.setAllPrivileges();

		final ValueContextForUpdate vc = context.getOccurrenceContextForUpdate();
		String userID = context.getSession().getUserReference().getUserId();

		/** Capitalize the userID */
		if (null != userID) {
			userID = userID.toUpperCase();
		}

		vc.setValue(userID, creationUserFieldPath);
		vc.setValue(userID, lastUpdateUserFieldPath);
		Date now = Calendar.getInstance().getTime();
		vc.setValue(now, creationDateFieldPath);
		vc.setValue(now, lastUpdateDateFieldPath);
		
		super.handleBeforeCreate(context);
	}

	@Override
	public void handleBeforeModify(BeforeModifyOccurrenceContext context)
			throws OperationException {
		context.setAllPrivileges();

		final ValueContextForUpdate vc = context
				.getOccurrenceContextForUpdate();


		String userID = context.getSession().getUserReference().getUserId();

		/** Capitalize the userID */
		if (null != userID) {
			userID = userID.toUpperCase();
		}

		vc.setValue(userID, lastUpdateUserFieldPath);
		vc.setValue(new Date(), lastUpdateDateFieldPath);

		super.handleBeforeModify(context);
	}

	
	
	public void handleBeforeDelete(BeforeDeleteOccurrenceContext context)
	throws OperationException {

	}
}