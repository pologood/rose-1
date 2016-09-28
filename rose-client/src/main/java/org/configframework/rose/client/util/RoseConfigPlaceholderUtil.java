/**
 * 
 */
package org.configframework.rose.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.configframework.rose.client.listener.DataChangeListener;
import org.configframework.rose.core.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.core.Ordered;
import org.springframework.util.StringValueResolver;

/**
 * @author yuantengkai 配置完bean后，spring配置中的替换符才会被替换<br/>
 *         <bean id="roseConfigPlaceholderUtil"
 *         class="org.configframework.rose.client.util.RoseConfigPlaceholderUtil"
 *         />
 *         要优先于org.springframework.beans.factory.config.PropertyPlaceholderConfigurer<br/>
 *         如果rose上没配，则会从PropertyPlaceholderConfigurer里加载的去取
 */
public class RoseConfigPlaceholderUtil extends PropertyPlaceholderConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(RoseConfigPlaceholderUtil.class);

	private String placeholderPrefix = "${";
	private String placeholderSuffix = "}";
	
	private String beanName;
	
	private BeanFactory beanFactory;
	
	//key: propertyValue-placeholderKey
	private Map<String, List<BeanData>> propertyMap = new HashMap<String, List<BeanData>>();

	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			Properties props) throws BeansException {
		
		BeanPropertyValueChange valueChangeListener = new BeanPropertyValueChange();
		
		StringValueResolver valueResolver = new PlaceholderStringValueResolver(valueChangeListener);

		BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);

		String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
		if (beanNames != null && beanNames.length > 0) {
			for (String beanName : beanNames) {
				if (!(beanName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
					BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(beanName);
					//cache beanName and fieldName to listen configchange
					MutablePropertyValues mpvs = bd.getPropertyValues();
                    PropertyValue[] pvs = mpvs.getPropertyValues();
                    if (pvs != null) {
                        for (PropertyValue pv : pvs) {
                            Object value = pv.getValue();
                            if (value instanceof TypedStringValue) {
                                String valuePlaceholderKey = ((TypedStringValue) value).getValue();
                                if (valuePlaceholderKey.startsWith(this.placeholderPrefix)
                                        && valuePlaceholderKey.endsWith(this.placeholderSuffix)) {
                                	valuePlaceholderKey = valuePlaceholderKey.substring(placeholderPrefix.length(),
                                			valuePlaceholderKey.length() - placeholderSuffix.length());
                                	
                                    List<BeanData> beanDataList = propertyMap.get(valuePlaceholderKey);
                                    if(beanDataList == null) {
                                        beanDataList = new ArrayList<BeanData>();
                                        propertyMap.put(valuePlaceholderKey, beanDataList);
                                    }
                                    beanDataList.add(new BeanData(beanName, pv.getName()));
                                }
                            }
                        }
                    }
					visitor.visitBeanDefinition(bd);
				}
			}
		}

//		super.processProperties(beanFactoryToProcess, props);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}


	@Override
	public void setBeanName(String name) {
		this.beanName = name;
		super.setBeanName(beanName);
	}



	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
		super.setBeanFactory(beanFactory);
	}

	@Override
	public void setIgnoreUnresolvablePlaceholders(
			boolean ignoreUnresolvablePlaceholders) {
		super.setIgnoreUnresolvablePlaceholders(true);
	}

	private class PlaceholderStringValueResolver implements StringValueResolver {
		
		public PlaceholderStringValueResolver(BeanPropertyValueChange valueChangeListener){
			RoseConfigUtil.addDataChangeListener(Environment.getAppname(), valueChangeListener);
		}

		@Override
		public String resolveStringValue(String strVal) {

			StringBuffer buf = new StringBuffer(strVal);
			boolean start = strVal.startsWith(placeholderPrefix);
			boolean end = strVal.endsWith(placeholderSuffix);
			//TODO 暂不支持嵌套的key,就是value中配的是另外的key
			if (start && end) {
				String placeholderKey = buf.substring(placeholderPrefix.length(),
						buf.length() - placeholderSuffix.length());
				//如果是内部properties的替换符，那么rose中配置了就从rose取；若没配，则通过PropertyPlaceholderConfigurer取内部的
				String value = RoseConfigUtil.getValue(placeholderKey);
				if(value != null){
					buf = new StringBuffer(value);
				}
			}
			return buf.toString();
		}

	}
	
	private class BeanData {
		
		private String beanName;
		
	    private String fieldName;
	    
	    public BeanData(String beanName, String fieldName) {
	        this.beanName = beanName;
	        this.fieldName = fieldName;
	    }

	    public String getBeanName() {
	        return beanName;
	    }

	    public String getFieldName() {
	        return fieldName;
	    }
	    
	    public String toString() {
	        return ToStringBuilder.reflectionToString(this,ToStringStyle.SHORT_PREFIX_STYLE);
		}

	}
	
	private class BeanPropertyValueChange implements DataChangeListener{

		@Override
		public void onChange(String key, String value) {
			List<BeanData> beanDataList = RoseConfigPlaceholderUtil.this.propertyMap.get(key);
            if (beanDataList != null) {
                for(BeanData bd : beanDataList) {
                    Object bean = RoseConfigPlaceholderUtil.this.beanFactory.getBean(bd.getBeanName());
                    if (bean != null) {
                        try {
							BeanUtils.setProperty(bean, bd.getFieldName(), value);
						} catch (Exception e) {
							logger.error("rose key-value changed,BeanUtils.setProperty exception,key:"+key+",value:"+value+bd,e);
						} 
                    }
                }
            }
			
		}
		
	}
	
	public static void main(String[] args) {
		String tmp = "${xxx.ttt}";
		StringBuffer buf = new StringBuffer(tmp);
		System.out.println(buf.substring("${".length(),
						buf.length() - "}".length()));
		System.out.println(buf.toString());
	}
}
