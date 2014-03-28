--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.4
-- Dumped by pg_dump version 9.3.1
-- Started on 2014-03-28 07:06:17

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

DROP DATABASE scrumkin;
--
-- TOC entry 2117 (class 1262 OID 16394)
-- Name: scrumkin; Type: DATABASE; Schema: -; Owner: scrumkin_admin
--

CREATE DATABASE scrumkin WITH TEMPLATE = template0 ENCODING = 'UTF8';


ALTER DATABASE scrumkin OWNER TO scrumkin_admin;

\connect scrumkin

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 6 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 2118 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- TOC entry 194 (class 3079 OID 11750)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2120 (class 0 OID 0)
-- Dependencies: 194
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 170 (class 1259 OID 16395)
-- Name: acceptence_tests; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE acceptence_tests (
    id integer NOT NULL,
    user_story_id integer NOT NULL,
    test text NOT NULL,
    accepted boolean
);


ALTER TABLE public.acceptence_tests OWNER TO scrumkin_admin;

--
-- TOC entry 171 (class 1259 OID 16401)
-- Name: acceptence_tests_id_seq; Type: SEQUENCE; Schema: public; Owner: scrumkin_admin
--

CREATE SEQUENCE acceptence_tests_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.acceptence_tests_id_seq OWNER TO scrumkin_admin;

--
-- TOC entry 2121 (class 0 OID 0)
-- Dependencies: 171
-- Name: acceptence_tests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE acceptence_tests_id_seq OWNED BY acceptence_tests.id;


--
-- TOC entry 172 (class 1259 OID 16403)
-- Name: group_permissions; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE group_permissions (
    id integer NOT NULL,
    group_id integer NOT NULL,
    permission_id integer NOT NULL
);


ALTER TABLE public.group_permissions OWNER TO scrumkin_admin;

--
-- TOC entry 173 (class 1259 OID 16406)
-- Name: group_permissions_id_seq; Type: SEQUENCE; Schema: public; Owner: scrumkin_admin
--

CREATE SEQUENCE group_permissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.group_permissions_id_seq OWNER TO scrumkin_admin;

--
-- TOC entry 2122 (class 0 OID 0)
-- Dependencies: 173
-- Name: group_permissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE group_permissions_id_seq OWNED BY group_permissions.id;


--
-- TOC entry 174 (class 1259 OID 16408)
-- Name: groups; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE groups (
    id integer NOT NULL,
    project_id integer,
    name text NOT NULL
);


ALTER TABLE public.groups OWNER TO scrumkin_admin;

--
-- TOC entry 175 (class 1259 OID 16414)
-- Name: groups_id_seq; Type: SEQUENCE; Schema: public; Owner: scrumkin_admin
--

CREATE SEQUENCE groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.groups_id_seq OWNER TO scrumkin_admin;

--
-- TOC entry 2123 (class 0 OID 0)
-- Dependencies: 175
-- Name: groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE groups_id_seq OWNED BY groups.id;


--
-- TOC entry 193 (class 1259 OID 24578)
-- Name: login_tokens; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE login_tokens (
    id integer NOT NULL,
    user_id integer NOT NULL,
    token text NOT NULL,
    CONSTRAINT login_tokens_check_token CHECK ((token ~ '[0-9a-f]{128}'::text))
);


ALTER TABLE public.login_tokens OWNER TO scrumkin_admin;

--
-- TOC entry 192 (class 1259 OID 24576)
-- Name: login_tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: scrumkin_admin
--

CREATE SEQUENCE login_tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.login_tokens_id_seq OWNER TO scrumkin_admin;

--
-- TOC entry 2124 (class 0 OID 0)
-- Dependencies: 192
-- Name: login_tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE login_tokens_id_seq OWNED BY login_tokens.id;


--
-- TOC entry 176 (class 1259 OID 16416)
-- Name: permissions; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE permissions (
    id integer NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.permissions OWNER TO scrumkin_admin;

--
-- TOC entry 177 (class 1259 OID 16422)
-- Name: permissions_id_seq; Type: SEQUENCE; Schema: public; Owner: scrumkin_admin
--

CREATE SEQUENCE permissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.permissions_id_seq OWNER TO scrumkin_admin;

--
-- TOC entry 2125 (class 0 OID 0)
-- Dependencies: 177
-- Name: permissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE permissions_id_seq OWNED BY permissions.id;


--
-- TOC entry 178 (class 1259 OID 16424)
-- Name: priorities; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE priorities (
    id integer NOT NULL,
    priority integer NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.priorities OWNER TO scrumkin_admin;

--
-- TOC entry 179 (class 1259 OID 16430)
-- Name: priorities_id_seq; Type: SEQUENCE; Schema: public; Owner: scrumkin_admin
--

CREATE SEQUENCE priorities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.priorities_id_seq OWNER TO scrumkin_admin;

--
-- TOC entry 2126 (class 0 OID 0)
-- Dependencies: 179
-- Name: priorities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE priorities_id_seq OWNED BY priorities.id;


--
-- TOC entry 180 (class 1259 OID 16432)
-- Name: projects; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE projects (
    id integer NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.projects OWNER TO scrumkin_admin;

--
-- TOC entry 181 (class 1259 OID 16438)
-- Name: projects_id_seq; Type: SEQUENCE; Schema: public; Owner: scrumkin_admin
--

CREATE SEQUENCE projects_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.projects_id_seq OWNER TO scrumkin_admin;

--
-- TOC entry 2127 (class 0 OID 0)
-- Dependencies: 181
-- Name: projects_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE projects_id_seq OWNED BY projects.id;


--
-- TOC entry 182 (class 1259 OID 16440)
-- Name: sprints; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE sprints (
    id integer NOT NULL,
    project_id integer NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    velocity numeric(5,2) NOT NULL,
    CONSTRAINT sprints_check_start_date_end_date CHECK ((start_date < end_date)),
    CONSTRAINT sprints_check_velocity CHECK ((velocity > (0)::numeric))
);


ALTER TABLE public.sprints OWNER TO scrumkin_admin;

--
-- TOC entry 183 (class 1259 OID 16445)
-- Name: sprints_id_seq; Type: SEQUENCE; Schema: public; Owner: scrumkin_admin
--

CREATE SEQUENCE sprints_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sprints_id_seq OWNER TO scrumkin_admin;

--
-- TOC entry 2128 (class 0 OID 0)
-- Dependencies: 183
-- Name: sprints_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE sprints_id_seq OWNED BY sprints.id;


--
-- TOC entry 184 (class 1259 OID 16447)
-- Name: tasks; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE tasks (
    id integer NOT NULL,
    user_story_id integer NOT NULL,
    assignee_id integer,
    description text NOT NULL,
    estimated_time numeric(5,1),
    work_done numeric(5,1) DEFAULT 0 NOT NULL,
    accepted boolean,
    CONSTRAINT tasks_check_estimated_time CHECK ((estimated_time > (0)::numeric)),
    CONSTRAINT tasks_check_work_done CHECK ((work_done >= (0)::numeric))
);


ALTER TABLE public.tasks OWNER TO scrumkin_admin;

--
-- TOC entry 185 (class 1259 OID 16456)
-- Name: tasks_id_seq; Type: SEQUENCE; Schema: public; Owner: scrumkin_admin
--

CREATE SEQUENCE tasks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tasks_id_seq OWNER TO scrumkin_admin;

--
-- TOC entry 2129 (class 0 OID 0)
-- Dependencies: 185
-- Name: tasks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE tasks_id_seq OWNED BY tasks.id;


--
-- TOC entry 186 (class 1259 OID 16458)
-- Name: user_groups; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE user_groups (
    id integer NOT NULL,
    user_id integer NOT NULL,
    group_id integer NOT NULL
);


ALTER TABLE public.user_groups OWNER TO scrumkin_admin;

--
-- TOC entry 187 (class 1259 OID 16461)
-- Name: user_groups_id_seq; Type: SEQUENCE; Schema: public; Owner: scrumkin_admin
--

CREATE SEQUENCE user_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_groups_id_seq OWNER TO scrumkin_admin;

--
-- TOC entry 2130 (class 0 OID 0)
-- Dependencies: 187
-- Name: user_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE user_groups_id_seq OWNED BY user_groups.id;


--
-- TOC entry 188 (class 1259 OID 16463)
-- Name: user_stories; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE user_stories (
    id integer NOT NULL,
    project_id integer NOT NULL,
    priority_id integer NOT NULL,
    sprint_id integer,
    title text NOT NULL,
    story text DEFAULT ''::text NOT NULL,
    bussiness_value integer DEFAULT 0 NOT NULL,
    estimated_time numeric(4,1),
    CONSTRAINT user_stories_check_bussiness_value CHECK ((bussiness_value > 0)),
    CONSTRAINT user_stories_check_estimated_time CHECK ((estimated_time > (0)::numeric))
);


ALTER TABLE public.user_stories OWNER TO scrumkin_admin;

--
-- TOC entry 189 (class 1259 OID 16473)
-- Name: user_stories_id_seq; Type: SEQUENCE; Schema: public; Owner: scrumkin_admin
--

CREATE SEQUENCE user_stories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_stories_id_seq OWNER TO scrumkin_admin;

--
-- TOC entry 2131 (class 0 OID 0)
-- Dependencies: 189
-- Name: user_stories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE user_stories_id_seq OWNED BY user_stories.id;


--
-- TOC entry 190 (class 1259 OID 16475)
-- Name: users; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE users (
    id integer NOT NULL,
    username text NOT NULL,
    password text NOT NULL,
    name text NOT NULL,
    email text NOT NULL,
    CONSTRAINT users_check_email CHECK ((email ~ '.+@.+'::text))
);


ALTER TABLE public.users OWNER TO scrumkin_admin;

--
-- TOC entry 191 (class 1259 OID 16482)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: scrumkin_admin
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO scrumkin_admin;

--
-- TOC entry 2132 (class 0 OID 0)
-- Dependencies: 191
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- TOC entry 1898 (class 2604 OID 16484)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY acceptence_tests ALTER COLUMN id SET DEFAULT nextval('acceptence_tests_id_seq'::regclass);


--
-- TOC entry 1899 (class 2604 OID 16485)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY group_permissions ALTER COLUMN id SET DEFAULT nextval('group_permissions_id_seq'::regclass);


--
-- TOC entry 1900 (class 2604 OID 16486)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY groups ALTER COLUMN id SET DEFAULT nextval('groups_id_seq'::regclass);


--
-- TOC entry 1919 (class 2604 OID 24581)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY login_tokens ALTER COLUMN id SET DEFAULT nextval('login_tokens_id_seq'::regclass);


--
-- TOC entry 1901 (class 2604 OID 16487)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY permissions ALTER COLUMN id SET DEFAULT nextval('permissions_id_seq'::regclass);


--
-- TOC entry 1902 (class 2604 OID 16488)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY priorities ALTER COLUMN id SET DEFAULT nextval('priorities_id_seq'::regclass);


--
-- TOC entry 1903 (class 2604 OID 16489)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY projects ALTER COLUMN id SET DEFAULT nextval('projects_id_seq'::regclass);


--
-- TOC entry 1904 (class 2604 OID 16490)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY sprints ALTER COLUMN id SET DEFAULT nextval('sprints_id_seq'::regclass);


--
-- TOC entry 1908 (class 2604 OID 16491)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY tasks ALTER COLUMN id SET DEFAULT nextval('tasks_id_seq'::regclass);


--
-- TOC entry 1911 (class 2604 OID 16492)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_groups ALTER COLUMN id SET DEFAULT nextval('user_groups_id_seq'::regclass);


--
-- TOC entry 1914 (class 2604 OID 16493)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_stories ALTER COLUMN id SET DEFAULT nextval('user_stories_id_seq'::regclass);


--
-- TOC entry 1917 (class 2604 OID 16494)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- TOC entry 2089 (class 0 OID 16395)
-- Dependencies: 170
-- Data for Name: acceptence_tests; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY acceptence_tests (id, user_story_id, test, accepted) FROM stdin;
\.


--
-- TOC entry 2133 (class 0 OID 0)
-- Dependencies: 171
-- Name: acceptence_tests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('acceptence_tests_id_seq', 1, false);


--
-- TOC entry 2091 (class 0 OID 16403)
-- Dependencies: 172
-- Data for Name: group_permissions; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY group_permissions (id, group_id, permission_id) FROM stdin;
\.


--
-- TOC entry 2134 (class 0 OID 0)
-- Dependencies: 173
-- Name: group_permissions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('group_permissions_id_seq', 1, false);


--
-- TOC entry 2093 (class 0 OID 16408)
-- Dependencies: 174
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY groups (id, project_id, name) FROM stdin;
\.


--
-- TOC entry 2135 (class 0 OID 0)
-- Dependencies: 175
-- Name: groups_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('groups_id_seq', 1, false);


--
-- TOC entry 2112 (class 0 OID 24578)
-- Dependencies: 193
-- Data for Name: login_tokens; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY login_tokens (id, user_id, token) FROM stdin;
\.


--
-- TOC entry 2136 (class 0 OID 0)
-- Dependencies: 192
-- Name: login_tokens_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('login_tokens_id_seq', 1, false);


--
-- TOC entry 2095 (class 0 OID 16416)
-- Dependencies: 176
-- Data for Name: permissions; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY permissions (id, name) FROM stdin;
\.


--
-- TOC entry 2137 (class 0 OID 0)
-- Dependencies: 177
-- Name: permissions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('permissions_id_seq', 1, false);


--
-- TOC entry 2097 (class 0 OID 16424)
-- Dependencies: 178
-- Data for Name: priorities; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY priorities (id, priority, name) FROM stdin;
\.


--
-- TOC entry 2138 (class 0 OID 0)
-- Dependencies: 179
-- Name: priorities_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('priorities_id_seq', 1, false);


--
-- TOC entry 2099 (class 0 OID 16432)
-- Dependencies: 180
-- Data for Name: projects; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY projects (id, name) FROM stdin;
\.


--
-- TOC entry 2139 (class 0 OID 0)
-- Dependencies: 181
-- Name: projects_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('projects_id_seq', 1, false);


--
-- TOC entry 2101 (class 0 OID 16440)
-- Dependencies: 182
-- Data for Name: sprints; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY sprints (id, project_id, start_date, end_date, velocity) FROM stdin;
\.


--
-- TOC entry 2140 (class 0 OID 0)
-- Dependencies: 183
-- Name: sprints_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('sprints_id_seq', 1, false);


--
-- TOC entry 2103 (class 0 OID 16447)
-- Dependencies: 184
-- Data for Name: tasks; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY tasks (id, user_story_id, assignee_id, description, estimated_time, work_done, accepted) FROM stdin;
\.


--
-- TOC entry 2141 (class 0 OID 0)
-- Dependencies: 185
-- Name: tasks_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('tasks_id_seq', 1, false);


--
-- TOC entry 2105 (class 0 OID 16458)
-- Dependencies: 186
-- Data for Name: user_groups; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY user_groups (id, user_id, group_id) FROM stdin;
\.


--
-- TOC entry 2142 (class 0 OID 0)
-- Dependencies: 187
-- Name: user_groups_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('user_groups_id_seq', 1, false);


--
-- TOC entry 2107 (class 0 OID 16463)
-- Dependencies: 188
-- Data for Name: user_stories; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY user_stories (id, project_id, priority_id, sprint_id, title, story, bussiness_value, estimated_time) FROM stdin;
\.


--
-- TOC entry 2143 (class 0 OID 0)
-- Dependencies: 189
-- Name: user_stories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('user_stories_id_seq', 1, false);


--
-- TOC entry 2109 (class 0 OID 16475)
-- Dependencies: 190
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY users (id, username, password, name, email) FROM stdin;
\.


--
-- TOC entry 2144 (class 0 OID 0)
-- Dependencies: 191
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('users_id_seq', 1, false);


--
-- TOC entry 1922 (class 2606 OID 16496)
-- Name: acceptence_tests_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY acceptence_tests
    ADD CONSTRAINT "acceptence_tests_PK" PRIMARY KEY (id);


--
-- TOC entry 1924 (class 2606 OID 16498)
-- Name: acceptence_tests_unique_test_user_story_id; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY acceptence_tests
    ADD CONSTRAINT acceptence_tests_unique_test_user_story_id UNIQUE (user_story_id, test);


--
-- TOC entry 1926 (class 2606 OID 16500)
-- Name: group_permissions_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY group_permissions
    ADD CONSTRAINT "group_permissions_PK" PRIMARY KEY (id);


--
-- TOC entry 1928 (class 2606 OID 16502)
-- Name: group_permissions_unique_user_id_group_id; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY group_permissions
    ADD CONSTRAINT group_permissions_unique_user_id_group_id UNIQUE (group_id, permission_id);


--
-- TOC entry 1930 (class 2606 OID 16504)
-- Name: groups_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT "groups_PK" PRIMARY KEY (id);


--
-- TOC entry 1932 (class 2606 OID 16506)
-- Name: groups_unique_name; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT groups_unique_name UNIQUE (name);


--
-- TOC entry 1966 (class 2606 OID 24587)
-- Name: login_tokens_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY login_tokens
    ADD CONSTRAINT "login_tokens_PK" PRIMARY KEY (id);


--
-- TOC entry 1968 (class 2606 OID 24589)
-- Name: login_tokens_unique_token; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY login_tokens
    ADD CONSTRAINT login_tokens_unique_token UNIQUE (token);


--
-- TOC entry 1934 (class 2606 OID 16508)
-- Name: permissions_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT "permissions_PK" PRIMARY KEY (id);


--
-- TOC entry 1936 (class 2606 OID 16510)
-- Name: permissions_unique_name; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT permissions_unique_name UNIQUE (name);


--
-- TOC entry 1938 (class 2606 OID 16512)
-- Name: priorities_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY priorities
    ADD CONSTRAINT "priorities_PK" PRIMARY KEY (id);


--
-- TOC entry 1940 (class 2606 OID 16514)
-- Name: priorities_unique_name; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY priorities
    ADD CONSTRAINT priorities_unique_name UNIQUE (name);


--
-- TOC entry 1942 (class 2606 OID 16516)
-- Name: priorities_unique_priority; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY priorities
    ADD CONSTRAINT priorities_unique_priority UNIQUE (priority);


--
-- TOC entry 1944 (class 2606 OID 16518)
-- Name: projects_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY projects
    ADD CONSTRAINT "projects_PK" PRIMARY KEY (id);


--
-- TOC entry 1946 (class 2606 OID 16520)
-- Name: projects_unique_name; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY projects
    ADD CONSTRAINT projects_unique_name UNIQUE (name);


--
-- TOC entry 1948 (class 2606 OID 16522)
-- Name: sprints_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY sprints
    ADD CONSTRAINT "sprints_PK" PRIMARY KEY (id);


--
-- TOC entry 1950 (class 2606 OID 16524)
-- Name: tasks_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY tasks
    ADD CONSTRAINT "tasks_PK" PRIMARY KEY (id);


--
-- TOC entry 1952 (class 2606 OID 16526)
-- Name: tasks_unique_user_story_id_description; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY tasks
    ADD CONSTRAINT tasks_unique_user_story_id_description UNIQUE (user_story_id, description);


--
-- TOC entry 1954 (class 2606 OID 16528)
-- Name: user_groups_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY user_groups
    ADD CONSTRAINT "user_groups_PK" PRIMARY KEY (id);


--
-- TOC entry 1956 (class 2606 OID 16530)
-- Name: user_groups_unique_user_id_group_id; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY user_groups
    ADD CONSTRAINT user_groups_unique_user_id_group_id UNIQUE (user_id, group_id);


--
-- TOC entry 1958 (class 2606 OID 16532)
-- Name: user_stories_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY user_stories
    ADD CONSTRAINT "user_stories_PK" PRIMARY KEY (id);


--
-- TOC entry 1960 (class 2606 OID 16534)
-- Name: users_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT "users_PK" PRIMARY KEY (id);


--
-- TOC entry 1962 (class 2606 OID 16536)
-- Name: users_unique_email; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_unique_email UNIQUE (email);


--
-- TOC entry 1964 (class 2606 OID 16538)
-- Name: users_unique_username; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_unique_username UNIQUE (username);


--
-- TOC entry 1969 (class 2606 OID 16539)
-- Name: acceptence_tests_user_story_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY acceptence_tests
    ADD CONSTRAINT "acceptence_tests_user_story_FK" FOREIGN KEY (user_story_id) REFERENCES user_stories(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1970 (class 2606 OID 16544)
-- Name: group_permissions_group_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY group_permissions
    ADD CONSTRAINT "group_permissions_group_id_FK" FOREIGN KEY (group_id) REFERENCES groups(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1971 (class 2606 OID 16549)
-- Name: group_permissions_permission_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY group_permissions
    ADD CONSTRAINT "group_permissions_permission_id_FK" FOREIGN KEY (permission_id) REFERENCES permissions(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1972 (class 2606 OID 16554)
-- Name: groups_project_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT "groups_project_id_FK" FOREIGN KEY (project_id) REFERENCES projects(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1981 (class 2606 OID 24590)
-- Name: login_tokens_user_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY login_tokens
    ADD CONSTRAINT "login_tokens_user_id_FK" FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1973 (class 2606 OID 16559)
-- Name: sprints_project_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY sprints
    ADD CONSTRAINT "sprints_project_id_FK" FOREIGN KEY (project_id) REFERENCES projects(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1974 (class 2606 OID 16564)
-- Name: tasks_assignee_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY tasks
    ADD CONSTRAINT "tasks_assignee_id_FK" FOREIGN KEY (assignee_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 1975 (class 2606 OID 16569)
-- Name: tasks_story_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY tasks
    ADD CONSTRAINT "tasks_story_id_FK" FOREIGN KEY (user_story_id) REFERENCES user_stories(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1976 (class 2606 OID 16574)
-- Name: user_groups_group_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_groups
    ADD CONSTRAINT "user_groups_group_id_FK" FOREIGN KEY (group_id) REFERENCES groups(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1977 (class 2606 OID 16579)
-- Name: user_groups_user_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_groups
    ADD CONSTRAINT "user_groups_user_id_FK" FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1978 (class 2606 OID 16584)
-- Name: user_stories_priority_id; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_stories
    ADD CONSTRAINT user_stories_priority_id FOREIGN KEY (priority_id) REFERENCES priorities(id) ON UPDATE CASCADE ON DELETE SET DEFAULT;


--
-- TOC entry 1979 (class 2606 OID 16589)
-- Name: user_stories_project_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_stories
    ADD CONSTRAINT "user_stories_project_id_FK" FOREIGN KEY (project_id) REFERENCES projects(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1980 (class 2606 OID 16594)
-- Name: user_stories_sprint_id; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_stories
    ADD CONSTRAINT user_stories_sprint_id FOREIGN KEY (sprint_id) REFERENCES sprints(id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 2119 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2014-03-28 07:06:18

--
-- PostgreSQL database dump complete
--

