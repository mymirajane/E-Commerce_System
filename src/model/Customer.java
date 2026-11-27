package model; 
/**
 * class extends the User class
 * representing for customer and saving email and mobile
 * Customer
 */
public class Customer extends User {
    private String userEmail;
    private String userMobile;

   /**
    * Constructs a customer object
    * @param userId
    * @param userName
    * @param userPassword
    * @param userRegisterTime
    * @param userRole
    * @param userEmail
    * @param userMobile
    */
    public Customer(String userId, String userName, String userPassword, String userRegisterTime, String userRole, String userEmail, String userMobile) {
        super(userId, userName, userPassword, userRegisterTime, userRole);
        this.userEmail = userEmail;
        this.userMobile = userMobile;
    }
    /**
     * Default constructor
     */
     public Customer() {
        super();
        this.userEmail = "";
        this.userMobile = "";
     }

     public String getUserEmail() {
        return userEmail;
     }
     public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
     }

     public String getUserMobile() {
        return userMobile;
     }
     public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
     }

   /**   (non-Javadoc)
    * Returns the customer information as a formatted string.
    * @return String in JSON-like format
    */
     @Override 
     public String toString() {
        return "{\"user_id\":\"" + getUserId() + "\", \"user_name\":\"" + getUserName() + "\", \"user_password\":\"" + getUserPassword() + "\", \"user_register_time\":\"" + getUserRegisterTime() + "\", \"user_role\":\"" + getUserRole() + "\", \"user_email\":\"" + userEmail + "\", \"user_mobile\":\"" + userMobile + "\"}";
     } 
}
