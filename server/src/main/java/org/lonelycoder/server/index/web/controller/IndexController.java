package org.lonelycoder.server.index.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Author : lihaoquan
 * Description :
 */
@Controller
@RequestMapping("/index")
public class IndexController {

    @RequestMapping(method=RequestMethod.GET)
    public String index() {

        System.out.println("��ӭ��¼��į��ũ����!");

        return "index";
    }
}