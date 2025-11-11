package nl.svsticky.crazy88.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
public class Team {

    @Id
    @GeneratedValue
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @ManyToOne
    @JoinColumn(name = "super_team_id")
    @NotNull
    private SuperTeam superTeam;

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
    public SuperTeam getSuperTeam() {
        return this.superTeam;
    }

    public void setSuperTeam(@Nullable SuperTeam superTeam) {
        this.superTeam = superTeam;
    }

}
