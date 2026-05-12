package com.cs.bcjis.main.web;

import org.apache.log4j.Logger;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(MainController.class);

    @RequestMapping("/main/main.do")
    public String login(Map<String, String> commandMap, ModelMap model, HttpServletRequest request) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("login(Map, ModelMap, HttpServletRequest) - start");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("login(Map, ModelMap, HttpServletRequest) - end");
        }
        return "main/main";
    }
}
