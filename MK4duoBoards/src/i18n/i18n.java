package i18n;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class i18n extends ResourceBundle{
	private final String BASE_NAME= "i18n/i18n";
	private ResourceBundle bundle;
	
	public i18n(Locale locale) {
		super();
		this.bundle = ResourceBundle.getBundle(BASE_NAME, locale);
	}

	@Override
	public Enumeration<String> getKeys() {
		return bundle.getKeys();
	}

	@Override
	protected Object handleGetObject(String key) {
		if(bundle.containsKey(key)){
			return bundle.getString(key);
		}else{
			return "!"+key+"!";
		}
	}
}
