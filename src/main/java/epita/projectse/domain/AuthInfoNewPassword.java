package epita.projectse.domain;

public class AuthInfoNewPassword {
    private AuthHash auth_hash;
    private AuthInfo auth_info;
    private String new_password;

    public AuthInfoNewPassword(AuthHash auth_hash, AuthInfo auth_info, String new_password) {
        this.auth_hash = auth_hash;
        this.auth_info = auth_info;
        this.new_password = new_password;
    }

    public AuthHash getAuthHash() {
        return auth_hash;
    }
    public void setAuthHash(AuthHash auth_hash) {
        this.auth_hash = auth_hash;
    }
    public AuthInfo getAuthInfo() {
        return auth_info;
    }
    public void setAuthInfo(AuthInfo auth_info) {
        this.auth_info = auth_info;
    }
    public String getNewPassword() {
        return new_password;
    }
    public void setNewPassword(String new_password) {
        this.new_password = new_password;
    }

    public String toJson(){
        return "{ \"auth_hash\":\"" + auth_hash.getHash() + "\"" +
                ", \"auth_info\": " + auth_info.toJson() +
                ", \"new_password\": \"" + new_password + "\" }";
    }
}
