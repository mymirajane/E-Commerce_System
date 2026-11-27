package model;
/**
 * class extends the User class
 * representing for admin
 * Admin
 */

public class Admin extends User{
/**
 * Constructs an admin object.
 * @param userId
 * @param userName
 * @param userPassword
 * @param userRegisterTime
 * @param userRole
 */
    public Admin(String userId, String userName, String userPassword, String userRegisterTime, String userRole) {
        super(userId, userName, userPassword, userRegisterTime, userRole);
    }
/**
 * Default construct
 */
    public Admin() {
        super();
        setUserRole("admin");

    }

    /**    (non-Javadoc)
     * Returns the admin's attributes as a formatted string.
     * @return String in JSON-like format
     */
    @Override 
     public String toString() {
        return "{\"user_id\":\"" + getUserId() + "\", \"user_name\":\"" + getUserName() + "\", \"user_password\":\"" + getUserPassword() + "\", \"user_register_time\":\"" + getUserRegisterTime() + "\", \"user_role\":\"" + getUserRole() + "\"}";

     } 
}
