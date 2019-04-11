package piiksuma.api;

import org.mockito.Mock;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.api.dao.*;

import static org.mockito.Mockito.mock;

public class FacadeTest {

    @Mock
    protected UserDao mockedUserDao = mock(UserDao.class);
    @Mock
    protected PostDao mockedPostDao = mock(PostDao.class);
    @Mock
    protected InteractionDao mockedInteractionDao = mock(InteractionDao.class);
    @Mock
    protected MultimediaDao mockedMultimediaDao = mock(MultimediaDao.class);
    @Mock
    protected MessagesDao mockedMessagesDao = mock(MessagesDao.class);


    protected UserDao oldUserDao;
    protected PostDao oldPostDao;
    protected InteractionDao oldInteractionDao;
    protected MultimediaDao oldMultimediaDao;
    protected MessagesDao oldMessagesDao;

    protected User normalUser = new User("nombre", "id", "mail@gmail.com", "pass", UserType.user);
    protected User normalUserNoPass = new User("nombre", "id", "mail@gmail.com");
    protected User distinctEmailUser = new User("nombre", "id", "masssil@gmail.com", "pass", UserType.user);
    protected User distinctIdUser = new User("nombre", "idsss", "ma@gmail.com", "pass", UserType.user);
    protected User normalUser2 = new User("nombre2", "id2", "mail@gmail.com2", "pass", UserType.user);
    protected User adminUser = new User("nombre", "id", "mail@gmail.com", "pass", UserType.administrator);
    protected User adminUser2 = new User("nombre", "id", "lol@gmail.com", "pass", UserType.administrator);
    protected User emptyUser = new User();
    protected User emptyAdmin = new User();
}
