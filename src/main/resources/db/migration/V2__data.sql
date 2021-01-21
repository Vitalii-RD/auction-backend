insert into users(name, email, password) values ('admin', 'admin@acme.com', 'admin');
insert into users(name, email, password) values ('vasya', 'vasya@acme.com', 'vasya');
insert into users(name, email, password) values ('andriy', 'andriy@acme.com', 'andriy');

insert into items(title, user_id) values ('red cup', 1);
insert into auctions(initial_bid, date_created, done, item_id) values (30, '2020-01-06 20:00:00', false, 1);
insert into bids(bid, date, user_id, auction_id) values (50, '2020-01-06 20:00:00', 2, 1);
