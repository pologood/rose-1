/**
 * 
 */
package org.configframework.rose.admin.web.controller;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.configframework.rose.admin.component.PageModel;
import org.configframework.rose.admin.dto.RoseConfigLogDto;
import org.configframework.rose.admin.service.RoseConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author yuantengkai
 *
 */
@Controller
@RequestMapping("/logconfig")
public class ConfigLogController {
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigLogController.class);
	
	@Resource
	private RoseConfigService roseConfigService;
	
	@RequestMapping
	public String index(Model model, String key) {
		// 默认filterTime
		Calendar todayz = Calendar.getInstance();
		todayz.set(Calendar.HOUR_OF_DAY, 0);
		todayz.set(Calendar.MINUTE, 0);
		todayz.set(Calendar.SECOND, 0);
		model.addAttribute("startTime", todayz.getTime());
		model.addAttribute("endTime", Calendar.getInstance().getTime());
		if(!StringUtils.isBlank(key)){
			model.addAttribute("key", key);
		}
		return "configlog/index";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length, String key, String filterTime) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		key = toTrim(key);
		int pageIndex = start/length + 1;
		// parse param
		Date startTime = null;
		Date endTime = null;
		if (!StringUtils.isBlank(filterTime)) {
			String[] temp = filterTime.split(" - ");
			if (temp != null && temp.length == 2) {
				try {
					startTime = DateUtils.parseDate(temp[0], new String[]{"yyyy-MM-dd HH:mm:ss"});
					endTime = DateUtils.parseDate(temp[1], new String[]{"yyyy-MM-dd HH:mm:ss"});
				} catch (ParseException e) {
					logger.error("parseDate exception,key="+key+",filterTime="+filterTime,e);
				}
			}
		}
		PageModel<RoseConfigLogDto> pageResult = roseConfigService.pageQueryLogList(pageIndex, length, key,startTime, endTime);
		
		resultMap.put("recordsTotal", pageResult.getRecordCount());		// 总记录数
		resultMap.put("recordsFiltered", pageResult.getRecordCount());	// 过滤后的总记录数
		resultMap.put("data", pageResult.getRecords());  					// 分页列表
		return resultMap;
	}
	
	private String toTrim(String v){
		if(v != null){
			return v.trim();
		}
		return v;
	}

}
