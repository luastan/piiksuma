-- ==================================================== DATA USERS =====================================================
-- Piiuser
INSERT INTO piiuser (id, email, name, pass, gender, description, home,
                     postalcode, province, country, city, birthdate, registrationdate)
VALUES ('Alvaru', 'alvaru@gmail.com', 'Álvaro Goldar Dieste',
        'fwOqspRFP2Z+P1m57Lso4A==$a2vujM97TD31jBCJc/AXAqya7dcVhrnNn/2zyrQKJCPKj38MvI84+FD+ZZ4Tj7ksPp/ONqdWopu4IUt8BkDccw==',
        'M', 'I send work to my minions', 'Home', '36112', 'Pontevedra', 'España', 'A toxa', '1999-04-12',
        CURRENT_DATE);

INSERT INTO piiuser (id, email, name, pass, gender, description, home,
                     postalcode, province, country, city, birthdate, registrationdate)
VALUES ('Danilo', 'danilo@gmail.com', 'Daniel Martínez Fernández',
        'fwOqspRFP2Z+P1m57Lso4A==$a2vujM97TD31jBCJc/AXAqya7dcVhrnNn/2zyrQKJCPKj38MvI84+FD+ZZ4Tj7ksPp/ONqdWopu4IUt8BkDccw==W',
        'M', 'FXML Designer', 'My house', '231', 'Pontevedra', 'España', 'Vigo', '1999-12-1', CURRENT_DATE);

INSERT INTO piiuser (id, email, name, pass, gender, description, home,
                     postalcode, province, country, city, birthdate, registrationdate)
VALUES ('Cardamis', 'cardamis@gmail.com', 'Francisco Javier Cardama Santiago',
        'fwOqspRFP2Z+P1m57Lso4A==$a2vujM97TD31jBCJc/AXAqya7dcVhrnNn/2zyrQKJCPKj38MvI84+FD+ZZ4Tj7ksPp/ONqdWopu4IUt8BkDccw==W',
        'M', 'Im going to make a son out of Tanenbaum', 'La Isla', '12341', 'Pontevedra', 'España', 'THE Island',
        '1999-04-12', CURRENT_DATE);

INSERT INTO piiuser (id, email, name, pass, gender, description, home,
                     postalcode, province, country, city, birthdate, registrationdate)
VALUES ('Marpin', 'marpin@gmail.com', 'Marcos López Lamas',
        'fwOqspRFP2Z+P1m57Lso4A==$a2vujM97TD31jBCJc/AXAqya7dcVhrnNn/2zyrQKJCPKj38MvI84+FD+ZZ4Tj7ksPp/ONqdWopu4IUt8BkDccw==W',
        'M', 'Worst programmer Galicia', 'My team friends <3', '39218', 'Pontevedra', 'España', 'Vigo', '2019-04-12',
        CURRENT_DATE);

INSERT INTO piiuser (id, email, name, pass, gender, description, home,
                     postalcode, province, country, city, birthdate, registrationdate)
VALUES ('Piblo', 'piblo@gmail.com', 'Pablo Martínez Gómez',
        'fwOqspRFP2Z+P1m57Lso4A==$a2vujM97TD31jBCJc/AXAqya7dcVhrnNn/2zyrQKJCPKj38MvI84+FD+ZZ4Tj7ksPp/ONqdWopu4IUt8BkDccw==W',
        'M', 'I like my roomMate', 'VIGO', '39218', 'Pontevedra', 'España', 'Vigo', '1999-05-05',
        CURRENT_DATE);

INSERT INTO piiuser (id, email, name, pass, gender, description, home,
                     postalcode, province, country, city, birthdate, registrationdate)
VALUES ('Saul', 'saul@gmail.com', 'Saúl Gay Barral',
        'fwOqspRFP2Z+P1m57Lso4A==$a2vujM97TD31jBCJc/AXAqya7dcVhrnNn/2zyrQKJCPKj38MvI84+FD+ZZ4Tj7ksPp/ONqdWopu4IUt8BkDccw==W',
        'M', 'Hacker and freelancer', 'Cows', '39218', 'Lugo', 'Cow-ntry', 'CowLugo', '1996-11-27',
        CURRENT_DATE);


-- Admin
INSERT INTO administrator (id)
VALUES ('Alvaru');

-- Associated account
INSERT INTO associatedaccount (id, token, usr)
VALUES ('aaaa', 'tokenaaaa', 'Alvaru');

INSERT INTO associatedaccount (id, token, usr)
VALUES ('aaab', 'tokenaaaab', 'Danilo');

INSERT INTO associatedaccount (id, token, usr)
VALUES ('aaac', 'tokenaaac', 'Cardamis');

INSERT INTO associatedaccount (id, token, usr)
VALUES ('aaad', 'tokenaaad', 'Alvaru');

-- Blocked Users
INSERT INTO blockuser (usr, blocked)
VALUES ('Alvaru', 'Marpin');

-- Follow Users
INSERT INTO followuser (followed, follower)
VALUES ('Danilo', 'Alvaru');

INSERT INTO followuser (followed, follower)
VALUES ('Cardamis', 'Danilo');

INSERT INTO followuser (followed, follower)
VALUES ('Alvaru', 'Cardamis');

INSERT INTO followuser (followed, follower)
VALUES ('Alvaru', 'Marpin');

-- Phone
INSERT INTO phone (prefix, phone, usr)
VALUES ('+34', '608564255', 'Alvaru');

INSERT INTO phone (prefix, phone, usr)
VALUES ('+34', '698281823', 'Cardamis');

INSERT INTO phone (prefix, phone, usr)
VALUES ('+34', '613224123', 'Cardamis');

INSERT INTO phone (prefix, phone, usr)
VALUES ('+34', '655110033', 'Alvaru');

-- Silence User
INSERT INTO silenceuser (usr, silenced)
VALUES ('Alvaru', 'Marpin');
-- =====================================================================================================================

-- ==================================================== DATA POSTS =====================================================
-- Post
INSERT INTO post (author, text, publicationdate)
VALUES ('Alvaru', 'Hi! My name is... what?', DEFAULT);

INSERT INTO post (author, text, publicationdate)
VALUES ('Danilo', 'My name is... who?', DEFAULT);

INSERT INTO post (author, text, publicationdate)
VALUES ('Cardamis', 'My name is... Slim Shady ?', DEFAULT);

INSERT INTO post (author, text, publicationdate)
VALUES ('Marpin',
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et ' ||
        'dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ' ||
        'ex ea commodo consequat.',
        DEFAULT);

INSERT INTO post (author, text, publicationdate)
VALUES ('Marpin', 'Hi kids! Do you like violence?', DEFAULT);

-- React to post
INSERT INTO react (reactiontype, post, usr, author)
SELECT 'likeit', id, 'Cardamis', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 0;

INSERT INTO react (reactiontype, post, usr, author)
SELECT 'hateit', id, 'Cardamis', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 1;

INSERT INTO react (reactiontype, post, usr, author)
SELECT 'makesmeangry', id, 'Marpin', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 2;

INSERT INTO react (reactiontype, post, usr, author)
SELECT 'loveit', id, 'Marpin', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 3;

INSERT INTO react (reactiontype, post, usr, author)
SELECT 'loveit', id, 'Alvaru', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 4;


-- Repost
INSERT INTO repost (post, usr, author)
SELECT id, 'Alvaru', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 0;


INSERT INTO repost (post, usr, author)
SELECT id, 'Danilo', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 1;


INSERT INTO repost (post, usr, author)
SELECT id, 'Cardamis', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 2;


INSERT INTO repost (post, usr, author)
SELECT id, 'Alvaru', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 3;


INSERT INTO repost (post, usr, author)
SELECT id, 'Danilo', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 4;

-- Archive post
INSERT INTO archivePost (post, usr, author)
SELECT id, 'Marpin', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 0;

INSERT INTO archivePost (post, usr, author)
SELECT id, 'Danilo', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 1;

INSERT INTO archivePost (post, usr, author)
SELECT id, 'Alvaru', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 2;

INSERT INTO archivePost (post, usr, author)
SELECT id, 'Cardamis', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 3;

INSERT INTO archivePost (post, usr, author)
SELECT id, 'Danilo', author
FROM post
ORDER BY id
LIMIT 1 OFFSET 4;

-- =====================================================================================================================

-- =================================================== DATA EVENTS =====================================================
INSERT INTO event (name, description, location, date, author)
VALUES ('USC party', 'Party for all usc students', 'USC campus',
        '2019-04-23', 'Alvaru');

INSERT INTO event (name, description, location, date, author)
VALUES ('Suprise exam USC', 'Dont tell the teachers', 'USC campus',
        '2019-04-29', 'Danilo');

INSERT INTO event (name, description, location, date, author)
VALUES ('Test1', 'Is late i dont have any more ideas', 'Home if where your heart is',
        '2019-06-29', 'Danilo');

INSERT INTO event (name, description, location, date, author)
VALUES ('Test2', 'things', 'Santiago de Compostela',
        '2019-07-10', 'Alvaru');

-- Participate Event
INSERT INTO participateevent (event, eventauthor, usr)
SELECT id, author, 'Alvaru'
FROM event
ORDER BY id
LIMIT 1 OFFSET 0;

INSERT INTO participateevent (event, eventauthor, usr)
SELECT id, author, 'Cardamis'
FROM event
ORDER BY id
LIMIT 1 OFFSET 1;

INSERT INTO participateevent (event, eventauthor, usr)
SELECT id, author, 'Alvaru'
FROM event
ORDER BY id
LIMIT 1 OFFSET 2;

INSERT INTO participateevent (event, eventauthor, usr)
SELECT id, author, 'Alvaru'
FROM event
ORDER BY id
LIMIT 1 OFFSET 3;
-- =====================================================================================================================

-- ================================================ DATA ACHIVEMENTS ===================================================
-- Achievement
INSERT INTO achievement (name, description)
VALUES ('First follow', 'You have successfully follow someone');

INSERT INTO achievement (name, description)
VALUES ('First follower', 'You have been followed for the first time');

INSERT INTO achievement (name, description)
VALUES ('10 posts', 'you have published 10 posts');

INSERT INTO achievement (name, description)
VALUES ('100 posts', 'you have published 100 posts');

-- Own Achivement
INSERT INTO ownachievement (achiev, usr, acquisitiondate)
SELECT id, 'Alvaru', CURRENT_DATE
FROM achievement
ORDER BY id
LIMIT 1 OFFSET 0;

INSERT INTO ownachievement (achiev, usr, acquisitiondate)
SELECT id, 'Danilo', CURRENT_DATE
FROM achievement
ORDER BY id
LIMIT 1 OFFSET 1;

INSERT INTO ownachievement (achiev, usr, acquisitiondate)
SELECT id, 'Cardamis', CURRENT_DATE
FROM achievement
ORDER BY id
LIMIT 1 OFFSET 2;


INSERT INTO ownachievement (achiev, usr, acquisitiondate)
SELECT id, 'Alvaru', CURRENT_DATE
FROM achievement
ORDER BY id
LIMIT 1 OFFSET 3;
-- =====================================================================================================================

-- ================================================= DATA HASHTAGS =====================================================
-- Hastag
INSERT INTO hashtag (name)
VALUES ('test');

INSERT INTO hashtag (name)
VALUES ('ThisProyectIsA10');

INSERT INTO hashtag (name)
VALUES ('AlvaroSendingWorkAgain');

-- Own Hastag
INSERT INTO ownhashtag (hashtag, post, author)
SELECT 'ThisProyectIsA10', id, author
FROM post
ORDER BY id
LIMIT 1 OFFSET 0;

INSERT INTO ownhashtag (hashtag, post, author)
SELECT 'test', id, author
FROM post
ORDER BY id
LIMIT 1 OFFSET 1;

INSERT INTO ownhashtag (hashtag, post, author)
SELECT 'AlvaroSendingWorkAgain', id, author
FROM post
ORDER BY id
LIMIT 1 OFFSET 2;


-- Follow Hashtag
INSERT INTO followhashtag (piiuser, hashtag)
VALUES ('Alvaru', 'test');

INSERT INTO followhashtag (piiuser, hashtag)
VALUES ('Alvaru', 'ThisProyectIsA10');

INSERT INTO followhashtag (piiuser, hashtag)
VALUES ('Alvaru', 'AlvaroSendingWorkAgain');

INSERT INTO followhashtag (piiuser, hashtag)
VALUES ('Danilo', 'ThisProyectIsA10');

INSERT INTO followhashtag (piiuser, hashtag)
VALUES ('Cardamis', 'test');

INSERT INTO followhashtag (piiuser, hashtag)
VALUES ('Marpin', 'AlvaroSendingWorkAgain');
-- =====================================================================================================================

-- ================================================ DATA MULTIMEDIA ====================================================
-- Multimedia
INSERT INTO multimedia (hash, resolution, uri)
VALUES ('aaaa', '1080p', 'here');

INSERT INTO multimedia (hash, resolution, uri)
VALUES ('aaab', '1080p', 'here');

INSERT INTO multimedia (hash, resolution, uri)
VALUES ('aaac', '1080p', 'here');

INSERT INTO multimedia (hash, resolution, uri)
VALUES ('aaad', '1080p', 'here');

-- Multimedia Image
INSERT INTO multimediaimage (hash)
VALUES ('aaaa');

INSERT INTO multimediaimage (hash)
VALUES ('aaab');

-- Multimedia Video
INSERT INTO multimediavideo (hash)
VALUES ('aaac');

INSERT INTO multimediavideo (hash)
VALUES ('aaad');
-- =====================================================================================================================

-- ================================================== DATA TICKET ======================================================
-- Ticket
INSERT INTO ticket (usr, section, text, creationdate)
VALUES ('Alvaru', 'easyGameEasyLife', 'dont know what to say', CURRENT_DATE);

-- =====================================================================================================================

-- ================================================= DATA MESSAGES =====================================================
-- Message
INSERT INTO message (author, text, date, multimedia)
VALUES ('Marpin', 'Alvaro is sendig work again... run !', '2019-04-10', 'aaaa');
INSERT INTO message (author, text, date, multimedia)
VALUES ('Cardamis', 'Alvaro is sendig work again... run !', '2019-04-11', 'aaaa');
INSERT INTO message (author, text, date, multimedia, ticket)
VALUES ('Danilo', 'Alvaro is sendig work again... run !', '2019-04-12', 'aaaa', 1);
INSERT INTO message (author, text, date, multimedia)
VALUES ('Alvaru', 'WORK MINIONS WORK !', '2019-04-13', 'aaad');

-- Recieve Message
INSERT INTO receivemessage (message, author, receiver)
SELECT id, author, 'Alvaru'
FROM message
ORDER BY message
LIMIT 1 OFFSET 0;

INSERT INTO receivemessage (message, author, receiver)
SELECT id, author, 'Danilo'
FROM message
ORDER BY message
LIMIT 1 OFFSET 1;

INSERT INTO receivemessage (message, author, receiver)
SELECT id, author, 'Cardamis'
FROM message
ORDER BY message
LIMIT 1 OFFSET 2;

INSERT INTO receivemessage (message, author, receiver)
SELECT id, author, 'Marpin'
FROM message
ORDER BY message
LIMIT 1 OFFSET 3;
-- =====================================================================================================================

-- =============================================== DATA NOTIFICATIONS ==================================================
-- Notification
INSERT INTO notification (creationdate, content)
VALUES ('2019-04-12', 'Viqueira sais: 4.9');

INSERT INTO notification (creationdate, content)
VALUES ('2019-04-11', 'Easy game easy life');

INSERT INTO notification (creationdate, content)
VALUES ('2019-04-10', 'Hi');

INSERT INTO notification (creationdate, content)
VALUES ('2019-04-10', 'No sleep');

-- Have Notification
INSERT INTO havenotification (notification, usr)
SELECT id, 'Alvaru'
FROM notification
ORDER BY id
LIMIT 1 OFFSET 0;

INSERT INTO havenotification (notification, usr)
SELECT id, 'Danilo'
FROM notification
ORDER BY id
LIMIT 1 OFFSET 1;

INSERT INTO havenotification (notification, usr)
SELECT id, 'Cardamis'
FROM notification
ORDER BY id
LIMIT 1 OFFSET 2;

INSERT INTO havenotification (notification, usr)
SELECT id, 'Marpin'
FROM notification
ORDER BY id
LIMIT 1 OFFSET 3;
-- =====================================================================================================================