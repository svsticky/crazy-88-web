package nl.svsticky.crazy88.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "users") // user is a reserved keyword in some SQL databases
public class User {

    @Id
    @GeneratedValue
    @NotNull
    private Long id;
    @NotNull
    private Integer koalaUserId;
    @NotNull
    private UserRole role;
    @NotNull
    private String name;

    @NotNull
    public Long getId() {
        return this.id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @NotNull
    public Integer getKoalaUserId() {
        return this.koalaUserId;
    }

    public void setKoalaUserId(@NotNull Integer koalaUserId) {
        this.koalaUserId = koalaUserId;
    }

    @NotNull
    public UserRole getRole() {
        return this.role;
    }

    public void setRole(@NotNull UserRole role) {
        this.role = role;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

}
