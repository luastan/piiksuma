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
    resolucion text
    -- uri varchar(10)
);



CREATE TABLE multimediaFotos
(
    id text not null primary key references multimedia (id) on delete cascade on update cascade
);


CREATE TABLE multimediaVideos
(
    id text not null primary key references multimedia (id) on delete cascade on update cascade
);


/*

    Usuarios

*/


CREATE TABLE usuarios
(
  id                   text         not null primary key,
  nombre               varchar(50)  not null default '',
  pass                 varchar(256) not null,
  sexo                 varchar(100) CHECK (sexo IN ('hombre', 'mujer', 'helicoptero apache', 'ojalá',
                                                    'helicoptero estrellado')),
  descripcion          varchar(256),
  domicilio            varchar(256),
  email                varchar(50),
  codigoPostal         varchar(50),
  provincia            varchar(50),
  pais                 varchar(50),
  ciudad               varchar(50),
  lugarNacimiento      varchar(50),
  fechaNacimiento      timestamp,
  fechaRegistro        timestamp             default now(),
  fechaMuerte          timestamp,
  religion             varchar(50),
  situacionSentimental varchar(50),
  trabajo              varchar(50),

  -- Aqui hay que pensar como va a funcionar por defecto
  fotoPerfil           text references multimediaFotos (id) on delete set null on update cascade

);



CREATE TABLE administradores
(
    id text primary key references usuarios (id) on delete set null on update cascade
);


CREATE TABLE cuentasAsociadas
(
    id      text primary key,
    token   text,
    usuario text,

    foreign key (usuario) references usuarios (id) on delete cascade on update cascade
);


CREATE TABLE telefonos
(
    prefijo  varchar(3),
    telefono text,
    usuario  text,

    primary key (telefono, usuario, prefijo),
    foreign key (usuario) references usuarios (id) on delete cascade on update cascade
);

/*

    Interacciones

*/

CREATE TABLE seguirUsuario
(
    seguido  text,
    seguidor text,

    primary key (seguido, seguidor),
    foreign key (seguido) references usuarios (id) on delete cascade on update cascade,
    foreign key (seguidor) references usuarios (id) on delete cascade on update cascade
);



CREATE TABLE mensajesPrivados
(
    id         text,
    texto      text,
    emisor     text,
    receptor   text,
    multimedia text,
    fecha      timestamp default now(),

    primary key (emisor, receptor, fecha, id),
    foreign key (emisor) references usuarios (id) on delete cascade on update cascade,
    foreign key (receptor) references usuarios (id) on delete cascade on update cascade,
    foreign key (multimedia) references multimedia (id) on delete set null on update cascade
);


CREATE TABLE posts
(
    autor            text,
    id               text,
    fechaPublicacion timestamp default now() not null,
    paapa            timestamp,
    autorPaapa       text,
    multimedia       text,

    primary key (id)
);


/*

    Hashtags

*/


CREATE TABLE hashtag (
    nombre varchar(280) not null primary key
);

CREATE TABLE tenerHashtag
(
    hashtag   text,
    post      text,

    primary key (hashtag, post),
    foreign key (post) references posts (id) on delete cascade on update cascade
);


/*

    Reacciones y Reposts

*/


CREATE TABLE reacciones
(
    tipoReaccion text,
    post         text,
    usuario      text,
    primary key (post, usuario),
    foreign key (post) references posts (id) on delete cascade on update cascade,
    foreign key (usuario) references usuarios (id) on delete cascade on update cascade
);


CREATE TABLE reposts
(
    post    text,
    usuario text,
    primary key (post, usuario),
    foreign key (post) references posts (id) on delete cascade on update cascade,
    foreign key (usuario) references usuarios (id) on delete cascade on update cascade
);


/*

    Eventos

*/


CREATE TABLE eventos
(
    id             text,
    nombre         text not null default '',
    descripcion    text not null default '',
    localizacion   text not null default '',
    fecha          timestamp,
    usuarioCreador text,
    primary key (id),
    foreign key (usuarioCreador) references usuarios (id) on delete cascade on update cascade
);

CREATE TABLE participarEvento
(
    evento  text,
    usuario text,
    primary key (evento, usuario),
    foreign key (evento) references eventos (id) on delete cascade on update cascade,
    foreign key (usuario) references usuarios (id) on delete cascade on update cascade
);


/*

    Logros

*/

CREATE TABLE logros
(
    id          text primary key,
    nombre      varchar(25),
    descripcion varchar(100)
);


CREATE TABLE tenerLogros
(
    logro            text,
    usuario          text,
    fechaAdquisicion timestamp not null default now(),

    foreign key (logro) references logros (id) ON DELETE CASCADE ON UPDATE CASCADE,
    foreign key (usuario) references usuarios (id) ON DELETE CASCADE ON UPDATE CASCADE
);


/*

    Tickets

*/


CREATE TABLE tickets
(
    id            text,
    usuario       text,
    seccion       text,
    texto         text,
    fechaCreacion timestamp not null default now(),
    fechaCierre   timestamp,
    adminCierre   text,
    primary key (id),
    foreign key (usuario) references usuarios (id) on delete set null on update cascade ,
    foreign key (adminCierre) references administradores (id) on delete set null on update cascade
);


CREATE TABLE respuestasTicket
(
    id               text,
    ticket           text,
    respuesta        text,
    usuarioRespuesta text,
    fecha            timestamp not null default now(),

    primary key (id),
    foreign key (ticket) references tickets (id),
    foreign key (usuarioRespuesta) references usuarios (id) on delete set null on update cascade
);

