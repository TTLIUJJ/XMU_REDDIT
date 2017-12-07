package com.ackerman.reddit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

@Controller
public class ErrorController {
    private static Logger logger = Logger.getLogger("ErrorController");

    @ExceptionHandler()
    public String error(){
        return "error";
    }

    @RequestMapping(value = "/exception", method = {RequestMethod.GET, RequestMethod.POST})
    public void exception(Model model,
                          HttpSession session){
        model.addAttribute("msg", session.getAttribute("msg"));
        session.removeAttribute("msg");
    }
}
