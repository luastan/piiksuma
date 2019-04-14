package piiksuma.api;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import piiksuma.Achievement;
import piiksuma.Multimedia;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikForbiddenException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class InsertionFacadeTest extends FacadeTest {

    private static InsertionFacade insertionFacade = ApiFacade.getEntrypoint().getInsertionFacade();

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

    @Test
    public void createUser() {
        // TODO: Discuss logic when creating admin users
    }

    @Test(expected = PiikForbiddenException.class)
    public void createAchievementNormalUser() throws PiikException {
        when(mockedUserDao.getUserType(normalUser.getEmail())).thenReturn(normalUser.getType());
        insertionFacade.createAchievement(new Achievement(), normalUser);
    }

    @Test()
    public void createAchievementAdminUser() throws PiikException {
        when(mockedUserDao.getUserType(adminUser.getEmail())).thenReturn(adminUser.getType());
        insertionFacade.createAchievement(new Achievement(), adminUser);
    }


    @Test
    public void addMultimedia() {
        // TODO: Unauthenticaded users cant upload content
        fail();
    }


    @Test
    public void createHashtag() {
        // TODO: Hashtags shoould be created automatically when inserting a Post
        fail();
    }

}