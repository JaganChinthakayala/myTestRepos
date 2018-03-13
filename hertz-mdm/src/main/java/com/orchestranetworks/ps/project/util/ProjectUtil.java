/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.project.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.procedure.ProcedureExecutor;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.ps.project.path.SubjectPathConfig;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.ps.workflow.WorkflowUtilities;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Procedure;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.Role;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.service.UserReference;
import com.orchestranetworks.service.directory.DirectoryHandler;

/**
 */
public class ProjectUtil
{
	public static Adaptation lookupProjectRole(
		Adaptation projectRecord,
		Path projectRoleTablePath,
		Path[] projectPathsForProjectRolePK,
		Adaptation adminDataSet) throws OperationException
	{
		AdaptationTable projRoleTable = adminDataSet.getTable(projectRoleTablePath);

		Object[] pkValues = new Object[projectPathsForProjectRolePK.length];
		for (int i = 0; i < projectPathsForProjectRolePK.length; i++)
		{
			pkValues[i] = projectRecord.get(projectPathsForProjectRolePK[i]);
			if (pkValues[i] == null)
			{
				throw OperationException.createError(
					"Could not construct primary key for Project Role record for project "
						+ projectRecord.getOccurrencePrimaryKey().format());
			}
		}

		PrimaryKey projRolePK = projRoleTable.computePrimaryKey(pkValues);
		Adaptation projectRole = projRoleTable.lookupAdaptationByPrimaryKey(projRolePK);

		if (projectRole == null)
		{
			throw OperationException.createError(
				"Could not find Project Role record for project "
					+ projectRecord.getOccurrencePrimaryKey().format());
		}
		return projectRole;
	}

	public static String getCurrentProjectType(
		Adaptation subjectRecord,
		String masterDataSpaceName,
		SubjectPathConfig subjectPathConfig)
	{
		Path currProjTypePath = subjectPathConfig.getSubjectCurrentProjectTypeFieldPath();
		if (currProjTypePath == null)
		{
			return null;
		}
		AdaptationHome dataSpace = subjectRecord.getHome();
		if (masterDataSpaceName.equals(dataSpace.getKey().getName()))
		{
			return subjectRecord.getString(currProjTypePath);
		}
		AdaptationHome masterDataSpace = dataSpace.getParentBranch();
		Adaptation masterDataSet = masterDataSpace
			.findAdaptationOrNull(subjectRecord.getContainer().getAdaptationName());
		AdaptationTable masterTable = masterDataSet
			.getTable(subjectRecord.getContainerTable().getTablePath());
		Adaptation masterItemRecord = masterTable
			.lookupAdaptationByPrimaryKey(subjectRecord.getOccurrencePrimaryKey());
		if (masterItemRecord == null)
		{
			return subjectRecord.getString(currProjTypePath);
		}
		return masterItemRecord.getString(currProjTypePath);
	}

	public static void setCurrentProjectType(
		String projectType,
		Adaptation subjectRecord,
		ProcedureContext pContext,
		Session session,
		ProjectPathConfig projectPathConfig,
		SubjectPathConfig subjectPathConfig) throws OperationException
	{
		ArrayList<Adaptation> subjectRecords = new ArrayList<Adaptation>();
		subjectRecords.add(subjectRecord);
		setCurrentProjectType(
			projectType,
			subjectRecords,
			pContext,
			session,
			projectPathConfig,
			subjectPathConfig);
	}

	public static void setCurrentProjectType(
		final String projectType,
		List<Adaptation> subjectRecords,
		ProcedureContext pContext,
		Session session,
		ProjectPathConfig projectPathConfig,
		SubjectPathConfig subjectPathConfig) throws OperationException
	{
		final List<Adaptation> masterSubjectRecords = new ArrayList<Adaptation>();
		final Path currentProjectTypeFieldPath = subjectPathConfig
			.getSubjectCurrentProjectTypeFieldPath();
		if (currentProjectTypeFieldPath != null)
		{
			for (Adaptation subjectRecord : subjectRecords)
			{
				Map<Path, Object> pathValueMap = new HashMap<Path, Object>();
				pathValueMap.put(currentProjectTypeFieldPath, projectType);
				if (!ObjectUtils
					.equals(subjectRecord.getString(currentProjectTypeFieldPath), projectType))
				{
					// Set the current project type in this data space
					ModifyValuesProcedure
						.execute(pContext, subjectRecord, pathValueMap, true, false);
				}

				AdaptationHome parentDataSpace = subjectRecord.getHome().getParentBranch();
				// If we're in a child data space of the master, then set it in the master as well.
				// Store it so that later we can loop through setting all of them
				if (parentDataSpace != null && projectPathConfig.getMasterDataSpaceName()
					.equals(parentDataSpace.getKey().getName()))
				{
					Adaptation masterDataSet = parentDataSpace
						.findAdaptationOrNull(subjectRecord.getContainer().getAdaptationName());
					AdaptationTable masterTable = masterDataSet
						.getTable(subjectRecord.getContainerTable().getTablePath());
					Adaptation masterSubjectRecord = masterTable
						.lookupAdaptationByPrimaryKey(subjectRecord.getOccurrencePrimaryKey());
					if (masterSubjectRecord != null && !ObjectUtils.equals(
						masterSubjectRecord.getString(currentProjectTypeFieldPath),
						projectType))
					{
						masterSubjectRecords.add(masterSubjectRecord);
					}
				}
			}

			// Now do the actual write to the master
			if (!masterSubjectRecords.isEmpty())
			{
				Procedure proc = new Procedure()
				{
					@Override
					public void execute(ProcedureContext masterPContext) throws Exception
					{
						Map<Path, Object> pathValueMap = new HashMap<Path, Object>();
						pathValueMap.put(currentProjectTypeFieldPath, projectType);
						for (Adaptation masterSubjectRecord : masterSubjectRecords)
						{
							ModifyValuesProcedure.execute(
								masterPContext,
								masterSubjectRecord,
								pathValueMap,
								true,
								false);
						}
					}
				};
				Session sessionToUse = session == null ? pContext.getSession() : session;
				ProcedureExecutor.executeProcedure(
					proc,
					sessionToUse,
					masterSubjectRecords.iterator().next().getHome());
			}
		}
	}

	public static Adaptation getInProcessProjectForSubject(
		Adaptation subjectRecord,
		ProjectPathConfig projectPathConfig,
		SubjectPathConfig subjectPathConfig)
	{
		Path projectSubjectsFieldPath = subjectPathConfig.getSubjectProjectSubjectsFieldPath();
		if (projectSubjectsFieldPath == null)
		{
			List<Adaptation> projectRecords = AdaptationUtil.getLinkedRecordList(
				subjectRecord,
				subjectPathConfig.getSubjectProjectsFieldPath());
			for (Adaptation projectRecord : projectRecords)
			{
				String projectStatus = projectRecord
					.getString(projectPathConfig.getProjectStatusFieldPath());
				if (projectPathConfig.isInProcessProjectStatus(projectStatus))
				{
					return projectRecord;
				}
			}
		}
		else
		{
			List<Adaptation> projectSubjectRecords = AdaptationUtil
				.getLinkedRecordList(subjectRecord, projectSubjectsFieldPath);
			for (Adaptation projectSubjectRecord : projectSubjectRecords)
			{
				Adaptation projectRecord = AdaptationUtil.followFK(
					projectSubjectRecord,
					subjectPathConfig.getProjectSubjectProjectFieldPath());
				// Shouldn't normally happen but it can be null if a project got deleted without deleting the project subject
				if (projectRecord != null)
				{
					String projectStatus = projectRecord
						.getString(projectPathConfig.getProjectStatusFieldPath());
					if (projectPathConfig.isInProcessProjectStatus(projectStatus))
					{
						return projectRecord;
					}
				}
			}
		}
		return null;
	}

	public static Adaptation getInProcessProjectSubjectForSubject(
		Adaptation subjectRecord,
		ProjectPathConfig projectPathConfig,
		SubjectPathConfig subjectPathConfig)
	{
		Adaptation inProcessProject = getInProcessProjectForSubject(
			subjectRecord,
			projectPathConfig,
			subjectPathConfig);
		if (inProcessProject == null)
		{
			return null;
		}
		Path projectSubjectTablePath = subjectPathConfig.getProjectSubjectTablePath();
		if (projectSubjectTablePath == null)
		{
			return null;
		}

		AdaptationTable projectSubjectTable = subjectRecord.getContainer()
			.getTable(projectSubjectTablePath);
		PrimaryKey projectSubjectRecordPK = projectSubjectTable.computePrimaryKey(
			new Object[] { inProcessProject.getOccurrencePrimaryKey().format(),
					subjectRecord.getOccurrencePrimaryKey().format() });
		return projectSubjectTable.lookupAdaptationByPrimaryKey(projectSubjectRecordPK);
	}

	// Get the User from the Project Team
	public static UserReference getProjectTeamUserForRole(
		Adaptation projectRecord,
		Role userRole,
		Session session,
		Repository repo,
		ProjectPathConfig projectPathConfig) throws OperationException
	{
		Adaptation teamMember = getProjectTeamMemberRecord(
			projectRecord,
			userRole,
			projectPathConfig);
		if (teamMember != null)
		{
			UserReference userReference = WorkflowUtilities.getUserReference(
				teamMember.getString(projectPathConfig.getProjectTeamMemberUserFieldPath()),
				repo);
			if (userReference != null
				&& DirectoryHandler.getInstance(repo).isUserInRole(userReference, userRole))
			{
				return userReference;
			}
		}
		return null;
	}

	// Get the record for the project team member with the given role
	public static Adaptation getProjectTeamMemberRecord(
		Adaptation projectRecord,
		Role userRole,
		ProjectPathConfig projectPathConfig)
	{
		AdaptationTable teamMemberTable = projectRecord.getContainer()
			.getTable(projectPathConfig.getProjectTeamMemberTablePath());
		PrimaryKey teamMemberPK = teamMemberTable.computePrimaryKey(
			new Object[] { projectRecord.getOccurrencePrimaryKey().format(),
					userRole.getRoleName() });
		return teamMemberTable.lookupAdaptationByPrimaryKey(teamMemberPK);
	}

	protected ProjectUtil()
	{
	}
}
