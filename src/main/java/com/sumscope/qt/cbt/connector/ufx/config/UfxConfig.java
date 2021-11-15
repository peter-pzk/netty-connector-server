/**
* @Description: TODO
* @author limeng
* @date 2020年6月27日
*/
package com.sumscope.qt.cbt.connector.ufx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UfxConfig {
	@Value("${ufx.login.username}")
	private String username;
	@Value("${ufx.login.password}")
	private String password;
	@Value("${ufx.t2sdk-config-path}")
	private String t2sdkConfigPath ;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getT2sdkConfigPath() {
		return t2sdkConfigPath;
	}

	public void setT2sdkConfigPath(String t2sdkConfigPath) {
		this.t2sdkConfigPath = t2sdkConfigPath;
	}
}
