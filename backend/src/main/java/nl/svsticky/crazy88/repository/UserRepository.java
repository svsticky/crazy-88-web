package nl.svsticky.crazy88.repository;

import nl.svsticky.crazy88.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    @NotNull
    Optional<User> findById(@NotNull Long id);

    @NotNull
    Optional<User> findByKoalaUserId(@NotNull Integer koalaUserId);

}
