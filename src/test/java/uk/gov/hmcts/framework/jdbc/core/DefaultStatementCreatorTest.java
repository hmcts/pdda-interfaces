package uk.gov.hmcts.framework.jdbc.core;

import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: DefaultStatementCreator Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author harrism
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultStatementCreatorTest {

    private static final String NOTNULL = "Result is Null";

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private CallableStatement mockCallableStatement;

    @TestSubject
    private final DefaultStatementCreator classUnderTest = new DefaultStatementCreator();

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testSchedulableBadData() {
        String sql = "SELECT 1 FROM DUAL";
        try {
            Mockito.when(mockConnection.prepareStatement(sql)).thenReturn(mockPreparedStatement);
            try (PreparedStatement result = classUnderTest.createPreparedStatement(mockConnection,
                sql, Parameter.getInParameter(0, sql))) {
                assertNotNull(result, NOTNULL);
            }
        } catch (SQLException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    void testGetInstance() {
        StatementCreator result = DefaultStatementCreator.getInstance();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateCallableStatement() {
        String sql = "SELECT 1 FROM DUAL";
        try {
            Mockito.when(mockConnection.prepareCall(sql)).thenReturn(mockCallableStatement);
            try (CallableStatement result = classUnderTest.createCallableStatement(mockConnection,
                sql, Parameter.getInParameter(0, sql))) {
                assertNotNull(result, NOTNULL);
            }
        } catch (SQLException ex) {
            fail(ex.getMessage());
        }

    }

}
