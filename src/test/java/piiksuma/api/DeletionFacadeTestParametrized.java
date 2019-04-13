package piiksuma.api;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import piiksuma.*;
import piiksuma.api.dao.MultimediaDao;
import piiksuma.api.dao.PostDao;
import piiksuma.api.dao.UserDao;
import piiksuma.exceptions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.*;

@RunWith(value = Parameterized.class)
public class DeletionFacadeTestParametrized extends FacadeTest {


    private User altered;
    private User unaltered;
    private User current;
    private Object expectedException;

    private static DeletionFacade deletionFacade = ApiFacade.getEntrypoint().getDeletionFacade();

    public DeletionFacadeTestParametrized(User altered, User unaltered, User current, Object expectedException) {
        this.altered = altered;
        this.unaltered = unaltered;
        this.current = current;
        this.expectedException = expectedException;
    }

    @Parameters(name = "In: {0}, {1}, {2} - Expected: {3}")
    public static Collection<Object[]> data() {

        ArrayList<Object[]> testParams = new ArrayList<>();

        Object[] invalidParamsUsers = new Object[]{normalUser, adminUser};
        for (Object user : invalidParamsUsers) {
            for (int j = 0; j < 3; j++) {
                for (int i = 0; i < 3; i++) {
                    Object[] elems = new Object[]{normalUser, normalUser2, user, PiikInvalidParameters.class};
                    elems[i] = null;
                    elems[j] = null;
                    testParams.add(elems);
                }
            }

        }


        User emptyAdmin = new User();
        emptyAdmin.setType(UserType.administrator);
        Object[] databaseErrUsers = new Object[]{emptyUser, emptyAdmin};
        for (Object user : databaseErrUsers) {
            for (int i = 0; i < 2; i++) {
                Object[] elems = new Object[]{normalUser, normalUser2, adminUser, PiikDatabaseException.class};
                elems[i] = user;
                testParams.add(elems);
            }
        }

        // altered, unaltered, current

        Object[][] forbidden = {
                {adminUser, normalUser, adminUser2, PiikForbiddenException.class},
                {adminUser, adminUser, adminUser2, PiikForbiddenException.class},
                {normalUser, normalUser2, normalUser2, PiikInvalidParameters.class},
                {adminUser, normalUser2, normalUser2, PiikInvalidParameters.class}
        };
        testParams.addAll(Arrays.asList(forbidden));

        Object[][] valid = {
                {normalUser, normalUser, adminUser, null},
                {adminUser, adminUser2, adminUser, null},
                {normalUser, adminUser, normalUser, null},
                {normalUser, normalUser2, normalUser, null},
        };
        testParams.addAll(Arrays.asList(valid));

        return testParams;
    }


    @BeforeClass
    public static void setUpClass() throws Exception {
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

    @AfterClass
    public static void tearDown() throws Exception {
        ApiFacade.getEntrypoint().setInteractionDao(oldInteractionDao);
        ApiFacade.getEntrypoint().setUserDao(oldUserDao);
        ApiFacade.getEntrypoint().setPostDao(oldPostDao);
        ApiFacade.getEntrypoint().setMultimediaDao(oldMultimediaDao);
        ApiFacade.getEntrypoint().setMessagesDao(oldMessagesDao);
    }

    @Before
    public void setUp() throws Exception {
        doNothing().when(mockedUserDao).removeUser(altered);
    }

    /* USER */

    @Test
    public void removeUserTest() throws PiikException {
        boolean thrown = false;
        if (expectedException == null) {
            ApiFacade.getEntrypoint().setUserDao(mockedUserDao);
            deletionFacade.removeUser(altered, current);
            verify(mockedUserDao, atLeastOnce()).removeUser(altered);
        } else if (expectedException == PiikInvalidParameters.class) {
            if (altered != null && current != null) {
                return;
            }
            try {
                deletionFacade.removeUser(altered, current);
            } catch (PiikInvalidParameters invalid) {
                thrown = true;
            }
            Assert.assertTrue(thrown);

        } else if (expectedException == PiikDatabaseException.class) {
            if (altered.getEmail() != null) {
                return;
            }
            try {
                ApiFacade.getEntrypoint().setUserDao(mockedUserDao);
                doThrow(PiikDatabaseException.class).when(mockedUserDao).removeUser(altered);
                deletionFacade.removeUser(altered, current);
            } catch (PiikDatabaseException db) {
                thrown = true;
            }

            Assert.assertTrue(thrown);
        }
    }

    /* MULTIMEDIA */

    @Test
    public void removeMultimediaTest() throws PiikException {
        boolean thrown = false;
        if (expectedException == null) {
            ApiFacade.getEntrypoint().setMultimediaDao(mockedMultimediaDao);
            Multimedia m = new Multimedia();
            deletionFacade.removeMultimedia(m, current);
            verify(mockedMultimediaDao, atLeastOnce()).removeMultimedia(m);
        } else if (expectedException == PiikInvalidParameters.class) {
            if (altered != null && current != null) {
                return;
            }
            try {
                Multimedia m = new Multimedia();
                deletionFacade.removeMultimedia(altered == null ? null : m, current);
            } catch (PiikInvalidParameters invalid) {
                thrown = true;
            }
            Assert.assertTrue(thrown);

        } else if (expectedException == PiikDatabaseException.class) {
            if (altered.getEmail() != null) {
                return;
            }
            try {
                Multimedia m = new Multimedia();
                ApiFacade.getEntrypoint().setMultimediaDao(mockedMultimediaDao);
                doThrow(PiikDatabaseException.class).when(mockedMultimediaDao).removeMultimedia(m);
                deletionFacade.removeMultimedia(m, current);
            } catch (PiikDatabaseException db) {
                thrown = true;
            }

            Assert.assertTrue(thrown);
        }
    }

    /* UNFOLLOW */

    @Test
    public void unfollowUserTest() throws PiikException {
        boolean thrown = false;
        if (expectedException == null) {
            ApiFacade.getEntrypoint().setUserDao(mockedUserDao);
            deletionFacade.unfollowUser(unaltered, altered, current);
            verify(mockedUserDao, atLeastOnce()).unfollowUser(unaltered, altered);
        } else if (expectedException == PiikInvalidParameters.class) {
            if (altered != null && current != null && unaltered != null) {
                return;
            }
            try {
                deletionFacade.unfollowUser(unaltered, altered, current);
            } catch (PiikInvalidParameters invalid) {
                thrown = true;
            }
            Assert.assertTrue(thrown);

        } else if (expectedException == PiikDatabaseException.class) {
            try {
                ApiFacade.getEntrypoint().setUserDao(mockedUserDao);
                doThrow(PiikDatabaseException.class).when(mockedUserDao).unfollowUser(unaltered, altered);
                deletionFacade.unfollowUser(unaltered, altered, current);
            } catch (PiikDatabaseException db) {
                thrown = true;
            }

            Assert.assertTrue(thrown);
        }
    }

    /* POST */

    @Test
    public void removePostTest() throws PiikException {
        boolean thrown = false;
        Post p = new Post();

        if (expectedException == null) {
            ApiFacade.getEntrypoint().setPostDao(mockedPostDao);
            p.setPostAuthor(altered.getEmail());
            deletionFacade.removePost(p, current);
            verify(mockedPostDao, atLeastOnce()).removePost(p);
        } else if (expectedException == PiikInvalidParameters.class) {
            if (altered != null && current != null) {
                return;
            }
            try {
                deletionFacade.removePost(altered == null ? null : new Post(), current);
            } catch (PiikInvalidParameters invalid) {
                thrown = true;
            }
            Assert.assertTrue(thrown);

        } else if (expectedException == PiikDatabaseException.class) {
            if (altered.getEmail() != null) {
                return;
            }
            try {
                ApiFacade.getEntrypoint().setPostDao(mockedPostDao);
                p.setPostAuthor(altered.getEmail());
                doThrow(PiikDatabaseException.class).when(mockedPostDao).removePost(p);
                deletionFacade.removePost(p, current);
            } catch (PiikDatabaseException db) {
                thrown = true;
            }

            Assert.assertTrue(thrown);
        }
    }

    /* Repost */

    @Ignore
    public void removeRePost() {
        //TODO: Make tests once method is propperly defined
    }


    @Test
    public void removeEvent() throws PiikException {
        Event event = new Event();

        boolean thrown = false;

        if (expectedException == null) {
            ApiFacade.getEntrypoint().setInteractionDao(mockedInteractionDao);
            event.setCreator(altered.getEmail());
            deletionFacade.removeEvent(event, current);
            verify(mockedInteractionDao, atLeastOnce()).removeEvent(event);
        } else if (expectedException == PiikInvalidParameters.class) {
            if (altered != null && current != null) {
                return;
            }
            try {
                deletionFacade.removeEvent(altered == null ? null : event, current);
            } catch (PiikInvalidParameters invalid) {
                thrown = true;
            }
            Assert.assertTrue(thrown);

        } else if (expectedException == PiikDatabaseException.class) {
            if (altered.getEmail() != null) {
                return;
            }
            try {
                ApiFacade.getEntrypoint().setInteractionDao(mockedInteractionDao);
                event.setCreator(altered.getEmail());
                doThrow(PiikDatabaseException.class).when(mockedInteractionDao).removeEvent(event);
                deletionFacade.removeEvent(event, current);
            } catch (PiikDatabaseException db) {
                thrown = true;
            }
            Assert.assertTrue(thrown);
        }
    }

    @Test
    public void removeReaction() throws PiikException {
        Reaction reaction = new Reaction();

        boolean thrown = false;

        if (expectedException == null) {
            ApiFacade.getEntrypoint().setInteractionDao(mockedInteractionDao);
            reaction.setUser(altered);
            deletionFacade.removeReaction(reaction, current);
            verify(mockedInteractionDao, atLeastOnce()).removeReaction(reaction);
        } else if (expectedException == PiikInvalidParameters.class) {
            if (altered != null && current != null) {
                return;
            }
            try {
                deletionFacade.removeReaction(altered == null ? null : reaction, current);
            } catch (PiikInvalidParameters invalid) {
                thrown = true;
            }
            Assert.assertTrue(thrown);

        } else if (expectedException == PiikDatabaseException.class) {
            if (altered.getEmail() != null) {
                return;
            }
            try {
                ApiFacade.getEntrypoint().setInteractionDao(mockedInteractionDao);
                reaction.setUser(altered);
                doThrow(PiikDatabaseException.class).when(mockedInteractionDao).removeReaction(reaction);
                deletionFacade.removeReaction(reaction, current);
            } catch (PiikDatabaseException db) {
                thrown = true;
            }
            Assert.assertTrue(thrown);
        }
    }

    @Test
    public void deleteMessage() throws PiikException {
        Message message = new Message();

        boolean thrown = false;

        if (expectedException == null) {
            ApiFacade.getEntrypoint().setMessagesDao(mockedMessagesDao);
            message.setSender(altered.getEmail());
            deletionFacade.deleteMessage(message, current);
            verify(mockedMessagesDao, atLeastOnce()).deleteMessage(message);
        } else if (expectedException == PiikInvalidParameters.class) {
            if (altered != null && current != null) {
                return;
            }
            try {
                deletionFacade.deleteMessage(altered == null ? null : message, current);
            } catch (PiikInvalidParameters invalid) {
                thrown = true;
            }
            Assert.assertTrue(thrown);

        } else if (expectedException == PiikDatabaseException.class) {
            if (altered.getEmail() != null) {
                return;
            }
            try {
                ApiFacade.getEntrypoint().setMessagesDao(mockedMessagesDao);
                message.setSender(altered.getEmail());
                doThrow(PiikDatabaseException.class).when(mockedMessagesDao).deleteMessage(message);
                deletionFacade.deleteMessage(message, current);
            } catch (PiikDatabaseException db) {
                thrown = true;
            }
            Assert.assertTrue(thrown);
        }
    }


}