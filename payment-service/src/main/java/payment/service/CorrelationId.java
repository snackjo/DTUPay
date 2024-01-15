package payment.service;

import lombok.Value;

import java.util.UUID;

@Value
public class CorrelationId {
	String id;

	public static CorrelationId randomId() {
		return new CorrelationId(UUID.randomUUID().toString());
	}
}
