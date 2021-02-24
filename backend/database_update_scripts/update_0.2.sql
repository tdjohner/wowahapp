
CREATE TABLE tblSubbedItems (
    id INT AUTO_INCREMENT PRIMARY KEY,
    itemId int,
    userId int
);


CREATE TABLE luProfessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name varchar(32)
);


INSERT INTO luProfessions (name)
VALUES
("Herbalism"),
("Mining"),
("Skinning"),
("Alchemy"),
("Blacksmithing"),
("Enchanting"),
("Engineering"),
("Inscription"),
("Jewelcrafting"),
("Leatherworking"),
("Tailoring"),
("Archaeology"),
("Cooking"),
("Fishing");