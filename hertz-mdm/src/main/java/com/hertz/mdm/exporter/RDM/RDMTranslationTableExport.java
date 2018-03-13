package com.hertz.mdm.exporter.RDM;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.Request;
import com.onwbp.adaptation.RequestResult;
import com.hertz.mdm.crossreference.path.CrossReferencePaths;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;


/**
 * An exporter for the PMF Function table that outputs all leaves of the table along with a flattened
 * representation of their complete path to the root. There is a specific format this conforms to. 
 * Refer to a sample output file.
 */
public class RDMTranslationTableExport {

	protected static final LoggingCategory LOG = LoggingCategory.getKernel();
	
	private static final Path sourceTablePath=Path.parse("/root/Cross_Reference_Data/SOURCE_LOV_ANCILLARY_PRODUCTS");
	private static final Path targetTablePath=Path.parse("/root/Cross_Reference_Data/TARGET_LOV_ANCILLARY_PRODUCTS");
	private static final Path rdmTablePath=Path.parse("/root/Cross_Reference_Data/RDM_LOV_ANCILLARY_PRODUCTS");	
	private static final Path regTablePath=Path.parse("/root/Cross_Reference_Data/RDM_LOV_SYSTEM_REG");
	private static final Path translationTablePath=Path.parse("/root/Cross_Reference_Data/RDM_ANCILLARY_TRANSLATION");
	
	protected ArrayList<Adaptation> allRDMTranslationRecord;

	// Global variables for Header
	int mSrcInitCount = 0;
	int mRdmInitCount = 0;
	int mTgtInitCount = 0;
	int mSrcMaxCount = 0;
	int mRdmMaxCount = 0;
	int mTgtMaxCount = 0;
	String mHeaderStr = "";
	
	// ArrayList to store table records
	List<String> mRecordsArray = new ArrayList<String>();
	
	/**
	 * Export the table records and write to a flat file 
	 * @param session
	 * @param table
	 * @throws IOException
	 * @throws OperationException
	 */
	public void exportTable( Session session, AdaptationTable table ) throws IOException, OperationException {
		
		final String FILENAME = "C:\\Users\\sjayaraj\\Desktop\\Location\\export\\rdm_translation.csv";
		AdaptationTable translationTable = table.getContainerAdaptation().getTable( translationTablePath );
		
		// Create translation records map
		translationAllRecordsMap( translationTable );
		
		BufferedWriter writer = null;
		FileWriter filewriter = null;
		filewriter = new FileWriter( FILENAME );
		writer = new BufferedWriter( filewriter );

		// Create header line and fetch max no. of rows in each table
		mHeaderStr = createInitialHeaderLine();
		fetchMaxCounters( table );
		
		// Create rows
		Iterator<Adaptation> locationIterator = allRDMTranslationRecord.iterator();
		
		try{
			
			while( locationIterator.hasNext() ){
				
				String rowStr = "";
				// Create one single record row and add it to ArrayList
				rowStr = createRow( locationIterator.next(), table );
				mRecordsArray.add( rowStr );
			}
		} 
		catch( Exception e ){
			
			LOG.info( e.getMessage() );
		}

		// Update HeaderLine and write to flat file
		mHeaderStr = updateHeaderLine( mSrcMaxCount, mRdmMaxCount, mTgtMaxCount );
		writer.write( mHeaderStr );
		writer.newLine();

		// Write the table records to flat file
		Iterator<String> itr = mRecordsArray.iterator();
		
		try{
			while( itr.hasNext() ){
				
				writer.write( itr.next().toString() );
				writer.newLine();
			}
		}
		catch( Exception e ){
			
			LOG.info( e.getMessage() );
		}finally {
			writer.close();		
		}
	}
	

	/**
	 * Creates translation records map
	 * 
	 * @param table
	 */
	private void translationAllRecordsMap( final AdaptationTable table ) {
		
		//allRecordsLocationMap = new HashMap<PrimaryKey, Adaptation>();
		// If you specify no predicate, it will request every record in the table
		// (that meets any filter you set)
		
		allRDMTranslationRecord = new ArrayList<Adaptation>();
		final Request request = table.createRequest();
		final RequestResult reqRes = request.execute();
		
		try{
			// Loop through all the results and put each into the map
			for( Adaptation adaptation; (adaptation = reqRes.nextAdaptation()) != null; ){
				
				//allRecordsLocationMap.put(adaptation.getOccurrencePrimaryKey(), adaptation);
				allRDMTranslationRecord.add( adaptation );
			}
		} finally {
			reqRes.close();
		}
		
	}
	
	
	/**
	 * Creates a record by appending source, rdm and target record
	 * 
	 * @param translationrecord
	 * @param table
	 * @return
	 * @throws OperationException
	 */
	protected String createRow( Adaptation translationrecord, AdaptationTable table) throws OperationException {
		
		final StringBuffer strBuff = new StringBuffer();
	
		int translationID = translationrecord.get_int( CrossReferencePaths._Root_Cross_Reference_Data_RDM_ANCILLARY_TRANSLATION._Root_Cross_Reference_Data_RDM_ANCILLARY_TRANSLATION_ROW_ID );
		
		// Create Source record
		String sourceLinkedRecord = createSourceRecord( translationID, table );
		
		// Create RDM record
		String rdmLinkedRecord = createRDMRecord( translationID, table );
		
		// Create Target record		
		String targetLinkedRecord = createTargetRecord( translationID, table );
		
		strBuff.append( sourceLinkedRecord );
		strBuff.append("|");
		strBuff.append( rdmLinkedRecord );
		strBuff.append("|");
		strBuff.append( targetLinkedRecord );
		
		System.out.println("Record Data = " + strBuff.toString());
		return strBuff.toString();
	}
	
	
	/**
	 * Creates Source record from TranslationID
	 * 
	 * @param TranslationID
	 * @param table
	 * @return
	 */
	private String createSourceRecord( int translationID, AdaptationTable table ){
		
		final StringBuffer strBuff = new StringBuffer();
		RequestResult result = getSourceRequestResult( translationID, table );
		int srcSize = result.getSize();
		
		// Update source max counter
		if( result != null && result.getSize() > 0 ){
		
			updateSourceMaxCounter( result.getSize() );
		}
		
		for( Adaptation record; (record = result.nextAdaptation()) != null; ){
		
			String lovid = record.getString(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_LOV_EXT_SYST_ID) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_LOV_CODE) )  
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_LOV_VALUE) )  
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_LOV_TYPE) )  
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_LOV_DESCRIPTION) )  
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_LOV_STATUS) )  
			+ "|" + formatDate( record.getDate(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_LOV_EFFECTIVE_DT) )  
			+ "|" + formatDate( record.getDate(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_LOV_END_DT) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_CREATED_BY) )  
			+ "|" + formatDate( record.getDate(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_CREATED_DT) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_LAST_UPD_BY) )  
			+ "|" + formatDate( record.getDate(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_LAST_UPD_DT) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_DB_LAST_UPD_SRC) );
			
			strBuff.append( lovid );	
		}
		
		// Get source max counter
		if( srcSize < mSrcMaxCount ){
			 
			int diff = (mSrcMaxCount - srcSize);
			
			for( int i=0; i<diff; i++ ){
				strBuff.append( getSourcePipe() );
			}
		}
		
		return strBuff.toString();
	}
	
	
	private RequestResult getSourceRequestResult( int translationID, AdaptationTable table ){

		final String predicateSource = CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_TRANSLATION_ID.format() + "= '" + translationID + "'";
		AdaptationTable sourceTable = table.getContainerAdaptation().getTable( sourceTablePath );
		RequestResult result = sourceTable.createRequestResult( predicateSource );
		
		return result;
	}


	private void updateSourceMaxCounter( int size ){
		
		if( mSrcMaxCount < size ){
			
			mSrcMaxCount = size;
		}
	}


	/**
	 * Creates RDM record from TranslationID
	 * 
	 * @param TranslationID
	 * @param table
	 * @return
	 */
	private String createRDMRecord( int translationID, AdaptationTable table ){
	
		final StringBuffer strBuff = new StringBuffer();
		RequestResult result = getRDMRequestResult( translationID, table ); 
		int rdmSize = result.getSize();
		
		// Update RDM max counter
		if( result != null && result.getSize() > 0 ){
		
			updateRDMMaxCounter( result.getSize() );
		}

		for( Adaptation record; (record = result.nextAdaptation()) != null; ){
			
			String lovid = record.getString(CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS_LOV_CODE) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS_LOV_VALUE) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS_LOV_TYPE) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS_LOV_DESCRIPTION) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS_LOV_STATUS) ) 
			+ "|" + formatDate( record.getDate(CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS_LOV_EFFECTIVE_DT) ) 
			+ "|" + formatDate( record.getDate(CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS_LOV_END_DT) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS_CREATED_BY) ) 
			+ "|" + formatDate( record.getDate(CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS_CREATED_DT) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS_LAST_UPD_BY) ) 
			+ "|" + formatDate( record.getDate(CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS_LAST_UPD_DT) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_RDM_LOV_ANCILLARY_PRODUCTS_DB_LAST_UPD_SRC) );
			
			strBuff.append( lovid );
		}
	
		// Get rdm max counter
		if( rdmSize < mRdmMaxCount ){
			 
			int diff = (mRdmMaxCount - rdmSize);
			
			for( int i=0; i<diff; i++ ){
				strBuff.append( getRDMPipe() );
			}
		}
		
		return strBuff.toString();
	}


	private RequestResult getRDMRequestResult( int translationID, AdaptationTable table ){
		
		final String predicateRDM = CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_TRANSLATION_ID.format() + "= '" + translationID + "'";
		AdaptationTable rdmTable = table.getContainerAdaptation().getTable( rdmTablePath );
		RequestResult result = rdmTable.createRequestResult( predicateRDM );
		
		return result;
	}


	private void updateRDMMaxCounter( int size ){
		
		if( mRdmMaxCount < size ){
			
			mRdmMaxCount = size;
		}
	}

	
	/**
	 * Creates Target record from TranslationID
	 * 
	 * @param TranslationID
	 * @param table
	 * @return
	 */
	private String createTargetRecord( int translationID, AdaptationTable table ){
	
		final StringBuffer strBuff = new StringBuffer();
		RequestResult result = getTargetRequestResult( translationID, table ); 
		int tgtSize = result.getSize();
		
		// Update target max counter
		if( result != null && result.getSize() > 0 ){
		
			updateTargetMaxCounter( result.getSize() );
		}

		for( Adaptation record; (record = result.nextAdaptation()) != null; ){	
		
			String lovid = record.getString(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_LOV_EXT_SYST_ID) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_LOV_CODE) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_LOV_VALUE) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_LOV_TYPE) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_LOV_DESCRIPTION) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_LOV_STATUS) ) 
			+ "|" + formatDate( record.getDate(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_LOV_EFFECTIVE_DT) ) 
			+ "|" + formatDate( record.getDate(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_LOV_END_DT) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_CREATED_BY) ) 
			+ "|" + formatDate( record.getDate(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_CREATED_DT) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_LAST_UPD_BY) ) 
			+ "|" + formatDate( record.getDate(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_LAST_UPD_DT) ) 
			+ "|" + formatString( record.getString(CrossReferencePaths._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_TARGET_LOV_ANCILLARY_PRODUCTS_DB_LAST_UPD_SRC) );
			
			strBuff.append( lovid );
		}
		
		// Get tgt max counter
		if( tgtSize < mTgtMaxCount ){
			 
			int diff = (mTgtMaxCount - tgtSize);
			
			for( int i=0; i<diff; i++ ){
				strBuff.append( getTargetPipe() );
			}
		}
		
		return strBuff.toString();	
	}

	
	private RequestResult getTargetRequestResult( int translationID, AdaptationTable table ){

		final String predicateTarget = CrossReferencePaths._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS._Root_Cross_Reference_Data_SOURCE_LOV_ANCILLARY_PRODUCTS_TRANSLATION_ID.format() + "= '" + translationID + "'";
		AdaptationTable targetTable = table.getContainerAdaptation().getTable( targetTablePath );
		RequestResult result = targetTable.createRequestResult( predicateTarget );
		
		return result;
	}


	private void updateTargetMaxCounter( int size ){
		
		if( mTgtMaxCount < size ){
			
			mTgtMaxCount = size;
		}
	}

	
	/**
	 * Creates a record by appending source, rdm and target record
	 * 
	 * @param translationrecord
	 * @param table
	 * @return
	 * @throws OperationException
	 */
	protected String createHeader( Adaptation translationrecord, AdaptationTable table) throws OperationException {
		
		int srcCount = 0;
		int rdmCount = 0;
		int tgtCount = 0;
		String strHeaderLine = "";
	
		int translationID = translationrecord.get_int( CrossReferencePaths._Root_Cross_Reference_Data_RDM_ANCILLARY_TRANSLATION._Root_Cross_Reference_Data_RDM_ANCILLARY_TRANSLATION_ROW_ID );
		
		RequestResult result1 = getSourceRequestResult( translationID, table );
		RequestResult result2 = getRDMRequestResult( translationID, table );
		RequestResult result3 = getTargetRequestResult( translationID, table );
		
		srcCount = result1.getSize();

		if( srcCount <= 0 ){
			mSrcInitCount = 0;
		} else{
			mSrcInitCount = srcCount;
		}

		rdmCount = result2.getSize();

		if( rdmCount <= 0 ){
			mRdmInitCount = 0;
		} else{
			mRdmInitCount = rdmCount;
		}
		
		tgtCount = result3.getSize();
		
		if( tgtCount <= 0 ){
			mTgtInitCount = 0;
		} else{
			mTgtInitCount = tgtCount;
		}

		strHeaderLine = updateHeaderLine( mSrcInitCount, mRdmInitCount, mTgtInitCount );
		
		System.out.println("Data = " + strHeaderLine.toString());
		return strHeaderLine;
	}

	
	/**
	 * Creates a record header by appending source, rdm and target headers
	 * 
	 * @param srcCount
	 * @param rdmCount
	 * @param tgtCount
	 * @return
	 * @throws OperationException
	 */
	protected String createInitialHeaderLine( ) throws OperationException {
		
		final StringBuffer strBuff = new StringBuffer();
	
		String sourceHeaderLine = "Source LOV External System Name|Source LOV CODE|Source LOV Value|Source LOV TYPE|Source LOV Description|Source Status|Source LOV Effective Date|Source LOV End Date|Source Created By User|Source Created By Date/Time|Source Last Updated User|Source Last Updated Date/Time|Source Last Updated Source";
		String rdmHeaderLine = "RDM LOV CODE|RDM LOV Value|RDM LOV TYPE|RDM LOV Description|RDM Status|RDM LOV Effective Date|RDM LOV End Date|RDM Created By User|RDM Created By Date/Time|RDM Last Updated User|RDM Last Updated Date/Time|RDM Last Updated Source";
		String targetHeaderLine = "Target LOV External Name|Target LOV CODE|Target LOV Value|Target LOV TYPE|Target LOV Description|Target Status|Target LOV Effective Date|Target LOV End Date|Target Created By User|Target Created By Date/Time|Target Last Updated User|Target Last Updated Date/Time|Target Last Updated Source";
		
		strBuff.append( sourceHeaderLine );
		strBuff.append( rdmHeaderLine );
		strBuff.append( targetHeaderLine );
		
		System.out.println( "Header = " + strBuff.toString() );
		return strBuff.toString();
	}

	
	/**
	 * Creates a record header by appending source, rdm and target headers
	 * 
	 * @param srcCount
	 * @param rdmCount
	 * @param tgtCount
	 * @return
	 * @throws OperationException
	 */
	protected String updateHeaderLine( int srcCount, int rdmCount, int tgtCount ) throws OperationException {
		
		String srcNewHeader = "";
		String rdmNewHeader = "";
		String tgtNewHeader = "";

		String sourceHeaderLine = "Source LOV External System Name|Source LOV CODE|Source LOV Value|Source LOV TYPE|Source LOV Description|Source Status|Source LOV Effective Date|Source LOV End Date|Source Created By User|Source Created By Date/Time|Source Last Updated User|Source Last Updated Date/Time|Source Last Updated Source";
		String rdmHeaderLine = "RDM LOV CODE|RDM LOV Value|RDM LOV TYPE|RDM LOV Description|RDM Status|RDM LOV Effective Date|RDM LOV End Date|RDM Created By User|RDM Created By Date/Time|RDM Last Updated User|RDM Last Updated Date/Time|RDM Last Updated Source";
		String targetHeaderLine = "Target LOV External Name|Target LOV CODE|Target LOV Value|Target LOV TYPE|Target LOV Description|Target Status|Target LOV Effective Date|Target LOV End Date|Target Created By User|Target Created By Date/Time|Target Last Updated User|Target Last Updated Date/Time|Target Last Updated Source";

		
		for( int i=0; i < srcCount; i++ ){
			
			if( i==0 ){
				srcNewHeader = sourceHeaderLine;
			} else {
				srcNewHeader = srcNewHeader + "|" + sourceHeaderLine;
			}
		}
		
		for( int j=0; j < rdmCount; j++ ){
			
			if( j==0 ){
				rdmNewHeader = rdmHeaderLine;
			} else{
				rdmNewHeader = rdmNewHeader + "|" + rdmHeaderLine;
			}
		}

		for( int k=0; k < tgtCount; k++ ){
			
			if( k==0 ){
				tgtNewHeader = targetHeaderLine;
			} else{
				tgtNewHeader = tgtNewHeader + "|" + targetHeaderLine;	
			}
		}

		final StringBuffer strBuff = new StringBuffer();
		
		strBuff.append( srcNewHeader );
		strBuff.append("|");
		strBuff.append( rdmNewHeader );
		strBuff.append("|");
		strBuff.append( tgtNewHeader );
		
		System.out.println( "Updated Header = " + strBuff.toString() );
		return strBuff.toString();
	}

	
	/**
	 * Checks for Null and formats the String
	 * 
	 * @param data
	 * @return
	 */
	public String formatString( String strObj ){
		
		if( null == strObj ){
			strObj = "";
		} else{
			strObj = strObj.toString();
		}
		
		return strObj;
	}

	
	/**
	 * Checks for Null and formats the Date
	 * 
	 * @param data
	 * @return
	 */
	public String formatDate( Date dateObj ){
		
		String strObj = "";
		
		if( null == dateObj ){
			strObj = "";
		} else{
			strObj = dateObj.toString();
		}
		
		return strObj;
	}
	
	// 13 Columns
	public String getSourcePipe(){
		
		String srcPipe = "|||||||||||||";
		
		return srcPipe;
	}
	
	// 12 Columns
	public String getRDMPipe(){
		
		String srcPipe = "||||||||||||";
		
		return srcPipe;
	}

	// 13 Columns
	public String getTargetPipe(){
		
		String srcPipe = "|||||||||||||";
		
		return srcPipe;
	}
	
	/**
	 * Fetch and update max counters
	 * 
	 */
	private void fetchMaxCounters( AdaptationTable table  ){

		// Create rows
		Iterator<Adaptation> locIterator = allRDMTranslationRecord.iterator();

		try{
			
			while( locIterator.hasNext() ){
				
				String rowStr = "";
				// Pass translation id & table; create one single record row
				rowStr = createRow( locIterator.next(), table );
			}
			

		} 
		catch( Exception e ){
			
			LOG.info( e.getMessage() );
		}
		
	}

	/**
	 * Creates a record by appending source, rdm and target record
	 * 
	 * @param translationrecord
	 * @param table
	 * @return
	 * @throws OperationException
	 */
	public void fetchMaxRows( Adaptation translationrecord, AdaptationTable table) throws OperationException {
		
		int translationID = translationrecord.get_int( CrossReferencePaths._Root_Cross_Reference_Data_RDM_ANCILLARY_TRANSLATION._Root_Cross_Reference_Data_RDM_ANCILLARY_TRANSLATION_ROW_ID );
		
		// Update Source max records
		updateSourceMaxCounter( translationID, table );
		
		// Update RDM max records
		updateRDMMaxCounter( translationID, table );
		
		// Update Target max records		
		updateTargetMaxCounter( translationID, table );
		
		System.out.println("Source max = " + mSrcMaxCount );
		System.out.println("RDM max = " + mRdmMaxCount );
		System.out.println("Target max = " + mTgtMaxCount );
		
	}
	
	/**
	 * 
	 * @param translationID
	 * @param table
	 */
	public void updateSourceMaxCounter( int translationID, AdaptationTable table ){
		
		RequestResult result = getSourceRequestResult( translationID, table );
		
		// Update source max counter
		if( result != null && result.getSize() > 0 ){
		
			updateSourceMaxCounter( result.getSize() );
		}
	}

	/**
	 * 
	 * @param translationID
	 * @param table
	 */
	public void updateRDMMaxCounter( int translationID, AdaptationTable table ){
		
		RequestResult result = getSourceRequestResult( translationID, table );
		
		// Update source max counter
		if( result != null && result.getSize() > 0 ){
		
			updateRDMMaxCounter( result.getSize() );
		}
	}

	
	/**
	 * 
	 * @param translationID
	 * @param table
	 */
	public void updateTargetMaxCounter( int translationID, AdaptationTable table ){
		
		RequestResult result = getSourceRequestResult( translationID, table );
		
		// Update source max counter
		if( result != null && result.getSize() > 0 ){
		
			updateTargetMaxCounter( result.getSize() );
		}
	}
	
}