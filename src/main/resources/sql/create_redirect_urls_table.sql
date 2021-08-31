CREATE TABLE redirect_urls (
	id varchar(10) not null,
	offer_id varchar(10) not null,
	url varchar(2000) NOT null,
	PRIMARY KEY(id),
	CONSTRAINT fk_redirect_urls
	  FOREIGN KEY(offer_id)
	  REFERENCES offers(id)
);