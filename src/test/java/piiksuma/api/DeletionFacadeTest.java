package piiksuma.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.Parameterized;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
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


    @Test(expected = PiikInvalidParameters.class)
    public void removeUserValidNeedsPass() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(normalUserNoPass, normalUser);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void removeUserLoggedNeedsPass() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(normalUser, normalUserNoPass);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void removeUserNoPassAtAll() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.removeUser(normalUserNoPass, normalUserNoPass);
    }

    // Database Invalid
    @Test(expected = PiikDatabaseException.class)
    public void removeUserInvalidAtribs() throws PiikInvalidParameters, PiikDatabaseException {
        doThrow(PiikDatabaseException.class).when(mockedUserDao).removeUser(emptyUser);
        deletionFacade.removeUser(emptyUser, adminUser);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void removeUserInvalidAtribsDeleter() throws PiikInvalidParameters, PiikDatabaseException {
        doThrow(PiikDatabaseException.class).when(mockedUserDao).removeUser(emptyUser);
        deletionFacade.removeUser(normalUser, emptyUser);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void removeUserInvalidAtribsDeleterSameInstance() throws PiikInvalidParameters, PiikDatabaseException {
        doThrow(PiikDatabaseException.class).when(mockedUserDao).removeUser(emptyUser);
        deletionFacade.removeUser(emptyUser, emptyUser);
    }


    @Test(expected = PiikDatabaseException.class)
    public void removeUserEmptyAdminDeleter() throws PiikInvalidParameters, PiikDatabaseException {
        emptyAdmin.setType(UserType.administrator);
        doThrow(PiikDatabaseException.class).when(mockedUserDao).removeUser(emptyUser);
        deletionFacade.removeUser(normalUser, emptyAdmin);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void removeUserEmptyNoTypeItself() throws PiikInvalidParameters, PiikDatabaseException {
        emptyAdmin.setType(UserType.administrator);
        doThrow(PiikDatabaseException.class).when(mockedUserDao).removeUser(emptyUser);
        deletionFacade.removeUser(normalUserNoPass, normalUserNoPass);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void removeUserEmptyNoTypeAdmin() throws PiikInvalidParameters, PiikDatabaseException {
        emptyAdmin.setType(UserType.administrator);
        doThrow(PiikDatabaseException.class).when(mockedUserDao).removeUser(emptyUser);
        deletionFacade.removeUser(normalUserNoPass, adminUser);
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

    /* Unfollow User */

    // Invalid Params


    @Test(expected = PiikInvalidParameters.class)
    public void unfollowUserNull001() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.unfollowUser(normalUser, normalUser, null);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void unfollowUserNull010() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.unfollowUser(normalUser, null, normalUser);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void unfollowUserNull011() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.unfollowUser(normalUser, null, null);
    }

    @Test(expected = PiikInvalidParameters.class)
    public void unfollowUserNull100() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.unfollowUser(null, normalUser, normalUser);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void unfollowUserNull101() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.unfollowUser(null, normalUser, null);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void unfollowUserNull110() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.unfollowUser(null, null, normalUser);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void unfollowUserNull111() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.unfollowUser(null, null, null);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void unfollowUserNoPermission() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.unfollowUser(normalUser, normalUser2, normalUser);
    }


    @Test(expected = PiikInvalidParameters.class)
    public void unfollowUserNoPermissionAdmin() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.unfollowUser(normalUser, adminUser, normalUser);
    }

    @Test(expected = PiikInvalidParameters.class)
    public void unfollowUserNoPermissionAdminAdmin() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.unfollowUser(normalUser, adminUser2, adminUser);
    }


    // Invalid Database


    // Valid

    @Test
    public void unfollowUser() throws PiikInvalidParameters, PiikDatabaseException {
        deletionFacade.unfollowUser(normalUser, normalUser2, normalUser);
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