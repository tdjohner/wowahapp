
CREATE TABLE tblSubbedItems (
    id INT AUTO_INCREMENT PRIMARY KEY,
    itemId int,
    userId int
);


CREATE TABLE luProfessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name varchar(32)
);

CREATE TABLE luExpansions (
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

INSERT INTO luExpansions (name)
VALUES
("Classic"),
("The Burning Crusade"),
("Wrath of the Lich King"),
("Cataclysm"),
("Mists of Pandaria"),
("Warlords of Draenor"),
("Legion"),
("Battle for Azeroth"),
("Shadowlands");