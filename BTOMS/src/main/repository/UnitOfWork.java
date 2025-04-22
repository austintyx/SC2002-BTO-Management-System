package main.repository;

public class UnitOfWork {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final EnquiryRepository enquiryRepository;


    public UnitOfWork() {
        this.userRepository = new UserRepository(); 
        this.projectRepository = new ProjectRepository(this.userRepository);
        this.applicationRepository = new ApplicationRepository();
        this.enquiryRepository = new EnquiryRepository();

    }

    public ProjectRepository getProjectRepository() { return projectRepository; }
    public UserRepository getUserRepository() { return userRepository; }
    public ApplicationRepository getApplicationRepository() { return applicationRepository; }
    public EnquiryRepository getEnquiryRepository() { return enquiryRepository; }


}
