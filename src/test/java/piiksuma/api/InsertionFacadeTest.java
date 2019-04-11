package piiksuma.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.api.dao.*;
import piiksuma.exceptions.PiikDatabaseException;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class InsertionFacadeTest {

    @Mock
    UserDao mockedUserDao;
    @Mock
    PostDao mockedPostDao;
    @Mock
    InteractionDao mockedInteractionDao;
    @Mock
    MultimediaDao mockedMultimediaDao;
    @Mock
    MessagesDao mockedMessagesDao;


    private UserDao oldUserDao;
    private PostDao oldPostDao;
    private InteractionDao oldInteractionDao;
    private MultimediaDao oldMultimediaDao;
    private MessagesDao oldMessagesDao;

    private User normalUser = new User("nombre", "id", "mail@gmail.com", "pass", UserType.user);
    private User distinctEmailUser = new User("nombre", "id", "masssil@gmail.com", "pass", UserType.user);
    private User distinctIdUser = new User("nombre", "idssss", "ma@gmail.com", "pass", UserType.user);
    private User normalUser2 = new User("nombre2", "id2", "mail@gmail.com2", "pass", UserType.user);
    private User adminUser = new User("nombre", "id", "mail@gmail.com", "pass", UserType.administrator);
    private User noPassUser = new User("nombre", "id", "mail@gmail.com");
    private User emptyUser = new User();


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
    public void createUser() throws PiikDatabaseException {
        ApiFacade.getEntrypoint().getInsertionFacade().createUser(normalUser);
    }

    @Test
    public void createPost() {

    }
}