create table tblauction (
                            id int unsigned auto_increment primary key,
                            auctionID int,
                            itemID int,
                            quantity int,
                            unitPrice decimal,
                            timeLeft varchar(16)
)