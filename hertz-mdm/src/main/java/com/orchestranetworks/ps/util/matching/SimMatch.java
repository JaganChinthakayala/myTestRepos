package com.orchestranetworks.ps.util.matching;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.addon.daqa.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 * <p><b>SimMatch</b> <i>Simulate Record Match</i> takes a list of <code>Adaptation</code> records and executes the
 * DAQA matching add-on to identify candidate matches according to the matching configuration defined
 * for the records table.</p>
 * <p><b>Usage:</b>  Create an instance of this class, use the <code>simMatch.addPivotRecord(Adaptation)</code> to
 * add one or more record adaptations to the records to match. Next create a <code>ProcedureContext</code> pctx then
 * use <code>pctx.execute(simMatch)</code> to execute the matching process. Finally use the <code>simMatch.getMatchCandidtes</code> to
 * return a list of <code>SimMatchCandidates</code> that will contain the list of matched records.</p>
 * <p><b>Limitation:</b> although pivot records provided for a single match do not need to be from the same table
 * they MUST all exist in the same Data space.</p>
 *
 * @author Craig Cox - Orchestra Networks - March 2015
 *
 */
public class SimMatch implements Procedure
{

	// All tables configured for matching must have a field group called "DaqaMetaData" in the
	//root of the record that reuses the DAQA meta data structure.
	//These path constants provide access to those necessary to use this class.
	public static final Path MATCHING_STATE = Path.parse("DaqaMetaData/State");
	public static final Path MATCHING_CLUSTERID = Path.parse("DaqaMetaData/ClusterId");
	public static final Path MATCHING_SCORE = Path.parse("DaqaMetaData/Score");
	public static final Path MATCHING_SIMPLE_SCORE = Path.parse("DaqaMetaData/simpleMatchingScore");
	public static final Path MATCHING_TIMESTAMP = Path.parse("DaqaMetaData/Timestamp");

	private List<Adaptation> pivotRecords = new ArrayList<Adaptation>();
	private List<SimMatchCandidate> candidates = new ArrayList<SimMatchCandidate>();

	public SimMatch()
	{

	}
	/**
	 * <p><b>execute</b> will parse through each of the provided pivot records and find any matches as defined in the
	 * DAQA matching add on rules and populate the list of match candidates.</p>
	 * <p><b>Usage:</b> Create a <code>ProcedureContext</code> pctx then use <code>pctx.execute(simMatch)</code>
	 * to execute the matching process.
	 * @param pctx a <code>ProcedureContext</code>
	 */
	@Override
	public void execute(ProcedureContext pctx) throws Exception
	{

		boolean recordWasGolden = false;
		// Create a matching operations object
		final MatchingOperations matchOps = MatchingOperationsFactory.getMatchingOperations();
		// For each provided pivot record execute the match
		for (Adaptation PIVOT_RECORD : pivotRecords)
		{
			//If pivot record is golden make it "unMatched"
			String recordState = PIVOT_RECORD.getString(MATCHING_STATE);

			if (recordState.equals(MatchingState.GOLDEN.getValue()))
			{
				setRecordUnMatched(PIVOT_RECORD, pctx);
				recordWasGolden = true;
			}
			else
			{
				recordWasGolden = false;
			}
			// convert the pivot record adaptation into a matchable record context.
			final RecordContext recordCtx = new RecordContext(PIVOT_RECORD, pctx);
			pctx.setAllPrivileges(true);
			//Perform the simulate match using the pivot record, see matching configuration of the table containing the record.
			List<SearchResult> matchResults = matchOps.simulateMatchTable(recordCtx);
			pctx.setAllPrivileges(false);
			//Populate the CANDIDATES array.
			for (SearchResult matchResult : matchResults)
			{
				SimMatchCandidate candidate = new SimMatchCandidate(
					matchResult.getPrimaryKey(),
					matchResult.getScore(),
					PIVOT_RECORD.getAdaptationName());
				candidates.add(candidate);
				resetDaqaMetaData(candidate, pctx, PIVOT_RECORD);
			}
			if (recordWasGolden)
			{
				setRecordGolden(PIVOT_RECORD, pctx);
			}
		}
	}
	private void resetDaqaMetaData(
		SimMatchCandidate candidate,
		ProcedureContext pctx,
		Adaptation pivotRecord)
	{
		AdaptationTable matchTable = pivotRecord.getContainerTable();
		Adaptation candiateRecord = matchTable.lookupAdaptationByPrimaryKey(candidate.getPrimaryKey());
		// Reset the Matching simple score to null, as per default record value.
		HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();
		pathValueMap.put(MATCHING_SIMPLE_SCORE, null);
		try
		{
			ModifyValuesProcedure.execute(pctx, candiateRecord, pathValueMap, true, false);
		}
		catch (ConstraintViolationException e)
		{
			// Cannot update candidate record.
			e.printStackTrace();
		}
		catch (OperationException e)
		{
			// Cannot update candidate record.
			e.printStackTrace();
		}
	}

	/**
	 * <p><b>getCandidates</b> will return an empty list or a list populated with <code>SimMatchCandidates</code>,
	 * one for each match. If the match is executed with more than one pivot record, the results list will
	 * identify which pivot record the match belongs to.</p>
	 * @return <code>List <i>SimMatchCandidate</i></code>
	 */
	public List<SimMatchCandidate> getCandidates()
	{
		return candidates;
	}

	/**
	 * <p><b>addPivotRecord</b> adds a single adaptation to the collection of pivot records to be used in this match.</p>
	 * @param pivotRecord a record <code>Adaptation</code> that exists in a table configured for matching in the DAQA
	 * matching add-on.
	 */
	public void addPivotRecord(Adaptation pivotRecord)
	{
		this.pivotRecords.add(pivotRecord);
	}

	/**
	 * <p><b>clear</b> deletes the values from the list of pivot records and the list of candidate matches.</p>
	 */
	public void clear()
	{
		pivotRecords.clear();
		candidates.clear();
	}

	/**
	 * p><b>getPivotRecordSize</b> returns the number of pivot records currently define for the match.</p>
	 * @return size of pivot records
	 */
	public int getPivotRecordSize()
	{
		return pivotRecords.size();
	}

	public List<Adaptation> getPivotRecords()
	{
		return pivotRecords;
	}

	/**
	 * p><b>getCandidatesSize</b> returns the number of match candidates currently returned from the match.</p>
	 * @return size of candidates
	 */
	public int getCandidatesSize()
	{
		return candidates.size();
	}

	public void addPivotRecord(List<Adaptation> pivotRecordsList)
	{
		this.pivotRecords.addAll(pivotRecordsList);
	}

	private void setRecordUnMatched(Adaptation record, ProcedureContext pctx)
		throws OperationException
	{
		MatchingOperations matchOps = MatchingOperationsFactory.getMatchingOperations();
		final RecordContext recordCtx = new RecordContext(record, pctx);
		// Set record to unmatched and reset the simpleMatchScore.
		pctx.setAllPrivileges(true);
		matchOps.setAtOnceUnmatched(recordCtx);
		pctx.setAllPrivileges(false);
	}

	public static void setRecordGolden(Adaptation record, ProcedureContext pctx)
		throws OperationException
	{
		MatchingOperations matchOps = MatchingOperationsFactory.getMatchingOperations();
		final RecordContext recordCtx = new RecordContext(record, pctx);
		pctx.setAllPrivileges(true);
		matchOps.setGolden(recordCtx);
		pctx.setAllPrivileges(false);
	}

}
