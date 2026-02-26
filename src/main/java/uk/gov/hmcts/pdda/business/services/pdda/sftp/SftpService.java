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
import uk.gov.hmcts.pdda.business.services.pdda.CpDocumentStatus;
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

@SuppressWarnings({"PMD", "squid:S3776"})
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
    protected static final String XHIBIT_LIST_PREFIX = "PDDA_XDL";

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
        XhbSittingRepository sittingRepository,
        XhbScheduledHearingRepository scheduledHearingRepository,
        XhbPublicNoticeRepository publicNoticeRepository,
        XhbConfiguredPublicNoticeRepository configuredPublicNoticeRepository) {
        super(entityManager, xhbConfigPropRepository, environment, pddaMessageHelper,
            clobRepository, courtRepository, courtRoomRepository, courtSiteRepository,
            xhbCaseRepository, hearingRepository, sittingRepository, scheduledHearingRepository,
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
    void retrieveFromBais(SftpConfig config, BaisValidation baisValidation) throws IOException {

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
                    LOG.debug(
                        "Checking current list of files in remote folder before processing current file: {}",
                        filename);
                    List<String> listOfFilesInFolder = getPddaSftpHelperSshj().listFilesInFolder(
                        config.getSshjSftpClient(), config.getActiveRemoteFolder(), baisValidation);

                    // If the filename is not in the list then its already been processed and
                    // deleted previously
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
            } else if (filename.startsWith("WebPage_")) {
                isList = false;
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
        if (event == null) {
            LOG.warn("checkProcessAndSendEvent called with null event");
            return;
        }
        if (event instanceof PddaHearingProgressEvent pddaHearingProgressEvent) {
            LOG.debug("PDDA Hearing Progress Event received from XHIBIT");
            processHearingProgressEvent(pddaHearingProgressEvent);
        } else {
            if (event instanceof CaseStatusEvent caseStatusEvent) {
                LOG.debug("Case Status Event received from XHIBIT");
                // Process the CaseStatusEvent
                CourtLogViewValue updatedCourtLogViewValue =
                    processCaseStatusEvent(caseStatusEvent);
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
        PublicDisplayEvent event, boolean isList, String clobData, String listType)
        throws NotFoundException {
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
        
        // Resolve site early for better breadcrumbs (non-fatal)
        try {
            List<XhbCourtSiteDao> sites = getCourtSiteRepository().findByCourtIdSafe(courtId);
            if (sites != null && !sites.isEmpty()) {
                LOG.debug("HP_EVENT site resolved early: {}", siteCtx(sites.get(0)));
            } else {
                LOG.debug("HP_EVENT site not resolved early: courtId={}", courtId);
            }
        } catch (Exception e) {
            LOG.debug("HP_EVENT site resolution failed early: courtId={} err={}", courtId, e.getMessage());
        }

        // Grep-friendly breadcrumb for correlation (single line)
        LOG.debug(
            "HP_EVENT breadcrumb: courtId={} courtName='{}' caseType={} caseNumber={} courtRoomName='{}'"
                + " hpi={} isCaseActive={}",
            courtId, courtName, caseType, caseNumber, courtRoomName,
            event.getHearingProgressIndicator(), event.getIsCaseActive());

        if (courtId != null && courtName != null && caseType != null && caseNumber != null
            && courtRoomName != null) {
            LOG.debug(
                "All case & court fields for PddaHearingProgressEvent are present: {}{}{}{}{}",
                courtId, courtName, caseType, caseNumber, courtRoomName);

            // Drill down to the scheduledHearingDao record
            XhbScheduledHearingDao scheduledHearingDao =
                hearingProgressDrillDown(courtId, caseType, caseNumber, courtRoomName);

            if (scheduledHearingDao == null) {
                LOG.warn(
                    "HP_EVENT drilldown FAILED: courtId={} courtName='{}' caseType={} caseNumber={} courtRoomName='{}'",
                    courtId, courtName, caseType, caseNumber, courtRoomName);
                return;
            }

            // Get the delay period from xhb_config_prop
            List<XhbConfigPropDao> xhbConfigPropDao =
                getXhbConfigPropRepository().findByPropertyNameSafe(HEARING_PROGRESS_DELAY_MINUTES);

            if (xhbConfigPropDao == null || xhbConfigPropDao.isEmpty()) {
                LOG.warn("HP_EVENT config missing: propertyName={}",
                    HEARING_PROGRESS_DELAY_MINUTES);
                return;
            } else {
                Integer delay = Integer.parseInt(xhbConfigPropDao.get(0).getPropertyValue());
                // Ensure last update date is outside of the delay period to prevent processing a
                // duplicate
                if (scheduledHearingDao != null && scheduledHearingDao.getLastUpdateDate()
                    .plusMinutes(delay).isBefore(LocalDateTime.now())) {
                    LOG.debug("ScheduledHearing found with ID: {}",
                        scheduledHearingDao.getScheduledHearingId());
                    Integer hearingProgressIndicator = event.getHearingProgressIndicator();
                    String caseActive = event.getIsCaseActive();

                    // Update hearingProgressIndicator if its present and not the same as current
                    // value
                    if (hearingProgressIndicator != null && hearingProgressIndicator != 0
                        && !hearingProgressIndicator
                            .equals(scheduledHearingDao.getHearingProgress())) {
                        LOG.debug(
                            "HP_EVENT updating scheduledHearingId={} oldProgress={} newProgress={}",
                            scheduledHearingDao.getScheduledHearingId(),
                            scheduledHearingDao.getHearingProgress(), hearingProgressIndicator);
                        scheduledHearingDao.setHearingProgress(hearingProgressIndicator);
                        LOG.debug("ScheduledHearing hearingProgress set to: {}",
                            hearingProgressIndicator);

                        // Update isCaseActive if its present
                        if (caseActive != null) {
                            scheduledHearingDao.setIsCaseActive(caseActive);
                            LOG.debug("ScheduledHearing isCaseActive set to: {}", caseActive);
                        }
                        getScheduledHearingRepository().update(scheduledHearingDao);
                        LOG.debug("ScheduledHearing with ID: {} updated",
                            scheduledHearingDao.getScheduledHearingId());
                    }
                }
            }
        }
    }

    private XhbScheduledHearingDao hearingProgressDrillDown(Integer courtId, String caseType,
        Integer caseNumber, String courtRoomName) {

        List<XhbCourtSiteDao> sites = getCourtSiteRepository().findByCourtIdSafe(courtId);
        if (sites == null || sites.isEmpty()) {
            LOG.warn("HP_EVENT courtSite NOT FOUND: courtId={}", courtId);
            return null;
        }
        XhbCourtSiteDao site = sites.get(0);
        LOG.debug("HP_EVENT using site: {}", siteCtx(site));
        
        Optional<XhbCaseDao> xhbCaseDao = getCaseRepository()
            .findByNumberTypeAndCourtSafe(site.getCourtId(), caseType, caseNumber);

        if (xhbCaseDao.isEmpty()) {
            LOG.warn("HP_EVENT case NOT FOUND: {} caseType={} caseNumber={}",
                siteCtx(site), caseType, caseNumber);
            return null;
        }

        Optional<XhbHearingDao> xhbHearingDao =
            getHearingRepository().findByCaseIdWithTodaysStartDateSafe(
                xhbCaseDao.get().getCaseId(), LocalDate.now().atStartOfDay());

        if (xhbHearingDao.isEmpty()) {
            LOG.warn("HP_EVENT hearing NOT FOUND: {} caseId={} hearingStartDate={}",
                siteCtx(site), xhbCaseDao.get().getCaseId(), LocalDate.now().atStartOfDay());
            return null;
        }

        LOG.debug("HP_EVENT finding court room: {} courtRoomName='{}'",
            siteCtx(site), courtRoomName);
        
        List<XhbCourtRoomDao> rooms =
            getCourtRoomRepository().findByCourtSiteIdAndCourtRoomNameSafe(site.getCourtSiteId(), courtRoomName);

        if (rooms == null || rooms.isEmpty()) {
            LOG.warn("HP_EVENT courtRoom NOT FOUND (exact match): {} courtRoomName='{}'",
                siteCtx(site), courtRoomName);
            LOG.warn("HP_EVENT roomName normalised: incoming='{}' normalised='{}'",
                courtRoomName, courtRoomName == null ? null : courtRoomName.trim());

            // Diagnostic: list known room names for that site (first 30)
            try {
                List<XhbCourtRoomDao> allRoomsForSite =
                    getCourtRoomRepository().findByCourtSiteIdSafe(site.getCourtSiteId());
                if (allRoomsForSite != null && !allRoomsForSite.isEmpty()) {
                    String sample = allRoomsForSite.stream()
                        .map(r -> String.format("'%s' (trim='%s', upper='%s')",
                            r.getCourtRoomName(),
                            r.getCourtRoomName() == null ? null : r.getCourtRoomName().trim(),
                            r.getCourtRoomName() == null ? null : r.getCourtRoomName().trim().toUpperCase()
                        ))
                        .filter(n -> n != null && !n.isBlank())
                        .distinct()
                        .limit(30)
                        .collect(java.util.stream.Collectors.joining(", "));
                    LOG.warn("HP_EVENT courtRoom available names (first 30): {} names=[{}]",
                        siteCtx(site), sample);
                } else {
                    LOG.warn("HP_EVENT no court rooms exist at all for site: {}", siteCtx(site));
                }
            } catch (Exception e) {
                LOG.warn("HP_EVENT failed listing rooms for site {} due to {}",
                    siteCtx(site), e.getMessage());
            }
            return null;
        }

        XhbCourtRoomDao xhbCourtRoomDao = rooms.get(0);
        LOG.debug("Court room found with ID: {}", xhbCourtRoomDao.getCourtRoomId());

        return findScheduledHearing(xhbCourtRoomDao.getCourtRoomId(), site,
            xhbHearingDao.get());
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
                LOG.debug("CourtRoomIdentifier found for event: {}, {}", event,
                    courtRoomIdentifier);
                // Check fields are present
                if (caseType != null && caseNumber != null
                    && courtRoomIdentifier.getCourtId() != null
                    && courtRoomIdentifier.getCourtRoomId() != null) {
                    LOG.debug("All fields present for CaseStatusEvent: {}, {}, {}, {}", caseType,
                        caseNumber, courtRoomIdentifier.getCourtId(),
                        courtRoomIdentifier.getCourtRoomId());

                    // Drill down to the scheduledHearingDao record
                    XhbScheduledHearingDao scheduledHearingDao =
                        caseStatusDrillDown(courtRoomIdentifier, caseType, caseNumber);

                    // Set values in CourtLogViewValue
                    if (scheduledHearingDao != null) {
                        // Update the CourtLogViewValue with the scheduledHearingId
                        LOG.debug("ScheduledHearing found with ID: {}",
                            scheduledHearingDao.getScheduledHearingId());
                        courtLogViewValue
                            .setScheduledHearingId(scheduledHearingDao.getScheduledHearingId());
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
        List<XhbCourtSiteDao> sites =
            getCourtSiteRepository().findByCourtIdSafe(courtRoomIdentifier.getCourtId());
        if (sites == null || sites.isEmpty()) {
            LOG.warn("CS_EVENT courtSite NOT FOUND: courtId={}", courtRoomIdentifier.getCourtId());
            return null;
        }
        XhbCourtSiteDao site = sites.get(0);
        LOG.debug("CS_EVENT using site: {}", siteCtx(site));

        if (site != null) {
            LOG.debug("Court site found with ID: {}", site.getCourtId());
            LOG.debug("Finding case using case number: {}{}", caseType, caseNumber);

            // Get caseId using the case number from the event
            Optional<XhbCaseDao> xhbCaseDao = getCaseRepository()
                .findByNumberTypeAndCourtSafe(site.getCourtId(), caseType, caseNumber);
            
            if (xhbCaseDao.isEmpty()) {
                LOG.warn("CS_EVENT case NOT FOUND: {} caseType={} caseNumber={}",
                    siteCtx(site), caseType, caseNumber);
                return null;
            }

            if (xhbCaseDao.isPresent()) {
                LOG.debug("Case found with ID: {}", xhbCaseDao.get().getCaseId());

                // Get the hearingId using the caseId and todays date
                Optional<XhbHearingDao> xhbHearingDao =
                    getHearingRepository().findByCaseIdWithTodaysStartDateSafe(
                        xhbCaseDao.get().getCaseId(), LocalDate.now().atStartOfDay());
                if (xhbHearingDao.isEmpty()) {
                    LOG.warn("CS_EVENT hearing NOT FOUND: {} caseId={} hearingStartDate={}",
                        siteCtx(site), xhbCaseDao.get().getCaseId(), LocalDate.now().atStartOfDay());
                    return null;
                }
                if (xhbHearingDao.isPresent()) {
                    LOG.debug("Hearing found with ID: {}", xhbHearingDao.get().getHearingId());

                    // Find scheduled hearing record
                    return findScheduledHearing(courtRoomIdentifier.getCourtRoomId(),
                        site, xhbHearingDao.get());
                } else {
                    LOG.warn("No hearing found for caseId {} on {}", xhbCaseDao.get().getCaseId(),
                        LocalDate.now());
                    return null;
                }
            }
        }
        return null;
    }

    private XhbScheduledHearingDao findScheduledHearing(Integer courtRoomId,
        XhbCourtSiteDao xhbCourtSiteDao, XhbHearingDao xhbHearingDao) {

        if (courtRoomId == null || xhbCourtSiteDao == null || xhbHearingDao == null) {
            LOG.warn(
                "findScheduledHearing called with nulls: courtRoomId={} courtSiteId={} hearingId={}",
                courtRoomId, xhbCourtSiteDao == null ? null : xhbCourtSiteDao.getCourtSiteId(),
                xhbHearingDao == null ? null : xhbHearingDao.getHearingId());
            return null;
        }

        // Get the SittingId using the courtRoomId and courtSiteId
        List<XhbSittingDao> xhbSittingDaos =
            getSittingRepository().findByCourtRoomIdAndCourtSiteIdWithTodaysSittingDateSafe(
                courtRoomId, xhbCourtSiteDao.getCourtSiteId(), LocalDate.now().atStartOfDay());
        if (xhbSittingDaos == null || xhbSittingDaos.isEmpty()) {
            LOG.warn("No sittings found: courtRoomId={} courtSiteId={} sittingDate={}", courtRoomId,
                xhbCourtSiteDao.getCourtSiteId(), LocalDate.now().atStartOfDay());
        } else {
            LOG.debug("No. of Sittings found using courtRoomId: {} and courtSiteId: {} is: {}",
                courtRoomId, xhbCourtSiteDao.getCourtSiteId(), xhbSittingDaos.size());

            // Loop through the SittingId's to match with hearingId for the xhb_scheduled_hearing
            // record
            for (XhbSittingDao sittingDao : xhbSittingDaos) {
                LOG.debug(
                    "Attempting to find ScheduledHearing using sittingId: {} and hearingId: {}",
                    sittingDao.getSittingId(), xhbHearingDao.getHearingId());
                Optional<XhbScheduledHearingDao> scheduledHearingDao =
                    getScheduledHearingRepository().findBySittingIdAndHearingIdSafe(
                        sittingDao.getSittingId(), xhbHearingDao.getHearingId());

                // Return the xhb_scheduled_hearing record
                if (scheduledHearingDao.isPresent()) {
                    return scheduledHearingDao.get();
                }
            }
            String sittingIds = xhbSittingDaos.stream()
                .map(s -> String.valueOf(s.getSittingId()))
                .distinct()
                .collect(java.util.stream.Collectors.joining(","));

            LOG.warn("No scheduled hearing found: courtRoomId={} hearingId={} site={} checkedSittingIds=[{}]",
                courtRoomId, xhbHearingDao.getHearingId(), siteCtx(xhbCourtSiteDao), sittingIds);
        }
        return null;
    }

    /**
     * Handle a {@code PublicNoticeEvent} for a single court room. Behaviour summary:
     * <ul>
     * <li>Extracts the {@code CourtRoomIdentifier} from the {@code event} and logs diagnostic
     * information about its runtime class and classloader.</li>
     * <li>Uses reflection (defensive, read-only) to inspect a declared private field named
     * {@code publicNotices} if present: logs whether the field is present, its array length,
     * component type and up to the first 10 elements (or that the array is null). Any
     * {@link NoSuchFieldException} is logged at DEBUG; other reflection errors are caught and
     * logged as errors so processing can continue.</li>
     * <li>If a {@code CourtRoomIdentifier} is available, reads all configured public notices for
     * that court room via {@code getConfiguredPublicNoticeRepository().findByCourtRoomIdSafe(...)}
     * and sets each configured notice to inactive (writes are performed via the repository's
     * {@code update(...)} method).</li>
     * <li>Finally obtains the {@code DisplayablePublicNoticeValue[]} from the court room identifier
     * and delegates to
     * {@link #setActivePublicNotices(DisplayablePublicNoticeValue[], CourtRoomIdentifier)} to
     * activate any notices that should be active.</li>
     * </ul>
     * Side effects: performs repository reads and updates (mutating configured public notice
     * {@code isActive} flags). The method logs extensively for diagnostics. Exceptions raised
     * during reflection are handled locally and logged; exceptions thrown by repository operations
     * are not explicitly propagated by this method (calling code should assume repository I/O may
     * throw runtime exceptions).

     * @param event the {@code PublicNoticeEvent} to process; its {@code CourtRoomIdentifier} may be
     *        null, in which case only limited diagnostic logging occurs and no repository updates
     *        are performed
     */
    private void processPublicNoticeEvent(PublicNoticeEvent event) {
        // Get Court Room Identifier from the event
        CourtRoomIdentifier courtRoomIdentifier = event.getCourtRoomIdentifier();

        if (courtRoomIdentifier == null) {
            LOG.debug("courtRoomIdentifier is NULL");
        } else {
            Class<?> criClass = courtRoomIdentifier.getClass();
            LOG.debug("CourtRoomIdentifier class: {}", criClass.getName());
            LOG.debug("CourtRoomIdentifier classloader: {}", criClass.getClassLoader());

            try {
                java.lang.reflect.Field f = criClass.getDeclaredField("publicNotices");
                f.setAccessible(true);
                Object arrObj = f.get(courtRoomIdentifier);
                if (arrObj == null) {
                    LOG.debug("=> publicNotices value = NULL");
                } else {
                    int len = java.lang.reflect.Array.getLength(arrObj);
                    Class<?> comp = arrObj.getClass().getComponentType();
                    LOG.debug(
                        "=> publicNotices array length = {}, componentType = {}, component classloader = {}",
                        len, comp.getName(), comp.getClassLoader());
                    // print up to first 10 elements
                    for (int i = 0; i < Math.min(len, 10); i++) {
                        Object el = java.lang.reflect.Array.get(arrObj, i);
                        LOG.debug("    [{}] class={} toString={}", i,
                            el == null ? "null" : el.getClass().getName(), el);
                    }
                }
            } catch (NoSuchFieldException nsf) {
                LOG.debug(
                    "publicNotices field not declared on CourtRoomIdentifier (unexpected): {}",
                    nsf.toString());
            } catch (Throwable t) {
                LOG.error("Error inspecting publicNotices via reflection", t);
            }
        }

        if (courtRoomIdentifier != null) {
            // Get all existing public notices for that court room
            List<XhbConfiguredPublicNoticeDao> xhbConfiguredPublicNoticeDaos =
                getConfiguredPublicNoticeRepository()
                    .findByCourtRoomIdSafe(courtRoomIdentifier.getCourtRoomId());

            if (!xhbConfiguredPublicNoticeDaos.isEmpty()) {
                LOG.debug("No. of XhbConfiguredPublicNoticeDao's to be set to inactive: {}",
                    xhbConfiguredPublicNoticeDaos.size());

                // Loop through all public notices for that court room and set to inactive
                for (XhbConfiguredPublicNoticeDao xhbConfiguredPublicNoticeDao : xhbConfiguredPublicNoticeDaos) {
                    xhbConfiguredPublicNoticeDao.setIsActive("0");
                    getConfiguredPublicNoticeRepository().update(xhbConfiguredPublicNoticeDao);
                    LOG.debug("XhbConfiguredPublicNoticeDao's with ID: {} reset to inactive",
                        xhbConfiguredPublicNoticeDao.getConfiguredPublicNoticeId());
                }
            }
            // Get the public notices from the event
            Class<?> cls = courtRoomIdentifier.getClass();
            LOG.debug("== CourtRoomIdentifier loaded from ==");
            LOG.debug("Class: {}", cls.getName());
            LOG.debug("ClassLoader: {}", cls.getClassLoader());
            LOG.debug("Declared fields:");
            for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
                LOG.debug(" - {}{}{}", f.getName(), " : ", f.getType());
            }

            DisplayablePublicNoticeValue[] publicNotices = courtRoomIdentifier.getPublicNotices();

            if (publicNotices.length > 0) {
                LOG.debug("No. of DisplayablePublicNoticeValue's to be processed: {}",
                    publicNotices.length);
                setActivePublicNotices(publicNotices, courtRoomIdentifier);
            }
        }
    }

    /**
     * Process an array of {@code DisplayablePublicNoticeValue} and set the corresponding configured
     * public notices to active where appropriate. This method is defensive: it returns immediately
     * if {@code publicNotices} is null/empty or if {@code courtRoomIdentifier} (or its id) is null.
     * For each non-null {@code publicNotice} it validates that both the court id and the definitive
     * public notice id are present; missing ids are logged and skipped. For each valid notice it:
     * <ol>
     * <li>looks up the corresponding {@code XhbPublicNoticeDao} via
     * {@code getPublicNoticeRepository()},</li>
     * <li>uses {@link #findConfiguredPublicNotice} to resolve the configured notice for the given
     * court room and public notice,</li>
     * <li>and if the incoming notice indicates it should be active, sets the configured object's
     * {@code isActive} flag and updates it via
     * {@code getConfiguredPublicNoticeRepository().update(...)}.</li>
     * </ol>
     * All repository lookups and update attempts are logged. Exceptions thrown while updating a
     * single configured public notice are caught and logged; processing then continues for the
     * remaining notices. This method performs side effects (repository updates) and does not throw
     * checked exceptions.

     * @param publicNotices array of displayable public notice values to process; may be null/empty
     * @param courtRoomIdentifier identifies the target court room; if null (or its id is null) no
     *        processing occurs
     */
    private void setActivePublicNotices(DisplayablePublicNoticeValue[] publicNotices,
        CourtRoomIdentifier courtRoomIdentifier) {

        if (publicNotices == null || publicNotices.length == 0) {
            LOG.debug("No public notices to process for courtRoomId={}",
                courtRoomIdentifier == null ? "null" : courtRoomIdentifier.getCourtRoomId());
            return;
        }

        if (courtRoomIdentifier == null || courtRoomIdentifier.getCourtRoomId() == null) {
            LOG.warn(
                "Cannot set active public notices: courtRoomIdentifier or courtRoomId is null");
            return;
        }

        for (DisplayablePublicNoticeValue publicNotice : publicNotices) {
            if (publicNotice == null) {
                LOG.debug("Skipping null DisplayablePublicNoticeValue for courtRoomId={}",
                    courtRoomIdentifier.getCourtRoomId());
                continue;
            }

            Integer courtId = courtRoomIdentifier.getCourtId();
            Integer definitivePublicNotice = publicNotice.getDefinitivePublicNotice();

            if (courtId == null || definitivePublicNotice == null) {
                LOG.warn(
                    "Skipping publicNotice due to missing ids (courtId={}, definitivePublicNotice={})",
                    courtId, definitivePublicNotice);
                continue;
            }

            // Find the XhbPublicNoticeDao; repository returns Optional already
            Optional<XhbPublicNoticeDao> xhbPublicNoticeDaoOpt = getPublicNoticeRepository()
                .findByCourtIdAndDefPublicNoticeIdSafe(courtId, definitivePublicNotice);

            xhbPublicNoticeDaoOpt.ifPresentOrElse(xhbPublicNoticeDao -> {
                Integer publicNoticeId = xhbPublicNoticeDao.getPublicNoticeId();
                LOG.debug(
                    "XhbPublicNoticeDao found with publicNoticeId={} (courtId={}, definitivePublicNotice={})",
                    publicNoticeId, courtId, definitivePublicNotice);

                // Find configured public notice defensively (uses the helper you had)
                Optional<XhbConfiguredPublicNoticeDao> configuredOpt =
                    findConfiguredPublicNotice(getConfiguredPublicNoticeRepository(),
                        courtRoomIdentifier, xhbPublicNoticeDaoOpt, LOG);

                configuredOpt.ifPresentOrElse(configured -> {
                    try {
                        LOG.debug("XhbConfiguredPublicNoticeDao found with ID: {}",
                            configured.getConfiguredPublicNoticeId());

                        // Only update if incoming flag says active; avoid unnecessary writes
                        if (publicNotice.getIsActive()) {
                            configured.setIsActive("1");
                            getConfiguredPublicNoticeRepository().update(configured);
                            LOG.debug("Set configuredPublicNoticeId={} to active",
                                configured.getConfiguredPublicNoticeId());
                        } else {
                            LOG.debug(
                                "Public notice indicates not active; no update performed for"
                                    + "configuredPublicNoticeId={}",
                                configured.getConfiguredPublicNoticeId());
                        }
                    } catch (Exception e) {
                        LOG.error("Failed to update configured public notice (configuredId={}): {}",
                            configured.getConfiguredPublicNoticeId(), e.getMessage(), e);
                        // swallow or rethrow depending on desired behaviour; here we continue
                        // processing others
                    }
                }, () -> LOG.info(
                    "ConfiguredPublicNotice not found for publicNoticeId={} courtRoomId={}",
                    publicNoticeId, courtRoomIdentifier.getCourtRoomId()));

            }, () -> {
                LOG.info("XhbPublicNoticeDao not found (courtId={}, definitivePublicNotice={})",
                    courtId, definitivePublicNotice);
            });
        }
    }


    /**
     * Find the configured public notice DAO for a given court room and public notice. This helper
     * validates inputs and performs a repository lookup:
     * <ul>
     * <li>If {@code courtRoomIdentifier} or its courtRoomId is null the method returns
     * {@code Optional.empty()} and logs a warning.</li>
     * <li>If {@code xhbPublicNoticeDaoOpt} is empty or its contained {@code publicNoticeId} is null
     * the method returns {@code Optional.empty()} and logs a warning.</li>
     * <li>Otherwise it queries
     * {@code repo.findByDefinitivePnCourtRoomValueSafe(courtRoomId, publicNoticeId)}. If the query
     * returns no results it returns {@code Optional.empty()}; if multiple results are returned it
     * logs a warning and returns the first element wrapped in {@code Optional}.</li>
     * </ul>
     * This method does not mutate data; it only performs a read and returns the found DAO wrapped
     * in an {@code Optional}. All important decision points are logged via the supplied logger.

     * @param repo repository used to look up configured public notices; must not be null
     * @param courtRoomIdentifier identifies the court room to search for; its courtRoomId must not
     *        be null
     * @param xhbPublicNoticeDaoOpt optional containing the matched XhbPublicNoticeDao (expected
     *        non-empty)
     * @param log logger used for warnings and debug information
     * @return an {@code Optional<XhbConfiguredPublicNoticeDao>} containing the first matching
     *         configured public notice, or {@code Optional.empty()} if none could be resolved
     */

    Optional<XhbConfiguredPublicNoticeDao> findConfiguredPublicNotice(
        XhbConfiguredPublicNoticeRepository repo, CourtRoomIdentifier courtRoomIdentifier,
        Optional<XhbPublicNoticeDao> xhbPublicNoticeDaoOpt, Logger log) {

        // Validate inputs early
        if (courtRoomIdentifier == null || courtRoomIdentifier.getCourtRoomId() == null) {
            log.warn("Cannot look up ConfiguredPublicNotice: courtRoomIdentifier or ID is null");
            return Optional.empty();
        }

        if (xhbPublicNoticeDaoOpt.isEmpty()) {
            log.warn(
                "Cannot look up ConfiguredPublicNotice: XhbPublicNoticeDao Optional is empty (courtRoomId={})",
                courtRoomIdentifier.getCourtRoomId());
            return Optional.empty();
        }

        Integer courtRoomId = courtRoomIdentifier.getCourtRoomId();
        Integer publicNoticeId = xhbPublicNoticeDaoOpt.get().getPublicNoticeId();

        if (publicNoticeId == null) {
            log.warn(
                "Cannot look up ConfiguredPublicNotice: publicNoticeId is null (courtRoomId={})",
                courtRoomId);
            return Optional.empty();
        }

        log.debug("Looking up XhbConfiguredPublicNoticeDao for courtRoomId={}, publicNoticeId={}",
            courtRoomId, publicNoticeId);

        List<XhbConfiguredPublicNoticeDao> results =
            repo.findByDefinitivePnCourtRoomValueSafe(courtRoomId, publicNoticeId);

        if (results == null || results.isEmpty()) {
            log.info("No XhbConfiguredPublicNoticeDao found for courtRoomId={}, publicNoticeId={}",
                courtRoomId, publicNoticeId);
            return Optional.empty();
        }

        // Defensive: if multiple results appear, log it but return the first safely
        if (results.size() > 1) {
            log.warn("Multiple XhbConfiguredPublicNoticeDao results found for courtRoomId={},"
                + "publicNoticeId={}. Returning first.", courtRoomId, publicNoticeId);
        }

        return Optional.ofNullable(results.get(0));
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

        // Check for a second time that there is not already a duplicate entry in the pdda_message
        // table
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

        // Set the default valid not processed status
        String status = CpDocumentStatus.VALID_NOT_PROCESSED.status;

        // Check if we need to set this document to On Hold if its an XHIBIT list
        if (filename.contains(XHIBIT_LIST_PREFIX)) {
            LOG.debug(
                "Setting document status to On Hold for: {} as it is a list recieved from XHIBIT",
                filename);
            status = CpDocumentStatus.ON_HOLD.status;
        }

        // Call createMessage
        PddaMessageUtil.createMessage(getPddaMessageHelper(), courtId, null,
            messageTypeDao.get().getPddaMessageTypeId(), pddaMessageDataId, null, updatedFilename,
            NO, errorMessage, status);
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
            return FIRM_LIST_DOCUMENT_TYPE;
        } else if (clobData.contains("<cs:WarnedList")) {
            return WARNED_LIST_DOCUMENT_TYPE;
        } else {
            LOG.debug("Unknown list type");
            return "Unknown";
        }
    }
    
    private String siteCtx(XhbCourtSiteDao site) {
        if (site == null) {
            return "courtSite=null";
        }
        return String.format(
            "courtSiteId=%s courtId=%s crestCourtId=%s siteName='%s' displayName='%s' shortName='%s' code=%s obsInd=%s",
            site.getCourtSiteId(),
            site.getCourtId(),
            site.getCrestCourtId(),
            site.getCourtSiteName(),
            site.getDisplayName(),
            site.getShortName(),
            site.getCourtSiteCode(),
            site.getObsInd()
        );
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
            {DAILY_LIST_DOCUMENT_TYPE, FIRM_LIST_DOCUMENT_TYPE, WARNED_LIST_DOCUMENT_TYPE,
                WEB_PAGE_DOCUMENT_TYPE, PUBLIC_DISPLAY_DOCUMENT_TYPE};

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

            if (errorMessages.length() > 0) {
                String debugErrorPrefix = filename + " error: {}";
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
