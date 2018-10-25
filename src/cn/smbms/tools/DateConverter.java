package cn.smbms.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 日期转换器
 * @author monkey
 *
 */
@Component("dataConverter")
public class DateConverter implements Converter<String, Date> {
	private SimpleDateFormat[] sdfs = {
		 new SimpleDateFormat("yyyy-MM-dd"),	  
		new SimpleDateFormat("MM/dd/yyyy"),
		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
		new SimpleDateFormat("yyyy*MM*dd")
	};
	@Override
	public Date convert(String arg0) {
		// TODO Auto-generated method stub
		for(SimpleDateFormat sdf :sdfs) {
		   try {
			     return sdf.parse(arg0);
		   }catch(Exception ex) {
			   continue;
		   }
		
		}
		return null;
	}

}




