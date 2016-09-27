/**
 * 
 */
package org.configframework.rose.admin.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.configframework.rose.admin.component.PageModel;
import org.configframework.rose.admin.dto.RoseConfigDto;
import org.configframework.rose.admin.service.RoseConfigService;
import org.configframework.rose.admin.web.model.ReturnT;
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
@RequestMapping("/config")
public class ConfigController {
	
	@Resource
	private RoseConfigService roseConfigService;
	
	@RequestMapping
	public String index(Model model) {
		return "config/index";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			String appName, String key) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(StringUtils.isBlank(appName) && StringUtils.isBlank(key)){
			resultMap.put("recordsTotal", 0);	
			resultMap.put("recordsFiltered", 0);	
			resultMap.put("data",new ArrayList<RoseConfigDto>());  
			return resultMap;
		}
		appName = toTrim(appName);
		key = toTrim(key);
		int pageIndex = start/length + 1;
		PageModel<RoseConfigDto> pageResult = roseConfigService.pageQueryConfigList(pageIndex, length, appName, key);
		
		resultMap.put("recordsTotal", pageResult.getRecordCount());		// 总记录数
		resultMap.put("recordsFiltered", pageResult.getRecordCount());	// 过滤后的总记录数
		resultMap.put("data", pageResult.getRecords());  					// 分页列表
		return resultMap;
	}
	
	@RequestMapping("/addRose")
	@ResponseBody
	public ReturnT<String> addRose(String appName, String key, String configValue, 
			String configDesc) {
		if (StringUtils.isBlank(appName)) {
			return new ReturnT<String>(500, "请输入“项目名”");
		}
		if (StringUtils.isBlank(key)) {
			return new ReturnT<String>(500, "请输入“配置key”");
		}
		if (StringUtils.isBlank(configDesc)) {
			return new ReturnT<String>(500, "请输入“配置项描述”");
		}
		if(!key.startsWith(appName)){
			return new ReturnT<String>(500, "key必须以项目名开头");
		}
		if(configValue == null){
			configValue = "";
		}
		RoseConfigDto configDto = new RoseConfigDto();
		configDto.setAppName(toTrim(appName));
		configDto.setConfigKey(toTrim(key));
		configDto.setConfigDesc(toTrim(configDesc));
		configDto.setConfigValue(toTrim(configValue));
		configDto.setOperator("sys");
		configDto.setIsDeleted(0);
		boolean result = roseConfigService.addConfig(configDto);
		if(!result){
			return ReturnT.FAIL;
		}
		return ReturnT.SUCCESS;
	}
	
	@RequestMapping("/updateRose")
	@ResponseBody
	public ReturnT<String> updateRose(String key, String configValue) {
		if (StringUtils.isBlank(key)) {
			return new ReturnT<String>(500, "请输入“配置key”");
		}
		key = toTrim(key);
		configValue = toTrim(configValue);
		if(configValue == null){
			configValue = "";
		}
		boolean result = roseConfigService.updateConfig(key, configValue, "sys");
		if(!result){
			return ReturnT.FAIL;
		}
		return ReturnT.SUCCESS;
	}
	
	@RequestMapping("/removeRose")
	@ResponseBody
	public ReturnT<String> removeRose(String key) {
		if (StringUtils.isBlank(key)) {
			return new ReturnT<String>(500, "请输入“配置key”");
		}
		key = toTrim(key);
		boolean result = roseConfigService.deleteConfig(key, "sys");
		if(!result){
			return ReturnT.FAIL;
		}
		return ReturnT.SUCCESS;
	}
	
	private String toTrim(String v){
		if(v != null){
			return v.trim();
		}
		return v;
	}
	
	public static void main(String[] args) {
		String ss = " ssdfs dfdf fsd  ";
		System.out.println(ss);
		System.out.println(ss.trim());
	}

}
