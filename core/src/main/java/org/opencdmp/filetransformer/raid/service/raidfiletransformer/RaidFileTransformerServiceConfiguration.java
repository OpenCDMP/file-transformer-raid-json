package org.opencdmp.filetransformer.raid.service.raidfiletransformer;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RaidFileTransformerServiceProperties.class})
public class RaidFileTransformerServiceConfiguration {
	private final RaidFileTransformerServiceProperties raidFileTransformerServiceProperties;

	public RaidFileTransformerServiceConfiguration(RaidFileTransformerServiceProperties raidFileTransformerServiceProperties) {
		this.raidFileTransformerServiceProperties = raidFileTransformerServiceProperties;
	}

	public RaidFileTransformerServiceProperties getRaidFileTransformerServiceProperties() {
		return raidFileTransformerServiceProperties;
	}
}
