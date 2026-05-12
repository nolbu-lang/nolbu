package com.cs.bcjis.report.web;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cs.bcjis.comm.BcjisMessageSource;

@Controller
public class ReportCloseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ReportCloseController.class);

    @Resource(name = "bcjisMessageSource")
    private BcjisMessageSource bcjisMessageSource;

    @RequestMapping("/report/reportClose.do")
    public String reportClose(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("reportClose(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("reportClose(Map, ModelMap, HttpServletRequest) - end");
        }
        return "/report/reportClose";
    }
}
