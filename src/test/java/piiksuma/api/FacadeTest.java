package piiksuma.api;

import org.mockito.Mock;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.api.dao.*;

public class FacadeTest {

    @Mock
    protected UserDao mockedUserDao;
    @Mock
    protected PostDao mockedPostDao;
    @Mock
    protected InteractionDao mockedInteractionDao;
    @Mock
    protected MultimediaDao mockedMultimediaDao;
    @Mock
    protected MessagesDao mockedMessagesDao;


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
