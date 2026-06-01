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
	sku TEXT,
	image TEXT
);

CREATE TABLE IF NOT EXISTS financial_entry (
	id TEXT PRIMARY KEY,
	type TEXT NOT NULL, -- IN or OUT
	amount REAL NOT NULL,
	description TEXT,
	occurred_at TEXT NOT NULL
);

