/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.lightweight.model.branding;

/**
 * Provides information about the application branding.
 * 
 * @author Tomasz Jędrzejewski
 */
public class BrandingModel {
	public static final String BRANDING_APP_NAME = "opentrans.branding.appName";
	public static final String BRANDING_APP_VERSION = "opentrans.branding.appVersion";
	public static final String BRANDING_APP_AUTHOR = "opentrans.branding.appAuthor";
	public static final String BRANDING_APP_WEBSITE = "opentrans.branding.appWebsite";
	public static final String BRANDING_APP_COPYRIGHT = "opentrans.branding.appCopyright";
	public static final String BRANDING_APP_LICENSE = "opentrans.branding.appLicense";
	public static final String BRANDING_PROMO_IMAGE = "opentrans.branding.promo.image";
	public static final String BRANDING_PROMO_BAR_COLOR = "opentrans.branding.promo.barColor";
	public static final String BRANDING_PROMO_TEXT_COLOR = "opentrans.branding.promo.textColor";
	
	private String appName;
	private String appVersion;
	private String appAuthor;
	private String appWebsite;
	private String appCopyright;
	private String appLicense;
	private String promoImagePath;
	private String promoBarColor;
	private String promoTextColor;

	public BrandingModel() {
		this.appName = System.getProperty(BRANDING_APP_NAME, "Application");
		this.appVersion = System.getProperty(BRANDING_APP_VERSION, "0.1.0");
		this.appAuthor = System.getProperty(BRANDING_APP_AUTHOR, "Author name");
		this.appWebsite = System.getProperty(BRANDING_APP_WEBSITE, "http://www.example.com");
		this.appCopyright = System.getProperty(BRANDING_APP_NAME, "Author name");
		this.appLicense = System.getProperty(BRANDING_APP_NAME, "Sample license");
		this.promoImagePath = System.getProperty(BRANDING_PROMO_IMAGE, "org/invenzzia/helium/gui/splash.png");
		this.promoBarColor = System.getProperty(BRANDING_PROMO_BAR_COLOR, "#0000dd");
		this.promoTextColor = System.getProperty(BRANDING_PROMO_TEXT_COLOR, "#0000dd");
	}
	
	public String getApplicationName() {
		return this.appName;
	}

	public String getApplicationVersion() {
		return this.appVersion;
	}

	public String getApplicationAuthor() {
		return this.appAuthor;
	}

	public String getApplicationWebsite() {
		return this.appWebsite;
	}

	public String getApplicationCopyright() {
		return this.appCopyright;
	}

	public String getApplicationLicense() {
		return this.appLicense;
	}

	public String getPromoImagePath() {
		return this.promoImagePath;
	}
	
	public String getPromoBarColor() {
		return this.promoBarColor;
	}
	
	public String getPromoTextColor() {
		return this.promoTextColor;
	}
}