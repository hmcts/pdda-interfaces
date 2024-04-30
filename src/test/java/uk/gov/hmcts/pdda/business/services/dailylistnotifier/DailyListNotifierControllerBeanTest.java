package uk.gov.hmcts.pdda.business.services.dailylistnotifier;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * <p>
 * Title: DailyListNotifierControllerBean Test.
 * </p>
 * <p>
 * Description: Unit tests for the DailyListNotifierControllerBean class
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Nathan Toft
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DailyListNotifierControllerBeanTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String FALSE = "Result is not False";
    private static final String TRUE = "Result is not True";

    @Mock
    private EntityManager mockEntityManager;

    @InjectMocks
    private final DailyListNotifierControllerBean classUnderTest =
        new DailyListNotifierControllerBean(mockEntityManager);


}
