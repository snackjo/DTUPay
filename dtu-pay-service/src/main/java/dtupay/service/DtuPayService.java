package dtupay.service;


import messaging.MessageQueue;

public class DtuPayService {
    public static final String STUDENT_REGISTRATION_REQUESTED = "StudentRegistrationRequested";
    public static final String CUSTOMER_REGISTERED = "StudentIdAssigned";
    private final MessageQueue queue;

    public DtuPayService(MessageQueue q) {
        queue = q;
        /*queue.addHandler(STUDENT_ID_ASSIGNED, this::handleStudentIdAssigned);*/
    }
    public String register(Customer customer) {
        return "fuckoff";
    }


}
