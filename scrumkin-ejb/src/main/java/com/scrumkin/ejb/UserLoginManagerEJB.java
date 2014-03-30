package com.scrumkin.ejb;


import com.scrumkin.api.UserLoginManager;
import com.scrumkin.jpa.LoginTokenEntity;
import com.scrumkin.jpa.UserEntity;
import org.mindrot.jbcrypt.BCrypt;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;
import java.util.Random;

/**
 */
@Stateless
public class UserLoginManagerEJB implements UserLoginManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;

    private Random r = new Random();

    @Override
    public String getLoginToken(String username, String password) {
        TypedQuery<UserEntity> getUserQuery = em.createNamedQuery("UserEntity.getUserByUsername", UserEntity.class);
        getUserQuery.setParameter("username", username);

        UserEntity user = getUserQuery.getSingleResult();

        if (user != null && BCrypt.checkpw(password, user.getPassword())) {

            String token = generateToken();

            LoginTokenEntity loginToken = new LoginTokenEntity();
            loginToken.setToken(token);
            loginToken.setUser(user);
            em.persist(loginToken);

            return token;

        } else {
            return null;
        }
    }

    @Override
    public void doLogout(String token) {
        Query deleteQuery = em.createNamedQuery("LoginToken.doDeleteToken");
        deleteQuery.setParameter("token", token);

        deleteQuery.executeUpdate();
    }

    @Override
    public UserEntity getUserFromToken(String token) {
        TypedQuery<LoginTokenEntity> getTokenQuery = em.createNamedQuery("LoginToken.getByToken",
                LoginTokenEntity.class);
        getTokenQuery.setParameter("token", token);

        try {
            LoginTokenEntity loginToken = getTokenQuery.getSingleResult();

            return loginToken.getUser();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    private boolean isUniqueToken(String token) {
        TypedQuery<LoginTokenEntity> getTokensQuery = em.createNamedQuery("LoginToken.getByToken",
                LoginTokenEntity.class);
        getTokensQuery.setParameter("token", token);

        return getTokensQuery.getResultList().size() == 0;
    }

    private String generateToken() {

        StringBuilder token;

        do {
            token = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                token.append(String.format("%08x", r.nextInt()));
            }

        } while (!isUniqueToken(token.toString()));

        return token.toString();
    }
}
