/*

Script de creación de tablas para Piiksuma

Authors:
    @luastan
    @danimf99
    @CardamaS99
    @alvrogd
    @Marcos-marpin
    @OswaldOswin1

Source Code:
      https://github.com/luastan/piiksuma
*/


/*

      Multimedia

*/

CREATE TABLE multimedia
(
    id         text primary key,
    hash       text not null,
    resolution text
    -- uri varchar(10)
);



CREATE TABLE multimediaImage
(
    id text not null primary key references multimedia (id) on delete cascade on update cascade
);


CREATE TABLE multimediaVideo
(
    id text not null primary key references multimedia (id) on delete cascade on update cascade
);


/*

      Usuarios

*/


CREATE TABLE piiUser
(
    id                 text primary key,
    name               varchar(50)  not null default '',
    pass               varchar(256) not null,
    gender             varchar(100) CHECK (gender IN ('hombre', 'mujer', 'helicoptero apache', 'ojalá',
                                                      'helicoptero estrellado')),
    description        varchar(256),
    home               varchar(256),
    email              varchar(50),
    postalCode         varchar(50),
    province           varchar(50),
    country            varchar(50),
    city               varchar(50),
    birthplace         varchar(50),
    birthdate          timestamp    not null,
    registration_date  timestamp    not null default now(),
    deathdate          timestamp,
    religion           varchar(50),
    emotionalSituation varchar(50),
    job                varchar(50),

    -- Aqui hay que pensar como va a funcionar por defecto
    profilePicture     text references multimediaImage (id) on delete set null on update cascade

);



CREATE TABLE administrator
(
    id text primary key references piiUser (id) on delete set null on update cascade
);


CREATE TABLE associatedAccount
(
    id    text not null primary key,
    token text,
    usr   text,

    foreign key (usr) references piiUser (id) on delete cascade on update cascade
);



CREATE TABLE phone
(
    prefix varchar(3),
    phone  text,
    usr    text,

    primary key (phone, usr, prefix),
    foreign key (usr) references piiUser (id) on delete cascade on update cascade
);


/*

      Interacciones

*/

CREATE TABLE followUser
(
    followed text,
    follower text,

    primary key (followed, follower),
    foreign key (followed) references piiUser (id) on delete cascade on update cascade,
    foreign key (follower) references piiUser (id) on delete cascade on update cascade
);



CREATE TABLE privateMessage
(
    id         text,
    text       text,
    sender     text,
    receiver   text,
    multimedia text,
    date       timestamp default now(),

    primary key (sender, receiver, date, id),
    foreign key (sender) references piiUser (id) on delete cascade on update cascade,
    foreign key (receiver) references piiUser (id) on delete cascade on update cascade,
    foreign key (multimedia) references multimedia (id) on delete set null on update cascade
);



CREATE TABLE post
(
    author          text,
    id              text,
    publicationDate timestamp default now() not null,
    sugarDaddy      text,
    authorDaddy     text,
    multimedia      text,

    primary key (id, author),
    foreign key(author) references piiUser(id),
    foreign key(sugarDaddy, authorDaddy) references post(id, author),
    foreign key(multimedia) references multimedia(id)
);


/*

      Hashtags

*/


CREATE TABLE hashtag
(
    name varchar(280) not null primary key
);


CREATE TABLE ownHashtag
(
    hashtag text,
    post    text,
    author  text,

    primary key (hashtag, post, author),
    foreign key (post, author) references post (id, author) on delete cascade on update cascade
);


/*

      Reacciones y Repost

*/


CREATE TABLE reaction
(
    reactionType text,
    post         text,
    usr          text,
    author       text,

    primary key (author, post, usr),
    foreign key (post, author) references post (id, author) on delete cascade on update cascade,
    foreign key (usr) references piiUser (id) on delete cascade on update cascade
);



CREATE TABLE repost
(
    post   text,
    usr    text,
    author text,
    primary key (post, usr, author),
    foreign key (post, author) references post (id, author) on delete cascade on update cascade,
    foreign key (usr) references piiUser (id) on delete cascade on update cascade
);



/*

      Eventos

*/


CREATE TABLE event
(
    id          text,
    name        text not null default '',
    description text not null default '',
    location    text not null default '',
    date        timestamp,
    creatorUser text,
    primary key (id),
    foreign key (creatorUser) references piiUser (id) on delete cascade on update cascade
);



CREATE TABLE participateEvent
(
    event text,
    usr   text,
    primary key (event, usr),
    foreign key (event) references event (id) on delete cascade on update cascade,
    foreign key (usr) references piiUser (id) on delete cascade on update cascade
);


/*

      Logros

*/

CREATE TABLE achievement
(
    id          text primary key,
    name        varchar(25),
    description varchar(100)
);



CREATE TABLE ownedAchievement
(
    logro           text,
    usr             text,
    acquisitionDate timestamp not null default now(),

    foreign key (logro) references achievement (id) ON DELETE CASCADE ON UPDATE CASCADE,
    foreign key (usr) references piiUser (id) ON DELETE CASCADE ON UPDATE CASCADE
);

/*

      Tickets

*/


CREATE TABLE ticket
(
    id           text,
    usr          text,
    section      text,
    text         text,
    creationDate timestamp not null default now(),
    deadline     timestamp,
    adminClosing text,
    primary key (id),
    foreign key (usr) references piiUser (id) on delete set null on update cascade,
    foreign key (adminClosing) references administrator (id) on delete set null on update cascade
);



CREATE TABLE ticketAnswer
(
    id          text,
    ticket      text,
    answer      text,
    usrResponse text,
    date        timestamp not null default now(),

    primary key (id),
    foreign key (ticket) references ticket (id),
    foreign key (usrResponse) references piiUser (id) on delete set null on update cascade
);