package uk.gov.hmcts.pdda.web.publicdisplay.setup.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Cath")
public class CathServlet extends HttpServlet {

    private static final long serialVersionUID = -4477899905926363639L;


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");

        ServletOutputStream output = response.getOutputStream();
        String content = "CaTH";

        output.print(content);
    }

}
