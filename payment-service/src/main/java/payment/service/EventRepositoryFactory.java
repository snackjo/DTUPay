package payment.service;

public class EventRepositoryFactory {
    private static EventRepository repository;

    public static EventRepository getRepository() {
        if (repository != null) {
            return repository;
        }

        repository = new EventRepository();
        return repository;
    }
}
