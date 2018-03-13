/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.multiselect;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.ui.*;

/**
 */
@SuppressWarnings("serial")
public class MultiSelectRecordCreationService extends HttpServlet
{
	private TrackingInfoHelper trackingInfoHelper;

	public MultiSelectRecordCreationService()
	{
		this(null);
	}

	public MultiSelectRecordCreationService(TrackingInfoHelper baseTrackingInfoHelper)
	{
		this.trackingInfoHelper = MultiSelectUtil.createTrackingInfoHelper(baseTrackingInfoHelper);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		ServiceContext sContext = ServiceContext.getServiceContext(request);

		UIServiceComponentWriter writer = sContext.getUIComponentWriter();

		Session session = sContext.getSession();
		String trackingInfo = session.getTrackingInfo();
		trackingInfoHelper.initTrackingInfo(trackingInfo);
		final String parentPK = trackingInfoHelper.getTrackingInfoSegment(MultiSelectUtil.SEGMENT_PARENT_RECORD_PK);
		final int joinParentPKPos = Integer.valueOf(
			trackingInfoHelper.getTrackingInfoSegment(MultiSelectUtil.SEGMENT_JOIN_TABLE_PK_POSITION))
			.intValue();
		AdaptationHome joinDataSpace = sContext.getCurrentHome()
			.getRepository()
			.lookupHome(
				HomeKey.forBranchName(trackingInfoHelper.getTrackingInfoSegment(MultiSelectUtil.SEGMENT_JOIN_TABLE_DATA_SPACE)));
		Adaptation joinDataSet = joinDataSpace.findAdaptationOrNull(AdaptationName.forName(trackingInfoHelper.getTrackingInfoSegment(MultiSelectUtil.SEGMENT_JOIN_TABLE_DATA_SET)));
		final AdaptationTable joinTable = joinDataSet.getTable(Path.parse(trackingInfoHelper.getTrackingInfoSegment(MultiSelectUtil.SEGMENT_JOIN_TABLE_PATH)));
		final Path joinFKPath = Path.parse(trackingInfoHelper.getTrackingInfoSegment(MultiSelectUtil.SEGMENT_JOIN_TABLE_FK_PATH));

		final List<Adaptation> selectedRecords;
		Adaptation currAdaptation = sContext.getCurrentAdaptation();
		if (currAdaptation.isTableOccurrence())
		{
			selectedRecords = new ArrayList<Adaptation>();
			selectedRecords.add(currAdaptation);
		}
		else
		{
			selectedRecords = sContext.getSelectedOccurrences();
		}

		Procedure createRecordsProc = new Procedure()
		{
			@Override
			public void execute(ProcedureContext pContext) throws Exception
			{
				for (Adaptation selectedRecord : selectedRecords)
				{
					PrimaryKey selectedRecordPK = selectedRecord.getOccurrencePrimaryKey();
					HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();
					Path[] pkPaths = joinTable.getPrimaryKeySpec();
					pathValueMap.put(pkPaths[joinParentPKPos], parentPK);
					pathValueMap.put(joinFKPath, selectedRecordPK.format());
					CreateRecordProcedure.execute(pContext, joinTable, pathValueMap, true, false);
				}
			}
		};
		try
		{
			ProcedureExecutor.executeProcedure(createRecordsProc, session, joinDataSpace);
		}
		catch (OperationException ex)
		{
			throw new ServletException(ex);
		}

		String joinTableLabel = joinTable.getTableNode().getLabel(session.getLocale());
		int size = selectedRecords.size();
		StringBuilder bldr = new StringBuilder("alert('");
		bldr.append(size);
		bldr.append(" ");
		if (joinTableLabel != null)
		{
			bldr.append(joinTableLabel);
			bldr.append(" ");
		}
		if (size == 1)
		{
			bldr.append("record was");
		}
		else
		{
			bldr.append("records were");
		}
		bldr.append(" created.');");
		writer.addJS_cr(bldr.toString());

		UIHttpManagerComponent uiMgr = sContext.createWebComponentForSubSession();
		SchemaNode currTableNode = sContext.getCurrentNode();
		Adaptation currDataSet = currAdaptation;
		if (currAdaptation.isTableOccurrence())
		{
			currDataSet = currAdaptation.getContainer();
			currTableNode = currAdaptation.getContainerTable().getTableNode();
		}
		// Shouldn't have to create a sub-session each time, but if I don't the view doesn't
		// get updated from the access rule immediately (updates when you page through
		// table for example).
		uiMgr.selectNode(currDataSet, currTableNode.getPathInSchema());
		uiMgr.setTrackingInfo(session.getTrackingInfo());
		String url = uiMgr.getURIWithParameters();
		writer.addJS_cr("window.location.href='" + url + "';");
	}
}
