package pt.unl.fct.di.adc.adc_individual_66062.repository;

import com.google.cloud.datastore.*;
import pt.unl.fct.di.adc.adc_individual_66062.model.User;
import java.util.*;

public class UserRepository {
    private final Datastore datastore;

    public UserRepository() {
        this.datastore = DatastoreOptions.getDefaultInstance().getService();
    }

    public void save(User user) {
        datastore.put(user.toEntity());
    }

    public Optional<User> findByUsername(String username) {
        Key key = datastore.newKeyFactory().setKind("User").newKey(username);
        Entity entity = datastore.get(key);
        if (entity != null) {
            return Optional.of(User.fromEntity(entity));
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("User")
                .build();

        List<User> users = new ArrayList<>();
        QueryResults<Entity> results = datastore.run(query);
        while (results.hasNext()) {
            users.add(User.fromEntity(results.next()));
        }
        return users;
    }

    public void delete(String username) {
        Key key = datastore.newKeyFactory().setKind("User").newKey(username);
        datastore.delete(key);
    }

    public void update(User user) {
        save(user);
    }
}