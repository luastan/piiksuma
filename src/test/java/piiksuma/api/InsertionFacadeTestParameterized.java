package piiksuma.api;

import javafx.geometry.Pos;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import piiksuma.*;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikException;
import piiksuma.exceptions.PiikForbiddenException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(value = Parameterized.class)
public class InsertionFacadeTestParameterized extends FacadeTest {

    private static InsertionFacade insertionFacade = ApiFacade.getEntrypoint().getInsertionFacade();

    private User altered;
    private User unaltered;
    private User current;
    private Object expectedException;

    public InsertionFacadeTestParameterized(User altered, User unaltered, User current, Object expectedException) {
        this.altered = altered;
        this.unaltered = unaltered;
        this.current = current;
        this.expectedException = expectedException;
    }

    @Parameters(name = "Expected: {3} - In: {0}, {1}, {2}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> tests = new ArrayList<>();


        // altered, unaltered, current

        for (Object user : new Object[]{normalUser, adminUser}) {
            Object[][] invalid = {
                    {null, null, null, PiikInvalidParameters.class},
                    {null, null, user, PiikInvalidParameters.class},
                    {null, user, null, PiikInvalidParameters.class},
                    {null, user, user, PiikInvalidParameters.class},
                    {user, null, null, PiikInvalidParameters.class},
                    {user, null, user, PiikInvalidParameters.class},
                    {user, user, null, PiikInvalidParameters.class},
            };
            tests.addAll(Arrays.asList(invalid));
        }


        Object[][] forbidden = {
                {adminUser, normalUser, adminUser2, PiikForbiddenException.class},
                {adminUser, adminUser, adminUser2, PiikForbiddenException.class},
                {normalUser, normalUser2, normalUser2, PiikForbiddenException.class},
                {adminUser, normalUser2, normalUser2, PiikForbiddenException.class}
        };
        tests.addAll(Arrays.asList(forbidden));

        Object[][] valid = {
                {normalUser, normalUser, adminUser, null},
                {adminUser, adminUser2, adminUser, null},
                {normalUser, adminUser, normalUser, null},
                {normalUser, normalUser2, normalUser, null},
        };
        tests.addAll(Arrays.asList(valid));

        return tests;
    }

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
        if (current != null && unaltered != null && altered != null) {
            if (current.getEmail() != null && unaltered.getEmail() != null && altered.getEmail() != null) {
                when(mockedUserDao.getUserType(current.getEmail())).thenReturn(current.getType());
                when(mockedUserDao.getUserType(unaltered.getEmail())).thenReturn(unaltered.getType());
                when(mockedUserDao.getUserType(altered.getEmail())).thenReturn(altered.getType());
            }
        }
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
    public void followUser() throws PiikException {

        if (expectedException == null) {
            insertionFacade.followUser(unaltered, altered, current);
        } else if (expectedException == PiikInvalidParameters.class) {
            try {
                insertionFacade.followUser(unaltered, altered, current);
                fail("Expected exception PiikInvalidParameters");
            } catch (PiikInvalidParameters ignore) {
            }
        } else if (expectedException == PiikForbiddenException.class) {
            try {
                insertionFacade.followUser(unaltered, altered, current);

            } catch (PiikForbiddenException ignore) {
            }
        }
    }

    @Test
    public void createPost() throws PiikException {
        Post post = new Post();
        if (expectedException == null) {
            post.setPostAuthor(altered.getEmail());
            insertionFacade.createPost(post, current);
        } else if (expectedException == PiikInvalidParameters.class) {
            if (altered != null && current != null) {
                return;
            }
            try {
                insertionFacade.createPost(altered == null ? null : post, current);
                fail();
            } catch (PiikInvalidParameters ignore) {
            }

        } else if (expectedException == PiikForbiddenException.class) {
            post.setPostAuthor(altered.getEmail());
            try {
                insertionFacade.createPost(post, current);
                fail();
            } catch (PiikForbiddenException ignore) {

            }

        }
    }

    @Test
    public void archivePost() throws PiikException {
        Post post = new Post();
        if (expectedException == null) {
            post.setPostAuthor(altered.getEmail());
            insertionFacade.archivePost(post, altered, current);
        } else if (expectedException == PiikInvalidParameters.class) {
            if (altered != null && current != null) {
                return;
            }
            try {
                insertionFacade.archivePost(altered == null ? null : post, altered, current);
                fail();
            } catch (PiikInvalidParameters ignore) {

            }
        } else if (expectedException == PiikForbiddenException.class) {
            post.setPostAuthor(altered.getEmail());
            try {
                insertionFacade.archivePost(post, altered, current);
                fail();
            } catch (PiikForbiddenException ignore) {
            }
        }
    }

    @Test
    public void newTicket() throws PiikException {
        Ticket ticket = new Ticket();
        if (expectedException == null) {
            ticket.setUser(altered.getEmail());
            insertionFacade.newTicket(ticket, current);
        } else if (expectedException == PiikInvalidParameters.class) {
            if (altered != null && current != null) {
                return;
            }
            try {
                insertionFacade.newTicket(altered == null ? null : ticket, current);
                fail();
            } catch (PiikInvalidParameters ignore) {
            }
        } else if (expectedException == PiikForbiddenException.class) {
            ticket.setUser(current.getEmail());
            try {
                ticket.setUser(altered.getEmail());
                insertionFacade.newTicket(ticket, current);
                fail();
            } catch (PiikForbiddenException ignore) {

            }
        }
    }

    @Test
    public void replyTicket() throws PiikException {
        Ticket ticket = new Ticket();
        Message message = new Message();
        if (expectedException == null) {
            ticket.setUser(altered.getEmail());
            message.setSender(unaltered.getEmail());
            insertionFacade.replyTicket(ticket, message, current);
        } else if (expectedException == PiikInvalidParameters.class) {
            try {
                insertionFacade.replyTicket(altered == null ? null : ticket, unaltered == null ? null : message, current);
                fail();
            } catch (PiikInvalidParameters ignore) {
            }
        } else if (expectedException == PiikForbiddenException.class) {
            ticket.setUser(current.getEmail());
            try {
                ticket.setUser(altered.getEmail());
                message.setSender(unaltered.getEmail());
                insertionFacade.replyTicket(ticket, message, current);
                fail();
            } catch (PiikForbiddenException ignore) {

            }
        }
    }

    @Test
    public void sendMessage() throws PiikException {
        Message message = new Message();
        if (expectedException == null) {
            message.setSender(altered.getEmail());
            insertionFacade.sendMessage(message, current);
        } else if (expectedException == PiikInvalidParameters.class) {
            if (altered != null && current != null) {
                return;
            }
            try {
                insertionFacade.sendMessage(altered == null ? null : message, current);
                fail();
            } catch (PiikInvalidParameters ignore) {

            }
        } else if (expectedException == PiikForbiddenException.class) {
            message.setSender(altered.getEmail());
            try {
                insertionFacade.sendMessage(message, current);
                fail();
            } catch (PiikForbiddenException ignore) {
            }
        }
    }

    @Test
    public void createNotification() throws PiikException {
        Notification notification = new Notification();
        if (expectedException == null) {
            return;
        } else if (expectedException == PiikInvalidParameters.class) {
            if (altered != null && current != null) {
                return;
            }
            try {
                insertionFacade.createNotification(altered == null ? null : notification, current);
                fail();
            } catch (PiikInvalidParameters ignore) {

            }
        } else if (expectedException == PiikForbiddenException.class) {
            return;
        }
    }

    @Test
    public void notifyUser() throws PiikException {
        Notification notification = new Notification();
        if (expectedException == null) {
            insertionFacade.notifyUser(notification, altered, current);
        } else if (expectedException == PiikInvalidParameters.class) {
            try {
                insertionFacade.notifyUser(unaltered == null ? null : notification, altered, current);
                fail();
            } catch (PiikInvalidParameters ignore) {

            }
        } else if (expectedException == PiikForbiddenException.class) {
            try {
                insertionFacade.notifyUser(notification, altered, current);
                fail();
            } catch (PiikForbiddenException ignore) {

            }
        }
    }
}