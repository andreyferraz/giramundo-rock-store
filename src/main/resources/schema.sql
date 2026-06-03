-- Schema for Giramundo store
CREATE TABLE IF NOT EXISTS admin (
	id TEXT PRIMARY KEY,
	username TEXT NOT NULL UNIQUE,
	password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS product (
	id TEXT PRIMARY KEY,
	name TEXT NOT NULL,
	description TEXT,
	price REAL NOT NULL,
	quantity INTEGER NOT NULL,
	image TEXT
);

CREATE TABLE IF NOT EXISTS financial_entry (
	id TEXT PRIMARY KEY,
	type TEXT NOT NULL, -- IN or OUT
	price REAL NOT NULL,
	description TEXT,
	occurred_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS event (
	id TEXT PRIMARY KEY,
	title TEXT NOT NULL,
	description TEXT NOT NULL,
	image TEXT,
	published_at TEXT NOT NULL
);

