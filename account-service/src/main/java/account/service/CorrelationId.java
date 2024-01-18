package account.service;

import java.util.UUID;

import lombok.Value;

// @author Emil
@Value
public class CorrelationId {
	String id;

	public static CorrelationId randomId() {
		return new CorrelationId(UUID.randomUUID().toString());
	}
}
