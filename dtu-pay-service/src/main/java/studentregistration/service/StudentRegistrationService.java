package studentregistration.service;

import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class StudentRegistrationService {

	public static final String STUDENT_REGISTRATION_REQUESTED = "StudentRegistrationRequested";
	public static final String STUDENT_ID_ASSIGNED = "StudentIdAssigned";
	private final MessageQueue queue;
	private final Map<CorrelationId, CompletableFuture<Student>> correlations = new ConcurrentHashMap<>();

	public StudentRegistrationService(MessageQueue q) {
		queue = q;
		queue.addHandler(STUDENT_ID_ASSIGNED, this::handleStudentIdAssigned);
	}

	public Student register(Student s) {
		var correlationId = CorrelationId.randomId();
		correlations.put(correlationId,new CompletableFuture<>());
		Event event = new Event(STUDENT_REGISTRATION_REQUESTED, new Object[] { s, correlationId });
		queue.publish(event);
		return correlations.get(correlationId).join();
	}

	public void handleStudentIdAssigned(Event e) {
		var s = e.getArgument(0, Student.class);
		var correlationid = e.getArgument(1, CorrelationId.class);
		correlations.get(correlationid).complete(s);
	}
}
