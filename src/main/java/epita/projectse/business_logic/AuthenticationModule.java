package epita.projectse.business_logic;

import epita.projectse.data_access.DbAccess;
import epita.projectse.domain.AuthHash;
import epita.projectse.domain.AuthInfo;
import epita.projectse.domain.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationModule implements IPassChecker{
    private DbAccess dbAccess;

    @Override
    public AuthHash authenticateUser(String email, String password) {
        User user = dbAccess.select_user_by_email(email);
        if (user == null) return null;
        AuthInfo authInfo = new AuthInfo(user, password);
        return dbAccess.authenticate(authInfo);
    }
    @Override
    public boolean registerUser(AuthInfo authInfo) {
        // Check if email adress is valid
        if (authInfo.getUser().getEmail() == null || authInfo.getUser().getEmail().isEmpty()) {
            return false;
        }
        // Check if email address valid with regex
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher for the input string
        Matcher matcher = pattern.matcher(authInfo.getUser().getEmail());

        // Check if the input string matches the pattern
        if(!matcher.matches())
            return false;
        return dbAccess.register(authInfo);
    }

    @Override
    public void setDbAccess(DbAccess dbAccess) {
        this.dbAccess = dbAccess;
    }

    @Override
    public DbAccess getDbAccess() {
        return this.dbAccess;
    }
}
