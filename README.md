# SC2002 BTO Management System

## About
SC2002 AY24/25 Sem 2 Group Project - BTO Management System (BTOMS)

BTO Management System is a system for applicants and HDB staffs to view,
apply and manage for BTO projects.

Overview of the System:  
- The system will act as a centralized hub for all applicants and HDB staffs
- All users will need to login to this hub using their Singpass account.
  - User ID will be their NRIC, that starts with S or T, followed by 7-digit number and ends with another letter
  - Assume all users use the default password, which is password.
  - A user can change password in the system.
  - Additional Information about the user: Age and Marital Status  
_ A user list can be initiated through a file uploaded into the system at initialization.  

## User Types and Capabilities
- Applicant  
  - View Visible Projects  
  - Apply for Project  
  - View Applied Project  
  - Request Withdrawal  
  - Manage Enquiry (Create, edit, delete)  
- HDBOfficer  
  - View Visible Projects and Projects being handled
  - Register to handle a Project
  - View Registration Status
  - Book flat
  - Manage Enquiries (View and Reply)
  - Generate Receipt of Booking Details
  - Apply for Project  
- HDBManager
  - Manage Projects (Create, edit, delete, toggle visibility)
  - View All Projects
  - View Projects being managed
  - Manage Officer Registration
  - Manage Applications
  - Manage Withdrawal Requests
  - Generate report of list of applicants
  - Manage Enquiries (View and Reply)  

All users will be able to change their password upon logging in.  

## How to run
Download the .jar file, and run using the following code in terminal  
`java -jar BTOMS.jar`
