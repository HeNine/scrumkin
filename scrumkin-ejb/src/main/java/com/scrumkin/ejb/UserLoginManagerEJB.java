package com.scrumkin.ejb;


import com.scrumkin.api.UserLoginManager;
import com.scrumkin.jpa.LoginTokenEntity;
import com.scrumkin.jpa.UserEntity;
import org.mindrot.jbcrypt.BCrypt;
import sun.rmi.runtime.Log;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;

/**
 */
@Stateless
public class UserLoginManagerEJB implements UserLoginManager {

    @PersistenceContext(unitName = "shtorya_main_PU")
    private EntityManager em;

    private Random r = new Random();

    @Override
    public String getLoginToken(String username, String password) {
        TypedQuery<UserEntity> getUserQuery = em.createNamedQuery("getUserByUsername", UserEntity.class);
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

    private String generateToken() {
        TypedQuery<Boolean> uniqueCheckQuery = em.createNamedQuery("LoginToken.isUnique",
                Boolean.class);

        StringBuilder token;

        do {
            token = new StringBuilder();
            // 512b = 64B = 16 * 4B = 16 * int
            for (int i = 0; i < 16; i++) {
                token.append(Integer.toHexString(r.nextInt()));
            }

            uniqueCheckQuery.setParameter("token", token.toString());
        } while (!uniqueCheckQuery.getSingleResult());

        return token.toString();
    }
}
