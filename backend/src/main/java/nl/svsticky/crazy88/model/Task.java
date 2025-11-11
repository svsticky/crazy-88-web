package nl.svsticky.crazy88.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Entity
public class Task {

    @Id
    @GeneratedValue
    @NotNull
    private Long id;
    @NotNull
    private String description;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> completedBy;

}
