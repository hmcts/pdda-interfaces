package uk.gov.courtservice.xhibit.cpp.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.tools.ant.util.FileUtils;

import oracle.jdbc.internal.OracleTypes;
import uk.gov.courtservice.xhibit.cpp.scripts.db.DBCPDataSource;
import uk.gov.courtservice.xhibit.cpp.scripts.pojo.DBConnectionProperties;
import uk.gov.courtservice.xhibit.cpp.scripts.pojo.FileSystemProperties;
import uk.gov.courtservice.xhibit.cpp.scripts.pojo.GeneralProperties;


/**
 * This class is the main class for dealing with inserting CPP data into XHB_CLOB and XHB_CPP_STAGING_INBOUND.
 * It will run until it receives a command to STOP
 * 
 * When running it will delegate as needed to:
 * - initially setup a database connection pool to be used throughout its lifetime
 * - check for the existence of (unprocessed) files in a local filesystem
 * - it will store the names of the files to be processed in a local cache
 * - threads, up to a defined maximum allowed, will be spawned to deal with a file
 * - when a thread picks up a file it marks it as being dealt with so that it is only picked up once
 * - for each file it will add an entry to XHB_CLOB and using the CLOB_ID will add a record to XHB_CPP_STAGING_INBOUND
 * - once processed it will then move the processed file to a separate location for files that have been "dealt with" 
 * 
 * @author atwells
 *
 */
public class LighthouseCPP {
		
	private static String propertiesFilename = "cppProperties.properties";
	static DBConnectionProperties dbProps;
	static FileSystemProperties fsProps;
	static GeneralProperties genProps;
	static final Logger logger = Logger.getLogger(LighthouseCPP.class);

	
	public static void main(String [] args) {
		logger.debug(System.currentTimeMillis()+" :: METHOD ENTRY:: main");
		setup();
		LighthouseCPP lhcpp = new LighthouseCPP();
		lhcpp.startCPPMonitoring();
		
		logger.debug(System.currentTimeMillis()+" :: METHOD EXIT:: main");
	}
	
	/**
	 * Do all setup tasks
	 */
	private static void setup() {
		logger.debug(System.currentTimeMillis()+" :: METHOD ENTRY:: setup");
		
		// Get all properties
		try {
			Configuration config = new PropertiesConfiguration(propertiesFilename);
			dbProps = new DBConnectionProperties();
			fsProps = new FileSystemProperties();
			genProps = new GeneralProperties();

			setDBConnectionProperties(config);
			setFSProperties(config);
			setGeneralProperties(config);
		} catch (ConfigurationException ce) {
			ce.printStackTrace();
		}

		// Setup connection pool
		DBCPDataSource.setupConnectionPool(dbProps);
		
		logger.debug(System.currentTimeMillis()+" :: METHOD EXIT:: setup");
	}
	
	/**
	 * The main method that handles starting a thread to process a file
	 */
	private void startCPPMonitoring() {
		logger.debug(System.currentTimeMillis()+" :: METHOD ENTRY:: startCPPMonitoring");
		boolean areWeStillGoing = true;
		while (areWeStillGoing) {
			// Get files to be processed, if none then do nothing this time round
			File [] filesToProcess = getNextFileDetails();
			
			if (filesToProcess!=null && filesToProcess.length>0) {
				areWeStillGoing = processFiles(filesToProcess);
			}
			
			// Sleep for a period
			try {
				Thread.sleep(genProps.getSleepTimePerLoop());
			} catch (InterruptedException e) {
				logger.error(System.currentTimeMillis()+" :: thread interrupted");
				Thread.currentThread().interrupt();
				
			} 
		}
		
		logger.debug(System.currentTimeMillis()+" :: All RunCPPJob threads are complete");
		try {
			DBCPDataSource.shutdownDataSource();
		} catch (SQLException e) {
			logger.error(System.currentTimeMillis()+" :: Error occurred shutting down data source "+e);
		}

		logger.debug(System.currentTimeMillis()+" :: METHOD EXIT:: startCPPMonitoring");
	}
	
	/**
	 * Process all the files, start a thread for each and insert where appropriate.
	 * @param filesToProcess the array of files needing to be processed
	 * @return true if a stop file hasn't been encountered
	 */
	private boolean processFiles(File[] filesToProcess) {
		boolean areWeStillGoing=true;
		ExecutorService executor = Executors.newFixedThreadPool(genProps.getNumThreads());
		//for every file 
		for(int i=0;i<filesToProcess.length;i++) {
			//If stop command sent then jump out and set the still going boolean to false so it will shutdown
			if(filesToProcess[i].getName().equalsIgnoreCase(fsProps.getStopFile())) {
				logger.debug(System.currentTimeMillis()+" Stop file encountered so shutting down");
				areWeStillGoing =false;
				break;
			}
			
			Runnable worker = new RunCPPJob(filesToProcess[i], fsProps.getAfterProcessSuccessFolder(), fsProps.getAfterProcessFailureFolder());
			executor.execute(worker);
		}	
		executor.shutdown();
		while (!executor.isTerminated()) {
			// Wait until all threads are finished
		}
		return areWeStillGoing;
		
	}

	/**
	 * Check if there is a file to process and mark it as in process
	 * Possibly consider getting the XML here too
	 * @return
	 */
	private File[] getNextFileDetails() {
		logger.debug(System.currentTimeMillis()+" :: METHOD ENTRY:: getNextFileDetails");
				
		File dir = new File(fsProps.getIncomingFolder());
		logger.debug(System.currentTimeMillis()+" Number of files to process : "+dir.list().length);
		if(dir.list().length>0) {
			return dir.listFiles();		
		} 		
		
		return new File[0];
	}
	
	
	
	///////////////////////
	/// Properties handling
	///////////////////////
	
	private static void setDBConnectionProperties(Configuration config) {
		dbProps.setUrl(config.getString("db.url"));
		dbProps.setUsername(config.getString("db.username"));
		dbProps.setPassword(config.getString("db.password"));
		dbProps.setMinIdle(config.getInt("db.minIdle"));
		dbProps.setMaxIdle(config.getInt("db.maxIdle"));
		dbProps.setMaxOpenStatements(config.getInt("db.maxOpenStatements"));
	}
	
	private static void setFSProperties(Configuration config) {
		fsProps.setIncomingFolder(config.getString("fs.incomingFolder"));
		fsProps.setAfterProcessSuccessFolder(config.getString("fs.afterProcessSuccessFolder"));
		fsProps.setAfterProcessFailureFolder(config.getString("fs.afterProcessFailureFolder"));
		fsProps.setStopFile(config.getString("fs.stopFile"));
	}
	
	private static void setGeneralProperties(Configuration config) {
		genProps.setSleepTimePerLoop(config.getInt("gen.sleepTimePerLoop"));
		genProps.setNumThreads(config.getInt("gen.numThreads"));
	}
	
}


/**
 * A class that does the actual work, adds a record to both
 * database tables.  Each file is picked up by its own thread. With a maximum
 * number of threads running at a time (defined in the property file).
 * @author atwells
 *
 */
class RunCPPJob implements Runnable {
	
	
	private final File currentFile;
	private final String failFolder;
	private final String successFolder;
	static final Logger logger = Logger.getLogger(RunCPPJob.class);
	static final String MOVE_FILE_ERROR = "Unable to move file ";
 
	RunCPPJob(File currentFile, String successFolder, String failFolder) {
		writeToLog("Setting up the RunCPPJob to process a file");
		this.currentFile=currentFile;
		this.successFolder=successFolder;
		this.failFolder = failFolder;
	}
	
	@Override
	public void run() {
		writeToLog("About to process file "+currentFile.getName());
		Connection conn = null;
		//move the file
		String clobData = setClobData(currentFile);
		if(clobData!=null && !clobData.equals("")) {
			try {
				//before adding it to the database we need to check if the filename is valid 
				writeToLog("About to add clob data to the database ");
			
				//split up the filename into its 3 parts : type_courtCode_dateTime
				String[] fileParts = currentFile.getName().split("_");
				if(fileParts.length==3){
					conn = DBCPDataSource.getConnection();
					long clobId=addClobToDB(clobData, conn);
					
					writeToLog("About to add "+ clobId +"to the cpp staging inbound table");
			
					long stagingInboundId = insertStaging(currentFile.getName(),fileParts[1], getDocType(fileParts[0]),fileParts[2].replaceAll(".xml", ""), clobId, "NP", conn);
					writeToLog("Successfully added  "+ stagingInboundId +" to the cpp staging inbound table");
	
					//if we get here we know it's all gone ok so move the file to the success folder.
					moveFile(currentFile, new File(successFolder+currentFile.getName()));
					writeToLog("Processing of "+ currentFile.getName()+" completed");
				} else {
					logger.error("Filename is not valid size so moving "+currentFile.getName()+" to error folder");
					moveFile(currentFile, new File(failFolder+currentFile.getName()));
				}

			} catch (SQLException e) {
				logger.error("Error adding data to the database for file "+currentFile.getName()+" :"+e.getStackTrace());
				//Move the file to the fail folder
				moveFile(currentFile, new File(failFolder+currentFile.getName()));
			} finally {
				try {
					if(conn!=null) {
						conn.close();
					}
				} catch (SQLException e) {
					logger.error("Error with closing connection : "+e);
				}
			}
		} else {
			writeToLog("No data to insert for "+currentFile.getName()+". Moving to error folder");
			//Move the file to the fail folder
			moveFile(currentFile,new File(failFolder+currentFile.getName()));
			
		}
	}
	
	/**
	 * Set the clob data from the file.
	 * @param xmlFile
	 * @return the clob data in a String
	 */
	private String setClobData(File xmlFile) {
		writeToLog("METHOD ENTRY: setClobData");
		
		// Read the contents of the file into clob data
		StringBuilder sb = new StringBuilder();
		String thisLine;
		Scanner scnr = null;
		try {
			scnr = new Scanner(new FileInputStream(xmlFile), "utf-8");
			int line = 1;
			while (scnr.hasNextLine()) {
				thisLine = scnr.nextLine();
				if (line == 1) {
					// Remove any leading characters before the first angle bracket
					thisLine = thisLine.substring(thisLine.indexOf('<'));
					line++;
				}
				sb.append(thisLine.trim().replace("'", "''"));
			}
			scnr.close();
			return sb.toString();
		} catch (FileNotFoundException e) {
			logger.error("Error setting the clob data from the incoming file "+xmlFile.getName());
			logger.error(e.getLocalizedMessage());
		} catch (StringIndexOutOfBoundsException e) {
			logger.error("Data is invalid in the incoming file "+xmlFile.getName());
			logger.error(e.getLocalizedMessage());	
		} finally {
			if(scnr!=null) {
				scnr.close();
			}
		}
		return null;
	}
	
	/**
	 * Creates the insert or update query and calls the appropriate method to
	 * either create or update clob.
	 * @param clobData
	 * @return clobid to be used later on for other inserts
	 * @throws SQLException
	 */
	private long addClobToDB(String clobData, Connection conn) throws SQLException {
		writeToLog("METHOD ENTRY: addClobToDB");
		writeToLog("Length of CLOB to insert: " + clobData.length());	
						
		String insQuery = "BEGIN insert into xhb_clob (clob_data) values(to_clob(?)) returning clob_id into ?; END;";
		String q3 = "update xhb_clob set clob_data=clob_data||to_clob('";
		String q4 = "') where clob_id=";
		String updQuery = "";

		long clobId=0;
		ArrayList<String> clobElements = (ArrayList<String>) splitEqually(clobData, 2400); // Using 2400 (rather than 2499 max) as some characters may be escaped so need to leave room for this
			
		writeToLog("CLOB has been split up");
			
		for (int i=0; i<clobElements.size(); i++) {
			writeToLog("CLOB loop: "+i);
				
			String thisPart = clobElements.get(i);
			if (i == 0) {
				clobId = insertCLOB(insQuery, thisPart, conn);
				writeToLog("ClobId="+clobId);
					
			} else {
				updQuery = q3+thisPart+q4+clobId;
				updateCLOB(updQuery, conn);
			}
		}
		return clobId;		
	}
	
	/**
	 * Write to the debug log if debug is enabled
	 * @param string
	 */
	private void writeToLog(String string) {
	if (logger.isDebugEnabled()) {
		logger.debug(System.currentTimeMillis()+" :: "+string);
	}
}
	
	/**
	 * Insert the clob into the database
	 * @param insertQuery
	 * @param clobData
	 * @param connection 
	 * @return clobid to be used in other inserts.
	 * @throws SQLException
	 */
	private long insertCLOB(String insertQuery, String clobData, Connection connection) throws SQLException {
		
        CallableStatement statement = null;
    	
        try {
			statement = connection.prepareCall(insertQuery);	
			statement.setString(1, clobData);
			statement.registerOutParameter(2, OracleTypes.NUMBER);
			statement.execute();	
			return statement.getLong(2);
        } catch (SQLException e) {
        	logger.error("Unable to insert CLOB");
        	throw e;
		} finally {
			closeStatement(statement);
		}
	}
	
	/**
	 * Insert the row into XHB_STAGING_INBOUND
	 * @param docName - Document name e.g. DailyList_453_20200101123213.xml
	 * @param courtCode from the document name
	 * @param documentType e.g. DL
	 * @param timeLoaded which is the 3rd part of the document name
	 * @param clobId that was created pre this insert
	 * @param validationStatus - 'NP'
	 * @return the staging inbound id for debugging/logging purposes
	 * @throws SQLException
	 */
	private long insertStaging(String docName, String courtCode, String documentType, String timeLoaded, Long clobId, String validationStatus, Connection connection) throws SQLException {
		
        CallableStatement statement = null;
        writeToLog("doc "+docName+ " courtCode: "+courtCode+" documentType: "+documentType+" timeLoaded: "+timeLoaded+" clobId :"+clobId+" validationStatus :"+validationStatus);
        
        String insertQuery = "begin "
        		+ "INSERT INTO XHB_CPP_STAGING_INBOUND (document_name, court_code, document_type, time_loaded, clob_id, validation_status) "
        		+ "VALUES (?, ?, ?, to_date(?,'YYYYMMDDHH24MISS'),  ?, ?) returning cpp_staging_inbound_id into ?; END;";
        
    	
        try {
			statement = connection.prepareCall(insertQuery);	
			statement.setString(1, docName);
			statement.setString(2, courtCode);
			statement.setString(3, documentType);
			statement.setString(4, timeLoaded);
			statement.setLong(5, clobId);
			statement.setString(6, validationStatus);
			statement.registerOutParameter(7, OracleTypes.NUMBER);
			statement.execute();	
			return statement.getLong(7);
        } catch (SQLException e) {
        	logger.error("Unable to insert staging info" +e);
        	throw e;
		} finally {
			closeStatement(statement);
		}
	}
	
	/**
	 * Update the clob
	 * @param updateQuery String query to execute
	 * @param connection 
	 * @throws SQLException
	 */
	private void updateCLOB(String updateQuery, Connection connection) throws SQLException {
		
		Statement stmt = null;
    	
        try {
	        stmt = connection.createStatement();
	        stmt.executeQuery(updateQuery);
        } catch (SQLException e) {
        	logger.error("Unable to update CLOB");
        	throw e;
		} finally {
			closeStatement(stmt);
		}
	}
	
	
	/**
	 * Split the string into small enough chucks to insert into database
	 * 
	 * @param text
	 * @param sizeOfEachElement
	 * @return
	 */
	private List<String> splitEqually(String text, int sizeOfEachElement) {
		writeToLog("METHOD ENTRY: splitEqually");
		
		
		List<String> ret = new ArrayList<String>((text.length() + sizeOfEachElement - 1) / sizeOfEachElement);
		
		for (int start=0; start<text.length(); start+=sizeOfEachElement) {
			ret.add(text.substring(start, Math.min(text.length(), start+sizeOfEachElement)));
		}
		return ret;
	}
	
	/**
	 * Return the document type depending on what's been used in the 
	 * file name
	 * @param fileType portion of the filename
	 * @return shorthand documentType
	 */
	private String getDocType(String fileType) {
		writeToLog("METHOD ENTRY: getDocType");
		
		if(fileType.equalsIgnoreCase(DocumentType.DL.getDocName())) {
			return DocumentType.DL.name();
		} else if (fileType.equalsIgnoreCase(DocumentType.WL.getDocName())) {
			return DocumentType.WL.name();
		} else if (fileType.equalsIgnoreCase(DocumentType.FL.getDocName())) {
			return DocumentType.FL.name();
		} else if (fileType.equalsIgnoreCase(DocumentType.WP.getDocName())) {
			return DocumentType.WP.name();
		} else if (fileType.equalsIgnoreCase(DocumentType.PD.getDocName())) {
			return DocumentType.PD.name();
		}
		
		return null;
		
	}
	
	/**
	 * Move file from the folder to another folder
	 * @param moveFrom from location
	 * @param moveTo to location
	 */
	private void moveFile(File moveFrom, File moveTo){
		try {
			FileUtils.getFileUtils().rename(moveFrom, moveTo);
		} catch (IOException e1) {
			logger.error(MOVE_FILE_ERROR+currentFile.getName()+" : "+e1);
		}
	}
	
	/**
	 * Close statement after create/update complete.
	 * @param stmt
	 */
	private void closeStatement(final Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (final SQLException t) {
            	logger.error("Error closing Statement", t);
            }
        }
    }

}