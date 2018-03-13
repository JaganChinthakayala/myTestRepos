package com.hertz.mdm._hertz.scripttask;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.ps.workflow.WorkflowConstants;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class ConstructAdaptationListFromContextScriptTask extends ScriptTask
{

	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		// Get the context variable and construct the Adaptation List
		String dataSpaceId = context.getVariableString(WorkflowConstants.PARAM_MASTER_DATA_SPACE);
		String dataSetId = context.getVariableString(WorkflowConstants.PARAM_DATA_SET);
		String deltaResults = context
			.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PARM_DELTA_RESULT);
		Repository repository = context.getRepository();
		AdaptationHome dataSpace = repository.lookupHome(HomeKey.forBranchName(dataSpaceId));
		if (dataSpace == null)
		{
			throw OperationException
				.createError("DataSpace " + dataSpaceId + " has not been found");
		}
		Adaptation dataSet = dataSpace.findAdaptationOrNull(AdaptationName.forName(dataSetId));
		if (dataSet == null)
		{
			throw OperationException.createError("DataSet " + dataSetId + " has not been found");
		}
		//	T[/root/ContentContainer]:U{5}T[/root/ContentContainer]:U{6}T[/root/ContentContainer]:C{21}T[/root/ContentContainer]:U{18}T[/root/ContentContainer]:U{9}T[/root/ContentContainer]:U{21}T[/root/ContentItem]:U{3}T[/root/ContentItem]:C{28}T[/root/ContentItem]:U{3}T[/root/ContentItem]:U{11}T[/root/ContentItem]:U{28}
		//	u5,u6,c21,u18,u9,u21  u3,c28,u3,u11,u28
		Map<AdaptationTable, Set<Adaptation>> createDeltaAdaptationMap = new LinkedHashMap<AdaptationTable, Set<Adaptation>>();
		Map<AdaptationTable, Set<Adaptation>> updateDeltaAdaptationMap = new LinkedHashMap<AdaptationTable, Set<Adaptation>>();
		//	Map with vmId and endeavorType
		Map<String, String> deleteDeltaAdaptationMap = new LinkedHashMap<String, String>();
		Set<Adaptation> createAdaptationSet = null;
		Set<Adaptation> updateAdaptationSet = null;
		if (deltaResults != null)
		{
			String[] tableArray = com.onwbp.base.misc.StringUtils
				.splitByStringSeparator(deltaResults, HtzConstants.TABLE_ADAPTATION_STARTS_WITH);
			if (tableArray.length > 0)
			{
				for (int i = 1; i < tableArray.length; i++)
				{
					String eachTableValue = tableArray[i];
					String[] eachTableArray = eachTableValue
						.split(HtzConstants.TABLE_ADAPTATION_SEPARATOR);
					String tableXPath = StringUtils.substringBetween(eachTableArray[0], "[", "]");
					if (eachTableArray[1] != null)
					{
						String adaptationPk = StringUtils
							.substringBetween(eachTableArray[1], "{", "}");
						AdaptationTable deltaTable = dataSet.getTable(Path.parse(tableXPath));
						Adaptation record = deltaTable
							.lookupAdaptationByPrimaryKey(PrimaryKey.parseString(adaptationPk));
						if (record != null)
						{
							if (eachTableArray[1]
								.startsWith(HtzConstants.CREATE_OCCURRENCES_SEPARATOR))
							{
								if (createDeltaAdaptationMap.containsKey(deltaTable))
								{
									createAdaptationSet = createDeltaAdaptationMap.get(deltaTable);
									createAdaptationSet.add(record);
									createDeltaAdaptationMap.put(deltaTable, createAdaptationSet);

								}
								else
								{
									createAdaptationSet = new LinkedHashSet<Adaptation>();
									createAdaptationSet.add(record);
									createDeltaAdaptationMap.put(deltaTable, createAdaptationSet);
								}
							}
							else if (eachTableArray[1]
								.startsWith(HtzConstants.UPDATE_OCCURRENCES_SEPARATOR))
							{
								if (createDeltaAdaptationMap != null
									&& createDeltaAdaptationMap.get(deltaTable) != null
									&& createDeltaAdaptationMap.get(deltaTable).contains(record))
								{
									continue;
								}
								if (updateDeltaAdaptationMap.containsKey(deltaTable))
								{
									updateAdaptationSet = updateDeltaAdaptationMap.get(deltaTable);
									updateAdaptationSet.add(record);
									updateDeltaAdaptationMap.put(deltaTable, updateAdaptationSet);

								}
								else
								{
									updateAdaptationSet = new LinkedHashSet<Adaptation>();
									updateAdaptationSet.add(record);
									updateDeltaAdaptationMap.put(deltaTable, updateAdaptationSet);
								}
							}
						}
					}
				}
				// Call the Publish utility to construct JSON objects and publish objects to RabbitMQ
				PublishUtil.publishToMQ(
					context.getSession(),
					dataSpace,
					dataSet,
					createDeltaAdaptationMap,
					updateDeltaAdaptationMap,
					deleteDeltaAdaptationMap);
			}
		}
	}
}
