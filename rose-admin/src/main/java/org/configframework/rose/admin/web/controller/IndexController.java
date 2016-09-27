/**
 * 
 */
package org.configframework.rose.admin.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yuantengkai
 *
 */
@Controller
public class IndexController {
	
	@RequestMapping("/")
	public String index(Model model) {
		return "redirect:config/";
	}

}
