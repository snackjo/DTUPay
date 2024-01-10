package account.service;

public class AccountRepositoryFactory {
    private static AccountRepository repository;

    public static AccountRepository getRepository() {
        if (repository != null) {
            return repository;
        }

        repository = new AccountRepository();
        return repository;
    }
}
