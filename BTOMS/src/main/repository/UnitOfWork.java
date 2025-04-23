package main.repository;

/**
 * The {@code UnitOfWork} class encapsulates and manages access to the core repositories
 * used within the application, providing a single point of coordination for data operations.
 * <p>
 * This class follows the Unit of Work design pattern, ensuring that all repositories
 * can be accessed together and their lifecycles are managed consistently.
 * </p>
 */
public class UnitOfWork {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final EnquiryRepository enquiryRepository;

    /**
     * Constructs a new {@code UnitOfWork} instance and initializes all repositories.
     * <p>
     * The {@code ProjectRepository} is initialized with a reference to the {@code UserRepository}.
     * </p>
     */
    public UnitOfWork() {
        this.userRepository = new UserRepository(); 
        this.projectRepository = new ProjectRepository(this.userRepository);
        this.applicationRepository = new ApplicationRepository();
        this.enquiryRepository = new EnquiryRepository();
    }

    /**
     * Returns the {@link ProjectRepository} managed by this unit of work.
     *
     * @return the project repository
     */
    public ProjectRepository getProjectRepository() { return projectRepository; }

    /**
     * Returns the {@link UserRepository} managed by this unit of work.
     *
     * @return the user repository
     */
    public UserRepository getUserRepository() { return userRepository; }

    /**
     * Returns the {@link ApplicationRepository} managed by this unit of work.
     *
     * @return the application repository
     */
    public ApplicationRepository getApplicationRepository() { return applicationRepository; }

    /**
     * Returns the {@link EnquiryRepository} managed by this unit of work.
     *
     * @return the enquiry repository
     */
    public EnquiryRepository getEnquiryRepository() { return enquiryRepository; }
}
