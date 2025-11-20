package uk.gov.hmcts.pdda.business.services.pdda.sftp;

import jakarta.persistence.EntityManager;
import javassist.NotFoundException;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.courtservice.xhibit.business.vos.services.publicnotice.DisplayablePublicNoticeValue;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.CaseStatusEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicNoticeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.pdda.PddaHearingProgressEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogViewValue;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseDao;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbpublicnotice.XhbPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbpublicnotice.XhbPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefpddamessagetype.XhbRefPddaMessageTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.business.services.pdda.BaisValidation;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageHelper;
import uk.gov.hmcts.pdda.business.services.pdda.PddaMessageUtil;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSerializationUtils;
import uk.gov.hmcts.pdda.business.services.pdda.PddaSftpValidationUtil;
import uk.gov.hmcts.pdda.business.services.pdda.XhibitPddaHelper;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveParameterList", 
    "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.TooManyMethods",
    "PMD.CognitiveComplexity"})
public class SftpService extends XhibitPddaHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SftpService.class);

    private static final String NO = "N";
    protected static final String INVALID_MESSAGE_TYPE = "Invalid";
    protected static final String SFTP_ERROR = "SFTP Error:";

    protected static final String LOG_CALLED = " called";
    protected static final String CP_CONNECTION_TYPE = "CP";
    protected static final String XHIBIT_CONNECTION_TYPE = "XHIBIT";
    protected static final String TEST_CONNECTION_TYPE = "TEST";
    protected static final String PDDA_FILENAME_PREFIX = "PDDA";
    protected static final String PUBLIC_DISPLAY_DOCUMENT_TYPE = "PublicDisplay";
    protected static final String DAILY_LIST_DOCUMENT_TYPE = "DailyList";
    protected static final String FIRM_LIST_DOCUMENT_TYPE = "FirmList";
    protected static final String WARNED_LIST_DOCUMENT_TYPE = "WarnedList";
    protected static final String WEB_PAGE_DOCUMENT_TYPE = "WebPage";
    protected static final String HEARING_PROGRESS_DELAY_MINUTES = "HEARING_PROGRESS_DELAY_MINUTES";

    public static final String NEWLINE = "\n";


    /**
     * JUnit constructor.

     * @param entityManager The EntityManager
     * @param xhbConfigPropRepository The XhbConfigPropRepository
     * @param environment The Environment
     */
    public SftpService(EntityManager entityManager, XhbConfigPropRepository xhbConfigPropRepository,
        Environment environment, PddaMessageHelper pddaMessageHelper,
        XhbClobRepository clobRepository, XhbCourtRepository courtRepository,
        XhbCourtRoomRepository courtRoomRepository, XhbCourtSiteRepository courtSiteRepository,
        XhbCaseRepository xhbCaseRepository, XhbHearingRepository hearingRepository,
        XhbSittingRepository sittingRepository, XhbScheduledHearingRepository scheduledHearingRepository,
        XhbPublicNoticeRepository publicNoticeRepository, XhbConfiguredPublicNoticeRepository
            configuredPublicNoticeRepository) {
        super(entityManager, xhbConfigPropRepository, environment,
            pddaMessageHelper, clobRepository, courtRepository,
            courtRoomRepository, courtSiteRepository, xhbCaseRepository,
            hearingRepository, sittingRepository, scheduledHearingRepository,
            publicNoticeRepository, configuredPublicNoticeRepository);
    }

    public SftpService(EntityManager entityManager) {
        super(entityManager, InitializationService.getInstance().getEnvironment());
    }



    /**
     * Retrieve messages from BAIS.

     * @param sftpPort The SFTP port - will be 0 unless we are testing
     * @return True if there was an error
     */
    @SuppressWarnings("PMD")
    public boolean processBaisMessages(int sftpPort) {
        
        boolean error = false;

        methodName = "processBaisMessages()";
        LOG.debug(methodName, LOG_CALLED);

        SftpConfig sftpConfig = getSftpHelperUtil().populateSftpConfig(sftpPort);

        // First get the CP data from BAIS
        LOG.debug("About to attempt to connect to BAIS to download any CP files");
        try (SSHClient ssh = getSftpConfigHelper().getNewSshClient()) {
            
            ssh.connect(sftpConfig.getHost(), sftpConfig.getPort());

            // Do the authentication based on the configuration; CP first, then XHIBIT
            LOG.debug("About to authenticate with CP credentials: {} and {}",
                sftpConfig.getCpUsername(), sftpConfig.getCpPassword());
            ssh.authPassword(sftpConfig.getCpUsername(), sftpConfig.getCpPassword());

            sftpConfig.setSshClient(ssh);
            LOG.debug("Setting active remote folder to look at as {}",
                sftpConfig.getCpRemoteFolder());
            sftpConfig.setActiveRemoteFolder(sftpConfig.getCpRemoteFolder());

            setupSftpClientAndProcessBaisData(sftpConfig, ssh, true);
            LOG.debug("Processed CP files");
        } catch (IOException e) {
            LOG.error("Error processing files from BAIS CP: {}", ExceptionUtils.getStackTrace(e));
            error = true;
        }

        // Second get the XHIBIT data from BAIS
        LOG.debug("About to attempt to connect to BAIS to download any XHIBIT files");
        try (SSHClient ssh = getSftpConfigHelper().getNewSshClient()) {
            ssh.connect(sftpConfig.getHost(), sftpConfig.getPort());

            // Do the authentication based on the configuration; CP first, then XHIBIT
            LOG.debug("About to authenticate with XHIBIT credentials: {} and {}",
                sftpConfig.getXhibitUsername(), sftpConfig.getXhibitPassword());
            ssh.authPassword(sftpConfig.getXhibitUsername(), sftpConfig.getXhibitPassword());

            sftpConfig.setSshClient(ssh);
            LOG.debug("Setting active remote folder to look at as {}",
                sftpConfig.getXhibitRemoteFolder());
            sftpConfig.setActiveRemoteFolder(sftpConfig.getXhibitRemoteFolder());

            setupSftpClientAndProcessBaisData(sftpConfig, ssh, false);
            LOG.debug("Processed XHIBIT files");
        } catch (IOException e) {
            LOG.error("Error processing files from BAIS XHIBIT: {}",
                ExceptionUtils.getStackTrace(e));
            error = true;
        }

        LOG.debug("{} - Finished processing files from BAIS", methodName);

        return error;
    }


    /**
     * Setup the SFTP client. The call to get the data from BAIS is made here too as it must happen
     * within the try with resources call.

     * @param config The SFTP configuration
     */
    void setupSftpClientAndProcessBaisData(SftpConfig config, SSHClient ssh,
        boolean isCpConnection) {
        methodName = "setupSftpClient()";
        LOG.debug(methodName, LOG_CALLED);

        try (SFTPClient sftpClient = ssh.newSFTPClient()) {
            config.setSshjSftpClient(sftpClient);
            processDataFromBais(config, isCpConnection);
        } catch (IOException e) {
            LOG.error("Error setting up SFTP client: {}", ExceptionUtils.getStackTrace(e));
        }
    }



    /**
     * Get the data from BAIS, and do the processing.

     * @param config The SFTP configuration
     */
    private void processDataFromBais(SftpConfig config, boolean isCpConnection) {
        methodName = "getDataFromBais()";
        LOG.debug(methodName, LOG_CALLED);

        try {
            if (isCpConnection) {
                retrieveFromBais(config, new BaisCpValidation(getCourtRepository()));
            } else {
                retrieveFromBais(config, new BaisXhibitValidation(getCourtRepository()));
            }
        } catch (IOException e) {
            LOG.error("Error processing file: {}", ExceptionUtils.getStackTrace(e));
        }
    }





    /**
     * Retrieve events from BAIS.

     * @param config The SFTP configuration
     * @param baisValidation The validation class
     * @throws IOException The IOException
     */
    void retrieveFromBais(SftpConfig config, BaisValidation baisValidation)
        throws IOException {

        methodName = "retrieveFromBais()";
        LOG.debug(methodName, LOG_CALLED);

        try {
            LOG.debug("retrieveFromBais({},{})", config, baisValidation);
            Map<String, String> files = getBaisFileList(config, baisValidation);
            LOG.debug("Total of {}{}", files.size(),
                " files, before processing, in this transaction.");
            if (!files.isEmpty()) {
                // Process the files we have retrieved.
                for (Map.Entry<String, String> entry : files.entrySet()) {
                    final String filename = entry.getKey();
                    final String clobData = entry.getValue();
                    
                    // Fetch the remote folder's file list each time a file is processed
                    LOG.debug("Checking current list of files in remote folder before processing current file: {}",
                        filename);
                    List<String> listOfFilesInFolder = getPddaSftpHelperSshj()
                        .listFilesInFolder(config.getSshjSftpClient(),
                                          config.getActiveRemoteFolder(),
                                          baisValidation);
                    
                    // If the filename is not in the list then its already been processed and deleted previously
                    if (!listOfFilesInFolder.contains(filename)) {
                        continue;
                    }
                    LOG.debug("File: {}{}", filename,
                        ", still exists in remote folder - calling processBaisFile()...");
                    processBaisFile(config, baisValidation, filename, clobData);
                }
            }
        } finally {
            if (config.getSshjSftpClient() == null) {
                // Check the contents of BAIS to verify all processed files are deleted
                Map<String, String> files = getBaisFileList(config, baisValidation);
                if (files.isEmpty()) {
                    LOG.debug("Contents of Bais is now empty");
                } else {
                    LOG.debug("Contents of Bais is still not empty - is that correct??");
                }
            }
        }
    }


    /**
     * Get a list of files on BAIS.

     * @param config The SFTP configuration
     * @param validation The validation class
     * @return The list of files
     */
    private Map<String, String> getBaisFileList(SftpConfig config, BaisValidation validation) {
        methodName = "getBaisFileList()";
        LOG.debug(methodName, LOG_CALLED);

        try {
            // Get the files from BAIS
            return getPddaSftpHelperSshj().sftpFetch(config.getSshjSftpClient(),
                config.getActiveRemoteFolder(), validation, config.getCpExcludedCourtIds());
        } catch (Exception ex) {
            LOG.error(SFTP_ERROR + " {} ", ExceptionUtils.getStackTrace(ex));
            return Collections.emptyMap();
        }
    }


    /**
     * Process a file from BAIS.

     * @param config The SFTP configuration
     * @param validation The validation class
     * @param filename The filename
     * @param clobData The CLOB data
     * @throws IOException The IOException
     */
    private void processBaisFile(SftpConfig config, BaisValidation validation, String filename,
        String clobData) throws IOException {

        methodName = "processBaisFile()";
        LOG.debug(methodName, LOG_CALLED);

        try {
            LOG.debug("Processing filename {}", filename);

            // Validate this filename hasn't been processed previously
            Optional<XhbPddaMessageDao> pddaMessageDao =
                validation.getPddaMessageDao(getPddaMessageHelper(), filename);
            if (pddaMessageDao.isPresent()) {
                LOG.debug("Filename {}{}", filename, " already processed");

                // Now delete this file
                getPddaSftpHelperSshj().sftpDeleteFile(config.getSshjSftpClient(),
                    config.getActiveRemoteFolder(), filename, validation);
                return;
            }

            // Get the event from XHIBIT, if applicable
            boolean isList = true;
            String listType = EMPTY_STRING;
            PublicDisplayEvent event = null;
            if (filename.startsWith(PDDA_FILENAME_PREFIX + "_XPD_")) {
                isList = false;
                event = validation.getPublicDisplayEvent(filename, clobData);
                // Now translate the event so that the court room id is correct, if necessary
                event = PddaMessageUtil.translatePublicDisplayEvent(event, getCourtRepository(),
                    getCourtRoomRepository(), getCourtSiteRepository());
                
                // Check what type of event it is, process and send it
                checkProcessAndSendEvent(event);
                
            } else if (filename.startsWith(PDDA_FILENAME_PREFIX + "_CPD_")) {
                isList = false;
                // We don't want to send a message for CPD files
            } else if (filename.startsWith(PDDA_FILENAME_PREFIX + "_XWP_")) {
                isList = false;
                // We don't want to send a message for XWP (XHIBIT Web Page) files
            } else {
                // What type of list is this?
                LOG.debug("Getting the list type.");
                listType = getListType(clobData);
            }
            
            // Validate filename and process the message
            validateAndProcessMessage(validation, filename, event, isList, clobData, listType);
            LOG.debug("Processed file {}", filename);
            
        } catch (IOException | NotFoundException ex) {
            LOG.error("Error processing BAIS file {} ", ExceptionUtils.getStackTrace(ex));
            CsServices.getDefaultErrorHandler().handleError(ex, getClass());
        } finally {
            // Try and remove the file from BAIS
            getPddaSftpHelperSshj().sftpDeleteFile(config.getSshjSftpClient(),
                config.getActiveRemoteFolder(), filename, validation);
            LOG.debug("Removed file from bais after processing: {}", filename);
        }
    }
    
    private void checkProcessAndSendEvent(PublicDisplayEvent event) {
        if (event instanceof PddaHearingProgressEvent pddaHearingProgressEvent) {
            LOG.debug("PDDA Hearing Progress Event received from XHIBIT");
            processHearingProgressEvent(pddaHearingProgressEvent);
        } else {
            if (event instanceof CaseStatusEvent caseStatusEvent) {
                LOG.debug("Case Status Event received from XHIBIT");
                // Process the CaseStatusEvent
                CourtLogViewValue updatedCourtLogViewValue = processCaseStatusEvent(caseStatusEvent);
                if (updatedCourtLogViewValue != null) {
                    // Update the public display status
                    getCrLiveStatusHelper().updatePublicDisplayStatus(updatedCourtLogViewValue);
                }
            } else if (event instanceof PublicNoticeEvent publicNoticeEvent) {
                LOG.debug("Public Notice Event received from XHIBIT");
                processPublicNoticeEvent(publicNoticeEvent);
            }
            sendMessage(event);
        }
    }
    
    private void validateAndProcessMessage(BaisValidation validation, String filename,
        PublicDisplayEvent event, boolean isList,
        String clobData, String listType) throws NotFoundException {
        // Validate the filename
        String errorMessage = validation.validateFilename(filename, event, isList);

        // Validate messageType
        String messageType = validation.getMessageType(filename, event);
        if (INVALID_MESSAGE_TYPE.equals(messageType)) {
            errorMessage = "Invalid filename - MessageType";
        }

        if (errorMessage != null) {
            LOG.debug("Filename {}{}", filename, " is invalid");
            // Continue to process though as we need a record of the file in the database
        }

        // Get the crestCourtId (should have already been validated by this point)
        Integer courtId = validation.getCourtId(filename, event);
        LOG.debug("CourtId is {}", courtId);
        LOG.debug("Validation of filename {} is now complete, attempting to process the file.",
            filename);

        // Write the pddaMessage
        createBaisMessage(courtId, messageType, filename, clobData, errorMessage, listType);
    }

    private void processHearingProgressEvent(PddaHearingProgressEvent event) {
        // Initially check the event fields are present
        Integer courtId = event.getCourtId();
        String courtName = event.getCourtName();
        String caseType = event.getCaseType();
        Integer caseNumber = event.getCaseNumber();
        String courtRoomName = event.getCourtRoomName();
        
        if (courtId != null
            && courtName != null
            && caseType != null
            && caseNumber != null
            && courtRoomName != null) {
            LOG.debug("All case & court fields for PddaHearingProgressEvent are present: {}{}{}{}{}",
                courtId, courtName, caseType, caseNumber, courtRoomName);
        
            // Drill down to the scheduledHearingDao record
            XhbScheduledHearingDao scheduledHearingDao = hearingProgressDrillDown(courtId, 
                caseType, caseNumber, courtRoomName);
            
            // Get the delay period from xhb_config_prop
            List<XhbConfigPropDao> xhbConfigPropDao = getXhbConfigPropRepository()
                .findByPropertyNameSafe(HEARING_PROGRESS_DELAY_MINUTES);
            
            if (!xhbConfigPropDao.isEmpty()) {
                Integer delay =  Integer.parseInt(xhbConfigPropDao.get(0).getPropertyValue());
                // Ensure last update date is outside of the delay period to prevent processing a duplicate
                if (scheduledHearingDao != null 
                    && scheduledHearingDao.getLastUpdateDate().plusMinutes(delay)
                    .isBefore(LocalDateTime.now())) {
                    LOG.debug("ScheduledHearing found with ID: {}",
                        scheduledHearingDao.getScheduledHearingId());
                    Integer hearingProgressIndicator = event.getHearingProgressIndicator();
                    String caseActive = event.getIsCaseActive();
                    
                    // Update hearingProgressIndicator if its present and not the same as current value
                    if (hearingProgressIndicator != null 
                        && hearingProgressIndicator != 0
                        && !hearingProgressIndicator.equals(scheduledHearingDao.getHearingProgress())) {
                        scheduledHearingDao.setHearingProgress(hearingProgressIndicator);
                        LOG.debug("ScheduledHearing hearingProgress set to: {}",
                            hearingProgressIndicator);
                    
                        // Update isCaseActive if its present
                        if (caseActive != null) {
                            scheduledHearingDao.setIsCaseActive(caseActive);
                            LOG.debug("ScheduledHearing isCaseActive set to: {}",
                                caseActive);
                        }
                        getScheduledHearingRepository().update(scheduledHearingDao);
                        LOG.debug("ScheduledHearing with ID: {} updated", 
                            scheduledHearingDao.getScheduledHearingId());
                    }
                }
            }
        }
    }
    
    private XhbScheduledHearingDao hearingProgressDrillDown(Integer courtId,
        String caseType, Integer caseNumber, String courtRoomName) {

        // Get the courtSiteId from the courtId
        XhbCourtSiteDao xhbCourtSiteDao = getCourtSiteRepository()
            .findByCourtIdSafe(courtId).get(0);
        
        if (xhbCourtSiteDao != null) {
            LOG.debug("Court site found with ID: {}", xhbCourtSiteDao.getCourtId());
            LOG.debug("Finding case using case number: {}{}", caseType, caseNumber);
            
            // Get caseId using the case number from the event
            Optional<XhbCaseDao> xhbCaseDao = getCaseRepository()
                .findByNumberTypeAndCourtSafe(xhbCourtSiteDao.getCourtId(), caseType, caseNumber);
            
            if (xhbCaseDao.isPresent()) {
                LOG.debug("Case found with ID: {}", xhbCaseDao.get().getCaseId());
                
                // Get the hearingId using the caseId and todays date
                Optional<XhbHearingDao> xhbHearingDao = getHearingRepository()
                    .findByCaseIdWithTodaysStartDateSafe(xhbCaseDao.get().getCaseId(),
                        LocalDate.now().atStartOfDay());
                if (!xhbHearingDao.isEmpty()) {
                    LOG.debug("Hearing found with ID: {}", xhbHearingDao.get().getHearingId());
                
                    // Get the courtRoomId from the courtSiteId and courtRoomName
                    LOG.debug("Finding court room using court room name: {} and court site id: {}",
                        courtRoomName, xhbCourtSiteDao.getCourtSiteId());
                    XhbCourtRoomDao xhbCourtRoomDao = getCourtRoomRepository()
                        .findByCourtSiteIdAndCourtRoomNameSafe(xhbCourtSiteDao.getCourtSiteId(),
                            courtRoomName).get(0);
                    if (xhbCourtRoomDao != null) {
                        LOG.debug("Court room found with ID: {}", xhbCourtRoomDao.getCourtRoomId());
                    
                        // Find scheduled hearing record
                        return findScheduledHearing(
                            xhbCourtRoomDao.getCourtRoomId(), xhbCourtSiteDao, xhbHearingDao.get());
                    }
                }
            }
        }
        return null;
    }
    
    private CourtLogViewValue processCaseStatusEvent(CaseStatusEvent event) {
        // Get the CourtRoomIdentifier from the event, which has been previously translated
        CourtRoomIdentifier courtRoomIdentifier = event.getCourtRoomIdentifier();
        // Get the CourtLogViewValue from the event
        CourtLogViewValue courtLogViewValue = event.getCaseCourtLogInformation()
            .getCourtLogSubscriptionValue().getCourtLogViewValue();
        
        if (courtLogViewValue != null) {
            // Get the caseType and caseNumber from the CourtLogViewValue
            String caseType = courtLogViewValue.getCaseType();
            Integer caseNumber = courtLogViewValue.getCaseNumber();
        
            if (courtRoomIdentifier != null) {
                LOG.debug("CourtRoomIdentifier found for event: {}, {}", event, courtRoomIdentifier);
                // Check fields are present
                if (caseType != null 
                    && caseNumber != null
                    && courtRoomIdentifier.getCourtId() != null
                    && courtRoomIdentifier.getCourtRoomId() != null) {
                    LOG.debug("All fields present for CaseStatusEvent: {}, {}, {}, {}",
                        caseType, caseNumber, courtRoomIdentifier.getCourtId(),
                        courtRoomIdentifier.getCourtRoomId());
                     
                    // Drill down to the scheduledHearingDao record
                    XhbScheduledHearingDao scheduledHearingDao = 
                        caseStatusDrillDown(courtRoomIdentifier, caseType, caseNumber);
                    
                    // Set values in CourtLogViewValue
                    if (scheduledHearingDao != null) {
                        // Update the CourtLogViewValue with the scheduledHearingId
                        LOG.debug("ScheduledHearing found with ID: {}",
                            scheduledHearingDao.getScheduledHearingId());
                        courtLogViewValue.setScheduledHearingId(
                            scheduledHearingDao.getScheduledHearingId());
                        LOG.debug("CourtLogViewValue updated with scheduledhearingID: {}",
                            scheduledHearingDao.getScheduledHearingId());
                        return courtLogViewValue;
                    }
                }
            }
        }
        return null;
    }
    
    private XhbScheduledHearingDao caseStatusDrillDown(CourtRoomIdentifier courtRoomIdentifier,
        String caseType, Integer caseNumber) {
        // Get XhbCourtSiteDao using courtId
        XhbCourtSiteDao xhbCourtSiteDao = getCourtSiteRepository()
            .findByCourtIdSafe(courtRoomIdentifier.getCourtId()).get(0);
        
        if (xhbCourtSiteDao != null) {
            LOG.debug("Court site found with ID: {}", xhbCourtSiteDao.getCourtId());
            LOG.debug("Finding case using case number: {}{}", caseType, caseNumber);
            
            // Get caseId using the case number from the event
            Optional<XhbCaseDao> xhbCaseDao = getCaseRepository()
                .findByNumberTypeAndCourtSafe(xhbCourtSiteDao.getCourtId(), caseType, caseNumber);
            
            if (xhbCaseDao.isPresent()) {
                LOG.debug("Case found with ID: {}", xhbCaseDao.get().getCaseId());
                
                // Get the hearingId using the caseId and todays date
                Optional<XhbHearingDao> xhbHearingDao = getHearingRepository()
                    .findByCaseIdWithTodaysStartDateSafe(xhbCaseDao.get().getCaseId(),
                        LocalDate.now().atStartOfDay());
                if (!xhbHearingDao.isEmpty()) {
                    LOG.debug("Hearing found with ID: {}", xhbHearingDao.get().getHearingId());
                
                    // Find scheduled hearing record
                    return findScheduledHearing(
                        courtRoomIdentifier.getCourtRoomId(), xhbCourtSiteDao, xhbHearingDao.get());
                }
            }
        }
        return null;
    }
    
    private XhbScheduledHearingDao findScheduledHearing(Integer courtRoomId,
        XhbCourtSiteDao xhbCourtSiteDao, XhbHearingDao xhbHearingDao) {
        
        // Get the SittingId using the courtRoomId and courtSiteId
        List<XhbSittingDao> xhbSittingDaos = getSittingRepository()
            .findByCourtRoomIdAndCourtSiteIdWithTodaysSittingDateSafe(courtRoomId,
                xhbCourtSiteDao.getCourtSiteId(), LocalDate.now().atStartOfDay());
        if (!xhbSittingDaos.isEmpty()) {
            LOG.debug("No. of Sittings found using courtRoomId: {} and courtSiteId: {} is: {}",
                courtRoomId, xhbCourtSiteDao.getCourtSiteId(), xhbSittingDaos.size());
        
            // Loop through the SittingId's to match with hearingId for the xhb_scheduled_hearing record
            for (XhbSittingDao sittingDao : xhbSittingDaos) {
                LOG.debug("Attempting to find ScheduledHearing using sittingId: {} and hearingId: {}",
                    sittingDao.getSittingId(), xhbHearingDao.getHearingId());
                Optional<XhbScheduledHearingDao> scheduledHearingDao = getScheduledHearingRepository()
                    .findBySittingIdAndHearingIdSafe(sittingDao.getSittingId(), 
                        xhbHearingDao.getHearingId());
                
                // Return the xhb_scheduled_hearing record
                if (!scheduledHearingDao.isEmpty()) {
                    return scheduledHearingDao.get();
                }
            }
        }
        return null;
    }
    
    private void processPublicNoticeEvent(PublicNoticeEvent event) {
        // Get Court Room Identifier from the event
        CourtRoomIdentifier courtRoomIdentifier = event.getCourtRoomIdentifier();
        
        // TODO If all public notices for that court room are present in the event then reset them here.
        
        if (courtRoomIdentifier != null) {
            DisplayablePublicNoticeValue[] publicNotices = courtRoomIdentifier.getPublicNotices();
            
            if (publicNotices.length > 0) {
                LOG.debug("No. of DisplayablePublicNoticeValue's: {}", publicNotices.length);
                
                for (DisplayablePublicNoticeValue publicNotice : publicNotices) {
                    // Get the XhbPublicNoticeDao
                    Optional<XhbPublicNoticeDao> xhbPublicNoticeDao = getPublicNoticeRepository()
                        .findByCourtIdAndDefPublicNoticeId(courtRoomIdentifier.getCourtId(),
                            publicNotice.getDefinitivePublicNotice());
                    
                    if (xhbPublicNoticeDao.isPresent()) {
                        LOG.debug("XhbPublicNoticeDao found with ID: {}",
                            xhbPublicNoticeDao.get().getPublicNoticeId());
                        
                        // Get the XhbConfiguredPublicNoticeDao
                        XhbConfiguredPublicNoticeDao xhbConfiguredPublicNoticeDao =
                            getConfiguredPublicNoticeRepository().findByDefinitivePnCourtRoomValueSafe(
                                courtRoomIdentifier.getCourtRoomId(), xhbPublicNoticeDao.get()
                                .getPublicNoticeId()).get(0);
                        
                        if (xhbConfiguredPublicNoticeDao != null) {
                            LOG.debug("XhbConfiguredPublicNoticeDao found with ID: {}",
                                xhbConfiguredPublicNoticeDao.getConfiguredPublicNoticeId());
                            if (publicNotice.isActive()) {
                                // TODO Check if we need to update the inactive displays too 
                                // or if the isActive var is populated
                                xhbConfiguredPublicNoticeDao.setIsActive("1");
                                getConfiguredPublicNoticeRepository().update(xhbConfiguredPublicNoticeDao);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Add a message retrieved from BAIS into the PDDA database.

     * @param courtId The court ID
     * @param messageType The message type
     * @param filename The filename
     * @param clobData The CLOB data
     * @param errorMessage The error message
     * @throws NotFoundException The NotFoundException
     */
    private void createBaisMessage(final Integer courtId, final String messageType,
        final String filename, final String clobData, String errorMessage, String listType)
        throws NotFoundException {

        methodName = "createBaisMessage(" + filename + ")";
        LOG.debug(methodName, LOG_CALLED);

        // Call to createMessageType
        Optional<XhbRefPddaMessageTypeDao> messageTypeDao =
            getPddaMessageHelper().findByMessageType(messageType);
        if (messageTypeDao.isEmpty()) {
            messageTypeDao = PddaMessageUtil.createMessageType(getPddaMessageHelper(), messageType,
                LocalDateTime.now());
        }
        if (messageTypeDao.isEmpty()) {
            // This should never occur
            throw new NotFoundException("Message Type");
        }

        // For daily lists the filename will need to be updated to ensure lists can
        // be picked up in XHB_PDDA_MESSAGE
        // Similarly for public display messages that have came from XHIBIT but
        // originated from CPP
        String updatedFilename = filename;
        if (listType != null && !listType.isEmpty() && filename.startsWith(PDDA_FILENAME_PREFIX)) {
            updatedFilename = getUpdatedFilename(filename, listType);
        } else if (filename.startsWith(PDDA_FILENAME_PREFIX + "_CPD_")) {
            updatedFilename = getUpdatedFilename(filename, PUBLIC_DISPLAY_DOCUMENT_TYPE);
        }

        // Check for a second time that there is not already a duplicate entry in the pdda_message table
        Optional<XhbPddaMessageDao> xhbPddaMessageDao = 
            getPddaMessageHelper().findByCpDocumentName(updatedFilename);
        
        if (!xhbPddaMessageDao.isEmpty()) {
            LOG.warn("The file: {}{}", updatedFilename, 
                " already has an entry in xhb_pdda_message, and therefore a duplicate entry has not been added");
            return;
        }
        
        // Create the clob data for the message
        Optional<XhbClobDao> clob = PddaMessageUtil.createClob(getClobRepository(), clobData);
        Long pddaMessageDataId = clob.isPresent() ? clob.get().getClobId() : null;
        // Call createMessage
        PddaMessageUtil.createMessage(getPddaMessageHelper(), courtId, null,
            messageTypeDao.get().getPddaMessageTypeId(), pddaMessageDataId, null, updatedFilename,
            NO, errorMessage);
    }
    
    /**
     * Applies to lists sent from XHIBIT, update the filename as follows: - Append the text
     * "list_filename = " to the filename - Further append what would be the name of the file were
     * it originating from CPP.

     * @param filename The original filename
     * @return The updated filename
     */
    protected String getUpdatedFilename(String filename, String documentType) {
        StringBuilder cppFilename = new StringBuilder(filename);
        if (PUBLIC_DISPLAY_DOCUMENT_TYPE.equals(documentType)) {
            cppFilename.append(" pd_filename = ");
        } else {
            cppFilename.append(" list_filename = ");
        }
        // We want to retain the last 18 chars of original filename
        int startingPosition = filename.length() - 18;

        cppFilename.append(documentType).append('_')
            .append(filename.substring(startingPosition, filename.length())).append(".xml");
        return cppFilename.toString();
    }


    /**
     * We know this is a list so determine if it is a daily, firm or warned list.

     * @param clobData The CLOB data
     * @return The list type
     */
    protected String getListType(String clobData) {
        if (clobData.contains("<cs:DailyList")) {
            return DAILY_LIST_DOCUMENT_TYPE;
        } else if (clobData.contains("<cs:FirmList")) {
            return "FirmList";
        } else if (clobData.contains("<cs:WarnedList")) {
            return "WarnedList";
        } else {
            LOG.debug("Unknown list type");
            return "Unknown";
        }
    }


    /**
     * A class to validate XHIBIT messages retrieved from BAIS. There should be 6 parts to a valid
     * file from BAIS originating from XHIBIT. 1. PDDA 2. Message Type 3. Batch id 4. Message number
     * in this batch 5. Court ID (crestCourtId) 6. Date and time e.g.
     * PDDA_XPD_1234_1_453_20200101120000

     */
    public static class BaisXhibitValidation extends BaisValidation {

        private static final String PDDA = "PDDA";

        /**
         * There should be 5 parts to a valid file from BAIS originating from XHIBIT. 1. PDDA 2.
         * Message Type 3. Batch id 4. Message number in this batch 5. Court ID (crestCourtId) 6.
         * Date and time

         * @param courtRepository The court repository
         */
        public BaisXhibitValidation(XhbCourtRepository courtRepository) {
            super(courtRepository, false, 6);
        }

        @Override
        public String validateFilename(String filename, PublicDisplayEvent event) {
            return validateFilename(filename, event, false);
        }

        @Override
        @SuppressWarnings("PMD.InefficientStringBuffering")
        public String validateFilename(String filename, PublicDisplayEvent event, boolean isList) {
            String errorMessage = super.validateFilename(filename);
            int expectedMaxErrorMessageSize = 150;
            StringBuilder errorMessages = new StringBuilder(expectedMaxErrorMessageSize);

            if (errorMessage != null) {
                errorMessages.append(errorMessage + NEWLINE);
            }

            // Check the file has the right overall format of 6 parts
            if (!isValidNoOfParts(filename)) {
                errorMessages.append("Invalid filename - No. Of Parts\n");
            }

            // Check Title is right format
            if (!PDDA.equalsIgnoreCase(getFilenamePart(filename, 0))) {
                errorMessages.append("Invalid filename - Title\n");
            }

            // Check the message type of the file is valid
            if (INVALID_MESSAGE_TYPE.equals(getMessageType(getFilenamePart(filename, 1), event))) {
                errorMessages.append("Invalid filename - MessageType\n");
            }

            // Check we have the event from the file contents, and if not null check the
            // crestcourtId.
            // Only applies to XHIBIT PD docs
            if (filename.contains("XPD")) {
                if (event == null) {
                    errorMessages.append("Invalid filename - Invalid event in filecontents\n");
                }
                if (getCourtId(filename, event) == null) {
                    errorMessages.append("Invalid filename - CrestCourtId\n");
                }
            }

            String debugErrorPrefix = filename + " error: {}";
            if (errorMessages.length() > 0) {
                LOG.debug(debugErrorPrefix, errorMessages.toString());
                return errorMessages.toString();
            }

            return null;
        }

        public String getFilenameMessageType(String filenamePart) {
            switch (filenamePart) {
                case "XPD":
                    return "XhibitPublicDisplay";
                case "CPD":
                    return "CpPublicDisplay";
                case "XDL":
                    return "XhibitDailyList";
                case "XWP":
                    return "XhibitWebPage";
                case "CDL":
                    return "CpDailyList";
                case "CFL":
                    return "CpFirmList";
                case "CWL":
                    return "CpWarnedList";
                default:
                    return INVALID_MESSAGE_TYPE;
            }
        }

        @Override
        public Optional<XhbPddaMessageDao> getPddaMessageDao(PddaMessageHelper pddaMessageHelper,
            String filename) {
            return Optional.empty();
        }

        @Override
        public boolean validateTitle(String filename) {
            return PddaSftpValidationUtil.validateTitle(getFilenamePart(filename, 0), PDDA);
        }

        @Override
        public String getMessageType(String filename, PublicDisplayEvent event) {
            if (event != null) {
                return event.getClass().getSimpleName().replace("Event", "");
            } else {
                if (filename.contains("XDL")) {
                    return DAILY_LIST_DOCUMENT_TYPE;
                } else if (filename.contains("XWP")) {
                    return WEB_PAGE_DOCUMENT_TYPE;
                } else if (filename.contains("CDL")) {
                    return DAILY_LIST_DOCUMENT_TYPE;
                } else if (filename.contains("CFL")) {
                    return FIRM_LIST_DOCUMENT_TYPE;
                } else if (filename.contains("CWL")) {
                    return WARNED_LIST_DOCUMENT_TYPE;
                } else if (filename.contains("CPD")) {
                    return PUBLIC_DISPLAY_DOCUMENT_TYPE;
                } else {
                    return INVALID_MESSAGE_TYPE;
                }
            }
        }

        @Override
        public Integer getCourtId(String filename, PublicDisplayEvent event) {
            if (event != null && event.getCourtId() != null) {
                return event.getCourtId();
            } else { // must be a list
                try {
                    String crestCourtId = getFilenamePart(filename, 4);
                    LOG.debug("getCourtId({},{})", filename, event);
                    return getCourtIdFromCrestCourtId(crestCourtId);
                } catch (NumberFormatException e) {
                    LOG.debug("Invalid crestCourtId in filename {}", filename);
                }
            }
            return null;
        }

        @Override
        public PublicDisplayEvent getPublicDisplayEvent(String filename, String fileContents) {
            if (isValidNoOfParts(filename) && validateTitle(filename)) {
                byte[] decodedEvent = PddaSerializationUtils.decodePublicEvent(fileContents);
                return PddaSerializationUtils.deserializePublicEvent(decodedEvent);
            }
            return null;
        }
    }


    /**
     * A class to validate CP messages retrieved from BAIS.
     */
    public static class BaisCpValidation extends BaisValidation {

        private static final String[] POSSIBLETITLES =
            {DAILY_LIST_DOCUMENT_TYPE, "FirmList", "WarnedList", "WebPage",
                PUBLIC_DISPLAY_DOCUMENT_TYPE};

        public BaisCpValidation(XhbCourtRepository courtRespository) {
            super(courtRespository, false, 3);
        }

        @Override
        public String validateFilename(String filename, PublicDisplayEvent event, boolean isList) {
            return validateFilename(filename, event);
        }

        @Override
        @SuppressWarnings("PMD.InefficientStringBuffering")
        public String validateFilename(String filename, PublicDisplayEvent event) {
            String errorMessage = super.validateFilename(filename);
            int expectedMaxErrorMessageSize = 150;
            StringBuilder errorMessages = new StringBuilder(expectedMaxErrorMessageSize);

            if (errorMessage != null) {
                errorMessages.append(errorMessage + NEWLINE);
            }

            // First check file extension is an xml file
            if (!PddaSftpValidationUtil.validateExtension(filename)) {
                errorMessages.append("Invalid filename - Extension\n");
            }

            // Check the file has the right overall format of 3 parts
            if (!isValidNoOfParts(filename)) {
                errorMessages.append("Invalid filename - No. Of Parts\n");
            }

            // Check Title is right format
            if (!validateTitle(filename)) {
                errorMessages.append("Invalid filename - Title\n");
            }

            // Check CrestCourtID is valid and exists in the database
            if (getCourtId(filename, event) == null) {
                errorMessages.append("Invalid filename - CrestCourtId\n");
            }

            // Check dateTime is valid format
            if (!validateDateTime(getFilenamePart(filename, 2))) {
                errorMessages.append("Invalid filename - DateTime\n");
            }

            String debugErrorPrefix = filename + " error: {}";
            if (errorMessages.length() > 0) {
                LOG.debug(debugErrorPrefix, errorMessages.toString());
                return errorMessages.toString();
            }

            return null;
        }

        @Override
        public boolean validateTitle(String filename) {
            return PddaSftpValidationUtil.validateTitle(getFilenamePart(filename, 0),
                POSSIBLETITLES);
        }

        @Override
        public Optional<XhbPddaMessageDao> getPddaMessageDao(PddaMessageHelper pddaMessageHelper,
            String filename) {
            return pddaMessageHelper.findByCpDocumentName(filename);
        }

        @Override
        public String getMessageType(String filename, PublicDisplayEvent event) {
            return getFilenamePart(filename, 0);
        }

        public String getCrestCourtId(String filename) {
            return getFilenamePart(filename, 1);
        }

        @Override
        public Integer getCourtId(String filename, PublicDisplayEvent event) {
            LOG.debug("getCourtId({},{})", filename, event);
            String crestCourtId = getCrestCourtId(filename);
            return getCourtIdFromCrestCourtId(crestCourtId);
        }

        @Override
        public PublicDisplayEvent getPublicDisplayEvent(String filename, String fileContents) {
            // Not required
            return null;
        }
    }

}
