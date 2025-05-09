package uk.gov.hmcts.pdda.web.publicdisplay.setup.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.gov.hmcts.pdda.business.services.pdda.cath.CathOAuth2Helper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("PMD.LawOfDemeter")
@WebServlet("/Cath")
public class CathServlet extends HttpServlet {

    private static final long serialVersionUID = -4477899905926363639L;

    private CathOAuth2Helper cathOAuth2Helper;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (ServletOutputStream output = response.getOutputStream()) {

            output.print(documentWrapper(bold("CaTH Token: ") + getToken() + eol() + linebreak()
                + bold("Date/Time: ") + now() + eol() + linebreak()
                + hrefLink("Home", "\\DisplaySelectorServlet") + eol()));
        }
    }

    private String getToken() {
        return getCathOAuth2Helper().getAccessToken();
    }

    private CathOAuth2Helper getCathOAuth2Helper() {
        if (cathOAuth2Helper == null) {
            cathOAuth2Helper = new CathOAuth2Helper();
        }
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
