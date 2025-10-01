package org.opencdmp.filetransformer.raid.service.raidfiletransformer;

import org.opencdmp.commonmodels.models.ConfigurationField;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "raid-file-transformer")
public class RaidFileTransformerServiceProperties {
	private String transformerId;
	private boolean useSharedStorage;

	private List<ConfigurationField> configurationFields;
	private List<ConfigurationField> userConfigurationFields;

	public String getTransformerId() {
		return transformerId;
	}

	public void setTransformerId(String transformerId) {
		this.transformerId = transformerId;
	}

	public boolean isUseSharedStorage() {
		return useSharedStorage;
	}

	public void setUseSharedStorage(boolean useSharedStorage) {
		this.useSharedStorage = useSharedStorage;
	}

	public List<ConfigurationField> getConfigurationFields() {
		return configurationFields;
	}

	public void setConfigurationFields(List<ConfigurationField> configurationFields) {
		this.configurationFields = configurationFields;
	}

	public List<ConfigurationField> getUserConfigurationFields() {
		return userConfigurationFields;
	}

	public void setUserConfigurationFields(List<ConfigurationField> userConfigurationFields) {
		this.userConfigurationFields = userConfigurationFields;
	}
}
