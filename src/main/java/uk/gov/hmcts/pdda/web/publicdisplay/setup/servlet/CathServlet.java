package uk.gov.hmcts.pdda.web.publicdisplay.setup.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathOAuth2Helper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("PMD.LawOfDemeter")
public class CathServlet extends HttpServlet {

    private static final long serialVersionUID = -4477899905926363639L;
    private static final Logger LOG = LoggerFactory.getLogger(CathServlet.class);

    private final CathOAuth2Helper cathOAuth2Helper;

    public CathServlet() {
        super(); // Explicit call to HttpServlet constructor
        throw new UnsupportedOperationException("CathOAuth2Helper must be injected");
    }
    
    // JUnit constructor
    public CathServlet(CathOAuth2Helper cathOAuth2Helper) {
        super();
        this.cathOAuth2Helper = cathOAuth2Helper;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        LOG.debug("CaTH Servlet");

        response.setContentType("text/html;charset=UTF-8");

        String token = getToken();

        LOG.debug("Output Stats");
        try (ServletOutputStream output = response.getOutputStream()) {

            output.print(documentWrapper(
                bold("CaTH Token: ") + token + eol() + linebreak() + bold("Date/Time: ") + now()
                    + eol() + linebreak() + hrefLink("Home", "\\DisplaySelectorServlet") + eol()));
        } catch (IOException ex) {
            LOG.error("Error: {}", ex.getMessage()); 
        }
    }

    private String getToken() {
        LOG.debug("getToken()");
        try {
            return getCathOAuth2Helper().getAccessToken();
        } catch (Exception ex) {
            return "ERROR: " + ex.getMessage();
        }
    }

    private CathOAuth2Helper getCathOAuth2Helper() {
        return cathOAuth2Helper;
    }

    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    private String bold(String text) {
        return "<b>" + text + "</b>";
    }

    private String linebreak() {
        return "<br>";
    }

    private String eol() {
        return "\r\n";
    }

    private String hrefLink(String text, String url) {
        return "<a href=" + url + ">" + text + "</a>";
    }

    private String documentWrapper(String body) {
        return "<!DOCTYPE html>" + eol() + "<html>" + eol() + "<body>" + eol() + body + "</body>"
            + eol() + "</html>";
    }


}
