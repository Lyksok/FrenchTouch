package epita.projectse.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Admin")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown properties
public class Admin extends User{
    @JsonProperty
    @Id
    private long id;
    @JsonProperty
    private long user_id;

    public Admin() {
    }

    public Admin(long user_id) {
        this.id = -1;
        this.user_id = user_id;
    }
    public Admin(Admin admin) {
        this.id = admin.id;
        this.user_id = admin.user_id;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getUser_id() {
        return user_id;
    }
    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ",user_id=" + user_id +
                '}';
    }

    public String toJson() {
        return "{" +
                "\"id\":" + id +
                ",\"user_id\":" + user_id +
                '}';
    }
}