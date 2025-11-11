package nl.svsticky.crazy88.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
public class SuperTeam {

    @Id
    @GeneratedValue
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @Nullable
    private String color;

    @NotNull
    public Long getId() {
        return this.id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Nullable
    public String getColor() {
        return this.color;
    }

    public void setColor(@Nullable String color) {
        this.color = color;
    }

}
