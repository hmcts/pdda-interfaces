package uk.gov.hmcts.pdda.config;

// package uk.gov.hmcts.pdda.config (or similar)
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"PMD", "java:S2696"})
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        if (ctx == null) {
            throw new IllegalStateException("Spring ApplicationContext not initialized yet");
        }
        return ctx.getBean(type);
    }
}

