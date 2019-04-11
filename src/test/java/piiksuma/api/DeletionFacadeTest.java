package piiksuma.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class DeletionFacadeTest extends FacadeTest {

    private static DeletionFacade deletionFacade = ApiFacade.getEntrypoint().getDeletionFacade();

    @Before
    public void setUp() throws Exception {
        oldUserDao = ApiFacade.getEntrypoint().getUserDao();
        oldPostDao = ApiFacade.getEntrypoint().getPostDao();
        oldInteractionDao = ApiFacade.getEntrypoint().getInteractionDao();
        oldMultimediaDao = ApiFacade.getEntrypoint().getMultimediaDao();
        oldMessagesDao = ApiFacade.getEntrypoint().getMessagesDao();

        ApiFacade.getEntrypoint().setInteractionDao(mockedInteractionDao);
        ApiFacade.getEntrypoint().setUserDao(mockedUserDao);
        ApiFacade.getEntrypoint().setPostDao(mockedPostDao);
        ApiFacade.getEntrypoint().setMultimediaDao(mockedMultimediaDao);
        ApiFacade.getEntrypoint().setMessagesDao(mockedMessagesDao);
    }

    @After
    public void tearDown() throws Exception {
        ApiFacade.getEntrypoint().setInteractionDao(oldInteractionDao);
        ApiFacade.getEntrypoint().setUserDao(oldUserDao);
        ApiFacade.getEntrypoint().setPostDao(oldPostDao);
        ApiFacade.getEntrypoint().setMultimediaDao(oldMultimediaDao);
        ApiFacade.getEntrypoint().setMessagesDao(oldMessagesDao);
    }

    /* removeUser() */

    // Invalid Params
    @Test(expected = PiikInvalidParameters.class)
    public void removeUserNullNull() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(null, null);
    }

    @Test(expected = PiikInvalidParameters.class)
    public void removeUserCurrentNull() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(normalUser, null);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void removeUserNullNormal() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(null, normalUser);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void removeUserNullAdmin() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(null, adminUser);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void removeUserDistinctAdmin() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(adminUser2, adminUser);
    }


    @Test
    public void removeUserValidNeedsPass() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(normalUserNoPass, normalUser);
    }


    @Test
    public void removeUserLoggedNeedsPass() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(normalUser, normalUserNoPass);
    }


    @Test
    public void removeUserNoPassAtAll() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(normalUserNoPass, normalUserNoPass);
    }


    // Valid
    @Test
    public void removeUserValidNormal() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(normalUser, normalUser);
    }


    @Test
    public void removeUserValidAdmin() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(normalUser, adminUser);
    }


    @Test
    public void removeUserValidAdminItself() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(adminUser, adminUser);
    }


    @Test
    public void unfollowUser() {
    }

    @Test
    public void removeMultimedia() {
    }

    @Test
    public void removePost() {
    }

    @Test
    public void removeRePost() {
    }

    @Test
    public void deleteMessage() {
    }

    @Test
    public void removeEvent() {
    }

    @Test
    public void removeReaction() {
    }
}