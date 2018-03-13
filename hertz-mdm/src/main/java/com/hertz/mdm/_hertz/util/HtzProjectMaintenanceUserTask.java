/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm._hertz.util;

import com.hertz.mdm.admin.path.AdminPaths;
import com.hertz.mdm.common.util.HtzCommonUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.ps.project.usertask.ProjectMaintenanceUserTask;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.Role;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.service.UserReference;
import com.orchestranetworks.workflow.DataContext;
import com.orchestranetworks.workflow.ProcessExecutionContext;

public abstract class HtzProjectMaintenanceUserTask extends ProjectMaintenanceUserTask
{
	protected boolean primaryUserTableUsed;

	@Override
	public ProjectPathConfig getProjectPathConfig()
	{
		return null;
	}

	protected abstract String getDomain();

	/**
	 * Return the path to the country field, used by the primary user lookup table.
	 * If {@ link #isPrimaryUserTableUsed()} returns <code>false</code>, then this is not checked and can return <code>null</code>.
	 * Default is <code>null</code>.
	 *
	 * @return the path to the country field, or <code>null</code> if no primary user table is used
	 */
	protected Path getCountryFieldPath()
	{
		return null;
	}

	@Override
	protected AdaptationTable getLookupTable(DataContext context, Repository repo)
	{
		Adaptation dataSet = HtzCommonUtilities.getAdminDataSet(repo);
		return dataSet.getTable(AdminPaths._Root_WorkflowRole.getPathInSchema());
	}

	@Override
	protected PrimaryKey[] getLookupTablePrimaryKeys(
		AdaptationTable lookupTable,
		DataContext context)
	{
		String[] tasks = getTasksForMultiWorkflowTask();
		PrimaryKey[] pks = new PrimaryKey[tasks.length];
		for (int i = 0; i < tasks.length; i++)
		{
			pks[i] = lookupTable
				.computePrimaryKey(new Object[] { getDomain() + PrimaryKey.SEPARATOR + tasks[i] });
		}
		return pks;
	}

	@Override
	protected Path getLookupTableRoleFieldPath(AdaptationTable lookupTable)
	{
		return AdminPaths._Root_WorkflowRole._Role;
	}

	@Override
	protected Role createRoleFromLookupTableRecord(Adaptation record, Path rolePath)
	{
		Role role = super.createRoleFromLookupTableRecord(record, rolePath);
		return role;
	}

	@Override
	protected String getFieldLabel(
		Adaptation adaptation,
		Path fieldPath,
		Session session,
		boolean includeGroupLabels)
	{
		String label = super.getFieldLabel(adaptation, fieldPath, session, includeGroupLabels);
		if (label == null)
		{
			return null;
		}
		if (includeGroupLabels)
		{
			SchemaNode fieldNode = adaptation.getSchemaNode().getNode(fieldPath);
			String fieldNodeLabel = fieldNode.getLabel(session.getLocale());
			if (fieldNodeLabel == null)
			{
				return null;
			}
			if (fieldNodeLabel.startsWith("*"))
			{
				if (label.length() == fieldNodeLabel.length())
				{
					return label.substring(1);
				}
				return label.substring(0, label.lastIndexOf(fieldNodeLabel))
					+ fieldNodeLabel.substring(1);
			}
			return label;
		}
		return label.startsWith("*") ? label.substring(1) : label;
	}

	@Override
	protected String getRecordLabel(Adaptation record, Session session)
	{
		ProjectPathConfig pathConfig = getProjectPathConfig();
		if (pathConfig.getProjectTablePath().equals(record.getContainerTable().getTablePath()))
		{
			return record.getOccurrencePrimaryKey().format() + " - "
				+ record.getString(pathConfig.getProjectNameFieldPath());
		}
		return super.getRecordLabel(record, session);
	}

	@Override
	protected UserReference getProjectTeamUserForRole(
		Adaptation projectRecord,
		ProcessExecutionContext context,
		Role userRole) throws OperationException
	{
		return null;
	}

	@Override
	protected Adaptation getProjectTeamMemberRecord(Adaptation projectRecord, Role userRole)
	{
		return super.getProjectTeamMemberRecord(projectRecord, userRole);
	}

	public boolean isPrimaryUserTableUsed()
	{
		return this.primaryUserTableUsed;
	}

	public void setPrimaryUserTableUsed(boolean primaryUserTableUsed)
	{
		this.primaryUserTableUsed = primaryUserTableUsed;
	}

	@Override
	protected void updateProjectTeamUserForRole(
		Adaptation projectRecord,
		Role role,
		UserReference user,
		ProcedureContext pContext,
		boolean overwrite) throws OperationException
	{
		super.updateProjectTeamUserForRole(projectRecord, role, user, pContext, true);
	}
}
