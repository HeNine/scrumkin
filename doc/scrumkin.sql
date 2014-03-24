--
-- PostgreSQL database dump
--

-- Dumped from database version 9.2.6
-- Dumped by pg_dump version 9.3.1
-- Started on 2014-03-23 20:57:50

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 2953 (class 1262 OID 17584)
-- Name: scrumkin; Type: DATABASE; Schema: -; Owner: scrumkin_admin
--

CREATE DATABASE scrumkin WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


ALTER DATABASE scrumkin OWNER TO scrumkin_admin;

\connect scrumkin

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 190 (class 3079 OID 12595)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2956 (class 0 OID 0)
-- Dependencies: 190
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 187 (class 1259 OID 17748)
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
-- TOC entry 186 (class 1259 OID 17746)
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
-- TOC entry 2957 (class 0 OID 0)
-- Dependencies: 186
-- Name: acceptence_tests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE acceptence_tests_id_seq OWNED BY acceptence_tests.id;


--
-- TOC entry 179 (class 1259 OID 17667)
-- Name: group_permissions; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE group_permissions (
    id integer NOT NULL,
    group_id integer NOT NULL,
    permission_id integer NOT NULL
);


ALTER TABLE public.group_permissions OWNER TO scrumkin_admin;

--
-- TOC entry 178 (class 1259 OID 17665)
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
-- TOC entry 2958 (class 0 OID 0)
-- Dependencies: 178
-- Name: group_permissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE group_permissions_id_seq OWNED BY group_permissions.id;


--
-- TOC entry 175 (class 1259 OID 17629)
-- Name: groups; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE groups (
    id integer NOT NULL,
    project_id integer,
    name text NOT NULL
);


ALTER TABLE public.groups OWNER TO scrumkin_admin;

--
-- TOC entry 174 (class 1259 OID 17627)
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
-- TOC entry 2959 (class 0 OID 0)
-- Dependencies: 174
-- Name: groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE groups_id_seq OWNED BY groups.id;


--
-- TOC entry 173 (class 1259 OID 17616)
-- Name: permissions; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE permissions (
    id integer NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.permissions OWNER TO scrumkin_admin;

--
-- TOC entry 172 (class 1259 OID 17614)
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
-- TOC entry 2960 (class 0 OID 0)
-- Dependencies: 172
-- Name: permissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE permissions_id_seq OWNED BY permissions.id;


--
-- TOC entry 183 (class 1259 OID 17702)
-- Name: priorities; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE priorities (
    id integer NOT NULL,
    priority integer NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.priorities OWNER TO scrumkin_admin;

--
-- TOC entry 182 (class 1259 OID 17700)
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
-- TOC entry 2961 (class 0 OID 0)
-- Dependencies: 182
-- Name: priorities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE priorities_id_seq OWNED BY priorities.id;


--
-- TOC entry 171 (class 1259 OID 17603)
-- Name: projects; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE projects (
    id integer NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.projects OWNER TO scrumkin_admin;

--
-- TOC entry 170 (class 1259 OID 17601)
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
-- TOC entry 2962 (class 0 OID 0)
-- Dependencies: 170
-- Name: projects_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE projects_id_seq OWNED BY projects.id;


--
-- TOC entry 181 (class 1259 OID 17687)
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
-- TOC entry 180 (class 1259 OID 17685)
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
-- TOC entry 2963 (class 0 OID 0)
-- Dependencies: 180
-- Name: sprints_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE sprints_id_seq OWNED BY sprints.id;


--
-- TOC entry 189 (class 1259 OID 17766)
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
-- TOC entry 188 (class 1259 OID 17764)
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
-- TOC entry 2964 (class 0 OID 0)
-- Dependencies: 188
-- Name: tasks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE tasks_id_seq OWNED BY tasks.id;


--
-- TOC entry 177 (class 1259 OID 17647)
-- Name: user_groups; Type: TABLE; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

CREATE TABLE user_groups (
    id integer NOT NULL,
    user_id integer NOT NULL,
    group_id integer NOT NULL
);


ALTER TABLE public.user_groups OWNER TO scrumkin_admin;

--
-- TOC entry 176 (class 1259 OID 17645)
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
-- TOC entry 2965 (class 0 OID 0)
-- Dependencies: 176
-- Name: user_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE user_groups_id_seq OWNED BY user_groups.id;


--
-- TOC entry 185 (class 1259 OID 17718)
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
-- TOC entry 184 (class 1259 OID 17716)
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
-- TOC entry 2966 (class 0 OID 0)
-- Dependencies: 184
-- Name: user_stories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE user_stories_id_seq OWNED BY user_stories.id;


--
-- TOC entry 169 (class 1259 OID 17587)
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
-- TOC entry 168 (class 1259 OID 17585)
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
-- TOC entry 2967 (class 0 OID 0)
-- Dependencies: 168
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: scrumkin_admin
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- TOC entry 2760 (class 2604 OID 17751)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY acceptence_tests ALTER COLUMN id SET DEFAULT nextval('acceptence_tests_id_seq'::regclass);


--
-- TOC entry 2750 (class 2604 OID 17670)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY group_permissions ALTER COLUMN id SET DEFAULT nextval('group_permissions_id_seq'::regclass);


--
-- TOC entry 2748 (class 2604 OID 17632)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY groups ALTER COLUMN id SET DEFAULT nextval('groups_id_seq'::regclass);


--
-- TOC entry 2747 (class 2604 OID 17619)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY permissions ALTER COLUMN id SET DEFAULT nextval('permissions_id_seq'::regclass);


--
-- TOC entry 2754 (class 2604 OID 17705)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY priorities ALTER COLUMN id SET DEFAULT nextval('priorities_id_seq'::regclass);


--
-- TOC entry 2746 (class 2604 OID 17606)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY projects ALTER COLUMN id SET DEFAULT nextval('projects_id_seq'::regclass);


--
-- TOC entry 2751 (class 2604 OID 17690)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY sprints ALTER COLUMN id SET DEFAULT nextval('sprints_id_seq'::regclass);


--
-- TOC entry 2761 (class 2604 OID 17769)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY tasks ALTER COLUMN id SET DEFAULT nextval('tasks_id_seq'::regclass);


--
-- TOC entry 2749 (class 2604 OID 17650)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_groups ALTER COLUMN id SET DEFAULT nextval('user_groups_id_seq'::regclass);


--
-- TOC entry 2755 (class 2604 OID 17721)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_stories ALTER COLUMN id SET DEFAULT nextval('user_stories_id_seq'::regclass);


--
-- TOC entry 2744 (class 2604 OID 17590)
-- Name: id; Type: DEFAULT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- TOC entry 2946 (class 0 OID 17748)
-- Dependencies: 187
-- Data for Name: acceptence_tests; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY acceptence_tests (id, user_story_id, test, accepted) FROM stdin;
\.


--
-- TOC entry 2968 (class 0 OID 0)
-- Dependencies: 186
-- Name: acceptence_tests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('acceptence_tests_id_seq', 1, false);


--
-- TOC entry 2938 (class 0 OID 17667)
-- Dependencies: 179
-- Data for Name: group_permissions; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY group_permissions (id, group_id, permission_id) FROM stdin;
\.


--
-- TOC entry 2969 (class 0 OID 0)
-- Dependencies: 178
-- Name: group_permissions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('group_permissions_id_seq', 1, false);


--
-- TOC entry 2934 (class 0 OID 17629)
-- Dependencies: 175
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY groups (id, project_id, name) FROM stdin;
\.


--
-- TOC entry 2970 (class 0 OID 0)
-- Dependencies: 174
-- Name: groups_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('groups_id_seq', 1, false);


--
-- TOC entry 2932 (class 0 OID 17616)
-- Dependencies: 173
-- Data for Name: permissions; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY permissions (id, name) FROM stdin;
\.


--
-- TOC entry 2971 (class 0 OID 0)
-- Dependencies: 172
-- Name: permissions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('permissions_id_seq', 1, false);


--
-- TOC entry 2942 (class 0 OID 17702)
-- Dependencies: 183
-- Data for Name: priorities; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY priorities (id, priority, name) FROM stdin;
\.


--
-- TOC entry 2972 (class 0 OID 0)
-- Dependencies: 182
-- Name: priorities_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('priorities_id_seq', 1, false);


--
-- TOC entry 2930 (class 0 OID 17603)
-- Dependencies: 171
-- Data for Name: projects; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY projects (id, name) FROM stdin;
\.


--
-- TOC entry 2973 (class 0 OID 0)
-- Dependencies: 170
-- Name: projects_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('projects_id_seq', 1, false);


--
-- TOC entry 2940 (class 0 OID 17687)
-- Dependencies: 181
-- Data for Name: sprints; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY sprints (id, project_id, start_date, end_date, velocity) FROM stdin;
\.


--
-- TOC entry 2974 (class 0 OID 0)
-- Dependencies: 180
-- Name: sprints_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('sprints_id_seq', 1, false);


--
-- TOC entry 2948 (class 0 OID 17766)
-- Dependencies: 189
-- Data for Name: tasks; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY tasks (id, user_story_id, assignee_id, description, estimated_time, work_done, accepted) FROM stdin;
\.


--
-- TOC entry 2975 (class 0 OID 0)
-- Dependencies: 188
-- Name: tasks_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('tasks_id_seq', 1, false);


--
-- TOC entry 2936 (class 0 OID 17647)
-- Dependencies: 177
-- Data for Name: user_groups; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY user_groups (id, user_id, group_id) FROM stdin;
\.


--
-- TOC entry 2976 (class 0 OID 0)
-- Dependencies: 176
-- Name: user_groups_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('user_groups_id_seq', 1, false);


--
-- TOC entry 2944 (class 0 OID 17718)
-- Dependencies: 185
-- Data for Name: user_stories; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY user_stories (id, project_id, priority_id, sprint_id, title, story, bussiness_value, estimated_time) FROM stdin;
\.


--
-- TOC entry 2977 (class 0 OID 0)
-- Dependencies: 184
-- Name: user_stories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('user_stories_id_seq', 1, false);


--
-- TOC entry 2928 (class 0 OID 17587)
-- Dependencies: 169
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: scrumkin_admin
--

COPY users (id, username, password, name, email) FROM stdin;
\.


--
-- TOC entry 2978 (class 0 OID 0)
-- Dependencies: 168
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: scrumkin_admin
--

SELECT pg_catalog.setval('users_id_seq', 1, false);


--
-- TOC entry 2802 (class 2606 OID 17756)
-- Name: acceptence_tests_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY acceptence_tests
    ADD CONSTRAINT "acceptence_tests_PK" PRIMARY KEY (id);


--
-- TOC entry 2804 (class 2606 OID 17758)
-- Name: acceptence_tests_unique_test_user_story_id; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY acceptence_tests
    ADD CONSTRAINT acceptence_tests_unique_test_user_story_id UNIQUE (user_story_id, test);


--
-- TOC entry 2788 (class 2606 OID 17672)
-- Name: group_permissions_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY group_permissions
    ADD CONSTRAINT "group_permissions_PK" PRIMARY KEY (id);


--
-- TOC entry 2790 (class 2606 OID 17674)
-- Name: group_permissions_unique_user_id_group_id; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY group_permissions
    ADD CONSTRAINT group_permissions_unique_user_id_group_id UNIQUE (group_id, permission_id);


--
-- TOC entry 2780 (class 2606 OID 17637)
-- Name: groups_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT "groups_PK" PRIMARY KEY (id);


--
-- TOC entry 2782 (class 2606 OID 17639)
-- Name: groups_unique_name; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT groups_unique_name UNIQUE (name);


--
-- TOC entry 2776 (class 2606 OID 17624)
-- Name: permissions_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT "permissions_PK" PRIMARY KEY (id);


--
-- TOC entry 2778 (class 2606 OID 17626)
-- Name: permissions_unique_name; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT permissions_unique_name UNIQUE (name);


--
-- TOC entry 2794 (class 2606 OID 17710)
-- Name: priorities_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY priorities
    ADD CONSTRAINT "priorities_PK" PRIMARY KEY (id);


--
-- TOC entry 2796 (class 2606 OID 17714)
-- Name: priorities_unique_name; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY priorities
    ADD CONSTRAINT priorities_unique_name UNIQUE (name);


--
-- TOC entry 2798 (class 2606 OID 17712)
-- Name: priorities_unique_priority; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY priorities
    ADD CONSTRAINT priorities_unique_priority UNIQUE (priority);


--
-- TOC entry 2772 (class 2606 OID 17611)
-- Name: projects_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY projects
    ADD CONSTRAINT "projects_PK" PRIMARY KEY (id);


--
-- TOC entry 2774 (class 2606 OID 17613)
-- Name: projects_unique_name; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY projects
    ADD CONSTRAINT projects_unique_name UNIQUE (name);


--
-- TOC entry 2792 (class 2606 OID 17694)
-- Name: sprints_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY sprints
    ADD CONSTRAINT "sprints_PK" PRIMARY KEY (id);


--
-- TOC entry 2806 (class 2606 OID 17777)
-- Name: tasks_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY tasks
    ADD CONSTRAINT "tasks_PK" PRIMARY KEY (id);


--
-- TOC entry 2808 (class 2606 OID 17779)
-- Name: tasks_unique_user_story_id_description; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY tasks
    ADD CONSTRAINT tasks_unique_user_story_id_description UNIQUE (user_story_id, description);


--
-- TOC entry 2784 (class 2606 OID 17652)
-- Name: user_groups_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY user_groups
    ADD CONSTRAINT "user_groups_PK" PRIMARY KEY (id);


--
-- TOC entry 2786 (class 2606 OID 17654)
-- Name: user_groups_unique_user_id_group_id; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY user_groups
    ADD CONSTRAINT user_groups_unique_user_id_group_id UNIQUE (user_id, group_id);


--
-- TOC entry 2800 (class 2606 OID 17730)
-- Name: user_stories_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY user_stories
    ADD CONSTRAINT "user_stories_PK" PRIMARY KEY (id);


--
-- TOC entry 2766 (class 2606 OID 17596)
-- Name: users_PK; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT "users_PK" PRIMARY KEY (id);


--
-- TOC entry 2768 (class 2606 OID 17598)
-- Name: users_unique_email; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_unique_email UNIQUE (email);


--
-- TOC entry 2770 (class 2606 OID 17600)
-- Name: users_unique_username; Type: CONSTRAINT; Schema: public; Owner: scrumkin_admin; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_unique_username UNIQUE (username);


--
-- TOC entry 2818 (class 2606 OID 17759)
-- Name: acceptence_tests_user_story_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY acceptence_tests
    ADD CONSTRAINT "acceptence_tests_user_story_FK" FOREIGN KEY (user_story_id) REFERENCES user_stories(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2812 (class 2606 OID 17675)
-- Name: group_permissions_group_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY group_permissions
    ADD CONSTRAINT "group_permissions_group_id_FK" FOREIGN KEY (group_id) REFERENCES groups(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2813 (class 2606 OID 17680)
-- Name: group_permissions_permission_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY group_permissions
    ADD CONSTRAINT "group_permissions_permission_id_FK" FOREIGN KEY (permission_id) REFERENCES permissions(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2809 (class 2606 OID 17640)
-- Name: groups_project_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT "groups_project_id_FK" FOREIGN KEY (project_id) REFERENCES projects(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2814 (class 2606 OID 17695)
-- Name: sprints_project_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY sprints
    ADD CONSTRAINT "sprints_project_id_FK" FOREIGN KEY (project_id) REFERENCES projects(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2820 (class 2606 OID 17785)
-- Name: tasks_assignee_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY tasks
    ADD CONSTRAINT "tasks_assignee_id_FK" FOREIGN KEY (assignee_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 2819 (class 2606 OID 17780)
-- Name: tasks_story_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY tasks
    ADD CONSTRAINT "tasks_story_id_FK" FOREIGN KEY (user_story_id) REFERENCES user_stories(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2811 (class 2606 OID 17660)
-- Name: user_groups_group_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_groups
    ADD CONSTRAINT "user_groups_group_id_FK" FOREIGN KEY (group_id) REFERENCES groups(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2810 (class 2606 OID 17655)
-- Name: user_groups_user_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_groups
    ADD CONSTRAINT "user_groups_user_id_FK" FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2816 (class 2606 OID 17736)
-- Name: user_stories_priority_id; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_stories
    ADD CONSTRAINT user_stories_priority_id FOREIGN KEY (priority_id) REFERENCES priorities(id) ON UPDATE CASCADE ON DELETE SET DEFAULT;


--
-- TOC entry 2815 (class 2606 OID 17731)
-- Name: user_stories_project_id_FK; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_stories
    ADD CONSTRAINT "user_stories_project_id_FK" FOREIGN KEY (project_id) REFERENCES projects(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2817 (class 2606 OID 17741)
-- Name: user_stories_sprint_id; Type: FK CONSTRAINT; Schema: public; Owner: scrumkin_admin
--

ALTER TABLE ONLY user_stories
    ADD CONSTRAINT user_stories_sprint_id FOREIGN KEY (sprint_id) REFERENCES sprints(id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 2955 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2014-03-23 20:57:51

--
-- PostgreSQL database dump complete
--

