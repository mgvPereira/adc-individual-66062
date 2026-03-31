package pt.unl.fct.di.adc.adc_individual_66062.repository;

import com.google.cloud.datastore.*;
import pt.unl.fct.di.adc.adc_individual_66062.model.Token;
import java.util.*;

public class TokenRepository {
    private final Datastore datastore;

    public TokenRepository() {
        this.datastore = DatastoreOptions.getDefaultInstance().getService();
    }

    public void save(Token token) {
        datastore.put(token.toEntity());
    }

    public Optional<Token> findByTokenId(String tokenId) {
        Key key = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
        Entity entity = datastore.get(key);
        if (entity != null) {
            return Optional.of(Token.fromEntity(entity));
        }
        return Optional.empty();
    }

    public List<Token> findByUsername(String username) {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("Token")
                .setFilter(StructuredQuery.PropertyFilter.eq("username", username))
                .build();

        List<Token> tokens = new ArrayList<>();
        QueryResults<Entity> results = datastore.run(query);
        while (results.hasNext()) {
            tokens.add(Token.fromEntity(results.next()));
        }
        return tokens;
    }

    public List<Token> findAll() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("Token")
                .build();

        List<Token> tokens = new ArrayList<>();
        QueryResults<Entity> results = datastore.run(query);
        while (results.hasNext()) {
            tokens.add(Token.fromEntity(results.next()));
        }
        return tokens;
    }

    public void delete(String tokenId) {
        Key key = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
        datastore.delete(key);
    }

    public void deleteAllForUser(String username) {
        List<Token> tokens = findByUsername(username);
        for (Token token : tokens) {
            delete(token.getTokenId());
        }
    }
}