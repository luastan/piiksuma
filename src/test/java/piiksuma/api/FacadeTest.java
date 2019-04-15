package piiksuma.api;

import org.mockito.Mock;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.api.dao.*;

import static org.mockito.Mockito.mock;

public class FacadeTest {

    @Mock
    protected static UserDao mockedUserDao = mock(UserDao.class);
    @Mock
    protected static PostDao mockedPostDao = mock(PostDao.class);
    @Mock
    protected static InteractionDao mockedInteractionDao = mock(InteractionDao.class);
    @Mock
    protected static MultimediaDao mockedMultimediaDao = mock(MultimediaDao.class);
    @Mock
    protected static MessagesDao mockedMessagesDao = mock(MessagesDao.class);


    protected static UserDao oldUserDao;
    protected static PostDao oldPostDao;
    protected static InteractionDao oldInteractionDao;
    protected static MultimediaDao oldMultimediaDao;
    protected static MessagesDao oldMessagesDao;

    protected static User normalUser = new User("nombre", "id", "mail@gmail.com", "pass", UserType.user);
    protected static User normalUserNoPass = new User("nombre", "id", "mail@gmail.com");
    protected static User distinctEmailUser = new User("nombre", "id", "masssil@gmail.com", "pass", UserType.user);
    protected static User distinctIdUser = new User("nombre", "idsss", "ma@gmail.com", "pass", UserType.user);
    protected static User normalUser2 = new User("nombre2", "id2", "mail@gmail.com2", "pass", UserType.user);
    protected static User adminUser = new User("admin", "id", "mail@gmail.com", "pass", UserType.administrator);
    protected static User adminUser2 = new User("admin2", "id", "lol@gmail.com", "pass", UserType.administrator);
    protected static User emptyUser = new User();
    protected static User emptyAdmin = new User();
}
