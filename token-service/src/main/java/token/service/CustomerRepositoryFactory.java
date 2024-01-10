package token.service;

public class CustomerRepositoryFactory {
    static CustomerRepository repository = null;

    public static CustomerRepository getRepository() {
        if (repository != null) {
            return repository;
        }

        repository = new CustomerRepository();
        return repository;
    }
}
