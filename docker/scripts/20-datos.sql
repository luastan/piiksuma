/*
-- Piiuser
INSERT INTO piiuser (id, email, name, pass, gender, description, home,
  postalcode, province, country, city, birthdate, registrationdate)
  VALUES ('usr1', 'usr1@gmail.com', 'user1','user1', 'M', 'dasdasd',
  'somewhere', '231', 'someProvince','someCountry','someCity', '2019-04-12', '2019-04-12');
INSERT INTO piiuser (id, email, name, pass, gender, description, home,
  postalcode, province, country, city, birthdate, registrationdate)
  VALUES ('usr2', 'usr2@gmail.com', 'user2','user2', 'M', 'dasdasd',
  'somewhere', '231', 'someProvince','someCountry','someCity', '2019-04-12', '2019-04-12');
INSERT INTO piiuser (id, email, name, pass, gender, description, home,
  postalcode, province, country, city, birthdate, registrationdate)
  VALUES ('usr3', 'usr3@gmail.com', 'user3','user3', 'M', 'dasdasd',
  'somewhere', '231', 'someProvince','someCountry','someCity', '2019-04-12', '2019-04-12');
INSERT INTO piiuser (id, email, name, pass, gender, description, home,
  postalcode, province, country, city, birthdate, registrationdate)
  VALUES ('usr4', 'usr4@gmail.com', 'user4','user4', 'M', 'dasdasd',
  'somewhere', '231', 'someProvince','someCountry','someCity', '2019-04-12', '2019-04-12');

  -- Admin
  INSERT INTO administrator (id) VALUES ('usr1');

-- Post
INSERT INTO post (author, id, text, publicationdate)
  VALUES ('usr1','aaaa', 'Hi! My name is... what?', '2019-05-12');
INSERT INTO post (author, id, text, publicationdate)
  VALUES ('usr2','aaab', 'My name is... who?', '2019-05-12');
INSERT INTO post (author, id, text, publicationdate)
  VALUES ('usr3','aaac', 'My name is... Slim Shady ?', '2019-05-12');
INSERT INTO post (author, id, text, publicationdate)
  VALUES ('usr4','aaad', 'Hi kids! Do you like violence?', '2019-05-12');

-- Achievement
INSERT INTO achievement (id, name, description)
  VALUES ('aaaa' , 'First follow', 'You have successfully follow someone');
INSERT INTO achievement (id, name, description)
  VALUES ('aaab' , 'First follower', 'You have been followed for the first time');
INSERT INTO achievement (id, name, description)
  VALUES ('aaac' , '10 posts', 'you have published 10 posts');
INSERT INTO achievement (id, name, description)
    VALUES ('aaad' , '100 posts', 'you have published 100 posts');

-- Hastag
INSERT INTO hashtag (name) VALUES ('test');
INSERT INTO hashtag (name) VALUES ('ThisProyectIsA10');

-- Event
INSERT INTO event (id, name, description, location, date, creatoruser)
  VALUES ('aaaa', 'USC party', 'Party for all usc students', 'USC campus',
          '2019-04-23', 'usr1');
INSERT INTO event (id, name, description, location, date, creatoruser)
  VALUES ('aaab', 'Suprise exam USC', 'Dont tell the teachers', 'USC campus',
          '2019-04-29', 'usr2');
INSERT INTO event (id, name, description, location, date, creatoruser)
  VALUES ('aaac', 'Test1', 'Is late i dont have any more ideas', 'Home if where your heart is',
        '2019-06-29', 'usr2');
INSERT INTO event (id, name, description, location, date, creatoruser)
  VALUES ('aaad', 'Test2', 'things', 'Santiago de Compostela',
        '2019-07-10', 'usr1');

-- Multimedia
INSERT INTO multimedia (hash, resolution, uri) VALUES ('aaaa', '1080p', 'here');
INSERT INTO multimedia (hash, resolution, uri) VALUES ('aaab', '1080p', 'here');
INSERT INTO multimedia (hash, resolution, uri) VALUES ('aaac', '1080p', 'here');
INSERT INTO multimedia (hash, resolution, uri) VALUES ('aaad', '1080p', 'here');

-- Ticket
INSERT INTO ticket (id, usr ,section ,text, creationdate)
  VALUES ('aaaaa', 'usr1', 'easyGameEasyLife', 'dont know what to say',CURRENT_DATE);

-- Message
INSERT INTO message (id, sender, text, date, multimedia)
  VALUES ('aaaa', 'usr3', 'Is this the real life?', '2019-04-10', 'aaaa');
INSERT INTO message (id, sender, text, date, multimedia)
  VALUES ('aaab', 'usr3', 'Is this the real life?', '2019-04-11', 'aaaa');
INSERT INTO message (id, sender, text, date, multimedia, ticket)
  VALUES ('aaac', 'usr2', 'Is this the real life?', '2019-04-12', 'aaaa', 'aaaa');
INSERT INTO message (id, sender, text, date, multimedia)
  VALUES ('aaad', 'usr1', 'Is this the real life?', '2019-04-13', 'aaad');

-- Notification
INSERT INTO notification (id, creationdate, content)
  VALUES ('aaaa', '2019-04-12', 'Viqueira sais: 4.9');
INSERT INTO notification (id, creationdate, content)
  VALUES ('aaab', '2019-04-11', 'Easy game easy life');
INSERT INTO notification (id, creationdate, content)
  VALUES ('aaac', '2019-04-10', 'Hi');
INSERT INTO notification (id, creationdate, content)
  VALUES ('aaad', '2019-04-10', 'No sleep');

-- Archive post
INSERT INTO archivepost (post, usr, author)
  VALUES ('aaaa', 'usr1' , 'usr1');
INSERT INTO archivepost (post, usr, author)
  VALUES ('aaab', 'usr1' , 'usr2');
INSERT INTO archivepost (post, usr, author)
  VALUES ('aaac', 'usr4' , 'usr3');
INSERT INTO archivepost (post, usr, author)
  VALUES ('aaad', 'usr2' , 'usr4');

-- Associated account
INSERT INTO associatedaccount (id, token, usr)
  VALUES ('aaaa', 'tokenaaaa' , 'usr1');
INSERT INTO associatedaccount (id, token, usr)
  VALUES ('aaab', 'tokenaaaab' , 'usr2');
INSERT INTO associatedaccount (id, token, usr)
  VALUES ('aaac', 'tokenaaac' , 'usr3');
INSERT INTO associatedaccount (id, token, usr)
  VALUES ('aaad', 'tokenaaad' , 'usr1');

-- Blocked Users
INSERT INTO blockuser (usr, blocked) VALUES ('usr1', 'usr4');

-- Follow Hashtag
INSERT INTO followhashtag (piiuser, hashtag) VALUES ('usr1', 'test');
INSERT INTO followhashtag (piiuser, hashtag) VALUES ('usr2', 'ThisProyectIsA10');
INSERT INTO followhashtag (piiuser, hashtag) VALUES ('usr3', 'test');
INSERT INTO followhashtag (piiuser, hashtag) VALUES ('usr4', 'ThisProyectIsA10');

-- Follow Users
INSERT INTO followuser (followed, follower) VALUES ('usr1','usr2');
INSERT INTO followuser (followed, follower) VALUES ('usr3','usr2');
INSERT INTO followuser (followed, follower) VALUES ('usr1','usr3');
INSERT INTO followuser (followed, follower) VALUES ('usr1','usr4');

-- Have Notification
INSERT INTO havenotification (notification, usr)
  VALUES ('aaaa', 'usr1');
INSERT INTO havenotification (notification, usr)
  VALUES ('aaab', 'usr2');
INSERT INTO havenotification (notification, usr)
  VALUES ('aaac', 'usr3');
INSERT INTO havenotification (notification, usr)
  VALUES ('aaad', 'usr4');

-- Multimedia Image
INSERT INTO multimediaimage (hash) VALUES ('aaaa');
INSERT INTO multimediaimage (hash) VALUES ('aaab');

-- Multimedia Video
INSERT INTO multimediavideo (hash) VALUES ('aaac');
INSERT INTO multimediavideo (hash) VALUES ('aaad');

-- Own Achivement
INSERT INTO ownachievement (achiev, usr, acquisitiondate)
  VALUES ('aaaa', 'usr1', '2019-04-12');
INSERT INTO ownachievement (achiev, usr, acquisitiondate)
  VALUES ('aaaa', 'usr2', '2019-04-12');
INSERT INTO ownachievement (achiev, usr, acquisitiondate)
  VALUES ('aaaa', 'usr3', '2019-04-12');
INSERT INTO ownachievement (achiev, usr, acquisitiondate)
  VALUES ('aaaa', 'usr4', '2019-04-12');

-- Own Hastag
INSERT INTO ownhashtag (hashtag, post, author)
  VALUES ('ThisProyectIsA10', 'aaaa', 'usr1');
INSERT INTO ownhashtag (hashtag, post, author)
  VALUES ('ThisProyectIsA10', 'aaab', 'usr2');
INSERT INTO ownhashtag (hashtag, post, author)
  VALUES ('ThisProyectIsA10', 'aaac', 'usr3');
INSERT INTO ownhashtag (hashtag, post, author)
  VALUES ('ThisProyectIsA10', 'aaad', 'usr4');

-- Participate Event
INSERT INTO participateevent (event, usr) VALUES ('aaaa','usr1');
INSERT INTO participateevent (event, usr) VALUES ('aaaa','usr2');
INSERT INTO participateevent (event, usr) VALUES ('aaaa','usr3');
INSERT INTO participateevent (event, usr) VALUES ('aaaa','usr4');

-- Phone
INSERT INTO phone (prefix, phone, usr) VALUES ('+34', '608564255', 'usr1');
INSERT INTO phone (prefix, phone, usr) VALUES ('+34', '698281823', 'usr2');
INSERT INTO phone (prefix, phone, usr) VALUES ('+34', '613224123', 'usr3');
INSERT INTO phone (prefix, phone, usr) VALUES ('+34', '655110033', 'usr1');

-- React
INSERT INTO react (reactiontype, post, usr, author)
  VALUES ('likeit', 'aaaa', 'usr2', 'usr1');
INSERT INTO react (reactiontype, post, usr, author)
  VALUES ('hateit', 'aaaa', 'usr3', 'usr1');
--INSERT INTO react (reactiontype, post, usr, author) --Error varchar(8)
  --VALUES ('makesmeangry', 'aaaa', 'usr4', 'usr1');
INSERT INTO react (reactiontype, post, usr, author)
  VALUES ('loveit', 'aaaa', 'usr1', 'usr1');

-- Recieve Message
INSERT INTO receivemessage (message, sender, receiver)
  VALUES ('aaaa', 'usr3', 'usr1');
INSERT INTO receivemessage (message, sender, receiver)
  VALUES ('aaaa', 'usr3', 'usr2');
INSERT INTO receivemessage (message, sender, receiver)
  VALUES ('aaaa', 'usr3', 'usr3');
INSERT INTO receivemessage (message, sender, receiver)
  VALUES ('aaaa', 'usr3', 'usr4');

-- Repost
INSERT INTO repost (post, usr, author)
  VALUES ('aaaa', 'usr2','usr1');
INSERT INTO repost (post, usr, author)
  VALUES ('aaaa', 'usr3','usr1');
INSERT INTO repost (post, usr, author)
  VALUES ('aaaa', 'usr4','usr1');
INSERT INTO repost (post, usr, author)
  VALUES ('aaaa', 'usr1','usr1');

-- Silence User
INSERT INTO silenceuser (usr, silenced)
  VALUES ('usr1', 'usr4');
*/