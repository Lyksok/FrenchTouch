package epita.projectse.domain;

public class SearchWrapper {
    private String str;

    public SearchWrapper(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }
    public void setStr(String str) {
        this.str = str;
    }

    public String toJson() {
        return "{\"str\":\"" + str + "\"}";
    }
}
