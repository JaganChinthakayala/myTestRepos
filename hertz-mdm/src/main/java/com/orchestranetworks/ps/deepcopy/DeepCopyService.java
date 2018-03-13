/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.deepcopy;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.ps.trigger.*;
import com.orchestranetworks.ps.trigger.TriggerActionValidator.TriggerAction;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.ui.*;

/**
 */
public class DeepCopyService extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	public static final String DEEP_COPY_SESSION_ATTRIBUTE = "deepCopy";

	private DeepCopyConfigFactory configFactory;

	public DeepCopyService(DeepCopyConfigFactory configFactory)
	{
		this.configFactory = configFactory;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
		ServiceContext sContext = ServiceContext.getServiceContext(req);
		Adaptation record = sContext.getCurrentAdaptation();
		Adaptation createdRecord;
		try
		{
			createdRecord = deepCopy(
				record,
				null,
				new DefaultDeepCopyDataModifier(),
				null,
				null,
				sContext.getSession());
		}
		catch (OperationException ex)
		{
			throw new ServletException(ex);
		}

		UIServiceComponentWriter writer = sContext.getUIComponentWriter();
		String url = getRedirectURL(sContext, createdRecord);
		writer.addJS_cr("window.location.href='" + url + "';");
	}

	protected String getRedirectURL(ServiceContext sContext, Adaptation createdRecord)
	{
		// TODO: Doesn't work when called from association. Need feedback from engineering.
		return sContext.getURLForEndingService();
	}

	/**
	 * @deprecated Use {@link deepCopy(ProcedureContext,Adaptation,AdaptationHome,DeepCopyDataModifier,DeepCopyRule,String)} instead.
	 */
	public Adaptation deepCopy(
		ProcedureContext pContext,
		Adaptation record,
		AdaptationHome dataSpaceToCopyTo,
		DeepCopyDataModifier dataModifier,
		String fkToParentSelectionNodeOrAssociationValue) throws OperationException
	{
		return deepCopy(
			pContext,
			record,
			dataSpaceToCopyTo,
			dataModifier,
			null,
			fkToParentSelectionNodeOrAssociationValue);
	}

	/**
	 * Call the deep copy when within an existing procedure context
	 *
	 * @param pContext the procedure context
	 * @param record the record to copy
	 * @param dataSpaceToCopyTo the data space to copy to,
	 *        or <code>null</code> if copying to same data space as the record
	 * @param dataModifier the data modifier, or <code>null</code>
	 * @param rule the rule determining whether to copy, or <code>null</code>
	 * @param fkToParentSelectionNodeOrAssociationValue the value of the foreign key to the parent node,
	 *        if this is a record of a table referencing a copied parent, or <code>null</code>
	 * @return the copied record
	 * @throws OperationException if an error occurs
	 */
	public Adaptation deepCopy(
		ProcedureContext pContext,
		Adaptation record,
		AdaptationHome dataSpaceToCopyTo,
		DeepCopyDataModifier dataModifier,
		DeepCopyRule rule,
		String fkToParentSelectionNodeOrAssociationValue) throws OperationException
	{
		return executeInProcedureContext(
			pContext,
			configFactory,
			record,
			dataSpaceToCopyTo,
			dataModifier,
			rule,
			fkToParentSelectionNodeOrAssociationValue);
	}

	/**
	 * @deprecated Use {@link deepCopy(Adaptation,AdaptationHome,DeepCopyDataModifier,DeepCopyRule,String,Session)} instead.
	 */
	public Adaptation deepCopy(
		Adaptation record,
		AdaptationHome dataSpaceToCopyTo,
		DeepCopyDataModifier dataModifier,
		String fkToParentSelectionNodeOrAssociationValue,
		Session session) throws OperationException
	{
		return deepCopy(
			record,
			dataSpaceToCopyTo,
			dataModifier,
			null,
			fkToParentSelectionNodeOrAssociationValue,
			session);
	}

	/**
	 * Call the deep copy in a new procedure
	 *
	 * @param record the record to copy
	 * @param dataSpaceToCopyTo the data space to copy to,
	 *        or <code>null</code> if copying to same data space as the record
	 * @param dataModifier the data modifier, or <code>null</code>
	 * @param rule the rule determining whether to copy, or <code>null</code>
	 * @param fkToParentSelectionNodeOrAssociationValue the value of the foreign key to the parent node,
	 *        if this is a record of a table referencing a copied parent, or <code>null</code>
	 * @param session the session
	 * @return the copied record
	 * @throws OperationException if an error occurs
	 */
	public Adaptation deepCopy(
		Adaptation record,
		AdaptationHome dataSpaceToCopyTo,
		DeepCopyDataModifier dataModifier,
		DeepCopyRule rule,
		String fkToParentSelectionNodeOrAssociationValue,
		Session session) throws OperationException
	{
		DeepCopyProcedure proc = new DeepCopyProcedure(
			record,
			dataSpaceToCopyTo,
			dataModifier,
			rule,
			fkToParentSelectionNodeOrAssociationValue);
		ProcedureExecutor.executeProcedure(
			proc,
			session,
			dataSpaceToCopyTo == null ? record.getHome() : dataSpaceToCopyTo);
		return proc.getCreatedRecord();
	}

	protected Adaptation doCopy(
		Adaptation record,
		AdaptationHome dataSpaceToCopyTo,
		String fkToParentSelectionNodeOrAssociationValue,
		DeepCopyConfig config,
		DeepCopyDataModifier dataModifier,
		DeepCopyRule deepCopyRule,
		ProcedureContext pContext) throws OperationException
	{
		AdaptationTable table = record.getContainerTable();
		if (dataSpaceToCopyTo != null)
		{
			Adaptation dataSet = dataSpaceToCopyTo.findAdaptationOrNull(AdaptationName.forName(record.getContainer()
				.getAdaptationName()
				.getStringName()));
			table = dataSet.getTable(table.getTablePath());
		}
		ValueContextForUpdate vc = pContext.getContextForNewOccurrence(record, table);

		// If there is a deep copy rule, check before proceeding.
		if (deepCopyRule != null)
		{
			if (!deepCopyRule.executeCopy(record, config))
			{
				return null;
			}
		}

		// If there is a data modifier, apply its modifications
		if (dataModifier != null)
		{
			dataModifier.modifyDuplicateRecordContext(vc, record, config);
		}

		// Copy records referenced via foreign key for any indicated
		Map<Path, DeepCopyConfig> foreignKeyConfigMap = config.getForeignKeyConfigMap();
		for (Map.Entry<Path,DeepCopyConfig> entry : foreignKeyConfigMap.entrySet())
		{
			Path path = entry.getKey();
			DeepCopyConfig fkConfig = entry.getValue();
			if (fkConfig != null)
			{
				Adaptation foreignRecord = AdaptationUtil.followFK(record, path);
				if (foreignRecord != null)
				{
					Adaptation newForeignRecord = doCopy(
						foreignRecord,
						dataSpaceToCopyTo,
						null,
						fkConfig,
						dataModifier,
						deepCopyRule,
						pContext);
					// Set the fk of the new record to the new foreign record
					if (newForeignRecord != null)
					{
						vc.setValue(newForeignRecord.getOccurrencePrimaryKey().format(), path);
					}
				}
			}
		}

		// If this is a record that was copied via a selection node or association,
		// need to set its foreign key back to the new parent record
		if (fkToParentSelectionNodeOrAssociationValue != null)
		{
			Path pathToParent = config.getPathToParentSelectionNodeOrAssociation();
			vc.setValue(fkToParentSelectionNodeOrAssociationValue, pathToParent);
		}

		// Create the record
		Adaptation newRecord = pContext.doCreateOccurrence(vc, table);
		String newRecordPK = newRecord.getOccurrencePrimaryKey().format();

		// Follow any specified selection nodes and for all records linked,
		// duplicate them and set them to link back to this new record
		Map<Path, DeepCopyConfig> selectionNodeConfigMap = config.getSelectionNodeConfigMap();
		for (Path path : selectionNodeConfigMap.keySet())
		{
			DeepCopyConfig selNodeConfig = selectionNodeConfigMap.get(path);
			if (selNodeConfig != null)
			{
				Path selNodeFKPath = selNodeConfig.getPathToParentSelectionNodeOrAssociation();
				if (selNodeFKPath == null)
				{
					throw OperationException.createError("Config for copy of " + path.format()
						+ " doesn't specify the foreign key back to parent selection node.");
				}
				List<Adaptation> selNodeRecords = AdaptationUtil.getLinkedRecordList(record, path);
				for (Adaptation selNodeRecord : selNodeRecords)
				{
					doCopy(
						selNodeRecord,
						dataSpaceToCopyTo,
						newRecordPK,
						selNodeConfig,
						dataModifier,
						deepCopyRule,
						pContext);
				}
			}
		}

		// Follow any specified associations and for all records linked,
		// duplicate them and set them to link back to this new record.
		// If the association uses a link table, handle that link table.
		Map<Path, DeepCopyConfig> associationConfigMap = config.getAssociationConfigMap();
		for (Path path : associationConfigMap.keySet())
		{
			DeepCopyConfig associationConfig = associationConfigMap.get(path);
			if (associationConfig != null)
			{
				Path associationFKPath = associationConfig.getPathToParentSelectionNodeOrAssociation();
				if (associationFKPath == null)
				{
					throw OperationException.createError("Config for copy of " + path.format()
						+ " doesn't specify the foreign key back to parent association.");
				}
				AdaptationTable linkTable = associationConfig.getAssociationLinkTable();
				// There is no link table so duplicate the record and point back to parent,
				// same as we do for selection nodes
				if (linkTable == null)
				{
					List<Adaptation> associationRecords = AdaptationUtil.getLinkedRecordList(
						record,
						path);
					for (Adaptation associationRecord : associationRecords)
					{
						doCopy(
							associationRecord,
							dataSpaceToCopyTo,
							newRecordPK,
							associationConfig,
							dataModifier,
							deepCopyRule,
							pContext);
					}
				}
				// Else find all records in the link table that point back to the parent and
				// do the copy on those
				else
				{
					RequestResult reqRes = linkTable.createRequestResult(associationFKPath.format()
						+ "='" + record.getOccurrencePrimaryKey().format() + "'");
					try
					{
						for (Adaptation linkRecord; (linkRecord = reqRes.nextAdaptation()) != null;)
						{
							doCopy(
								linkRecord,
								dataSpaceToCopyTo,
								newRecordPK,
								associationConfig,
								dataModifier,
								deepCopyRule,
								pContext);
						}
					}
					finally
					{
						reqRes.close();
					}
				}
			}
		}
		return newRecord;
	}
	private Adaptation executeInProcedureContext(
		ProcedureContext pContext,
		DeepCopyConfigFactory deepCopyConfigFactory,
		Adaptation record,
		AdaptationHome dataSpaceToCopyTo,
		DeepCopyDataModifier dataModifier,
		DeepCopyRule rule,
		String fkToParentSelectionNodeOrAssociationValue) throws OperationException
	{
		DeepCopyConfig config = deepCopyConfigFactory.createConfig(record.getContainerTable());

		Session session = pContext.getSession();
		boolean origAllPrivileges = pContext.isAllPrivileges();
		pContext.setAllPrivileges(true);
		// Need to set a session attribute to tell triggers not to fire.
		// Can't just turn off trigger activation because some aspects of
		// the triggers we do want to fire.
		// Trigger code needs to be implemented to check for this attribute.
		session.setAttribute(DEEP_COPY_SESSION_ATTRIBUTE, Boolean.TRUE);
		TriggerAction[] ignoreActions = (TriggerAction[]) session.getAttribute(BaseTableTrigger.IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE);
		session.setAttribute(
			BaseTableTrigger.IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE,
			new TriggerAction[] { TriggerAction.CREATE });
		Adaptation createdRecord = null;
		try
		{
			createdRecord = doCopy(
				record,
				dataSpaceToCopyTo,
				fkToParentSelectionNodeOrAssociationValue,
				config,
				dataModifier,
				rule,
				pContext);
		}
		finally
		{
			// Always set these back before exiting
			session.setAttribute(DEEP_COPY_SESSION_ATTRIBUTE, Boolean.FALSE);
			session.setAttribute(
				BaseTableTrigger.IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE,
				ignoreActions);
			pContext.setAllPrivileges(origAllPrivileges);
		}
		return createdRecord;
	}

	public DeepCopyConfigFactory getConfigFactory()
	{
		return this.configFactory;
	}

	public void setConfigFactory(DeepCopyConfigFactory configFactory)
	{
		this.configFactory = configFactory;
	}

	private class DeepCopyProcedure implements Procedure
	{
		private Adaptation record;
		private AdaptationHome dataSpaceToCopyTo;
		private DeepCopyDataModifier dataModifier;
		private DeepCopyRule rule;
		private String fkToParentSelectionNodeOrAssociationValue;
		private Adaptation createdRecord;

		public DeepCopyProcedure(
			Adaptation record,
			AdaptationHome dataSpaceToCopyTo,
			DeepCopyDataModifier dataModifier,
			DeepCopyRule rule,
			String fkToParentSelectionNodeOrAssociationValue)
		{
			this.record = record;
			this.dataSpaceToCopyTo = dataSpaceToCopyTo;
			this.dataModifier = dataModifier;
			this.rule = rule;
			this.fkToParentSelectionNodeOrAssociationValue = fkToParentSelectionNodeOrAssociationValue;
		}

		@Override
		public void execute(ProcedureContext pContext) throws Exception
		{
			createdRecord = executeInProcedureContext(
				pContext,
				configFactory,
				record,
				dataSpaceToCopyTo,
				dataModifier,
				rule,
				fkToParentSelectionNodeOrAssociationValue);
		}

		public Adaptation getCreatedRecord()
		{
			return this.createdRecord;
		}
	}
}
