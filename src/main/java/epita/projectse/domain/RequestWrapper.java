package epita.projectse.domain;

public class RequestWrapper {
    private String auth_hash;
    private String objectJson;

    public RequestWrapper(String auth_hash, String objectJson) {
        this.auth_hash = auth_hash;
        this.objectJson = objectJson;
    }

    public String getAuth_hash() {
        return auth_hash;
    }
    public void setAuth_hash(String auth_hash) {
        this.auth_hash = auth_hash;
    }
    public String getObjectJson() {
        return objectJson;
    }
    public void setObjectJson(String objectJson) {
        this.objectJson = objectJson;
    }
    public String toJson(){
        return "{\"auth_hash\":\""+auth_hash+"\",\"obj\":"+objectJson+"}";
    }
}
