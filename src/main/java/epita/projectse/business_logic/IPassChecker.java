package epita.projectse.business_logic;

import epita.projectse.data_access.DbAccess;
import epita.projectse.domain.AuthHash;
import epita.projectse.domain.AuthInfo;
import epita.projectse.domain.User;

import java.util.Map;

public interface IPassChecker {
        boolean registerUser(AuthInfo authInfo);
        AuthHash authenticateUser(String email, String password);
        void setDbAccess(DbAccess dbAccess);
        DbAccess getDbAccess();
}
