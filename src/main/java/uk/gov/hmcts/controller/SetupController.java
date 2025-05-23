package uk.gov.hmcts.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

@RestController
@Service
@RequestMapping("/setup")
@SuppressWarnings("PMD.SingularField")
public class SetupController {
    
    @Autowired
    private Environment env;

    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(SetupController.class);
    
    public SetupController() {
        super();
        log.info("Env = {}", env);
        InitializationService.getInstance().setEnvironment(env);
    }

    @GetMapping("/DisplaySelectorServlet")
    public ModelAndView displaySelectorServlet(ModelAndView model) {
        log.debug("Display selector servlet redirect");
        return new ModelAndView("redirect:/DisplaySelectorServlet");
    }

    @GetMapping("/Cath")
    public ModelAndView cathServlet(ModelAndView model) {
        log.debug("Cath servlet redirect");
        return new ModelAndView("redirect:/CathServlet");
    }

}
